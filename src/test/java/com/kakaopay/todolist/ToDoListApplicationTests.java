package com.kakaopay.todolist;

import com.kakaopay.todolist.common.ResponseObject;
import com.kakaopay.todolist.dto.ToDoDto;
import com.kakaopay.todolist.dto.ToDoReference;
import com.kakaopay.todolist.entity.ToDoListEntity;
import com.kakaopay.todolist.exception.ExceptionMessageBuilder;
import com.kakaopay.todolist.repository.ToDoListRepository;
import com.kakaopay.todolist.service.ToDoListService;
import com.kakaopay.todolist.tree.NodeFinder;
import com.kakaopay.todolist.tree.TreeMaker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ToDoListApplicationTests {

	@Autowired
	private ToDoListService toDoListService;

    @TestConfiguration
    static class ToDoServiceTestContextConfiguration {

        @Bean
        public ToDoListService toDoListService() {
            return new ToDoListService();
        }

        @Bean
        public TreeMaker treeMaker() {
            return new TreeMaker();
        }

        @Bean
        public NodeFinder nodeFinder() {
            return new NodeFinder();
        }
    }

	@Autowired
    private ToDoListRepository toDoListRepository;

	private ToDoListEntity createTodo (String todo, List<ToDoReference> todoReference) {
		return toDoListService.insertToDo(ToDoDto.builder()
                .todo(todo)
                .todoReference(todoReference)
                .build());
	}

	private ToDoDto createDto(String listId, List<ToDoReference> toDoReferences) {
	    return ToDoDto.builder().listId(listId).todoReference(toDoReferences).build();
    }

    private ToDoDto createCompletedDtoById(String listId) {
	    ToDoListEntity entity = Optional.of(toDoListRepository.findById(listId)).get().get();
	    return ToDoDto.builder().listId(entity.getListId()).parentId(entity.getParentId())
                .todo(entity.getTodo()).createdAt(entity.getCreatedAt()).lastModifiedAt(entity.getLastModifiedAt())
                .isCompleted(true).build();
    }

	private ToDoListEntity findToDo (String listId) {
	    ToDoListEntity entity = null;
	    Optional<ToDoListEntity> op = toDoListRepository.findById(listId);
	    if(op.isPresent()) {
	        entity = op.get();
        }
        return entity;
    }

    @Test
    public void testPagination () {
        String todo = "Test";

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 50 ; i++) {
            ToDoListEntity toDoListEntity = createTodo(todo+i, null);

            Assert.assertNotNull(toDoListEntity.getListId());
            Assert.assertEquals(todo+i, toDoListEntity.getTodo());

            ToDoListEntity foundEntity = findToDo(toDoListEntity.getListId());

            Assert.assertNotNull(foundEntity);
            Assert.assertEquals(toDoListEntity.getListId(), foundEntity.getListId());
            Assert.assertEquals(toDoListEntity.getTodo(), foundEntity.getTodo());
            ids.add(toDoListEntity.getListId());
        }

        for (int i = 0; i < 5; i++) {
            Pageable pageable = PageRequest.of(i, 10, new Sort(Sort.Direction.DESC, "lastModifiedAt"));
            Page<ToDoListEntity> result = toDoListService.getListsByPaging(pageable);
            Assert.assertEquals(10, result.getSize());
            Assert.assertEquals(10, result.getContent().size());
        }
    }

	@Test
	public void testCreateTodo () {
		String todo = "Test";

        ToDoListEntity toDoListEntity = createTodo(todo, null);

        Assert.assertNotNull(toDoListEntity.getListId());
        Assert.assertNotNull(toDoListEntity.getParentId());
        Assert.assertNotNull(toDoListEntity.getCreatedAt());
        Assert.assertNotNull(toDoListEntity.getLastModifiedAt());
        Assert.assertNotNull(toDoListEntity.getTodo());

        ToDoListEntity foundEntity = findToDo(toDoListEntity.getListId());

        Assert.assertNotNull(foundEntity);
        Assert.assertEquals(toDoListEntity.getListId(), foundEntity.getListId());
        Assert.assertEquals(toDoListEntity.getTodo(), foundEntity.getTodo());
	}

	@Test
	public void testUpdateTodo () {
		String todo = "update test";

        ToDoListEntity toDoListEntity = createTodo(todo, null);

		Assert.assertNotNull(toDoListEntity.getListId());
		Assert.assertEquals(todo, toDoListEntity.getTodo());

        ToDoListEntity foundEntity = findToDo(toDoListEntity.getListId());

		Assert.assertNotNull(foundEntity);
        Assert.assertEquals(toDoListEntity.getListId(), foundEntity.getListId());
        Assert.assertEquals(toDoListEntity.getTodo(), foundEntity.getTodo());

		ToDoDto updateToDoDto = new ToDoDto();
        updateToDoDto.setTodo("Update Test");
        updateToDoDto.setListId(toDoListEntity.getListId());

        ToDoListEntity response = toDoListService.modifyTodoListById(updateToDoDto);

        Assert.assertEquals(toDoListEntity.getTodo(), response.getTodo());
        Assert.assertEquals(toDoListEntity.getLastModifiedAt(), toDoListEntity.getLastModifiedAt());
	}

	@Test
	public void testCompleteTest () {
		String todo = "Todo";

		for(int i = 0 ; i < 3 ; i ++) {
            createTodo(todo, null);
        }

        List<ToDoListEntity> list = toDoListRepository.findAll();

		List<ToDoReference> references = new ArrayList<>();

        ListIterator<ToDoListEntity> listIt = list.listIterator();
        while(listIt.hasNext()) {
            ToDoListEntity e = listIt.next();
            String listId = Optional.of(e.getListId()).get();
            if(listIt.hasNext()) references.add(new ToDoReference(listId));
        }

        String lastId = list.stream().sorted(Comparator.comparing(ToDoListEntity::getListId).reversed()).findFirst().get().getListId();

        toDoListService.modifyTodoListById(createDto(lastId, references));

        String firstId = list.stream().sorted(Comparator.comparing(ToDoListEntity::getListId)).findFirst().get().getListId();

		ToDoDto firstDto = createCompletedDtoById(firstId);
		String listId = firstDto.getListId();

        ResponseObject updatedEntity = toDoListService.modifyCompleteStatus(listId, firstDto);

        Assert.assertTrue(updatedEntity instanceof ExceptionMessageBuilder);

        ExceptionMessageBuilder exceptionMessageBuilder = (ExceptionMessageBuilder) updatedEntity;

		Assert.assertEquals(exceptionMessageBuilder.getListId(), listId);

		ToDoDto lastDto = createCompletedDtoById(lastId);

		ResponseObject lastEntity =  toDoListService.modifyCompleteStatus(lastId, lastDto);

		Assert.assertTrue(lastEntity instanceof ToDoListEntity);

        ToDoListEntity lastTodoEntity = (ToDoListEntity) lastEntity;

        Assert.assertNotNull(lastTodoEntity.getLastModifiedAt());

        Assert.assertTrue(lastTodoEntity.isCompleted() == true);
	}
}