package ru.yandex.javacource.levin.schedule.java.manager.file;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;
import ru.yandex.javacource.levin.schedule.java.task.TaskType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveSomeTasksToFileTest {

    @Test
    public void testSaveSomeTasksToFile() throws IOException {
        File tempFile = File.createTempFile("save_some_tasks_to_file_test", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task1 = new Task("Task 1", "Description 1", StatusOfTask.NEW);
        Task task2 = new Task("Task 2", "Description 2", StatusOfTask.IN_PROGRESS);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getTasks();
        assertEquals(2, tasks.size(), "Should load two tasks");
        assertEquals(task1.getName(), tasks.get(0).getName(), "Loaded Task 1 should match");
        assertEquals(task2.getName(), tasks.get(1).getName(), "Loaded Task 2 should match");
    }
}
