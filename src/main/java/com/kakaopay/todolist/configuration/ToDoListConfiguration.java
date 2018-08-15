package com.kakaopay.todolist.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableJpaRepositories(basePackages = { "com.kakaopay.todolist.repository" })
public class ToDoListConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
