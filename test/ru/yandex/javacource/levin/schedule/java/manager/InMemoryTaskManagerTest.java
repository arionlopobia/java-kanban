package ru.yandex.javacource.levin.schedule.java.manager;


import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {


    @Test
    void ShouldAddAndFindTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task1", "new Task1", StatusOfTask.NEW, TypeOfTask.TASK);
        manager.createTask(task);

        Task foundTask = manager.getTask(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }


    @Test
    void ShouldAddAndFindEpicByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW, TypeOfTask.EPIC);
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpic(epic.getId());

        assertNotNull(foundEpic);
        assertEquals(epic, foundEpic);
    }

    @Test
    void ShouldAddAndFindSubTaskByIdTest() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW, TypeOfTask.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task1", "new Task1", StatusOfTask.NEW, TypeOfTask.SUB_TASK, epic.getId());
        manager.createSubtask(subTask);

        SubTask foundSubtask = manager.getSubtask(subTask.getId());

        assertNotNull(foundSubtask);
        assertEquals(subTask, foundSubtask);
    }


    @Test
    public void shouldPreserveTaskFieldsAfterAddition() {
        TaskManager manager = Managers.getDefault();

        Task originalTask = new Task("Test Task", "Task Description", StatusOfTask.NEW, TypeOfTask.TASK);
        manager.createTask(originalTask);

        Task retrievedTask = manager.getTask(originalTask.getId());

        assertNotNull(retrievedTask, "Retrieved task should not be null");

        assertEquals(originalTask.getName(), retrievedTask.getName(), "Task name should be unchanged");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Task description should be unchanged");
    }

    @Test
    public void shouldRemoveSubtaskAndClearIdFromEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic Description", StatusOfTask.NEW, TypeOfTask.EPIC);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", StatusOfTask.NEW, TypeOfTask.SUB_TASK, epic.getId());
        int subtaskId = manager.createSubtask(subTask);

        assertTrue(epic.getSubtaskIds().contains(subtaskId));

        manager.deleteSubtask(subtaskId);

        assertFalse(epic.getSubtaskIds().contains(subtaskId), "ID подзадачи должен быть удален из эпика");

        assertNull(manager.getSubtask(subtaskId), "Подзадача должна быть удалена");
    }

    @Test
    public void shouldNotAffectTaskInManagerWhenFieldsAreChangedViaSetters() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Original Task", "Original Description", StatusOfTask.NEW, TypeOfTask.TASK);
        manager.createTask(task);

        Task originalTask = manager.getTask(task.getId());
        Task taskCopy = task.copy();

        taskCopy.setName("Modified Task");
        taskCopy.setDescription("Modified Description");

        assertEquals("Original Task", originalTask.getName(), "Название задачи не должно измениться в менеджере");
        assertEquals("Original Description", originalTask.getDescription(), "Описание задачи не должно измениться в менеджере");
    }


}