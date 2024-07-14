import tz.manager.FileBackedTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("fileTaskManager.csv");

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
        fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW, LocalDateTime.now().minusHours(5), Duration.ofMinutes(130)));
        fileBackedTaskManager.createEpic(new Epic("Купить дом", "долго"));
        fileBackedTaskManager.createEpic((new Epic("Переехать за границу", "долго")));
        fileBackedTaskManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30), 2));
        fileBackedTaskManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW, LocalDateTime.now().minusHours(25), Duration.ofMinutes(30), 2)));
        fileBackedTaskManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE, LocalDateTime.now().minusHours(35), Duration.ofMinutes(30), 3));
        readFile();
        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileBackedTaskManager.equals(fileManager2));
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);

            for (Integer i : epic.getSubTaskId()) {
                System.out.println("--> " + manager.getSubTaskById(i));
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getAllSubTask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void readFile() throws IOException {

        List<String> strings = Files.readAllLines(Paths.get("fileTaskManager.csv"));
        strings.forEach(System.out::println);
    }
}
