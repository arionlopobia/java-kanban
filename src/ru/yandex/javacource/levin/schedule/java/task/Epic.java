package ru.yandex.javacource.levin.schedule.java.task;

import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;


    public Epic(String name, String description, StatusOfTask status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id && !subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtasks() {
        return new ArrayList<>(subtaskIds);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtaskIds.size() + " шт." +
                '}';
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public List<Integer> getSubtaskIds() {
        if (subtaskIds == null) {
            subtaskIds = new ArrayList<>();
        }
        return new ArrayList<>(subtaskIds);
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds.clear();
        this.subtaskIds.addAll(subtaskIds);
    }

    public StatusOfTask getEpicStatus() {
        return status;
    }

    public void setEpicStatus(StatusOfTask status) {
        this.status = status;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateFields(InMemoryTaskManager taskManager) {
        List<SubTask> subTasks = taskManager.getSubTasksForEpic(this.id);

        if (subTasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        this.duration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(d -> d != null)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(s -> s != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(e -> e != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}