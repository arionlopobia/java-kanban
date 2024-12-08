package ru.yandex.javacource.levin.schedule.java.manager.file;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class SaveEmptyFileTest {

    @Test
    public void testSaveEmptyFile() throws IOException {
        File tempFile = File.createTempFile("empty_file_test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        fileBackedTaskManager.save();

        assertTrue(tempFile.exists(), "Файл не создан");
        assertTrue(tempFile.length() > 0, "Файл пуст, заголовок не записан");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач не пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков не пуст");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач не пуст");
    }
}
