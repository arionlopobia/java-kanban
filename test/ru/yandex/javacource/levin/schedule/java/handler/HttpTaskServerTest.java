package ru.yandex.javacource.levin.schedule.java.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.levin.schedule.java.HttpTaskServer;
import ru.yandex.javacource.levin.schedule.java.manager.HistoryManager;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryHistoryManager;
import ru.yandex.javacource.levin.schedule.java.manager.InMemoryTaskManager;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Epic;
import ru.yandex.javacource.levin.schedule.java.task.StatusOfTask;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private TaskManager manager;
    private HistoryManager historyManager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        taskServer = new HttpTaskServer(manager, historyManager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Task was not created successfully");

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "No tasks returned");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");
        assertEquals("Test Task", tasksFromManager.get(0).getName(), "Task name does not match");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI taskUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(taskUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode(), "Task was not retrieved successfully");
        assertTrue(getResponse.body().contains("Test Task"), "Task name not found in response");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        Task updatedTask = new Task("Updated Task", "Updated description", StatusOfTask.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(5));
        updatedTask.setId(1);

        String updatedTaskJson = gson.toJson(updatedTask);

        URI updateUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(updateUrl)
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, putResponse.statusCode(), "Task was not updated successfully");

        URI taskUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(taskUrl)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode(), "Task was not retrieved successfully");
        assertTrue(getResponse.body().contains("Updated Task"), "Task name not found in response");
        assertTrue(getResponse.body().contains("Updated description"), "Task description not found in response");
    }


    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI deleteUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Task was not deleted successfully");

        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(0, tasksFromManager.size(), "Task was not deleted");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String epicJson = "{\"name\":\"Test Epic\", \"description\":\"Epic description\"}";

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Epic was not added successfully");

        Gson gson = new Gson();
        Map<String, Object> responseData = gson.fromJson(response.body(), Map.class);

        assertEquals("Test Epic", responseData.get("name"), "Epic name does not match");
        assertEquals("Epic description", responseData.get("description"), "Epic description does not match");

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "No epics returned");
        assertEquals(1, epicsFromManager.size(), "Incorrect number of epics");
        assertEquals("Test Epic", epicsFromManager.get(0).getName(), "Epic name does not match");
        assertEquals("Epic description", epicsFromManager.get(0).getDescription(), "Epic description does not match");
    }

    @Test
    public void testTaskInHistory() throws IOException, InterruptedException {
        System.out.println("Starting testTaskInHistory...");


        Task task = new Task("Test Task", "Task description", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        task.setId(1);
        String taskJson = gson.toJson(task);
        System.out.println("Task JSON: " + taskJson);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("POST Response: " + postResponse.statusCode() + " - " + postResponse.body());

        assertEquals(201, postResponse.statusCode(), "Task was not added successfully");

        URI historyUrl = URI.create("http://localhost:8080/history");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(historyUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("GET Response: " + getResponse.statusCode() + " - " + getResponse.body());

        assertEquals(200, getResponse.statusCode(), "History was not fetched successfully");

        JsonArray history = gson.fromJson(getResponse.body(), JsonArray.class);
        System.out.println("History JSON: " + history);

        boolean taskFound = false;
        for (JsonElement element : history) {
            JsonObject taskFromHistory = element.getAsJsonObject();
            System.out.println("Task in history: " + taskFromHistory);
            if (taskFromHistory.has("name") && taskFromHistory.get("name").getAsString().equals("Test Task")) {
                taskFound = true;
                break;
            }
        }

        System.out.println("Task found in history: " + taskFound);
        assertTrue(taskFound, "Test Task not found in the history response");
    }

    @Test
    public void testShouldReturnPrioritizedTasks() throws IOException {
        System.out.println("Starting testShouldReturnPrioritizedTasks...");

        Task highPriorityTask = new Task("High Priority Task", "Description of high priority task", StatusOfTask.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        highPriorityTask.setId(1);
        manager.createTask(highPriorityTask);

        Task lowPriorityTask = new Task("Low Priority Task", "Description of low priority task", StatusOfTask.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(15));
        lowPriorityTask.setId(2);
        manager.createTask(lowPriorityTask);

        List<Task> tasks = manager.getPrioritizedTasks();

        assertEquals("High Priority Task", tasks.get(0).getName(), "First task should be the high priority task");
        assertEquals("Low Priority Task", tasks.get(1).getName(), "Second task should be the low priority task");
    }
}










