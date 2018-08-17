package com.kakaopay.todolist.tree;

import com.kakaopay.todolist.entity.ToDoListEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;

@Service
@Slf4j
public class TreeMaker {

    public Node createTreeFromEntity(List<ToDoListEntity> entities) {


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
        //XXX : logging
        printTree(rootNode, "   ");
        return rootNode;
    }

    private <T> void printTree(Node node, String appender) {
        log.info(appender + node.getId() + " " + node.getTodoReference() + " " + node.isCompleted());
        node.getChildren().forEach(each -> printTree(each, appender + appender));
    }
}
