package ru.yandex.javacource.levin.schedule.java.manager;


import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    public class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }

        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node newNode = new Node(task.copy());
        linkLast(newNode);
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }
}