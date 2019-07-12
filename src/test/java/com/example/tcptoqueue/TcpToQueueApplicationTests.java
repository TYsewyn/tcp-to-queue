package com.example.tcptoqueue;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TcpToQueueApplicationTests {

	@Autowired
	private TcpNetClientConnectionFactory tcpClientFactory;

	@Autowired
	private Processor processor;

	@Autowired
	@Qualifier("responseChannel")
	private QueueChannel responseChannel;

	@Before
	public void init() {
		tcpClientFactory.start();
	}

	@After
	public void tearDown() {
		tcpClientFactory.stop();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void validPayloadSentToTcpConnection() throws Exception {
		Message<String> requestMessage = MessageBuilder
				.withPayload("This is a valid payload")
				.build();
		tcpClientFactory.getConnection().send(requestMessage);

		Message<String> responseMessage = MessageBuilder
				.withPayload(requestMessage.getPayload())
				.setHeader(IpHeaders.CONNECTION_ID, tcpClientFactory.getConnection().getConnectionId())
				.build();

		processor.input().send(responseMessage);

		Message<String> received = (Message<String>) responseChannel.receive(2000);
		assertNotNull(received);
	}
}