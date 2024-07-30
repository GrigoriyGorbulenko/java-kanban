import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tz.exception.NotFoundException;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void createManager() {
        taskManager = createTaskManager();
    }

    @Test
    void createTask() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        final Integer taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createSubTask() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        final Integer taskId = taskManager.createSubTask(subTask);

        final SubTask savedSubTask = taskManager.getSubTaskById(taskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final ArrayList<SubTask> subTasks = taskManager.getAllSubTask();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now());

        final Integer taskId = taskManager.createTask(epic);

        final Task savedEpic = taskManager.getTaskById(taskId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Task> epics = taskManager.getAllTask();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void getAllTask() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTask().size());
    }

    @Test
    void getAllSubTask() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        SubTask subTask2 = new SubTask("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTask().size());
    }

    @Test
    void getAllEpic() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now());
        epic2.setStartTime(LocalDateTime.now().plusMinutes(30));
        assertEquals(2, taskManager.getAllEpic().size());
    }

    @Test
    void removeAllTasks() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTask().size());
        taskManager.removeAllTasks();
        try {
            assertEquals(0, taskManager.getAllTask().size());
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException e) {
            assertNotEquals("", e.getMessage());
        }
    }

    @Test
    void removeAllSubTasks() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        SubTask subTask2 = new SubTask("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTask().size());
        taskManager.removeAllSubTasks();
        try {
            assertEquals(0, taskManager.getAllSubTask().size());
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException e) {
            assertNotEquals("", e.getMessage());
        }
    }

    @Test
    void removeAllEpics() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now());
        epic2.setStartTime(LocalDateTime.now().plusMinutes(30));
        assertEquals(2, taskManager.getAllEpic().size());
        taskManager.removeAllEpics();
        try {
            assertEquals(0, taskManager.getAllEpic().size());
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException e) {
            assertNotEquals("", e.getMessage());
        }
    }

    @Test
    void getTaskById() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        final Integer taskId = taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
    }

    @Test
    void getSubTaskById() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        final Integer taskId = taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTaskById(taskId));
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        final Integer taskId = taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now());
        assertEquals(epic, taskManager.getEpicById(taskId));
    }

    @Test
    void updateTask() {
        Task task = new Task("Test", "Старая ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.createTask(task);
        task = new Task("Test2 ", "Новая", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(2));
        taskManager.updateTask(task);
        assertNotEquals("Test", task.getDescription());
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        subTask = new SubTask("Test1", " ", Status.NEW, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(30), 1);
        taskManager.updateSubTask(subTask);
        assertNotEquals("Test", subTask.getDescription());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now());
        epic = new Epic("Test createEpic1", "Test createEpic description");
        epic.setStartTime(LocalDateTime.now().plusMinutes(55));
        assertNotEquals("Test createEpic", epic.getDescription());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTask().size());
        taskManager.deleteTaskById(task.getId());
        assertEquals(1, taskManager.getAllTask().size());
    }

    @Test
    void deleteSubTaskById() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        SubTask subTask2 = new SubTask("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        assertEquals(2, taskManager.getAllSubTask().size());
        taskManager.deleteSubTaskById(subTask.getId());
        assertEquals(1, taskManager.getAllSubTask().size());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now());
        epic2.setStartTime(LocalDateTime.now().plusMinutes(30));
        assertEquals(2, taskManager.getAllEpic().size());
        taskManager.deleteEpicById(epic.getId());
        assertEquals(1, taskManager.getAllEpic().size());
    }

    @Test
    void getPrioritizedTasks() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        epic2.setStartTime(LocalDateTime.now().plusMinutes(15));
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(2), epic.getId());
        SubTask subTask2 = new SubTask("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(2), epic2.getId());
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task);
        expectedList.add(task2);
        expectedList.add(subTask);
        expectedList.add(subTask2);
        assertEquals(expectedList, taskManager.getPrioritizedTasks(), "Сортировка не прошла");
    }

    @Test
    void getHistory() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic epic2 = new Epic("Test createEpic2", "Test createEpic description");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        epic2.setStartTime(LocalDateTime.now().plusMinutes(15));
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(2), epic.getId());
        SubTask subTask2 = new SubTask("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(2), epic2.getId());
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task);
        expectedList.add(task2);
        expectedList.add(subTask);
        expectedList.add(subTask2);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getSubTaskById(subTask2.getId());

        assertEquals(expectedList, taskManager.getHistory());
    }
}
