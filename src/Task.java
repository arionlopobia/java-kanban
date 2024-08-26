public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected StatusOfTask status;

    // Конструктор
    public Task(String name, String description, StatusOfTask status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(StatusOfTask newStatus) {
        this.status = newStatus;
    }

    public int getId() {
        return id;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
