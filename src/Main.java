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
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("fileTaskManager.csv");

        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
        fileManager.createTask(new Task("Тест", "сразу", Status.NEW));
        fileManager.createEpic(new Epic("Купить дом", "долго", Status.NEW));
        fileManager.createEpic((new Epic("Переехать за границу", "долго", Status.DONE)));
        fileManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW, 2));
        fileManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW, 2)));
        fileManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE, 3));

        fileManager.getEpicById(2);
        System.out.println(fileManager.getHistory());
        readFile();
        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(file);
        readFile();
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
        for (String string : strings) {
            System.out.println(string);
        }
    }
}
