import com.google.gson.Gson;
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
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
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
        assertEquals(200, response.statusCode());

        Task tasksFromManager = taskManager.getTaskById(id);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllTask().size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getName(), "Некорректное имя задачи");
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
        assertEquals(200, response.statusCode());

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
        assertEquals(200, response.statusCode(), "Задача не удалена");
        assertEquals("\"Задача удалена\"", response.body(), "Задача не удалена");
        assertEquals(1, taskManager.getAllTask().size(), "Некорректное количество задач");
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
        assertEquals(201, response.statusCode());

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
        assertEquals(200, response.statusCode());

        Epic tasksFromManager = taskManager.getEpicById(id);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllEpic().size(), "Некорректное количество задач");
        assertEquals("Test createEpic", tasksFromManager.getName(), "Некорректное имя задачи");
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
        assertEquals(200, response.statusCode());

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
        URI url = URI.create("http://localhost:8080/epics" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        assertEquals("\"Задача удалена\"", response.body());
        assertEquals(1, taskManager.getAllEpic().size(), "Некорректное количество задач");
    }
    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        int id = taskManager.createEpic(epic);
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description2");
        int id2 = taskManager.createEpic(epic2);

        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 2);
        int id3 = taskManager.createSubTask(subTask);
        int id4 = taskManager.createSubTask(subTask2);

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5));
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

        assertEquals(6, taskManager.getHistory().size(), "Некорректное количество задач");
    }


}
