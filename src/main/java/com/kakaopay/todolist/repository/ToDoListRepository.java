package com.kakaopay.todolist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.todolist.entity.ToDoListEntity;

public interface ToDoListRepository extends JpaRepository<ToDoListEntity, String> {
	List<ToDoListEntity> findByParentId(String parentid);
}
