package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.exception.ConflictTimeException;
import tz.exception.NotFoundException;
import tz.model.SubTask;
import tz.server.ErrorResponse;
import tz.server.HttpMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
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
                    writeResponse(taskManager.getAllSubTask(), exchange, 200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    SubTask subTask = taskManager.getSubTaskById(id);
                    writeResponse(subTask, exchange, 200);
                }
                default -> writeResponse(new ErrorResponse("Данный запрос не поддерживается"), exchange, 404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, 404);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(requestBody, SubTask.class);
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.createSubTask(subTask);
                    writeResponse(subTask, exchange, 201);
                }
                case 3 -> {
                    taskManager.updateSubTask(subTask);
                    writeResponse(subTask, exchange, 201);
                }
                default -> writeResponse(new ErrorResponse("Данный запрос не поддерживается"), exchange, 404);
            }
        } catch (ConflictTimeException e) {
            writeResponse(e.getMessage(), exchange, 406);
        }
    }


    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.removeAllSubTasks();
                    writeResponse("Задачи удалены", exchange, 200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    taskManager.deleteSubTaskById(id);
                    writeResponse("Задача удалена", exchange, 200);
                }
                default -> writeResponse(new ErrorResponse("Данный запрос не поддерживается"), exchange, 404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, 404);
        }
    }
}
