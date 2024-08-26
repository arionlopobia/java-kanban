public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();


        Task task1 = new Task("Первая таска", "я сделал таску",  StatusOfTask.NEW);
        taskManager.createTask(task1);


        Epic epic1 = new Epic("ЭПИИИИИИИИК", "ЭТО ПЕРВЫЙ ЭПИК",  StatusOfTask.NEW);
        taskManager.createTask(epic1);


        SubTask subTask1 = new SubTask("первая сабтаска", "первая сабтаска",  StatusOfTask.NEW, epic1);
        taskManager.createTask(subTask1);


        SubTask subTask2 = new SubTask("вторая сабтаска", "это вторая саб таска",  StatusOfTask.NEW, epic1);
        taskManager.createTask(subTask2);


        Epic epic2 = new Epic("epic - 2", "второй epic",  StatusOfTask.NEW);
        taskManager.createTask(epic2);


        SubTask subTask3 = new SubTask("subtask", "it is new subtask",  StatusOfTask.NEW, epic2);
        taskManager.createTask(subTask3);


        Task task2 = new Task("task - 2", "вторая таска", StatusOfTask.NEW);
        taskManager.createTask(task2);


        taskManager.updateStatus(1, StatusOfTask.IN_PROGRESS);


        taskManager.updateStatus(subTask1.getId(), StatusOfTask.DONE);
        taskManager.updateStatus(subTask2.getId(), StatusOfTask.DONE);
        taskManager.updateEpicStatus(epic1.getId());


        taskManager.removeTaskById(2);
        System.out.println(taskManager.getAllTasks());
    }
}
