package com.zeki.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final int MAX_IN_MEMORY_SIZE = 4 * 1024 * 1024;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(this::configureCodecs);
    }

    private void configureCodecs(CodecConfigurer codecs) {
        codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }
}
