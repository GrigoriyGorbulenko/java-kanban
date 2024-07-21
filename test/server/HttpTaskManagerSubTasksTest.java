package server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tz.exception.NotFoundException;
import tz.manager.InMemoryTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.SubTask;
import tz.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz.model.Status.NEW;

public class HttpTaskManagerSubTasksTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.gson;

    public HttpTaskManagerSubTasksTest() throws IOException {
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
    public void testCreateSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        // конвертируем её в JSON
        String taskJson = gson.toJson(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = taskManager.getAllSubTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testCreateSubTaskFail() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Test1", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        // конвертируем её в JSON
        String taskJson = gson.toJson(subTask1);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode(), "Задачу удалось создать");
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        int id = taskManager.createSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        SubTask tasksFromManager = taskManager.getSubTaskById(id);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllSubTask().size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubTaskWithBadId() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        int id = taskManager.createSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + 100);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetAllSubTasks() throws IOException, InterruptedException, NotFoundException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = taskManager.getAllSubTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, taskManager.getAllSubTask().size(), "Некорректное количество задач");
        assertEquals("Test2", tasksFromManager.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 1);
        int id = taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode(), "Задача не удалена");
        assertEquals("\"Задача удалена\"", response.body(), "Задача не удалена");
        assertEquals(1, taskManager.getAllSubTask().size(), "Некорректное количество задач");
    }
}
