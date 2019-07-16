package com.example.tcptoqueue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@MessageEndpoint
public class TcpToQueueHandler {

	private static final Logger logger = LoggerFactory.getLogger(TcpToQueueHandler.class);

	public static final String INPUT = "tcpIn";

	@ServiceActivator(inputChannel = INPUT, outputChannel = Processor.OUTPUT)
	public Message<String> handleTcpIn(Message<String> message) {
		logger.info("Got a message from TCP");

		isValid(message.getPayload());

		return message;
	}

	private void isValid(String payload) throws RuntimeException {
		if (payload.equals("")) {
			throw new RuntimeException();
		}
	}
}