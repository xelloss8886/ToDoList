package com.kakaopay.todolist.tree;

import com.kakaopay.todolist.entity.ToDoListEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;

@Service
@Slf4j
public class TreeMaker {

    Node rootNode;
    Node parentNode;
    Node previousNode;
    Node currentNode;
    Node leftFirstNode;

    public Node createTreeFromEntity(List<ToDoListEntity> entities) {
        ListIterator<ToDoListEntity> it = entities.listIterator();
        while (it.hasNext()) {
            ToDoListEntity e = it.next();
            //init
            if (rootNode == null) {
                rootNode = new Node(e.getParentId(), e.getListId(), e.getTodoReference(), e.isCompleted());
                previousNode = rootNode;
                parentNode = rootNode;
                leftFirstNode = rootNode;
            }

            currentNode = new Node(e.getParentId(), e.getListId(), e.getTodoReference(), e.isCompleted());

            if (parentNode.getId().equals(currentNode.getParentId())) {
                if(parentNode.getId().equals(currentNode.getId())) continue;
                previousNode = currentNode;
                parentNode.addChild(currentNode);

                if(!leftFirstNode.getId().equals(parentNode)) {
                    leftFirstNode = currentNode;
                }
            } else if(leftFirstNode.getId().equals(currentNode.getParentId())){
                parentNode = leftFirstNode;
                parentNode.addChild(currentNode);
                leftFirstNode = currentNode;
            }
        }
        //XXX : logging
        printTree(rootNode, "   ");
        return rootNode;
    }

    private <T> void printTree(Node node, String appender) {
        log.info(appender + node.getId() + " " + node.getTodoReference() + " " + node.isCompleted());
        node.getChildren().forEach(each -> printTree(each, appender + appender));
    }
}
