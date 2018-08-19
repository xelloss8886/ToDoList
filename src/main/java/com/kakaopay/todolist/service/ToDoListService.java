package com.kakaopay.todolist.service;

import java.util.*;

import com.kakaopay.todolist.exception.NotFoundTodosException;
import com.kakaopay.todolist.tree.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<ToDoListEntity> getListsByPaging(Pageable pageable) {
        return Optional.of(toDoListRepository.findAll(pageable)).orElseThrow(() -> new NotFoundTodosException(""));
    }

    public ToDoDto getAllLists() {
        return ToDoDto.builder().count(toDoListRepository.count()).build();
    }

    public ToDoListEntity insertToDo(ToDoDto dto) {
        String now = DateUtils.getNowAsString();
        String concatedString = convertListToConcatedString(dto.getTodoReference());
        int lastIndex = concatedString == null ? -1 : concatedString.lastIndexOf("|");
        String parentId = Optional.ofNullable(dto.getParentId())
                .orElse(lastIndex > 0 ? concatedString.substring(lastIndex+1) : concatedString);

        if(dto.getTodoReference() == null) {
            List<ToDoReference> defaultList = new ArrayList<>();
            ToDoReference tdr = new ToDoReference();
            tdr.setId(dto.getListId());
            defaultList.add(tdr);
            dto.setTodoReference(defaultList);
        }

        ToDoListEntity entity = ToDoListEntity.builder()
                .listId(dto.getListId())
                .parentId(parentId)
                .todoReference(convertListToConcatedString(dto.getTodoReference()))
                .todo(dto.getTodo())
                .isCompleted(false)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        ToDoListEntity insertedEntity = toDoListRepository.save(entity);
        Optional<String> op = Optional.ofNullable(insertedEntity.getParentId());
        if(!op.isPresent()) {
            String listId = op.orElse(insertedEntity.getListId());
            insertedEntity.setParentId(listId);
            toDoListRepository.save(insertedEntity);
        }
        return entity;
    }

    public ToDoListEntity modifyTodoListById(ToDoDto dto) {
        String now = DateUtils.getNowAsString();
        String todoReference = convertListToConcatedString(dto.getTodoReference());
        String parentId = dto.getListId();
        if(todoReference != null) {
            parentId = Arrays.stream(todoReference.split("|")).sorted().findFirst().get();
        }

        ToDoListEntity entity = ToDoListEntity.builder()
                .listId(dto.getListId())
                .parentId(parentId)
                .todo(dto.getTodo())
                .lastModifiedAt(now)
                .todoReference(todoReference).build();

        return toDoListRepository.saveAndFlush(entity);
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
        ToDoListEntity entity =
                Optional.of(toDoListRepository.findById(listId))
                        .orElseThrow(() -> new NotFoundTodosException("ToDos in database is not founded. "))
                        .get();

        List<ToDoListEntity> entities = toDoListRepository.findAllBiggerThan(listId);

        Node startNode = treeMaker.createTreeFromEntity(entities);
        //recursive
        List<Node> notCompletedTodos = new ArrayList<>();
        nodeFinder.recursiveToFind(startNode, listId, notCompletedTodos);

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
        if(todoReference == null || todoReference.size() < 1) return null;
        ListIterator<ToDoReference> listIt = todoReference.listIterator();
        StringBuilder sb = new StringBuilder();
        while (listIt.hasNext()) {
            ToDoReference node = listIt.next();
            sb.append(node.getId().replaceAll("[^0-9]", ""));
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
