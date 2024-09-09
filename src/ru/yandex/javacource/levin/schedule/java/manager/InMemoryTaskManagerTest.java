package ru.yandex.javacource.levin.schedule.java.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {


    @Test
    void ShouldAddAndFindTaskByIdTest(){
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Task1", "new Task1", StatusOfTask.NEW);
        manager.createTask(task);

        Task foundTask = manager.getTask(task.getId());

        assertNotNull(foundTask);
        assertEquals(task, foundTask);
    }


    @Test
    void ShouldAddAndFindEpicByIdTest(){
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW);
        manager.createEpic(epic);

        Epic foundEpic = manager.getEpic(epic.getId());

        assertNotNull(foundEpic);
        assertEquals(epic, foundEpic);
    }

    @Test
    void ShouldAddAndFindSubTaskByIdTest(){
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1 ", "new Epic 1", StatusOfTask.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Task1", "new Task1", StatusOfTask.NEW, epic.getId());
        manager.createSubtask(subTask);

        SubTask foundSubtask = manager.getSubtask(subTask.getId());

        assertNotNull(foundSubtask);
        assertEquals(subTask, foundSubtask);
    }



    @Test
    public void shouldPreserveTaskFieldsAfterAddition() {

        TaskManager manager = Managers.getDefault();


        Task originalTask = new Task("Test Task", "Task Description", StatusOfTask.NEW);

        manager.createTask(originalTask);


        Task retrievedTask = manager.getTask(originalTask.getId());


        assertNotNull(retrievedTask, "Retrieved task should not be null");


        assertEquals(originalTask.getName(), retrievedTask.getName(), "Task name should be unchanged");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Task description should be unchanged");
    }

}