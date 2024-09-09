package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subtasks;
    HistoryManager historyManager  = Managers.getDefaultHistory(); ;



    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    @Override
    public void createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }


    @Override
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


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void dealeateTasks() {
        tasks.clear();
    }

    @Override
    public void dealeateEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

        @Override
        public Task getTask(int id) {
           Task task = tasks.get(id);
           if (task != null) {
               historyManager.addHistory(task);
           }
           return task;
        }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addHistory(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subtasks.get(id);
        if (subTask != null) {
            historyManager.addHistory(subTask);
        }
        return subTask;
    }

    @Override
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

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        SubTask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);  // нужено сделать метод в ЭПИК
        updateEpicStatus(epic.getId());
    }



    @Override
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