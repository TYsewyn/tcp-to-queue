package com.example.tcptoqueue.service;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(Processor.class)
public class TcpToQueueHandler {

	public Message<String> handleTcpIn(Message<String> message) {
		String payload = message.getPayload();
		String replyChannel;

		try {
			isValid(payload); // throws Exception if payload is invalid
			replyChannel = Processor.OUTPUT;
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