package ru.yandex.javacource.levin.schedule.java.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.levin.schedule.java.manager.HistoryManager;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class TaskHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(HistoryManager historyManager, TaskManager taskManager, Gson gson) {
        this.historyManager = historyManager;
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            case "PUT":
                handlePUT(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();

        if (tasks == null || tasks.isEmpty()) {
            sendText(exchange, "Задачи не найдены.", 404);
            return;
        }

        String response = gson.toJson(tasks);

        sendText(exchange, response, 200);
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Task task = gson.fromJson(reader, Task.class);

            if (task.getStartTime() == null) {
                task.setStartTime(LocalDateTime.now());
            }

            if (task.getDuration() == null) {
                task.setDuration(Duration.ZERO);
            }

            if (taskManager.hasOverlap(task)) {
                sendError(exchange, 400, "Task overlaps with another task");
                return;
            }

            taskManager.createTask(task);
            historyManager.addHistory(task);

            String response = gson.toJson(task);
            sendText(exchange, response, 201);
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid task data");
        } catch (Exception e) {
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handlePUT(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();

        String[] parts = uri.split("/");
        if (parts.length != 3 || !parts[1].equals("tasks")) {
            String response = "Invalid URI format. Expected /tasks/{id}.";
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        int taskId;
        try {
            taskId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            String response = "Invalid task ID format. It must be an integer.";
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (requestBody.isBlank()) {
            String response = "Request body is empty.";
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        Task task;
        try {
            task = gson.fromJson(requestBody, Task.class);
        } catch (Exception e) {
            String response = "Invalid JSON format: " + e.getMessage();
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        if (task == null || task.getId() != taskId) {
            String response = "Task ID in the body does not match the URI or is missing.";
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        Task existingTask = taskManager.getTask(taskId);
        if (existingTask == null) {
            String response = "Task not found.";
            exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }

        try {
            taskManager.updateTask(task);
            String response = gson.toJson(task);
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IllegalArgumentException e) {
            String response = "Invalid task data: " + e.getMessage();
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            String response = "An unexpected error occurred: " + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        taskManager.dealeateTasks();
        sendText(exchange, "{\"message\": \"All tasks deleted\"}", 200);
    }

    protected void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (var outputStream = exchange.getResponseBody()) {
            outputStream.write(response.getBytes());
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        sendText(exchange, response, statusCode);
    }
}
