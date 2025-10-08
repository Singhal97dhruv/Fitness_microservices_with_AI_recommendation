package com.fitness.aiservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class AppConfig {

    public ModelMapper modelMapper(){return new ModelMapper();}

}
