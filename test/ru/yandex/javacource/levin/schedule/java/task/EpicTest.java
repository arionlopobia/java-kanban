package ru.yandex.javacource.levin.schedule.java.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private InMemoryTaskManager manager;

    @Test
    public void shouldReturnSameEpicWhenRequestedByIdTwice() {
        manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);
        int epicId = epic.getId();

        Epic firstRetrievedTask = manager.getEpic(epicId);
        Epic secondRetrievedTask = manager.getEpic(epicId);

        assertEquals(firstRetrievedTask, secondRetrievedTask, "Two task need to be equal");
    }


    @Test
    void cannotAddSelfAsSubtaskTest() {
        Epic epic = new Epic("Epic 1", "It is my first Epic");
        int epicId = epic.getId();

        epic.addSubtaskId(epicId);
        System.out.println(epic.getSubtasks());

        assertTrue(epic.getSubtasks().isEmpty(), "Epic cant be in subTask List");
    }

}