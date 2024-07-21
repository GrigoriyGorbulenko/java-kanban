package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.exception.NotFoundException;
import tz.model.Epic;
import tz.server.ErrorResponse;
import tz.server.HttpMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
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
        switch (splitPathLength) {
            case 2 -> {
                try {
                    writeResponse(taskManager.getAllEpic(), exchange, 200);
                } catch (NotFoundException e) {
                    writeResponse(e.getMessage(), exchange, 404);
                }
            }
            case 3 -> {
                int id = Integer.parseInt(splitPath[2]);
                try {
                Epic epic = taskManager.getEpicById(id);
                    writeResponse(epic, exchange, 200);
                } catch (NotFoundException e) {
                    writeResponse(e.getMessage(), exchange, 404);
                }
            }
            case 4 -> {
                int id = Integer.parseInt(splitPath[2]);
                try {
                    String sub = gson.toJson(taskManager.getSubTasksByEpic(id));
                    writeResponse(sub, exchange, 200);
                } catch (NotFoundException e) {
                        writeResponse(e.getMessage(), exchange, 404);
                }
            }
            default -> writeResponse(new ErrorResponse("Not found"), exchange, 404);
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(requestBody, Epic.class);
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;

        switch (splitPathLength) {
            case 2 -> {
                    taskManager.createEpic(epic);
                    writeResponse(epic, exchange, 201);
            }
            case 3 -> {
                int id = Integer.parseInt(splitPath[2]);
                if (taskManager.getEpicById(id) == null) {
                    writeResponse(new ErrorResponse("Задачи с указаным id не найдено"),
                            exchange, 404);
                } else {
                    taskManager.updateEpic(epic);
                    writeResponse(epic, exchange, 201);
                }
            }
            default -> writeResponse(new ErrorResponse("Not found"), exchange, 404);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        if (splitPathLength == 3) {
            int id = Integer.parseInt(splitPath[2]);
            taskManager.deleteEpicById(id);
            writeResponse(new ErrorResponse("Задача удалена"), exchange, 200);
        } else {
            writeResponse(new ErrorResponse("Задача не указана"), exchange, 404);
        }
    }
}
