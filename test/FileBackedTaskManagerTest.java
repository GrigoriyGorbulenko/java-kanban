import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tz.manager.FileBackedTaskManager;
import tz.manager.InMemoryTaskManager;
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
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    @Override
    FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Managers.getHistoryManager(), new File("testFile.csv"));
    }
    
    @Test
    void saveAndLoadEmptyFile() {
        String header = "id,type,name,status,description,epic";

        try {
            File file = File.createTempFile("testFile", "csv");

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
            List<String> strings = Files.readAllLines(Paths.get(file.getPath()));
            assertEquals(0, fileBackedTaskManager.getAllTask().size(), "Не удалось создать файл");
            assertEquals(0, fileBackedTaskManager.getAllEpic().size(), "Не удалось создать файл");
            assertEquals(0, fileBackedTaskManager.getAllSubTask().size(), "Не удалось создать файл");
            fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW));

            assertEquals(1, fileBackedTaskManager.getAllTask().size(), "Не удалось добавить задачу");
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
            fileBackedTaskManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW, 2)));
            fileBackedTaskManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE, 3));

            assertEquals(1, fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals(2, fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals(3, fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
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
            assertEquals(1, fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals(2, fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals(1, fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkLoadFromFile() {

        try {
            File file = File.createTempFile("testFile", "csv");
            FileBackedTaskManager fileManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
            fileManager.createTask(new Task("Тест", "сразу", Status.NEW));
            fileManager.createEpic(new Epic("Купить дом", "долго", Status.NEW));
            fileManager.createEpic((new Epic("Переехать за границу", "долго", Status.DONE)));
            fileManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW, 2));
            fileManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW, 2)));
            fileManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE, 3));
            FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);

            assertEquals(fileManager2, fileManager, "Файлы не соответствуют");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



