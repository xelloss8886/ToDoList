package com.kakaopay.todolist.tree;

import java.util.List;

public class NotCompleteState implements NodeState{

    private List<Node> notCompletedTodos;

    public NotCompleteState(List<Node> todos) {
        this.notCompletedTodos = todos;
    }

    @Override
    public boolean existNotCompleteToDos() {
        return notCompletedTodos.size() > 0 ? true : false;
    }
}
