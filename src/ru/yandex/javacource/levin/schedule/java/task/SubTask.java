package ru.yandex.javacource.levin.schedule.java.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;


    public SubTask(String name, String description, StatusOfTask status, TaskType taskType, int epicId) {
        super(name, description, status, taskType);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        if(startTime == null || duration == null) {
            throw new IllegalStateException("Start time and end time must be set");
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                ", duration=" + getDurationInMinutes() +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }
}
