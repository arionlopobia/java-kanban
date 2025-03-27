package ru.yandex.javacource.levin.schedule.java.manager.file;

import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;
import ru.yandex.javacource.levin.schedule.java.task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autosaveFile;
    private static final String HEADER = "id,type,name,status,description,duration,startTime,epic";

    public FileBackedTaskManager(File autosaveFile) {
        super();
        this.autosaveFile = autosaveFile;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autosaveFile))) {
            writer.write(HEADER);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(FileBackedTaskManager.toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(FileBackedTaskManager.toString(epic));
                writer.newLine();

                for (SubTask subtask : getSubtasks()) {
                    if (subtask.getEpicId() == epic.getId()) {
                        writer.write(FileBackedTaskManager.toString(subtask));
                        writer.newLine();
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Unable to save file " + autosaveFile.getPath(), e);
        }
    }

    protected static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                taskManager.addAnyTask(task);
            }
            for (Map.Entry<Integer, SubTask> e : taskManager.subtasks.entrySet()) {
                final SubTask subtask = e.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            taskManager.idCounter = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file.getName(), e);
        }
        return taskManager;
    }

    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getTaskType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (SubTask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    protected static String toString(Task task) {
        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                (task.getDurationInMinutes() > 0 ? task.getDurationInMinutes() : "") + "," +
                (task.getStartTime() != null ? task.getStartTime() : "") + "," +
                (task.getTaskType().equals(TaskType.SUBTASK) ? ((SubTask) task).getEpicId() : "");
    }

    protected static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String name = parts[2];
        StatusOfTask status = StatusOfTask.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = parts.length > 5 && !parts[5].isEmpty()
                ? Duration.ofMinutes(Long.parseLong(parts[5]))
                : Duration.ZERO;

        LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty()
                ? LocalDateTime.parse(parts[6])
                : null;

        Task task;

        switch (taskType) {
            case TASK:
                task = new Task(name, description, status, duration, startTime);
                break;

            case EPIC:
                task = new Epic(name, description, status, duration, startTime);
                break;

            case SUBTASK:
                if (parts.length < 8 || parts[7].isEmpty()) {
                    throw new IllegalArgumentException("SubTask должен содержать EpicId: " + value);
                }
                int epicId = Integer.parseInt(parts[7]);
                task = new SubTask(name, description, status, epicId, duration, startTime);
                break;

            default:
                throw new IllegalArgumentException("Неизвестный taskType: " + taskType);
        }

        task.setId(id);
        task.setDuration(duration);
        task.setStartTime(startTime);

        return task;
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
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        super.updateSubtask(subtask);
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
