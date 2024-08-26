import java.util.ArrayList;

public class Epic extends Task {
     ArrayList<SubTask> subtasks;

    public Epic(String name, String description, StatusOfTask status) {
        super(name, description, status);
        this.subtasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<SubTask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks.size() + " subtasks" +
                '}';
    }
}
