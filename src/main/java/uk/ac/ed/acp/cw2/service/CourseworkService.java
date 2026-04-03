package uk.ac.ed.acp.cw2.service;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dto.SplitterRequest;
import uk.ac.ed.acp.cw2.dto.TransformRequest;

@Service
public class CourseworkService {

    private final RabbitMqService rabbitMqService;
    private final KafkaService kafkaService;
    private final RedisService redisService;
    private final Gson gson = new Gson();

    public CourseworkService(RabbitMqService rabbitMqService,
                             KafkaService kafkaService,
                             RedisService redisService) {
        this.rabbitMqService = rabbitMqService;
        this.kafkaService = kafkaService;
        this.redisService = redisService;
    }

    public void processSplitter(SplitterRequest request) {
        throw new UnsupportedOperationException("splitter not implemented yet");
    }

    public void processTransformMessages(TransformRequest request) {
        throw new UnsupportedOperationException("transformMessages not implemented yet");
    }
}