package ru.yandex.javacource.levin.schedule.java.Manager;

import ru.yandex.javacource.levin.schedule.java.Task.Epic;
import ru.yandex.javacource.levin.schedule.java.Task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.Task.SubTask;
import ru.yandex.javacource.levin.schedule.java.Task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    public void createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        tasks.put(id, epic);
        epics.put(id, epic);
    }


    public Integer createSubtask(SubTask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        return id;
    }


    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void dealeateTasks() {
        tasks.clear();
    }

    public void dealeateEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void dealeateSubtasks() {
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
        if (task instanceof Epic) {
            epics.put(id, (Epic) task);
        } else if (task instanceof SubTask) {
            subtasks.put(id, (SubTask) task);
        }
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
            tasks.remove(subtaskId);
        }
        epics.remove(id);
        tasks.remove(id);
    }

    public void removeSubTaskById(int subtaskId) {
        SubTask subTask = subtasks.get(subtaskId);
        if (subTask == null) {
            return;
        }

        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        subtasks.remove(subtaskId);
        tasks.remove(subtaskId);

        epic.getSubtasks().remove(Integer.valueOf(subtaskId));

        updateEpicStatus(epic.getId());
    }



    public ArrayList<SubTask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        ArrayList<SubTask> subTasksForEpic = new ArrayList<>();
        for (Integer subTaskId : epic.getSubtasks()) {
            SubTask subTask = subtasks.get(subTaskId);
            if (subTask != null) {
                subTasksForEpic.add(subTask);
            }
        }
        return subTasksForEpic;
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        ArrayList<SubTask> subTasks = getSubTasksForEpic(epicId);
        if (subTasks.isEmpty()) {
            epic.status = StatusOfTask.NEW;
            return;
        }

        boolean allDone = true;
        boolean allNew = true;
        for (SubTask subTask : subTasks) {
            if (subTask.status != StatusOfTask.DONE) {
                allDone = false;
            }
            if (subTask.status != StatusOfTask.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.status = StatusOfTask.DONE;
        } else if (allNew) {
            epic.status = StatusOfTask.NEW;
        } else {
            epic.status = StatusOfTask.IN_PROGRESS;
        }
    }




    private int generateId() {
        return ++idCounter;
    }
}