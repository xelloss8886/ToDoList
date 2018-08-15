package com.kakaopay.todolist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakaopay.todolist.common.ResponseObject;
import com.kakaopay.todolist.dto.ToDoDto;
import com.kakaopay.todolist.dto.ToDoReference;
import com.kakaopay.todolist.entity.ToDoListEntity;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder.ChildNodes;
import com.kakaopay.todolist.map.Node;
import com.kakaopay.todolist.repository.ToDoListRepository;
import com.kakaopay.todolist.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ToDoListService {

	@Autowired
	private ToDoListRepository toDoListRepository;

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
				.rootId(dto.getRootId()).todoReference(convertListToConcatedString(dto.getTodoReference()))
				.todo(dto.getTodo()).isCompleted(false).createdAt(now).lastModifiedAt(now).build();
		toDoListRepository.save(entity);
		return entity;
	}

	public ToDoListEntity modifyTodoListById(ToDoDto dto) {
		String now = DateUtils.getNowAsString();
		String todoReference = convertListToConcatedString(dto.getTodoReference());
		ToDoListEntity entity = ToDoListEntity.builder().listId(dto.getListId()).parentId(dto.getParentId())
				.todo(dto.getTodo()).isCompleted(dto.getIsCompleted()).createdAt(now).lastModifiedAt(now)
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
		Optional<ToDoListEntity> op = toDoListRepository.findById(listId);
		ToDoListEntity entity = null;
		if (op.isPresent()) {
			entity = op.get();
		}
		String rootId = entity.getRootId();
		List<ToDoListEntity> entities = toDoListRepository.findByRootIdOrderByListId(rootId);

		ListIterator<ToDoListEntity> it = entities.listIterator();
		Node rootNode = null;
		Node parentNode = null;
		Node previousNode = null;
		Node currentNode = null;
		Node leftFirstNode = null;
		while (it.hasNext()) {
			ToDoListEntity e = it.next();
			if (rootNode == null) {
				rootNode = new Node(e.getParentId(), e.getRootId(), e.getListId(), e.getTodoReference(), e.isCompleted());
				previousNode = rootNode;
				parentNode = rootNode;
				leftFirstNode = rootNode;
			} 
			
			currentNode = new Node(e.getParentId(), e.getRootId(), e.getListId(), e.getTodoReference(), e.isCompleted());
			
			log.info("parentNode>> id = " + parentNode.getId() + " , parentId = " + parentNode.getParentId() + " , rootId = " + parentNode.getRootId());
			log.info("previousNode>> id = " + previousNode.getId() + " , parentId = " + previousNode.getParentId() + " , rootId = " + previousNode.getRootId());
			log.info("currentNode>> id = " + e.getListId() + " , parentId = " + e.getParentId() + " , rootId = " + e.getRootId());
			
			if (parentNode.getId().equals(currentNode.getParentId())) {
				if(parentNode.getId().equals(currentNode.getId())) continue;
				previousNode = currentNode;
				parentNode.addChild(currentNode);
				
				if(!leftFirstNode.getId().equals(parentNode)) {
					leftFirstNode = currentNode;
				}
			} else if(leftFirstNode.getId().equals(currentNode.getParentId())){
				log.info("!!");
				parentNode = leftFirstNode;
				parentNode.addChild(currentNode);
				leftFirstNode = currentNode;
			}
		}
		
		printTree(rootNode, "   ");
		
		List<Node> notCompletedTodos = new ArrayList<>();
		recursiveToFind(rootNode, listId, notCompletedTodos);

		// TODO : this method will be class.
		if (notCompletedTodos.size() > 0) {
			ListIterator<Node> listIt = notCompletedTodos.listIterator();
			ExceptionMessageBuilder exceptionMessages = ExceptionMessageBuilder.builder().listId(listId).build();
			while (listIt.hasNext()) {
				Node each = listIt.next();
				exceptionMessages.addChildNodes(
						ChildNodes.builder().id(each.getId()).isCompleted(each.isCompleted()).build());
			}
			return exceptionMessages;
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

	private static <T> void printTree(Node node, String appender) {
		log.info(appender + node.getId() + " " + node.getTodoReference() + " " + node.isCompleted());
		node.getChildren().forEach(each -> printTree(each, appender + appender));
	}
	
	private void recursiveToFind (Node node, String listId, List<Node> list) {
		List<Node> children = node.getChildren();
		ListIterator<Node> listIt = children.listIterator();
		while(listIt.hasNext()) {
			Node n = listIt.next();
			if(!n.getChildren().isEmpty()) {
				recursiveToFind(n, listId, list);
			} 
			if(n.getTodoReference().indexOf(listId) > -1 && !n.isCompleted()) list.add(n);
		}
	}
}
