package com.kakaopay.todolist.tree;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Node {

	private List<Node> children = new ArrayList<>();

	private Node parent = null;

	private String parentId;

	private String rootId;

	private String id;
	
	private String todoReference;
	
	private boolean isCompleted;
	
	public Node(String parentId, String rootId, String id, String todoReference, boolean isCompleted) {
		this.parentId = parentId;
		this.rootId = rootId;
		this.id = id;
		this.todoReference = todoReference;
		this.isCompleted = isCompleted;
	}
	
	public Node addChild(Node child) {
		this.children.add(child);
		return child;
	}

	public void addChildren(List<Node> children) {
		children.forEach(each -> each.setParent(this));
		this.children.addAll(children);
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
}
