package ru.yandex.javacource.levin.schedule.java.task;

import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected StatusOfTask status;
    protected TypeOfTask typeOfTask;


    public Task(String name, String description, StatusOfTask status, TypeOfTask typeOfTask) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.typeOfTask = typeOfTask;
    }

    public Task copy() {
        Task copy = new Task(this.name, this.description, this.status, this.typeOfTask);
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

    public TypeOfTask getTypeOfTask() {
        return typeOfTask;
    }

    public String toCSV() {
        return id + "," + typeOfTask + "," + name + "," + status + "," + description + ",";
    }

    public static Task fromCSV(String csv) {
        String[] parts = csv.split(",");

        int id = Integer.parseInt(parts[0]);
        TypeOfTask typeOfTask = TypeOfTask.valueOf(parts[1]);
        String name = parts[2];
        StatusOfTask statusOfTask = StatusOfTask.valueOf(parts[3]);
        String description = parts[4];

        switch (typeOfTask) {
            case TASK :
                return new Task(name, description, statusOfTask, typeOfTask);

            case EPIC:
                return new Epic(name, description, statusOfTask, typeOfTask);

            case SUB_TASK:
                int epicId = Integer.parseInt(parts[5]);
                return new SubTask(name, description,statusOfTask, typeOfTask, epicId);

            default:
                throw new IllegalArgumentException("Unknown task type: " + typeOfTask);
        }


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
