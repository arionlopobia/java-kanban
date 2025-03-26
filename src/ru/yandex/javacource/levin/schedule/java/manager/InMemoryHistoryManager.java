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

    private void addNodeToFront(Node newNode) {
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    }

    @Override
    public void addHistory(Task task) {
        if (task == null || task.getId() == 0) {
            return;
        }

        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }

        // Создаем копию задачи перед добавлением в историю
        Task taskCopy = task.copy(); // Используйте ваш метод copy(), если он есть

        Node newNode = new Node(taskCopy);  // Добавляем копию задачи в новый узел
        addNodeToFront(newNode);

        nodeMap.put(task.getId(), newNode);

        history.add(taskCopy); // Добавляем копию в историю
    }



    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node current = head;
        System.out.println(head);
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        System.out.println("Current history: " + historyList);
        for (Task task : historyList) {
            System.out.println("Task: " + task);
        }
        return historyList;
    }


    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        nodeMap.remove(node.task.getId());
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
