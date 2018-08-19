package com.kakaopay.todolist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.todolist.entity.ToDoListEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ToDoListRepository extends JpaRepository<ToDoListEntity, String> {
    Page<ToDoListEntity> findAll(Pageable pageable);

    @Query(value = "select e from ToDoListEntity e where e.listId >= :listId")
    List<ToDoListEntity> findAllBiggerThan(@Param("listId") String listId);
}
