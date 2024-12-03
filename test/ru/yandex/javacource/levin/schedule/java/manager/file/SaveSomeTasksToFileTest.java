package ru.yandex.javacource.levin.schedule.java.manager.file;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;
import ru.yandex.javacource.levin.schedule.java.task.TypeOfTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class SaveSomeTasksToFileTest {

    @Test
    public void testSaveSomeTasksToFile() throws IOException {
        // Создаем временный файл для теста
        File tempFile = File.createTempFile("save_some_tasks_to_file_test", ".csv");
        tempFile.deleteOnExit();

        // Создаем экземпляр FileBackedTaskManager
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        // Создаем задачи
        Task task1 = new Task("Task1", "Description 1", StatusOfTask.NEW, TypeOfTask.TASK);
        Task task2 = new Task("Task2", "Description 2", StatusOfTask.NEW, TypeOfTask.TASK);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);

        fileBackedTaskManager.save();


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        ArrayList<Task> tasksFromFile = loadedManager.getTasks();

        assertFalse(tasksFromFile.isEmpty(), "Файл пуст");

        assertEquals(task1.toCSV(), tasksFromFile.get(0).toCSV(), "Первая задача записана некорректно");
        assertEquals(task2.toCSV(), tasksFromFile.get(1).toCSV(), "Вторая задача записана некорректно");
    }
}
