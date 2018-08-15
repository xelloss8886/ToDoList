package com.kakaopay.todolist.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(Throwable.class)
	public void handlexception(Throwable e) {
		log.error(e.getMessage(), e);
	}

}
