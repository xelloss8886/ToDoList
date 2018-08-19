package com.kakaopay.todolist.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ToDoDto {

	private String listId;

	private String rootId;

	private String parentId;

	private List<ToDoReference> todoReference;

	private String todo;

	private String createdAt;

	private String lastModifiedAt;

	private Boolean isCompleted;

	private Long count;

}
