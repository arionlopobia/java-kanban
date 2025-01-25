package ru.yandex.javacource.levin.schedule.java.manager.file;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LoadSomeTaskFromFileTest {

    @Test
    public void testLoadSomeTasksFromFile() throws IOException {
        File tempFile = File.createTempFile("load_some_tasks_from_file_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task1", "Description 1", StatusOfTask.NEW);
        Task task2 = new Task("Task2", "Description 2", StatusOfTask.NEW);
        Task task3 = new Task("Task3", "Description 3", StatusOfTask.NEW);

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createTask(task3);
        fileBackedTaskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        ArrayList<Task> tasksFromFile = loadedManager.getTasks();

        assertFalse(tasksFromFile.isEmpty(), "Файл пустой");

        assertEquals(3, tasksFromFile.size(), "Колличество задач неверное");

        assertEquals(FileBackedTaskManager.toString(task1), FileBackedTaskManager.toString(tasksFromFile.get(0)), "Первая задача записана некорректно");
        assertEquals(FileBackedTaskManager.toString(task2), FileBackedTaskManager.toString(tasksFromFile.get(1)), "Вторая задача записана некорректно");
        assertEquals(FileBackedTaskManager.toString(task3), FileBackedTaskManager.toString(tasksFromFile.get(2)), "Третья задача записана некорректно");
    }


}
