package ru.yandex.javacource.levin.schedule.java.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private InMemoryTaskManager manager;

    @Test
    public void shouldReturnSameTaskWhenRequestedByIdTwice() {

        manager = new InMemoryTaskManager();


        Task task = new Task("Task 1", "Description 1", StatusOfTask.NEW, TaskType.TASK);
        manager.createTask(task);
        int taskId = task.getId();


        Task firstRetrievedTask = manager.getTask(taskId);
        Task secondRetrievedTask = manager.getTask(taskId);

        assertEquals(firstRetrievedTask, secondRetrievedTask, "Two task need to be equal");
    }


}