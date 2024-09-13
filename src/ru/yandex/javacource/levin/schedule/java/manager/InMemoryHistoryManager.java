package ru.yandex.javacource.levin.schedule.java.manager;


import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= MAX_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }
}