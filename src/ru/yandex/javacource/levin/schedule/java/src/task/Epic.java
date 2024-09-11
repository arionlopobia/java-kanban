package ru.yandex.javacource.levin.schedule.java.src.task;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, StatusOfTask status) {
        super(name, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id) {
            subtaskIds.add(subtaskId);

        }
    }

    public ArrayList<Integer> getSubtasks() {
        return subtaskIds;
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
}

