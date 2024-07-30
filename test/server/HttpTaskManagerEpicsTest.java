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
import static tz.server.support.ConstantStatusCode.*;

public class HttpTaskManagerEpicsTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.gson;

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testCreateEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        epic.setDuration(Duration.ofMinutes(30));
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test createEpic", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        int id = taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        taskManager.createSubTask(subTask);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode());

        Epic tasksFromManager = taskManager.getEpicById(id);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllEpic().size(), "Некорректное количество задач");
        assertEquals("Test createEpic", tasksFromManager.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicWithBadId() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        taskManager.createSubTask(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 100);
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
    public void testGetSubTasksWithBadEpicId() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        taskManager.createSubTask(subTask);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 100 +"/subtasks");
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
    public void testGetAllEpics() throws IOException, InterruptedException, NotFoundException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description2");
        taskManager.createEpic(epic2);

        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 2);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, taskManager.getAllEpic().size(), "Некорректное количество задач");
        assertEquals("Test createEpic2", tasksFromManager.getLast().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        int id = taskManager.createEpic(epic);
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description2");
        taskManager.createEpic(epic2);

        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 2);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(CODE200, response.statusCode());
        assertEquals("\"Задача удалена\"", response.body());
        assertEquals(1, taskManager.getAllEpic().size(), "Некорректное количество задач");
    }
}
