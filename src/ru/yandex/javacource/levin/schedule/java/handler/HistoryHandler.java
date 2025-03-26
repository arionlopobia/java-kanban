package ru.yandex.javacource.levin.schedule.java.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.levin.schedule.java.manager.HistoryManager;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final HistoryManager historyManager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, HistoryManager historyManager, Gson gson) {
        this.manager = manager;
        this.historyManager = historyManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> history = historyManager.getHistory();
            String response = gson.toJson(history);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else if ("POST".equals(exchange.getRequestMethod())) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                Task task = gson.fromJson(requestBody.toString(), Task.class);
                if (task == null) {
                    sendText(exchange, "Invalid task data", 400);
                    return;
                }

                manager.createTask(task);

                historyManager.addHistory(task);

                sendText(exchange, "Task created and added to history", 200);
            } catch (Exception e) {
                sendText(exchange, "Failed to add task", 400);
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }

}
