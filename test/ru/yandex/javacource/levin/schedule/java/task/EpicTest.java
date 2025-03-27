package ru.yandex.javacource.levin.schedule.java.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private InMemoryTaskManager manager;

    @Test
    public void shouldReturnSameEpicWhenRequestedByIdTwice() {
        manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic1", "Description 1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);
        int epicId = epic.getId();

        Epic firstRetrievedTask = manager.getEpic(epicId);
        Epic secondRetrievedTask = manager.getEpic(epicId);

        assertEquals(firstRetrievedTask, secondRetrievedTask, "Two task need to be equal");
    }

    @Test
    void cannotAddSelfAsSubtaskTest() {
        Epic epic = new Epic("Epic1", "Description 1", StatusOfTask.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int epicId = epic.getId();

        epic.addSubtaskId(epicId);
        System.out.println(epic.getSubtasks());

        assertTrue(epic.getSubtasks().isEmpty(), "Epic cant be in subTask List");
    }

    @Test
    void shouldAddSubtaskIdToEpic() {
        manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "Description 1", StatusOfTask.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2024, 3, 26, 12, 0));
        manager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subtask = new SubTask("SubTask1", "Description 1", StatusOfTask.NEW, epicId,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 3, 26, 12, 30));
        int subtaskId = manager.createSubtask(subtask);

        Epic retrievedEpic = manager.getEpic(epicId);
        assertNotNull(retrievedEpic, "Epic should exist in the manager");

        System.out.println("Subtask IDs in Epic: " + retrievedEpic.getSubtasks());

        assertTrue(retrievedEpic.getSubtasks().contains(subtaskId),
                "Subtask ID should be added to Epic's subtask list");
    }

}
