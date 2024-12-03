package ru.yandex.javacource.levin.schedule.java.manager.file;

import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autosaveFile;

    public FileBackedTaskManager(File autosaveFile) {
        super();
        this.autosaveFile = autosaveFile;
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(autosaveFile)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(task.toCSV() + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(epic.toCSV() + "\n");

                for (SubTask subtask : getSubtasks()) {
                    if(subtask.getEpicId() == epic.getId()) {
                        fileWriter.write(subtask.toCSV() + "\n");
                    }

                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Unable to save file" + autosaveFile.getPath(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try{
            String fileContent = Files.readString(file.toPath());

            String[] lines = fileContent.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if(!line.isEmpty()){
                    Task task = Task.fromCSV(line);
                    switch (task.getTypeOfTask()){
                        case TASK:
                            manager.createTask(task);
                            break;
                        case EPIC:
                            manager.createEpic((Epic) task);
                            break;
                        case SUB_TASK:
                            manager.createSubtask((SubTask) task);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Unable to load file " + file.getAbsolutePath(), e);
        }
        return manager;
    }


    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public Integer createSubtask(SubTask subtask) {
        Integer id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateTask(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
