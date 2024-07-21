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
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;
import tz.server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tz.model.Status.NEW;

public class HttpTaskManagerPrioritizedTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.gson;

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        int id = taskManager.createEpic(epic);
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description2");
        int id2 = taskManager.createEpic(epic2);

        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(55),
                Duration.ofMinutes(2), 2);
        int id3 = taskManager.createSubTask(subTask);
        int id4 = taskManager.createSubTask(subTask2);

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(3), Duration.ofMinutes(5));
        Task task2 = new Task("Test 1", "Testing task 1",
                Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(5));
        int id5 = taskManager.createTask(task);
        int id6 = taskManager.createTask(task2);

        taskManager.getTaskById(id5);
        taskManager.getTaskById(id6);
        taskManager.getSubTaskById(id3);
        taskManager.getSubTaskById(id4);
        taskManager.getEpicById(id);
        taskManager.getEpicById(id2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> priority = taskManager.getPrioritizedTasks();

        assertEquals(4, priority.size(), "Некорректное количество задач");
    }
}
