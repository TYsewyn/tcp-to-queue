package com.example.tcptoqueue.config;

import com.example.tcptoqueue.service.QueueToTcpHandler;
import com.example.tcptoqueue.service.TcpToQueueHandler;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableBinding(Processor.class)
public class TcpToQueueConfig {

	@Bean
	MessageChannel tcpIn() {
		return MessageChannels.queue(TcpToQueueHandler.INPUT).get();
	}

	@Bean
	MessageChannel tcpOut() {
		return MessageChannels.queue(QueueToTcpHandler.OUTPUT).get();
	}

	@Bean
	public IntegrationFlow tcpToQueueFlow() {
		return IntegrationFlows
				.from(Tcp.inboundAdapter(tcpServerFactory()))
				.channel(tcpIn())
				.get();
	}

	@Bean
	public IntegrationFlow tcpOutboundFlow() {
		return IntegrationFlows
				.from(tcpOut())
				.handle(Tcp.outboundAdapter(tcpServerFactory()))
				.get();
	}

	@Bean
	public AbstractServerConnectionFactory tcpServerFactory() {
		return Tcp.netServer(6060)
				.serializer(TcpCodecs.lengthHeader2())
				.deserializer(TcpCodecs.lengthHeader2())
				.get();
	}

}