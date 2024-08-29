package ru.yandex.javacource.levin.schedule.java.task;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Integer> subtaskIds;

    public Epic(String name, String description, StatusOfTask status) {
        super(name, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public void addSubTask(Integer subtask) {
        subtaskIds.add(subtask);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
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

    public Integer[] getSubtaskIds() {
        Integer[] result = new Integer[subtaskIds.size()];
        subtaskIds.toArray(result);
        return result;
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }
}
