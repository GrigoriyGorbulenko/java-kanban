package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.exception.NotFoundException;
import tz.model.Epic;
import tz.server.support.ErrorResponse;
import tz.server.support.HttpMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;
import static tz.server.support.ConstantStatusCode.*;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String httpMethod = exchange.getRequestMethod();
        switch (HttpMethod.valueOf(httpMethod)) {
            case GET -> handleGet(exchange);
            case POST -> handlePost(exchange);
            case DELETE -> handleDelete(exchange);
            default -> writeResponse((new ErrorResponse("Неверный HTTP-метод")), exchange, CODE404);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    writeResponse(taskManager.getAllEpic(), exchange, CODE200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    Epic epic = taskManager.getEpicById(id);
                    writeResponse(epic, exchange, CODE200);
                }
                case 4 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    String sub = gson.toJson(taskManager.getSubTasksByEpic(id));
                    writeResponse(sub, exchange, CODE200);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"), exchange, CODE404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, CODE404);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, CODE500);
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(requestBody, Epic.class);
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.createEpic(epic);
                    writeResponse(epic, exchange, CODE201);
                }
                case 3 -> {
                    taskManager.updateEpic(epic);
                    writeResponse(epic, exchange, CODE201);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"), exchange, CODE404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, CODE404);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, CODE500);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        int splitPathLength = splitPath.length;
        try {
            switch (splitPathLength) {
                case 2 -> {
                    taskManager.removeAllEpics();
                    writeResponse("Задачи удалены", exchange, CODE200);
                }
                case 3 -> {
                    int id = Integer.parseInt(splitPath[2]);
                    taskManager.deleteEpicById(id);
                    writeResponse("Задача удалена", exchange, CODE200);
                }
                default -> writeResponse(new ErrorResponse("Неверно указаны данные"), exchange, CODE404);
            }
        } catch (NotFoundException e) {
            writeResponse(e.getMessage(), exchange, CODE404);
        } catch (RuntimeException e) {
            writeResponse(e.getMessage(), exchange, CODE500);
        }
    }
}