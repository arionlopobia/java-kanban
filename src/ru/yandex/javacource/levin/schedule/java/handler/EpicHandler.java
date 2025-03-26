package ru.yandex.javacource.levin.schedule.java.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;
import ru.yandex.javacource.levin.schedule.java.task.Epic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Epic> epics = taskManager.getEpics();
            sendText(exchange, gson.toJson(epics), 200);
        } else if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                System.out.println("Received POST data: " + body.toString());

                try {
                    Epic newEpic = gson.fromJson(body.toString(), Epic.class);

                    if (newEpic == null) {
                        sendText(exchange, "{\"error\":\"Invalid epic data\"}", 400);
                        return;
                    }

                    taskManager.createEpic(newEpic);

                    String jsonResponse = gson.toJson(newEpic);
                    sendText(exchange, jsonResponse, 201);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendText(exchange, "{\"error\":\"Failed to create epic: " + e.getMessage() + "\"}", 500);
                    exchange.close();
                }
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
