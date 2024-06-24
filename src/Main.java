import tz.manager.FileBackedTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.SubTask;
import tz.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("test.txt");

        FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
        fileManager.createEpic(new Epic("Купить дом", ""));
        fileManager.createEpic((new Epic("Переехать за границу", "")));
        fileManager.createSubTask(new SubTask("Взять ипотеку", "", 1));
        fileManager.createSubTask((new SubTask("Выбрать дом", "", 1)));
        fileManager.createSubTask(new SubTask("Выбрать страну", "", 2));
        fileManager.createTask(new Task("Тест", ""));
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
        List<String> strings = Files.readAllLines(Paths.get("test.txt"));
        for (String string : strings) {
            System.out.println(string);
        }
    }
}
