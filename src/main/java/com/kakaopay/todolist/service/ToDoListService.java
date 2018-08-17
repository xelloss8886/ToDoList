package com.kakaopay.todolist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import com.kakaopay.todolist.exception.NotFoundTodosException;
import com.kakaopay.todolist.tree.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakaopay.todolist.common.ResponseObject;
import com.kakaopay.todolist.dto.ToDoDto;
import com.kakaopay.todolist.dto.ToDoReference;
import com.kakaopay.todolist.entity.ToDoListEntity;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder.ChildNodes;
import com.kakaopay.todolist.repository.ToDoListRepository;
import com.kakaopay.todolist.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ToDoListService {

    @Autowired
    private ToDoListRepository toDoListRepository;

    @Autowired
    private TreeMaker treeMaker;

    @Autowired
    private NodeFinder nodeFinder;

    public ToDoListEntity selectToDoById(String listId) {
        return Optional.of(toDoListRepository.findById(listId)).orElseThrow(() -> new NotFoundTodosException("")).get();
    }

    public ToDoListEntity insertToDo(ToDoDto dto) {
        String now = DateUtils.getNowAsString();
        ToDoListEntity entity = ToDoListEntity.builder()
                .listId(dto.getListId())
                .parentId(dto.getParentId())
                .rootId(dto.getRootId())
                .todoReference(convertListToConcatedString(dto.getTodoReference()))
                .todo(dto.getTodo())
                .isCompleted(false)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
        toDoListRepository.save(entity);
        return entity;
    }

    public ToDoListEntity modifyTodoListById(ToDoDto dto) {
        String now = DateUtils.getNowAsString();
        String todoReference = convertListToConcatedString(dto.getTodoReference());
        ToDoListEntity entity = ToDoListEntity.builder()
                .listId(dto.getListId())
                .parentId(dto.getParentId())
                .todo(dto.getTodo())
                .isCompleted(dto.getIsCompleted())
                .createdAt(now)
                .lastModifiedAt(now)
                .todoReference(todoReference).build();
        toDoListRepository.save(entity);
        return entity;
    }

    /**
     * 상태변경
     *
     * @param listId
     * @param dto
     * @return
     */
    public ResponseObject modifyCompleteStatus(String listId, ToDoDto dto) {
        // check
        Optional<ToDoListEntity> op =
                Optional.of(toDoListRepository.findById(listId))
                        .orElseThrow(() -> new NotFoundTodosException("ToDos in database is not founded. "));
        ToDoListEntity entity = op.get();
        String rootId = entity.getRootId();
        List<ToDoListEntity> entities = toDoListRepository.findByRootIdOrderByListId(rootId);
        Node rootNode = treeMaker.createTreeFromEntity(entities);
        //recursive
        List<Node> notCompletedTodos = new ArrayList<>();
        nodeFinder.recursiveToFind(rootNode, listId, notCompletedTodos);

        NodeState state = new NotCompleteState(notCompletedTodos);
        if (state.existNotCompleteToDos()) {
            return createExceptionMessage(listId, notCompletedTodos);
        } else {
            entity.setCompleted(dto.getIsCompleted());
            toDoListRepository.save(entity);
        }
        return entity;

    }

    private String convertListToConcatedString(List<ToDoReference> todoReference) {
        ListIterator<ToDoReference> listIt = todoReference.listIterator();
        StringBuilder sb = new StringBuilder();
        while (listIt.hasNext()) {
            ToDoReference node = listIt.next();
            sb.append(node.getId());
            if (listIt.hasNext())
                sb.append("|");
        }
        return sb.toString();
    }

    private ExceptionMessageBuilder createExceptionMessage(String listId, List<Node> notCompletedTodos) {
        ListIterator<Node> listIt = notCompletedTodos.listIterator();
        ExceptionMessageBuilder exceptionMessages =
                ExceptionMessageBuilder.builder()
                        .listId(listId)
                        .build();
        while (listIt.hasNext()) {
            Node each = listIt.next();
            exceptionMessages.addChildNodes(
                    ChildNodes.builder()
                            .id(each.getId())
                            .isCompleted(each.isCompleted())
                            .build());
        }
        return exceptionMessages;
    }


}
