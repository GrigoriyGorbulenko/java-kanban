package filebackedtaskmanager;

import org.junit.jupiter.api.Test;
import tz.manager.FileBackedTaskManager;
import tz.manager.Managers;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileBackedTaskManagerTest {

    @Test
    void saveAndLoadEmptyFile() {
         String header = "id,type,name,status,description,epic";

        try {
            File file = File.createTempFile("testFile", "csv");

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
            List<String> strings = Files.readAllLines(Paths.get(file.getPath()));
            assertEquals( 0,strings.size(), "Файл не пустой");
            assertEquals(0, fileBackedTaskManager.getAllTask().size(), "Файл не пустой");
            assertEquals(0, fileBackedTaskManager.getAllEpic().size(), "Файл не пустой");
            assertEquals(0, fileBackedTaskManager.getAllSubTask().size(), "Файл не пустой");
            Files.writeString(Path.of(file.getPath()), header);
            List<String> strings2 = Files.readAllLines(Paths.get(file.getPath()));
            assertEquals(header, strings2.getFirst(), "Первая строка не соответствует заголовку");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveMultipleTasks() {

        try {
            File file = File.createTempFile("testFile", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);

            fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW));
            fileBackedTaskManager.createEpic(new Epic("Купить дом", "долго", Status.NEW));
            fileBackedTaskManager.createEpic((new Epic("Переехать за границу", "долго", Status.DONE)));
            fileBackedTaskManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW, 2));
            fileBackedTaskManager.createSubTask((new SubTask("Выбрать дом", "быстро",Status.NEW, 2)));
            fileBackedTaskManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE, 3));

            assertEquals( 1,fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals( 2,fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals( 3,fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadMultipleTasks() {
        String header = "id,type,name,status,description,epic";
        try {
            File file = File.createTempFile("testFile", "csv");
            try (FileWriter write = new FileWriter(file)) {

                write.write("""
                id,type,name,status,description,epic
                1,TASK,Тест,NEW,сразу
                2,EPIC,Купить дом,NEW,долго
                3,EPIC,Переехать за границу,DONE,долго
                4,SUBTASK,Взять ипотеку,NEW,быстро,2""");
            }
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
            assertEquals( 1,fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals( 2,fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals( 1,fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
