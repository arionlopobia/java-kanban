package ru.yandex.javacource.levin.schedule.java;

import ru.yandex.javacource.levin.schedule.java.manager.*;
import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Создание задач и эпиков
        Task task1 = new Task("Первая таска", "я сделал таску", StatusOfTask.NEW);
        taskManager.createTask(task1);

        Epic epic1 = new Epic("ЭПИК", "ЭТО ПЕРВЫЙ ЭПИК", StatusOfTask.NEW);
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Первая сабтаска", "Это первая сабтаска", StatusOfTask.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subTask1);

        SubTask subTask2 = new SubTask("Вторая сабтаска", "Это вторая сабтаска", StatusOfTask.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subTask2);

        Epic epic2 = new Epic("epic - 2", "второй epic", StatusOfTask.NEW);
        taskManager.createEpic(epic2);

        SubTask subTask3 = new SubTask("subtask", "it is new subtask", StatusOfTask.NEW, epic2.getId());
        taskManager.createSubtask(subTask3);

        Task task2 = new Task("task - 2", "вторая таска", StatusOfTask.NEW);
        taskManager.createTask(task2);



        taskManager.getEpic(epic1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subTask1.getId());



        System.out.println(historyManager.getHistory());
    }
}
