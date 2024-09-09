package ru.yandex.javacource.levin.schedule.java.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void  shouldReturnInitializedTaskManager(){
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);

        assertTrue(taskManager.getTasks().isEmpty());

    }


    @Test
    void  shouldReturnInitializedHistoryManager(){
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);

        assertTrue(historyManager.getHistory().isEmpty());

    }

}