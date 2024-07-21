package tz.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.server.handler.*;
import tz.server.support.DurationAdapter;
import tz.server.support.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {
    private static final int PORT = 8080;
    public static TaskManager taskManager;
    private final HttpServer httpServer;
    public static Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = manager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubTaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
    }
    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager, HttpServer httpServer, Gson gson) {
        HttpTaskServer.taskManager = taskManager;
        this.httpServer = httpServer;
        HttpTaskServer.gson = gson;
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Сервер остановлен");
    }
    public void start() {
        System.out.println("Сервер запущен на порту: " + PORT);
        httpServer.start();
    }
}