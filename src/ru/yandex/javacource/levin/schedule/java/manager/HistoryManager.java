package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();
    void addHistory(Task task);
}

