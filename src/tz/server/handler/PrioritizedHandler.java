package tz.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tz.server.support.ErrorResponse;
import tz.server.support.HttpMethod;

import java.io.IOException;

import static tz.server.support.ConstantStatusCode.CODE200;
import static tz.server.support.ConstantStatusCode.CODE404;
import static tz.server.support.HttpMethod.GET;
import static tz.server.HttpTaskServer.gson;
import static tz.server.HttpTaskServer.taskManager;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String httpMethod = exchange.getRequestMethod();
        if (HttpMethod.valueOf(httpMethod) == GET) {
            handleGet(exchange);
            return;
        }
        writeResponse((new ErrorResponse("Данный запрос не поддерживается")), exchange, CODE404);
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        writeResponse(gson.toJson(taskManager.getPrioritizedTasks()), exchange, CODE200);
    }
}

