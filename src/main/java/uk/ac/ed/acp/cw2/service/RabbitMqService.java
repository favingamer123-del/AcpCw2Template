package uk.ac.ed.acp.cw2.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.RuntimeEnvironment;
import uk.ac.ed.acp.cw2.dto.CounterMessage;
import uk.ac.ed.acp.cw2.dto.SortedMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RabbitMqService {

    private final RuntimeEnvironment environment;
    private final Gson gson = new Gson();

    public RabbitMqService(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    private ConnectionFactory newFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(environment.getRabbitMqHost());
        factory.setPort(environment.getRabbitMqPort());
        return factory;
    }

    public void sendCounterMessages(String queueName, int messageCount, String uid) {
        try (Connection connection = newFactory().newConnection();
             Channel channel = connection.createChannel()) {

            for (int i = 0; i < messageCount; i++) {
                String json = gson.toJson(new CounterMessage(uid, i));
                channel.basicPublish("", queueName, null, json.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ send failed", e);
        }
    }

    public List<String> readMessagesUntilTimeout(String queueName, long timeoutMs) {
        List<String> result = new ArrayList<>();
        long deadline = System.currentTimeMillis() + Math.max(timeoutMs - 50, 1);

        try (Connection connection = newFactory().newConnection();
             Channel channel = connection.createChannel()) {

            while (System.currentTimeMillis() < deadline) {
                var response = channel.basicGet(queueName, true);
                if (response != null) {
                    result.add(new String(response.getBody(), StandardCharsets.UTF_8));
                } else {
                    Thread.sleep(10);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ read failed", e);
        }
    }

    public List<SortedMessage> readSortedMessages(String queueName, int count) {
        List<String> raw = readExactly(queueName, count);
        List<SortedMessage> parsed = new ArrayList<>();

        for (String s : raw) {
            parsed.add(gson.fromJson(s, SortedMessage.class));
        }

        parsed.sort(Comparator.comparingInt(SortedMessage::getId));
        return parsed;
    }

    public List<String> readExactly(String queueName, int count) {
        List<String> result = new ArrayList<>();

        try (Connection connection = newFactory().newConnection();
             Channel channel = connection.createChannel()) {

            while (result.size() < count) {
                var response = channel.basicGet(queueName, true);
                if (response != null) {
                    result.add(new String(response.getBody(), StandardCharsets.UTF_8));
                } else {
                    Thread.sleep(10);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ exact read failed", e);
        }
    }

    public void writeRawMessage(String queueName, String message) {
        try (Connection connection = newFactory().newConnection();
             Channel channel = connection.createChannel()) {

            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("RabbitMQ raw write failed", e);
        }
    }
}