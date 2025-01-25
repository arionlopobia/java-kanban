package ru.yandex.javacource.levin.schedule.java.task;

import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    public List<Integer> subtaskIds;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description, StatusOfTask.NEW);
        this.subtaskIds = new ArrayList<>();
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id) {
            subtaskIds.add(subtaskId);
        }
    }

    public List<Integer> getSubtasks() {
        return subtaskIds;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtaskIds.size() + " subtasks" +
                '}';
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    public StatusOfTask getEpicStatus() {
        return status;
    }

    public void setEpicStatus(StatusOfTask status) {
        this.status = status;
    }

    @Override
    public long getDurationInMinutes() {
        return duration != null ? duration.toMinutes() : 0;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            throw new IllegalStateException("Start time and end time must be set");
        }
        return startTime.plus(duration);
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
                .filter(subTask -> subTask.getDuration() != null)
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);


        this.startTime = subTasks.stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .map(SubTask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subTasks.stream()
                .filter(subTask -> subTask.getEndTime() != null)
                .map(SubTask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }


}


