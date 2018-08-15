package com.kakaopay.todolist.service;

import java.util.List;
import java.util.ListIterator;

import com.kakaopay.todolist.dto.ToDoDto;

public class PreProcessor {

	private ToDoDto toDoDto;

	public String convertListToConcatedString(ToDoDto dto) {
		List<String> list = dto.getTodoReference();
		ListIterator<String> listIt = list.listIterator();
		StringBuilder sb = new StringBuilder();
		while (listIt.hasNext()) {
			String node = listIt.next();
			sb.append(node);
			if (listIt.hasNext())
				sb.append("|");
		}
		return sb.toString();
	}

}
