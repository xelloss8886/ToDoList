package com.kakaopay.todolist.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.todolist.dto.ToDoDto;
import com.kakaopay.todolist.entity.ToDoListEntity;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder;
import com.kakaopay.todolist.repository.ToDoListRepository;
import com.kakaopay.todolist.util.DateUtils;

@Service
public class ToDoListService {

	@Autowired
	private ToDoListRepository toDoListRepository;

	@Autowired
	private ObjectMapper objectMapper;

	public ToDoListEntity selectToDoById(String listId) {
		Optional<ToDoListEntity> op = toDoListRepository.findById(listId);
		if (op.isPresent()) {
			return op.get();
		}
		return null;
	}

	public ToDoListEntity insertToDo(ToDoDto dto) {
		String now = DateUtils.getNowAsString();
		ToDoListEntity entity = ToDoListEntity.builder().listId(dto.getListId()).parentId(dto.getParentId())
				.todo(dto.getTodo()).isCompleted(false).createdAt(now).lastModifiedAt(now).build();
		toDoListRepository.save(entity);
		return entity;
	}

	public ToDoListEntity modifyTodoListById(ToDoDto dto) {
		String now = DateUtils.getNowAsString();
		String todoReference = convertListToConcatedString(dto.getTodoReference());
		ToDoListEntity entity = ToDoListEntity.builder().listId(dto.getListId()).parentId(dto.getParentId())
				.todo(dto.getTodo()).isCompleted(dto.isCompleted()).createdAt(now).lastModifiedAt(now)
				.todoReference(todoReference).build();
		toDoListRepository.save(entity);
		return entity;
	}

	public ToDoListEntity modifyCompleteStatus(String listId, ToDoDto dto) {
		//check
		List<ToDoListEntity> entities = toDoListRepository.findByParentId(listId);
		List<ToDoListEntity> notCompletedTodos = 
				entities.stream().filter(e -> !e.isCompleted()).collect(Collectors.toList());
		//TODO : create list for error message
		if(notCompletedTodos.size() > 0) {
			ListIterator<ToDoListEntity> listIt = notCompletedTodos.listIterator();
			ExceptionMessageBuilder.builder().listId(listId).build();
			//TODO : create message
			while(listIt.hasNext()) {
				
			}
			return 
		}
	}

	private String convertListToConcatedString(List<String> todoReference) {
		ListIterator<String> listIt = todoReference.listIterator();
		StringBuilder sb = new StringBuilder();
		while (listIt.hasNext()) {
			String node = listIt.next();
			sb.append(node);
			if (listIt.hasNext())
				sb.append("|");
		}
		return sb.toString();
	}
}
