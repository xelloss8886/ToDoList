package com.kakaopay.todolist.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


@Slf4j
@RestController
public class ToDoListController {

	@Autowired
	private ToDoListService toDoListService;

	/**
	 * TODOLIST 조회
	 *
	 * @return
	 */
	@GetMapping("/todolists")
	public Page<?> getById(Pageable pageable) {
	    log.info(toDoListService.getListsByPaging(pageable).toString());
		return toDoListService.getListsByPaging(pageable);
	}

    /**
     * 초기 페이징을 위한 count정보
     * @return
     */
	@GetMapping("/todolists/count")
    public ToDoDto getCount() {
	    return toDoListService.getAllLists();
    }

	/**
	 * TODOLIST 등록
	 *
	 * @param toDoDto
	 * @return
	 */
	@PostMapping(value = "/todolists", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseObject createTodoList(@RequestBody ToDoDto toDoDto) {
		log.info(toDoDto.toString());
		return toDoListService.insertToDo(toDoDto);
	}

	/**
	 * TODOLIST 수정
	 *
	 * @param toDoDto
	 * @return
	 */
	@PutMapping(value = "/todolists/{listId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseObject modifyTodoListById(@PathVariable("listId") String listId, @RequestBody ToDoDto toDoDto) {
	    toDoDto.setListId(listId);
		return toDoListService.modifyTodoListById(toDoDto);
	}

    /**
     * TODOLIST 완료처리
     * @param listId
     * @param toDoDto
     * @return
     */
	@PatchMapping(value = "/todolists/{listId}/complete")
	public ResponseEntity<?> modifyCompleteStatus(@PathVariable("listId") String listId, @RequestBody ToDoDto toDoDto) {
		return ResponseEntity.ok(toDoListService.modifyCompleteStatus(listId, toDoDto));
	}
}
