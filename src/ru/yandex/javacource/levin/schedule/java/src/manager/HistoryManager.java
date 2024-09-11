package ru.yandex.javacource.levin.schedule.java.src.manager;

import ru.yandex.javacource.levin.schedule.java.src.task.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addHistory(Task task);
}

