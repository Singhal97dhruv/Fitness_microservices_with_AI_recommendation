package com.fitness.activityservice.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableMongoAuditing
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.baseUrl("http://USERSERVICE").build();
    }

}
