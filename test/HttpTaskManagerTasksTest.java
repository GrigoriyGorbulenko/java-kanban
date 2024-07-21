import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tz.manager.InMemoryTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Status;
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
public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager(Managers.getHistoryManager());
    HttpTaskServer taskServer = new HttpTaskServer();
    Gson gson = HttpTaskServer.getGson();





    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        //taskManager.removeAllTasks();
        //taskManager.removeAllSubTasks();
        taskManager.removeAllEpics();
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
                Status.NEW,  LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }
}
