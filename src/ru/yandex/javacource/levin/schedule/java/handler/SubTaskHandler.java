package ru.yandex.javacource.levin.schedule.java.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.task.SubTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<SubTask> subtasks = taskManager.getSubtasks();
            sendText(exchange, gson.toJson(subtasks), 200);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            System.out.println("Received POST data: " + body.toString());

            try {
                SubTask newSubTask = gson.fromJson(body.toString(), SubTask.class);

                if (newSubTask == null || newSubTask.getEpicId() == 0) {
                    sendText(exchange, "{\"error\":\"Invalid subtask data or epicId is missing\"}", 400);
                    return;
                }

                taskManager.createSubtask(newSubTask);

                String jsonResponse = gson.toJson(newSubTask);
                sendText(exchange, jsonResponse, 201);
            } catch (Exception e) {
                e.printStackTrace();
                sendText(exchange, "{\"error\":\"Failed to create subtask: " + e.getMessage() + "\"}", 500);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }

    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(responseCode, text.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }
}
