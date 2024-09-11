package ru.yandex.javacource.levin.schedule.java.test;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.src.manager.HistoryManager;
import ru.yandex.javacource.levin.schedule.java.src.manager.Managers;
import ru.yandex.javacource.levin.schedule.java.src.manager.TaskManager;

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