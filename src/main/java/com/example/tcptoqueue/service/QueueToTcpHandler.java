package com.example.tcptoqueue.service;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(Processor.class)
public class QueueToTcpHandler {

	public Message<String> handleQueueIn(Message<String> messageIn) {
		String payload = messageIn.getPayload();
		String replyChannel;

		try {
			isValid(payload); // throws Exception if payload is invalid
			replyChannel = "tcpOut";
		} catch (Exception e) {
			System.out.println("Invalid payload, sending to nullChannel");
			replyChannel = "nullChannel";
		}

		return MessageBuilder
				.withPayload(payload)
				.setHeader("replyChannel", replyChannel)
				.build();
	}

	private void isValid(String payload) throws Exception {
		if (payload.equals("")) {
			throw new Exception();
		}
	}
}