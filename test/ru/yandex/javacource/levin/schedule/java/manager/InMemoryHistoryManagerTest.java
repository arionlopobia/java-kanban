package ru.yandex.javacource.levin.schedule.java.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;
import ru.yandex.javacource.levin.schedule.java.task.TaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldPreserveTaskStateWhenAddedToHistory() {
        Task originalTask = new Task("Task 1", "Description 1", StatusOfTask.NEW);
        originalTask.setId(1);

        historyManager.addHistory(originalTask);

        originalTask.setName("Task 1 Updated");
        originalTask.setDescription("Description 1 Updated");

        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.get(0);

        assertEquals("Task 1", taskFromHistory.getName());
        assertEquals("Description 1", taskFromHistory.getDescription());

        assertNotSame(originalTask, taskFromHistory);
    }


    @Test
    public void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1", StatusOfTask.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", StatusOfTask.NEW);
        task2.setId(2);
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(2, history.get(0).getId());
    }

    @Test
    public void shouldNotAddDuplicateTasksToHistory() {
        Task task = new Task("Task 1", "Description 1", StatusOfTask.NEW);
        task.setId(1);

        historyManager.addHistory(task);
        historyManager.addHistory(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(1, history.get(0).getId());
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        assertTrue(manager.getHistory().isEmpty(), "History should be empty when no tasks are viewed.");
    }

    @Test
    void shouldRemoveTaskFromHistoryCorrectly() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description", StatusOfTask.NEW);
        Task task2 = new Task("Task 2", "Description", StatusOfTask.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.deleteTask(task1.getId());

        assertFalse(manager.getHistory().contains(task1), "Task should be removed from history after deletion.");
    }


}
