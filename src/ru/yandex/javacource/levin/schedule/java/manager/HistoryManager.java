package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistory(Task task);
}

