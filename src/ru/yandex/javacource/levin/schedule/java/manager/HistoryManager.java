package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.List;

public interface HistoryManager {
    void remove(int id);

    void addHistory(Task task);

    List<Task> getHistory();
}

