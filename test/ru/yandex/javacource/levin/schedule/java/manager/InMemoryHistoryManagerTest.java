package ru.yandex.javacource.levin.schedule.java.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryHistoryManager;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

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
        // Создаем задачу
        Task originalTask = new Task("Task 1", "Description 1", StatusOfTask.NEW);
        originalTask.setId(1);

        // Добавляем задачу в историю
        historyManager.addHistory(originalTask);

        // Изменяем оригинальную задачу
        originalTask.setName("Task 1 Updated");
        originalTask.setDescription("Description 1 Updated");

        // Получаем задачу из истории
        List<Task> history = historyManager.getHistory();
        Task taskFromHistory = history.get(0);

        // Проверяем, что задача в истории не изменилась
        assertEquals("Task 1", taskFromHistory.getName());
        assertEquals("Description 1", taskFromHistory.getDescription());

        // Убеждаемся, что это разные объекты
        assertNotSame(originalTask, taskFromHistory);
    }
}

