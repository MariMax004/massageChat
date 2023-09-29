package com.example.messagechat.rSocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Component
@RequiredArgsConstructor
public class RSocketService extends AbstractRSocket {

    private final KafkaReceiver<Integer, String> kafkaReceiver;
    private final KafkaSender<Integer, String> kafkaSender;

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        sendToKafka("chat-messages", payload.getDataUtf8());
        log.info("requestStream {}");
        return readFromKafka().map(DefaultPayload::create);
    }

    public void sendToKafka(String topic, String message) {
        Flux<SenderRecord<Integer, String, Integer>> outboundFlux =
                Flux.just(SenderRecord.create(new ProducerRecord<>(topic, message), 1));
        kafkaSender.send(outboundFlux)
                .subscribe();
        log.info("send message is " + message);
    }

    public Flux<String> readFromKafka() {
        Flux<ReceiverRecord<Integer, String>> inboundFlux = kafkaReceiver.receive();
        Flux<String> records = inboundFlux
                .map(r -> {
                    String value = r.value();
                    log.info("received message is " + r.value());
                    r.receiverOffset().acknowledge();
                    return value;
                });
        return records;
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
//        System.out.println("fire-and-forget: server received " + payload.getDataUtf8());
//        sendToKafka("chat-messages", payload.getDataUtf8());
//
//        readFromKafka().subscribe(System.out::println);
//        System.out.println("fire-and-forget: server received");
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        log.info(payload.getDataUtf8());
        return Mono.just(DefaultPayload.create("Connection successful"));
    }


    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads).map(Payload::getDataUtf8)
                .doOnNext(str -> log.info("Received: " + str))
                .map(DefaultPayload::create);

    }
}
