package ru.yandex.javacource.levin.schedule.java.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private InMemoryTaskManager manager;

    @Test
    public void shouldReturnSameTaskWhenRequestedByIdTwice() {

        manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description", StatusOfTask.NEW);
        manager.createEpic(epic);


        SubTask subTask = new SubTask("Subtask 1", "Description 1", StatusOfTask.NEW, 1);
        manager.createSubtask(subTask);
        int subTaskId = subTask.getId(); // Получаем id задачи


        SubTask firstRetrievedTask = manager.getSubtask(subTaskId);
        SubTask secondRetrievedTask = manager.getSubtask(subTaskId);

        assertEquals(firstRetrievedTask, secondRetrievedTask, "Two task need to be equal");
    }

}