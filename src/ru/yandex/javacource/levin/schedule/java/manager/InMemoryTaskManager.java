package ru.yandex.javacource.levin.schedule.java.manager;

import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryTaskManager implements TaskManager {

    protected int idCounter = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subtasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    @Override
    public void createTask(Task task) {
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей!");
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (hasOverlap(epic)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей!");
        }
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        addToPrioritizedTasks(epic);
    }

    @Override
    public Integer createSubtask(SubTask subtask) {
        if (hasOverlap(subtask)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей!");
        }
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
        addToPrioritizedTasks(subtask);
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
        prioritizedTasks.clear();
    }

    @Override
    public void dealeateTasks() {
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.clear();
    }

    @Override
    public void dealeateEpics() {
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        epics.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        epics.values().forEach(epic -> {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        });

        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addHistory(epic);
        return epic;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subtasks.get(id);
        historyManager.addHistory(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);

        if (savedTask == null) {
            throw new IllegalArgumentException("Task not found");
        }

        prioritizedTasks.remove(savedTask);

        if (hasOverlap(task)) {
            throw new IllegalArgumentException("The updated task overlaps with another task!");
        }

        if (task instanceof Epic) {
            epics.put(id, (Epic) task);
        } else if (task instanceof SubTask) {
            subtasks.put(id, (SubTask) task);
        } else {
            tasks.put(id, task);
        }

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }

        prioritizedTasks.remove(savedEpic);

        if (hasOverlap(epic)) {
            throw new IllegalArgumentException("Обновляемая задача пересекается по времени с другой задачей!");
        }

        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setEpicStatus(savedEpic.getEpicStatus());
        epics.put(epic.getId(), epic);

        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
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

        prioritizedTasks.remove(savedSubtask);

        if (hasOverlap(subtask)) {
            throw new IllegalArgumentException("Обновляемая задача пересекается по времени с другой задачей!");
        }

        subtasks.put(id, subtask);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            prioritizedTasks.remove(epic);
            historyManager.remove(id);

            for (Integer subtaskId : epic.getSubtaskIds()) {
                SubTask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtaskId);
                }
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<SubTask> subTasks = getSubTasksForEpic(epicId);
        if (subTasks.isEmpty()) {
            epic.setStatus(StatusOfTask.NEW);
            return;
        }

        boolean allDone = subTasks.stream().allMatch(subTask -> subTask.getStatus() == StatusOfTask.DONE);
        boolean allNew = subTasks.stream().allMatch(subTask -> subTask.getStatus() == StatusOfTask.NEW);

        if (allDone) {
            epic.setStatus(StatusOfTask.DONE);
        } else if (allNew) {
            epic.setStatus(StatusOfTask.NEW);
        } else {
            epic.setStatus(StatusOfTask.IN_PROGRESS);
        }
    }

    protected int generateId() {
        return ++idCounter;
    }

    @Override
    public void addToPrioritizedTasks(Task task) {
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей!");
        }
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task getHighestPriorityTask() {
        return prioritizedTasks.isEmpty() ? null : prioritizedTasks.first();
    }

    @Override
    public boolean hasOverlap(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .anyMatch(existingTask -> {
                    if (existingTask.getStartTime() == null || existingTask.getEndTime() == null) {
                        return false;
                    }

                    return task.getStartTime().isBefore(existingTask.getEndTime()) &&
                            task.getEndTime().isAfter(existingTask.getStartTime());
                });
    }


    @Override
    public boolean hasAnyOverlaps() {
        List<Task> sortedTasks = getPrioritizedTasks();
        return IntStream.range(0, sortedTasks.size() - 1)
                .anyMatch(i -> {
                    Task current = sortedTasks.get(i);
                    Task next = sortedTasks.get(i + 1);

                    return current.getEndTime() != null && next.getStartTime() != null &&
                            current.getEndTime().isAfter(next.getStartTime());
                });
    }

}