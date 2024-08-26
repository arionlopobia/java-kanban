import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return ++idCounter;
    }

    public void createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        if (task instanceof Epic) {
            epics.put(id, (Epic) task);
        } else if (task instanceof SubTask) {
            subtasks.put(id, (SubTask) task);
        }
    }


    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    public void removeAllTask() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            return null;
        }
    }

    public void updateTask(Task task) {
        if (task instanceof Epic) {
            epics.put(task.id, (Epic) task);
        } else if (task instanceof SubTask) {
            subtasks.put(task.id, (SubTask) task);
        }
        tasks.put(task.id, task);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
        epics.remove(id);
        subtasks.remove(id);
    }

    public ArrayList<SubTask> getSubTasksForEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        ArrayList<SubTask> subTasksForEpic = new ArrayList<>();
        for (SubTask subTask : subtasks.values()) {
            if (epic.subtasks.contains(subTask)) {
                subTasksForEpic.add(subTask);
            }
        }
        return subTasksForEpic;
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        ArrayList<SubTask> subTasks = getSubTasksForEpic(epicId);
        if (subTasks.isEmpty()) {
            epic.status = StatusOfTask.NEW;
        } else {
            boolean allDone = true;
            boolean allNew = true;
            for (SubTask subTask : subTasks) {
                if (subTask.status != StatusOfTask.DONE) {
                    allDone = false;
                }
                if (subTask.status != StatusOfTask.NEW) {
                    allNew = false;
                }
            }
            if (allDone) {
                epic.status = StatusOfTask.DONE;
            } else if (allNew) {
                epic.status = StatusOfTask.NEW;
            } else {
                epic.status = StatusOfTask.IN_PROGRESS;
            }
        }
    }

    public void updateStatus(int id, StatusOfTask newStatus) {
        Task task = getTaskById(id);
        if (task == null) {
            System.out.println("Задача с таким ID не найдена.");
            return;
        }
        task.setStatus(newStatus);
        updateTask(task);

        if (task instanceof Epic) {
            updateEpicStatus(id);
        }
    }
}