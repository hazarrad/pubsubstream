package com.micro.pubstream.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class GCPConfig {

    Logger log = LoggerFactory.getLogger(GCPConfig.class);

    @Autowired
    @Qualifier("propertiesConfig")
    private PropertiesConfig propConfig;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {

        Path resourcesPath = Paths.get(propConfig.getResourcesFolder());
        log.info("Resources Path: {}", resourcesPath);
        String serviceAccountKeyFile = resourcesPath + propConfig.getKey();
        log.info("----------> service account key file : {}", serviceAccountKeyFile);
        InputStream serviceAccountStream = new ClassPathResource(propConfig.getKey()).getInputStream();
        log.info("Service account key loaded");
        return GoogleCredentials.fromStream(serviceAccountStream);

    }

    @Bean(destroyMethod = "shutdown")
    public Publisher publisher() throws IOException {

        TopicName topicName = TopicName.of(propConfig.getProjectId(),propConfig.getTopicId());
        return Publisher.newBuilder(topicName)
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials()))
                .build();
    }

}
