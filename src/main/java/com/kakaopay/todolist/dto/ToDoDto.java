package com.kakaopay.todolist.dto;

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

	private List<String> todoReference;

	private String todo;

	private String createdAt;

	private String lastModifiedAt;

	private boolean isCompleted;

}
