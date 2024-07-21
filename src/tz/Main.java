package tz;

import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;
import tz.server.HttpTaskServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static tz.model.Status.NEW;
import static tz.server.HttpTaskServer.taskManager;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        Task task = new Task("Test t", "Testing task 2",
                Status.NEW, LocalDateTime.now().minusHours(2), Duration.ofMinutes(5));
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);

        taskManager.createTask(task);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2),
                1);
        SubTask subTask2 = new SubTask("Test2 ", " ", NEW, LocalDateTime.now().plusMinutes(22),
                Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);


    }
}
