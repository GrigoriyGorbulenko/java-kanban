package server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tz.exception.NotFoundException;
import tz.manager.InMemoryTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;
import tz.server.HttpTaskServer;
import java.net.http.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz.model.Status.NEW;
import static tz.server.support.ConstantStatusCode.*;


public class HttpTaskManagerTasksTest {

    TaskManager taskManager = new InMemoryTaskManager(Managers.getHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.gson;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        try {
            taskManager.removeAllTasks();
            taskManager.removeAllSubTasks();
            taskManager.removeAllEpics();
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException e) {
            assertNotEquals("", e.getMessage());
        }

        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testCreateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testCreateTaskFail() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);
        // создаём задачу
        Task task1 = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE406, response.statusCode());
        List<Task> tasksFromManager = taskManager.getAllTask();
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        int id = taskManager.createTask(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode());

        Task tasksFromManager = taskManager.getTaskById(id);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllTask().size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskFailWithBadId() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        int id = taskManager.createTask(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + 100);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE404, response.statusCode());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException, NotFoundException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        Task task2 = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(5));
        taskManager.createTask(task);
        taskManager.createTask(task2);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, taskManager.getAllTask().size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
        Task task2 = new Task("Test 2", "Testing task 1",
                Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(5));
        int id = taskManager.createTask(task);
        int id2 = taskManager.createTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode(), "Задача не удалена");
        assertEquals("\"Задача удалена\"", response.body(), "Задача не удалена");
        assertEquals(1, taskManager.getAllTask().size(), "Некорректное количество задач");
    }
}
