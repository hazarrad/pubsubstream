package com.micro.pubstream.api;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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


	@GetMapping("/sub")
	public List<String> pullMessages() throws IOException {
		SubscriptionName subscriptionName = SubscriptionName.of("gke-greentech", "HEY");

		try (SubscriberStub subscriber = SubscriberStubSettings.newBuilder().build().createStub()) {
			PullRequest pullRequest =
					PullRequest.newBuilder()
							.setMaxMessages(10)
							.setSubscription(subscriptionName.toString())
							.build();

			PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
			List<String> messages =
					pullResponse.getReceivedMessagesList().stream()
							.map(msg -> msg.getMessage().getData().toStringUtf8())
							.collect(Collectors.toList());

			// Acknowledge messages
			List<String> ackIds =
					pullResponse.getReceivedMessagesList().stream()
							.map(ReceivedMessage::getAckId)
							.collect(Collectors.toList());

			if (!ackIds.isEmpty()) {
				AcknowledgeRequest ackRequest =
						AcknowledgeRequest.newBuilder()
								.setSubscription(subscriptionName.toString())
								.addAllAckIds(ackIds)
								.build();
				subscriber.acknowledgeCallable().call(ackRequest);
			}

			return messages;
		} catch (ApiException e) {
			throw new RuntimeException("Error pulling messages: " + e.getMessage());
		}
	}


}

