package ru.yandex.javacource.levin.schedule.java.manager;


import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void addHistory(Task task) {
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
    }
}