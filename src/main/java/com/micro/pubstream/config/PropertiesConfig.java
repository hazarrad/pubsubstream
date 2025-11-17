package com.micro.pubstream.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@NoArgsConstructor
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "gcp")
public class PropertiesConfig {

    private String resourcesFolder;
    private String bucketName;
    private String key;
    private String projectId;
    private String topicId;

}