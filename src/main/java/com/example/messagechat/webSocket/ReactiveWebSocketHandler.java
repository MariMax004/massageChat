package com.example.messagechat.webSocket;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {
    private final KafkaReceiver<Integer, String> kafkaReceiver;
    private final KafkaSender<Integer, String> kafkaSender;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> kafkaMessages = readFromKafka();

        return (session.send(kafkaMessages
                .map(session::textMessage)
        )).and(session.receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .doOnNext(message -> {
                    sendToKafka("chat-messages", message);
                }));
    }

    public void sendToKafka(String topic, String message) {
        Flux<SenderRecord<Integer, String, Integer>> outboundFlux =
                Flux.just(SenderRecord.create(new ProducerRecord<>(topic, message), 1));
        kafkaSender.send(outboundFlux)
                .subscribe();
        System.out.println("send message is " + message);
    }

    public Flux<String> readFromKafka() {
        Flux<ReceiverRecord<Integer, String>> inboundFlux = kafkaReceiver.receive();
        Flux<String> records = inboundFlux
                .map(r -> {
                    String value = r.value();
                    System.out.println("received message is " + r.value());
                    r.receiverOffset().acknowledge();
                    return value;
                });
        return records;
    }
}
