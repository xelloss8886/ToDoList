package com.kakaopay.todolist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kakaopay.todolist.common.ResponseObject;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
@ToString
@Table(name = "todolist")
public class ToDoListEntity implements ResponseObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "listId")
	private String listId;

	@Column(name = "rootId", updatable = false)
	private String rootId;

	@Column(name = "parentId")
	private String parentId;

	@Column(name = "todoReference")
	private String todoReference;

	@Column(name = "todo")
	private String todo;

	@Column(name = "createdAt", updatable = false)
	private String createdAt;

	@Column(name = "lastModifiedAt")
	private String lastModifiedAt;

	@Column(name = "isCompleted")
	private boolean isCompleted;
}
