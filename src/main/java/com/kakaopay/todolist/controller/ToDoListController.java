package com.kakaopay.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.todolist.common.ResponseObject;
import com.kakaopay.todolist.dto.ToDoDto;
import com.kakaopay.todolist.service.ToDoListService;

@RestController
public class ToDoListController {

	@Autowired
	private ToDoListService toDoListService;

	/**
	 * TODOLIST 조회
	 * 
	 * @param todoListId
	 * @return
	 */
	@GetMapping("/todolists/{id}")
	public ResponseObject getById(@PathVariable("id") String todoListId) {
		return toDoListService.selectToDoById(todoListId);
	}

	/**
	 * TODOLIST 등록
	 * 
	 * @param toDoDto
	 * @return
	 */
	@PostMapping(value = "/todolists", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseObject createTodoList(@RequestBody ToDoDto toDoDto) {
		return toDoListService.insertToDo(toDoDto);
	}

	/**
	 * TODOLIST 수정
	 * 
	 * @param toDoDto
	 * @return
	 */
	@PutMapping(value = "/todolists", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseObject modifyTodoListById(@RequestBody ToDoDto toDoDto) {
		return toDoListService.modifyTodoListById(toDoDto);
	}

	@PatchMapping(value = "/todolists/{id}/complete")
	public ResponseObject modifyCompleteStatus(@PathVariable("id") String listId, @RequestBody ToDoDto toDoDto) {
		return toDoListService.modifyCompleteStatus(listId, toDoDto);
	}
}
