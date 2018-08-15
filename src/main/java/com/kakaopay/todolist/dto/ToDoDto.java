package com.kakaopay.todolist.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoDto {
	
	private String listId;

	private String rootId;

	private String parentId;

	private List<ToDoReference> todoReference;

	private String todo;

	private String createdAt;

	private String lastModifiedAt;

	private Boolean isCompleted;

}
