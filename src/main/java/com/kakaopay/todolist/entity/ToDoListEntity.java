package com.kakaopay.todolist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kakaopay.todolist.common.ResponseObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "todolist")
public class ToDoListEntity implements ResponseObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "listId")
	private String listId;

	@Column(name = "rootId")
	private String rootId;

	@Column(name = "parentId")
	private String parentId;

	@Column(name = "todoReference")
	private String todoReference;

	@Column(name = "todo")
	private String todo;

	@Column(name = "createdAt")
	private String createdAt;

	@Column(name = "lastModifiedAt")
	private String lastModifiedAt;

	@Column(name = "isCompleted")
	private boolean isCompleted;
}
