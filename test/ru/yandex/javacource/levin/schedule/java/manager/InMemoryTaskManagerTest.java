package ru.yandex.javacource.levin.schedule.java.manager;


import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {


    @Test
    void ShouldAddAndFindTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task1", "new Task1", StatusOfTask.NEW, TaskType.TASK);
        manager.createTask(task);

        Task foundTask = manager.getTask(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }


    @Test
    void ShouldAddAndFindEpicByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpic(epic.getId());

        assertNotNull(foundEpic);
        assertEquals(epic, foundEpic);
    }

    @Test
    void ShouldAddAndFindSubTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task1", "new Task1", StatusOfTask.NEW, TaskType.SUBTASK, epic.getId());
        manager.createSubtask(subTask);

        SubTask foundSubtask = manager.getSubtask(subTask.getId());

        assertNotNull(foundSubtask);
        assertEquals(subTask, foundSubtask);
    }


    @Test
    public void shouldPreserveTaskFieldsAfterAddition() {
        TaskManager manager = Managers.getDefault();

        Task originalTask = new Task("Test Task", "Task Description", StatusOfTask.NEW, TaskType.TASK);
        manager.createTask(originalTask);

        Task retrievedTask = manager.getTask(originalTask.getId());

        assertNotNull(retrievedTask, "Retrieved task should not be null");

        assertEquals(originalTask.getName(), retrievedTask.getName(), "Task name should be unchanged");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Task description should be unchanged");
    }

    @Test
    public void shouldRemoveSubtaskAndClearIdFromEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic Description", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", StatusOfTask.NEW, TaskType.SUBTASK, epic.getId());
        int subtaskId = manager.createSubtask(subTask);

        assertTrue(epic.getSubtaskIds().contains(subtaskId));

        manager.deleteSubtask(subtaskId);

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "ID подзадачи должен быть удален из эпика");

        assertNull(manager.getSubtask(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    public void shouldNotAffectTaskInManagerWhenFieldsAreChangedViaSetters() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Original Task", "Original Description", StatusOfTask.NEW, TaskType.TASK);
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
            Epic epic = new Epic("Epic 1", "Description", StatusOfTask.NEW, TaskType.EPIC);
            manager.createEpic(epic);

            SubTask subtask1 = new SubTask("Subtask 1", "Description", StatusOfTask.NEW, TaskType.SUBTASK, epic.getId());
            SubTask subtask2 = new SubTask("Subtask 2", "Description", StatusOfTask.NEW, TaskType.SUBTASK, epic.getId());
            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);

            assertEquals(StatusOfTask.NEW, manager.getEpic(epic.getId()).getStatus(),
                    "Epic status should be NEW when all subtasks are NEW.");
        }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Description", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description", StatusOfTask.DONE, TaskType.SUBTASK, epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description", StatusOfTask.DONE, TaskType.SUBTASK, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(StatusOfTask.DONE, manager.getEpic(epic.getId()).getStatus(),
                "Epic status should be DONE when all subtasks are DONE.");
    }

    @Test
    void shouldCalculateEpicStatusWhenSubtasksHaveMixedStatuses() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Description", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description", StatusOfTask.NEW, TaskType.SUBTASK, epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description", StatusOfTask.DONE, TaskType.SUBTASK, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(StatusOfTask.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(),
                "Epic status should be IN_PROGRESS when subtasks have mixed statuses.");
    }

    @Test
    void shouldCalculateEpicStatusWhenAllSubtasksAreInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Description", StatusOfTask.NEW, TaskType.EPIC);
        manager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description", StatusOfTask.IN_PROGRESS, TaskType.SUBTASK, epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description", StatusOfTask.IN_PROGRESS, TaskType.SUBTASK, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(StatusOfTask.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus(),
                "Epic status should be IN_PROGRESS when all subtasks are IN_PROGRESS.");
    }

    @Test
    void shouldPreventOverlappingTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description", StatusOfTask.NEW, TaskType.TASK);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 1, 22, 10, 0));

        Task task2 = new Task("Task 2", "Description", StatusOfTask.NEW, TaskType.TASK);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2025, 1, 22, 10, 15));

        manager.createTask(task1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(task2),
                "Should throw exception for overlapping tasks.");

        assertEquals("Задача пересекается по времени с другой задачей!", exception.getMessage());
    }


}