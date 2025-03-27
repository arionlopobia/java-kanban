package ru.yandex.javacource.levin.schedule.java.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void shouldAddAndFindTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task1", "new Task1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);

        Task foundTask = manager.getTask(task.getId());

        assertNotNull(foundTask, "Задача не найдена");
        assertEquals(task, foundTask, "Найденная задача не совпадает с исходной");
    }

    @Test
    void shouldAddAndFindEpicByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "new Epic", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpic(epic.getId());

        assertNotNull(foundEpic, "Эпик не найден");
        assertEquals(epic, foundEpic, "Найденный эпик не совпадает с исходным");
    }

    @Test
    void shouldAddAndFindSubTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "new Epic", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task1", "new Task1", StatusOfTask.NEW, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now());
        manager.createSubtask(subTask);

        SubTask foundSubtask = manager.getSubtask(subTask.getId());

        assertNotNull(foundSubtask, "Подзадача не найдена");
        assertEquals(subTask, foundSubtask, "Найденная подзадача не совпадает с исходной");
    }

    @Test
    public void shouldPreserveTaskFieldsAfterAddition() {
        TaskManager manager = Managers.getDefault();

        Task originalTask = new Task("Test Task", "Task Description", StatusOfTask.NEW, Duration.ofMinutes(25), LocalDateTime.now());
        manager.createTask(originalTask);

        Task retrievedTask = manager.getTask(originalTask.getId());

        assertNotNull(retrievedTask, "Задача не должна быть null");
        assertEquals(originalTask.getName(), retrievedTask.getName(), "Название задачи должно совпадать");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Описание задачи должно совпадать");
    }

    @Test
    public void shouldRemoveSubtaskAndClearIdFromEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic1", "new Epic 1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", StatusOfTask.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        int subtaskId = manager.createSubtask(subTask);

        assertTrue(epic.getSubtaskIds().contains(subtaskId), "Эпик должен содержать подзадачу");

        manager.deleteSubtask(subtaskId);

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "ID подзадачи должен быть удален из эпика");
        assertNull(manager.getSubtask(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    public void shouldNotAffectTaskInManagerWhenFieldsAreChangedViaSetters() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Original Task", "Original Description", StatusOfTask.NEW, Duration.ofMinutes(45), LocalDateTime.now());
        manager.createTask(task);

        Task originalTask = manager.getTask(task.getId());
        Task taskCopy = task.copy();

        taskCopy.setName("Modified Task");
        taskCopy.setDescription("Modified Description");

        assertEquals("Original Task", originalTask.getName(), "Название задачи не должно измениться в менеджере");
        assertEquals("Original Description", originalTask.getDescription(), "Описание задачи не должно измениться в менеджере");
    }

    @Test
    void shouldCalculateEpicStatusAllNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Description", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        manager.createSubtask(new SubTask("Subtask 1", "Description", StatusOfTask.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now()));
        manager.createSubtask(new SubTask("Subtask 2", "Description", StatusOfTask.NEW, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now().plusHours(1)));

        assertEquals(StatusOfTask.NEW, manager.getEpic(epic.getId()).getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Description", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        manager.createSubtask(new SubTask("Subtask 1", "Description", StatusOfTask.DONE, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now()));
        manager.createSubtask(new SubTask("Subtask 2", "Description", StatusOfTask.DONE, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now().plusHours(1)));

        assertEquals(StatusOfTask.DONE, manager.getEpic(epic.getId()).getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksHaveMixedStatuses() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Description", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);

        manager.createSubtask(new SubTask("Subtask 1", "Description", StatusOfTask.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now()));
        manager.createSubtask(new SubTask("Subtask 2", "Description", StatusOfTask.DONE, epic.getId(), Duration.ofMinutes(20), LocalDateTime.now().plusHours(1)));

        assertEquals(StatusOfTask.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS при смешанных статусах подзадач");
    }

    @Test
    void shouldPreventOverlappingTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 22, 10, 0));
        Task task2 = new Task("Task 2", "Description", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 22, 10, 15));

        manager.createTask(task1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(task2),
                "Должно выбрасываться исключение при пересечении задач по времени");

        assertEquals("Задача пересекается по времени с другой задачей!", exception.getMessage());
    }
}
