package ru.yandex.javacource.levin.schedule.java.task;

import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected StatusOfTask status;
    protected TaskType taskType;


    public Task(String name, String description, StatusOfTask status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
    }

    public Task copy() {
        Task copy = new Task(this.name, this.description, this.status, this.taskType);
        copy.setId(this.id);
        return copy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }


}
