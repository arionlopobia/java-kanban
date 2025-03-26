package ru.yandex.javacource.levin.schedule.java;

import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.javacource.levin.schedule.java.adapters.DurationAdapter;
import ru.yandex.javacource.levin.schedule.java.adapters.LocalDateTimeAdapter;
import ru.yandex.javacource.levin.schedule.java.handler.*;
import ru.yandex.javacource.levin.schedule.java.manager.HistoryManager;
import ru.yandex.javacource.levin.schedule.java.manager.Managers;
import ru.yandex.javacource.levin.schedule.java.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private static Gson gson;

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) throws IOException {
        this.historyManager = historyManager;
        this.taskManager = taskManager;

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(historyManager,taskManager, gson));
        server.createContext("/subtasks", new SubTaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, historyManager, gson));
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));

        server.setExecutor(null);
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен.");
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(Managers.getDefault(), Managers.getDefaultHistory());
            server.start();
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }

    }
}
