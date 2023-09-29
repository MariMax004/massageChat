package com.example.messagechat.rSocket;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;

import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;

@Configuration
@RequiredArgsConstructor
public class RSocketServerConfig {

    private final KafkaReceiver<Integer, String> kafkaReceiver;
    private final KafkaSender<Integer, String> kafkaSender;

    //    @PostConstruct
//    public void startServer() {
//        RSocketFactory.receive()
//                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketService()))
//                .transport(TcpServerTransport.create(8000))
//                .start()
//                .block()
//                .onClose()
//                .block();
//    }
    @PostConstruct
    public void startServer() {
        RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(
                        new RSocketService(kafkaReceiver, kafkaSender)))
                .transport(WebsocketServerTransport.create("localhost", 8000))
                .start()
                .block()
                .onClose()
                .block();
    }
}

