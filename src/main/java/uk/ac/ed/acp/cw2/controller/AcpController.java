package uk.ac.ed.acp.cw2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.dto.SortedMessage;
import uk.ac.ed.acp.cw2.dto.SplitterRequest;
import uk.ac.ed.acp.cw2.dto.TransformRequest;
import uk.ac.ed.acp.cw2.service.CourseworkService;
import uk.ac.ed.acp.cw2.service.KafkaService;
import uk.ac.ed.acp.cw2.service.RabbitMqService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acp")
public class AcpController {

    private static final String STUDENT_ID = "s2172881";

    private final RabbitMqService rabbitMqService;
    private final KafkaService kafkaService;
    private final CourseworkService courseworkService;

    public AcpController(RabbitMqService rabbitMqService,
                         KafkaService kafkaService,
                         CourseworkService courseworkService) {
        this.rabbitMqService = rabbitMqService;
        this.kafkaService = kafkaService;
        this.courseworkService = courseworkService;
    }

    @PutMapping("/messages/rabbitmq/{queueName}/{messageCount}")
    public ResponseEntity<Void> writeRabbitMqMessages(@PathVariable String queueName,
                                                      @PathVariable int messageCount) {
        rabbitMqService.sendCounterMessages(queueName, messageCount, STUDENT_ID);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/messages/kafka/{writeTopic}/{messageCount}")
    public ResponseEntity<Void> writeKafkaMessages(@PathVariable String writeTopic,
                                                   @PathVariable int messageCount) {
        kafkaService.sendCounterMessages(writeTopic, messageCount, STUDENT_ID);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/rabbitmq/{queueName}/{timeoutInMsec}")
    public ResponseEntity<List<String>> readRabbitMqMessages(@PathVariable String queueName,
                                                             @PathVariable int timeoutInMsec) {
        return ResponseEntity.ok(rabbitMqService.readMessagesUntilTimeout(queueName, timeoutInMsec));
    }

    @GetMapping("/messages/kafka/{readTopic}/{timeoutInMsec}")
    public ResponseEntity<List<String>> readKafkaMessages(@PathVariable String readTopic,
                                                          @PathVariable int timeoutInMsec) {
        return ResponseEntity.ok(kafkaService.readMessagesUntilTimeout(readTopic, timeoutInMsec));
    }

    @GetMapping("/messages/sorted/rabbitmq/{queueName}/{messagesToConsider}")
    public ResponseEntity<List<SortedMessage>> readSortedRabbitMqMessages(@PathVariable String queueName,
                                                                          @PathVariable int messagesToConsider) {
        return ResponseEntity.ok(rabbitMqService.readSortedMessages(queueName, messagesToConsider));
    }

    @GetMapping("/messages/sorted/kafka/{topic}/{messagesToConsider}")
    public ResponseEntity<List<SortedMessage>> readSortedKafkaMessages(@PathVariable String topic,
                                                                       @PathVariable int messagesToConsider) {
        return ResponseEntity.ok(kafkaService.readSortedMessages(topic, messagesToConsider));
    }

    @PostMapping("/splitter")
    public ResponseEntity<Void> splitter(@RequestBody SplitterRequest request) {
        courseworkService.processSplitter(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transformMessages")
    public ResponseEntity<Void> transformMessages(@RequestBody TransformRequest request) {
        courseworkService.processTransformMessages(request);
        return ResponseEntity.ok().build();
    }
}