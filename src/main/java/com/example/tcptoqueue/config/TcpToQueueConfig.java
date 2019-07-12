package com.example.tcptoqueue.config;

import com.example.tcptoqueue.service.QueueToTcpHandler;
import com.example.tcptoqueue.service.TcpToQueueHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;

@Configuration
@EnableIntegration
public class TcpToQueueConfig {

	@Autowired
	private TcpToQueueHandler tcpToQueueHandler;

	@Autowired
	private QueueToTcpHandler queueToTcpHandler;

	// Main config

	@Bean
	public IntegrationFlow tcpToQueueFlow() {
		return IntegrationFlows
				.from(Tcp.inboundAdapter(tcpServerFactory()))
				.handle(tcpToQueueHandler, "handleTcpIn")
				.route("headers['replyChannel']")
				.get();
	}

	@Bean
	public IntegrationFlow queueToTcpFlow() {
		return IntegrationFlows
				.from(Processor.INPUT)
				.handle(queueToTcpHandler, "handleQueueIn")
				.route("headers['replyChannel']")
				.get();
	}

	@Bean
	@ServiceActivator(inputChannel = "tcpOut")
	public TcpSendingMessageHandler tcpOutboundAdapter() {
		TcpSendingMessageHandler adapter = new TcpSendingMessageHandler();
		adapter.setConnectionFactory(tcpServerFactory());
		return adapter;
	}

	@Bean
	public TcpNetServerConnectionFactory tcpServerFactory() {
		TcpNetServerConnectionFactory serverFactory = new TcpNetServerConnectionFactory(6060);
		serverFactory.setSerializer(TcpCodecs.lengthHeader2());
		serverFactory.setDeserializer(TcpCodecs.lengthHeader2());
		return serverFactory;
	}

	// Test config

	@Bean
	public IntegrationFlow tcpResponseFlow() {
		return IntegrationFlows
				.from(Tcp.inboundAdapter(tcpClientFactory()))
				.channel(MessageChannels
						.queue("responseChannel")
						.get())
				.get();
	}

	@Bean
	TcpNetClientConnectionFactory tcpClientFactory() {
		TcpNetClientConnectionFactory tcpClientFactory = new TcpNetClientConnectionFactory("localhost", 6060);
		tcpClientFactory.setSerializer(TcpCodecs.lengthHeader2());
		tcpClientFactory.setDeserializer(TcpCodecs.lengthHeader2());
		return tcpClientFactory;
	}
}