package ru.yandex.javacource.levin.schedule.java.manager.file;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    @Test
    void shouldThrowExceptionWhenLoadingInvalidFile() {
        File invalidFile = new File("invalid_file_path.csv");

        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(invalidFile);
        }, "Должно быть выброшено исключение, если файл не найден.");

        assertEquals("Can't read form file: invalid_file_path.csv", exception.getMessage());
    }


    @Test
    void shouldNotThrowExceptionForValidFile() throws IOException {
        File validFile = new File("valid_file_path.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(validFile))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            writer.write("1,TASK,Test Task,NEW,Description,");
            writer.newLine();
        }

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(validFile),
                "Should not throw exception when file is valid.");

        validFile.delete();
    }
}
