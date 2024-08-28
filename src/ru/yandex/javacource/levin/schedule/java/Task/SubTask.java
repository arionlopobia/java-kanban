package ru.yandex.javacource.levin.schedule.java.Task;

public class SubTask extends Task {
    private Epic epic;


    public SubTask(String name, String description, StatusOfTask status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public int getEpicId(){
        return epic.getId();
    }



    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epic.id +
                '}';
    }
}
