package com.micro.pubstream.api;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class PubstreamApi {

	@Autowired
	private Publisher publisher;

	Logger log = LoggerFactory.getLogger(PubstreamApi.class);

	@PostMapping("pub")
    public String publishMessage(@RequestParam("message") String message)
			throws ExecutionException, InterruptedException {

		ByteString data = ByteString.copyFromUtf8(message);
		PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

		// Once published, returns a server-assigned message id (unique within the topic)
		ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
		String messageId = messageIdFuture.get();
		log.info("Published message ID: {}", messageId);

		return "Published Successfully";

    }

}

