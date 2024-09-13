package ru.yandex.javacource.levin.schedule.java.manager;

public class Managers {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager();


    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }
}
