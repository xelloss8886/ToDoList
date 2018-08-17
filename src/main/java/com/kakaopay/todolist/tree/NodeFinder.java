package com.kakaopay.todolist.tree;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;

@Service
public class NodeFinder {

    public void recursiveToFind (Node node, String listId, List<Node> list) {
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
