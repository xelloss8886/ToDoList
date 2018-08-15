package com.kakaopay.todolist.configuration;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

public class H2Configuration {
	private static final String h2WebConsoleUrl = "/h2console/*";

	@Bean
	public ServletRegistrationBean h2ServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
		registration.addUrlMappings(h2WebConsoleUrl);
		return registration;
	}
}
