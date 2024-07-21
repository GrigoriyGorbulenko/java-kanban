package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.exception.ConflictTimeException;
import tz.exception.NotFoundException;
import tz.model.Task;
import tz.server.support.ErrorResponse;
import tz.server.support.HttpMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            case POST -> handlePost(exchange);
            case DELETE -> handleDelete(exchange);
            default -> writeResponse((new ErrorResponse("Неверный HTTP-метод")), exchange, 404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    writeResponse(taskManager.getAllTask(), exchange, 200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    Task task = taskManager.getTaskById(id);
                    writeResponse(task, exchange, 200);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"),
                        exchange, 404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, 404);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, 500);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.createTask(task);
                    writeResponse(task, exchange, 201);
                }
                case 3 -> {
                    taskManager.updateTask(task);
                    writeResponse(task, exchange, 201);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"),
                        exchange, 404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, 404);
        } catch (ConflictTimeException e) {
            writeResponse(e.getMessage(), exchange, 406);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, 500);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.removeAllTasks();
                    writeResponse("Задачи удалены", exchange, 200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    taskManager.deleteTaskById(id);
                    writeResponse("Задача удалена", exchange, 200);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"),
                        exchange, 404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, 404);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, 500);
        }
    }
}
