package com.example.tcptoqueue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@MessageEndpoint
public class QueueToTcpHandler {

	private static final Logger logger = LoggerFactory.getLogger(QueueToTcpHandler.class);

	public static final String OUTPUT = "tcpOut";

	@ServiceActivator(inputChannel = Processor.INPUT, outputChannel = OUTPUT)
	public Message<String> handleQueueIn(Message<String> message) {
		logger.info("Got a message to send to TCP");

		isValid(message.getPayload());

		return message;
	}

	private void isValid(String payload) throws RuntimeException {
		if (payload.equals("")) {
			throw new RuntimeException();
		}
	}
}