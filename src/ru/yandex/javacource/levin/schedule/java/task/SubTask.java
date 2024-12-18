package ru.yandex.javacource.levin.schedule.java.task;

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
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}
