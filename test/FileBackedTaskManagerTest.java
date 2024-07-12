import org.junit.jupiter.api.Test;
import tz.manager.FileBackedTaskManager;

import tz.manager.Managers;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Managers.getHistoryManager(), new File("testFile.csv"));
    }
    
    @Test
    void saveAndLoadEmptyFile() {
        String header = "id,type,name,status,description,startTime,endTime,duration,epic";

        try {
            File file = File.createTempFile("testFile", "csv");

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
            List<String> strings = Files.readAllLines(Paths.get(file.getPath()));
            assertEquals(0, fileBackedTaskManager.getAllTask().size(), "Не удалось создать файл");
            assertEquals(0, fileBackedTaskManager.getAllEpic().size(), "Не удалось создать файл");
            assertEquals(0, fileBackedTaskManager.getAllSubTask().size(), "Не удалось создать файл");
            fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW,
                    LocalDateTime.now().minusHours(5), Duration.ofMinutes(130)));

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

            fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW,
                    LocalDateTime.now().minusHours(5), Duration.ofMinutes(130)));
            fileBackedTaskManager.createEpic(new Epic("Купить дом", "долго"));
            fileBackedTaskManager.createEpic((new Epic("Переехать за границу", "долго")));
            fileBackedTaskManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW,
                    LocalDateTime.now(), Duration.ofMinutes(30), 2));
            fileBackedTaskManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW,
                    LocalDateTime.now().minusHours(25), Duration.ofMinutes(30), 2)));
            fileBackedTaskManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE,
                    LocalDateTime.now().minusHours(35), Duration.ofMinutes(30), 3));

            assertEquals(1, fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals(2, fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals(3, fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadMultipleTasks() {
        String header = "id,type,name,status,description,startTime,endTime,duration,epic";
        try {
            File file = File.createTempFile("testFile", "csv");
            try (FileWriter write = new FileWriter(file)) {

                write.write("""
                        id,type,name,status,description,startTime,endTime,duration,epic
                        1,TASK,Задача,NEW,Описание задачи,2024-11-08T20:41:36.631908300,2024-07-08T20:43:36.631908300,PT2M,
                        2,EPIC,Эпик,NEW,Описание эпика,2024-10-08T21:41:36.631908300,2024-10-08T20:43:36.631908300,PT2M
                        3,SUBTASK,Подзадача,NEW,Описание подзадачи,2024-10-08T22:41:36.631908300,2024-10-08T20:43:36.631908300,PT2M,2""");
            }
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
            assertEquals(1, fileBackedTaskManager.getAllTask().size(), "Количество задач неверное");
            assertEquals(1, fileBackedTaskManager.getAllEpic().size(), "Количество задач неверное");
            assertEquals(1, fileBackedTaskManager.getAllSubTask().size(), "Количество задач неверное");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkLoadFromFile() {

        try {
            File file = File.createTempFile("testFile", "csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);
            fileBackedTaskManager.createTask(new Task("Тест", "сразу", Status.NEW,
                    LocalDateTime.now().minusHours(5), Duration.ofMinutes(130)));
            fileBackedTaskManager.createEpic(new Epic("Купить дом", "долго"));
            fileBackedTaskManager.createEpic((new Epic("Переехать за границу", "долго")));
            fileBackedTaskManager.createSubTask(new SubTask("Взять ипотеку", "быстро", Status.NEW,
                    LocalDateTime.now(), Duration.ofMinutes(30), 2));
            fileBackedTaskManager.createSubTask((new SubTask("Выбрать дом", "быстро", Status.NEW,
                    LocalDateTime.now().minusHours(25), Duration.ofMinutes(30), 2)));
            fileBackedTaskManager.createSubTask(new SubTask("Выбрать страну", "быстро", Status.DONE,
                    LocalDateTime.now().minusHours(35), Duration.ofMinutes(30), 3));
            FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);

            assertEquals(fileManager2, fileBackedTaskManager, "Файлы не соответствуют");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



