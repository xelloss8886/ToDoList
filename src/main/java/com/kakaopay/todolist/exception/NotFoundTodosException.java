package com.kakaopay.todolist.exception;

public class NotFoundTodosException extends RuntimeException {

    public NotFoundTodosException(String message) {
        super(message);
    }
}
