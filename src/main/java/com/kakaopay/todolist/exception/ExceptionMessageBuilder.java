package com.kakaopay.todolist.exception;

import java.util.ArrayList;
import java.util.List;

import com.kakaopay.todolist.common.ResponseObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionMessageBuilder implements ResponseObject {

	// HttpStatus : 400
	@Getter
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private class ChildNodes {
		private String id;

		private boolean isCompleted;
	}

	private String listId;

	private List<ChildNodes> children;

	public void addChildNodes(ChildNodes nodes) {
		if (children == null)
			children = new ArrayList<>();
		children.add(nodes);
	}
}
