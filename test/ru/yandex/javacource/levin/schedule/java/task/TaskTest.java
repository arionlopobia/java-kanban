package ru.yandex.javacource.levin.schedule.java.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private InMemoryTaskManager manager;

    @Test
    public void shouldReturnSameTaskWhenRequestedByIdTwice() {
        manager = new InMemoryTaskManager();

        Task task = new Task("Task1", "Description 1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        int taskId = task.getId();

        Task firstRetrievedTask = manager.getTask(taskId);
        Task secondRetrievedTask = manager.getTask(taskId);

        assertEquals(firstRetrievedTask, secondRetrievedTask, "Two task need to be equal");
        assertNotNull(manager.getTask(taskId), "Задача должна быть добавлена в менеджер");
        assertTrue(manager.getPrioritizedTasks().contains(task), "Задача должна быть в приоритетных задачах");

    }

    @Test
    public void shouldUpdateTaskCorrectly() {
        manager = new InMemoryTaskManager();

        Task task = new Task("Task1", "Description 1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);
        int taskId = task.getId();

        Task originalTask = manager.getTask(taskId);
        assertEquals("Task1", originalTask.getName(), "Task name should be 'Task1'");
        assertEquals("Description 1", originalTask.getDescription(), "Task description should be 'Description 1'");

        Task updatedTask = new Task("Updated Task", "Updated Description", StatusOfTask.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(60));
        updatedTask.setId(taskId);
        manager.updateTask(updatedTask);

        Task retrievedUpdatedTask = manager.getTask(taskId);
        assertEquals("Updated Task", retrievedUpdatedTask.getName(), "Task name should be updated");
        assertEquals("Updated Description", retrievedUpdatedTask.getDescription(), "Task description should be updated");
        assertEquals(StatusOfTask.IN_PROGRESS, retrievedUpdatedTask.getStatus(), "Task status should be updated");
        assertEquals(Duration.ofMinutes(45), retrievedUpdatedTask.getDuration(), "Task duration should be updated");

        // Округляем время до секунд для корректного сравнения
        assertEquals(updatedTask.getStartTime().truncatedTo(ChronoUnit.SECONDS), retrievedUpdatedTask.getStartTime().truncatedTo(ChronoUnit.SECONDS), "Task start time should be updated");
    }

}
