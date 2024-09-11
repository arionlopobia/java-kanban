package ru.yandex.javacource.levin.schedule.java.src.manager;


import ru.yandex.javacource.levin.schedule.java.src.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    public static final int MAX_SIZE = 10;

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
        } else if (history.size() >= MAX_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }
}