package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    Integer createSubtask(SubTask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubtasks();

    void removeAllTask();

    void dealeateTasks();

    void dealeateEpics();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubtask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subtask);

    void deleteTaskById(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    ArrayList<SubTask> getSubTasksForEpic(int epicId);




}
