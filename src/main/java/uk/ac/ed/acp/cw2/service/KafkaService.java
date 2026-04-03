package uk.ac.ed.acp.cw2.service;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.RuntimeEnvironment;
import uk.ac.ed.acp.cw2.dto.CounterMessage;
import uk.ac.ed.acp.cw2.dto.SortedMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class KafkaService {

    private final RuntimeEnvironment environment;
    private final Gson gson = new Gson();

    public KafkaService(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    private Properties producerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", environment.getKafkaBootstrapServers());
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");
        return props;
    }

    private Properties consumerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", environment.getKafkaBootstrapServers());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cw2-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return props;
    }

    public void sendCounterMessages(String topic, int messageCount, String uid) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps())) {
            for (int i = 0; i < messageCount; i++) {
                String json = gson.toJson(new CounterMessage(uid, i));
                producer.send(new ProducerRecord<>(topic, json)).get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Kafka send failed", e);
        }
    }

    public List<String> readMessagesUntilTimeout(String topic, long timeoutMs) {
        List<String> result = new ArrayList<>();
        long deadline = System.currentTimeMillis() + Math.max(timeoutMs - 50, 1);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps())) {
            consumer.subscribe(Collections.singletonList(topic));

            while (System.currentTimeMillis() < deadline) {
                var records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> result.add(record.value()));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Kafka read failed", e);
        }
    }

    public List<SortedMessage> readSortedMessages(String topic, int count) {
        List<String> raw = readExactly(topic, count);
        List<SortedMessage> parsed = new ArrayList<>();

        for (String s : raw) {
            parsed.add(gson.fromJson(s, SortedMessage.class));
        }

        parsed.sort(Comparator.comparingInt(SortedMessage::getId));
        return parsed;
    }

    public List<String> readExactly(String topic, int count) {
        List<String> result = new ArrayList<>();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps())) {
            consumer.subscribe(Collections.singletonList(topic));

            while (result.size() < count) {
                var records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> result.add(record.value()));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Kafka exact read failed", e);
        }
    }

    public void writeRawMessage(String topic, String message) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps())) {
            producer.send(new ProducerRecord<>(topic, message)).get();
        } catch (Exception e) {
            throw new RuntimeException("Kafka raw write failed", e);
        }
    }
}