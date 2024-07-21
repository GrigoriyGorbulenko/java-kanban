package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.server.ErrorResponse;
import tz.server.HttpMethod;

import java.io.IOException;

import static tz.server.HttpMethod.GET;
import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        if (HttpMethod.valueOf(httpMethod) == GET) {
            handleGet(exchange);
            return;
        }
        writeResponse((new ErrorResponse("Данный запрос не поддерживается")), exchange,404);
    }
    private void handleGet(HttpExchange exchange) throws IOException {
        if(!taskManager.getHistory().isEmpty()) {
            writeResponse(gson.toJson(taskManager.getHistory()),exchange, 200);
        } else {
            writeResponse(new ErrorResponse("Задачи в истории отсутствуют"), exchange, 404);
        }
    }
}
