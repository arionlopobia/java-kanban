package ru.yandex.javacource.levin.schedule.java;

import ru.yandex.javacource.levin.schedule.java.Manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.Task.Epic;
import ru.yandex.javacource.levin.schedule.java.Task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.Task.SubTask;
import ru.yandex.javacource.levin.schedule.java.Task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // Создание задач и эпиков
        Task task1 = new Task("Первая таска", "я сделал таску", StatusOfTask.NEW);
        taskManager.createTask(task1);

        Epic epic1 = new Epic("ЭПИК", "ЭТО ПЕРВЫЙ ЭПИК", StatusOfTask.NEW);
        taskManager.createEpic(epic1);

        SubTask subTask1 = new SubTask("Первая сабтаска", "Это первая сабтаска", StatusOfTask.IN_PROGRESS, epic1);
        taskManager.createSubtask(subTask1);

        SubTask subTask2 = new SubTask("Вторая сабтаска", "Это вторая сабтаска", StatusOfTask.IN_PROGRESS, epic1);
        taskManager.createSubtask(subTask2);

        Epic epic2 = new Epic("epic - 2", "второй epic", StatusOfTask.NEW);
        taskManager.createEpic(epic2);

        SubTask subTask3 = new SubTask("subtask", "it is new subtask", StatusOfTask.NEW, epic2);
        taskManager.createSubtask(subTask3);

        Task task2 = new Task("task - 2", "вторая таска", StatusOfTask.NEW);
        taskManager.createTask(task2);



        taskManager.removeSubTaskById(subTask1.getId());
        System.out.println(taskManager.getEpic(epic1.getId()));


        taskManager.removeSubTaskById(subTask2.getId());
        System.out.println( taskManager.getEpic(epic1.getId()));



        System.out.println("Все задачи: " + taskManager.getTasks());
        System.out.println("Все эпики: " + taskManager.getEpics());
        System.out.println("Все подзадачи: " + taskManager.getSubtasks());


        taskManager.removeAllTask();
        System.out.println("После удаления всех задач:");
        System.out.println("Все задачи: " + taskManager.getTasks());
        System.out.println("Все эпики: " + taskManager.getEpics());
        System.out.println("Все подзадачи: " + taskManager.getSubtasks());
    }
}
