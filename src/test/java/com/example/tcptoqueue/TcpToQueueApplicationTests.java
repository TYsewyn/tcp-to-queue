package com.example.tcptoqueue;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TcpToQueueApplicationTests {

	@Autowired
	private AbstractClientConnectionFactory tcpClientFactory;

	@Autowired
	@Qualifier("responseChannel")
	private QueueChannel responseChannel;

	@Test
	@SuppressWarnings("unchecked")
	public void validPayloadSentToTcpConnection() throws Exception {
		Message<String> requestMessage = MessageBuilder
				.withPayload("This is a valid payload")
				.build();
		this.tcpClientFactory.getConnection().send(requestMessage);

		Message<String> received = (Message<String>) this.responseChannel.receive(2000);
		assertNotNull(received);
	}

	@TestConfiguration
	public static class Config {

		@Bean
		QueueChannel responseChannel() {
			return MessageChannels.queue("responseChannel").get();
		}

		@Bean
		IntegrationFlow tcpResponseFlow() {
			return IntegrationFlows
					.from(Tcp.inboundAdapter(tcpClientFactory()))
					.channel(responseChannel())
					.get();
		}

		@Bean
		AbstractClientConnectionFactory tcpClientFactory() {
			return Tcp.netClient("localhost", 6060)
					.serializer(TcpCodecs.lengthHeader2())
					.deserializer(TcpCodecs.lengthHeader2())
					.get();
		}


		@Bean
		IntegrationFlow bridgeBetweenQueues() {
			return IntegrationFlows
					.from(Processor.OUTPUT) // TcpToQueueHandler
					.channel(Processor.INPUT) // QueueToTcpHandler
					.get();
		}
	}
}