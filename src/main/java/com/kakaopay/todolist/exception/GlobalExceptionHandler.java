package com.kakaopay.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<?> handlexception(Throwable e) {
		log.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
	}

}
