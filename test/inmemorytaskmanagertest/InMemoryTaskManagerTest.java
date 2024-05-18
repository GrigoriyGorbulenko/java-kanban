package inmemorytaskmanagertest;

import tz5.manager.HistoryManager;
import tz5.manager.InMemoryTaskManager;
import tz5.manager.Managers;
import tz5.manager.TaskManager;
import tz5.model.Epic;
import tz5.model.Status;
import tz5.model.SubTask;
import tz5.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void createTask() {

        Task task = new Task("Test createNewTask", "Test createTask description", Status.NEW);
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
    void createTaskAndSubTaskAndEpicWithIdShouldBeEqualsWithTypeOfTask() {

        Task task = new Task("Test createTask", "Test createTask description", Status.NEW);


        final Integer taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        final Integer epicId = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epicId);
        final Integer subTaskId = taskManager.createSubTask(subTask);
        final SubTask savedSubTask = taskManager.getSubTaskById(subTaskId);


        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);


        assertEquals(task, savedTask);

        assertEquals(epic, savedEpic);

        assertEquals(subTask, savedSubTask);

        assertEquals(4, task.getId());

        assertEquals(5, epic.getId());

        assertEquals(6, subTask.getId());

        System.out.println(subTask.getId());
    }
    @Test
    void addEpicIntoEpicShouldReturnNull() {
        Epic epic = new Epic("Большая задача", "Уборка");
        taskManager.createEpic(epic);
        final Integer epicId = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epicId);
        taskManager.createSubTask(subTask);
        epic.setSubTaskId(new ArrayList<>() {
            {
                add(subTask.getId());
                add(savedEpic.getId());
            }
            });
        assertNull(taskManager.getSubTaskById(2), "Такой подзадачи не существует");
    }

    @Test
    void setEpicIdForSubTaskWithIdSubtaskShouldReturnNull() {
        Epic epic = new Epic("Большая задача", "Уборка");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epic.getId());
        subTask.getEpicId();
        assertNull(taskManager.getEpicById(0), "Эпика не существует");
    }

    @Test
    void createDifferentTaskAndFindByIdShouldReturnTrue() {
        Task task = new Task("Test createTask", "Test createTask description", Status.NEW);
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epic.getId());
        taskManager.createTask(task);
        taskManager.createSubTask(subTask);
        epic.setSubTaskId(new ArrayList<>() {
            {
                add(subTask.getId());
            }
        });

        assertEquals(task.getClass(), Task.class, "Не соответствует классу");
        assertEquals(epic.getClass(), Epic.class, "Не соответствует классу");
        assertEquals(subTask.getClass(), SubTask.class, "Не соответствует классу");

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не соответствует своему Id");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Задача не соответствует своему Id");
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()), "Задача не соответствует своему Id");

        assertEquals(task.getId(), taskManager.getTaskById(2).getId(), "Заданное id не соответствует");
        assertEquals(epic.getId(), taskManager.getEpicById(1).getId(), "Заданное id не соответствует");
        assertEquals(subTask.getId(), taskManager.getSubTaskById(3).getId(), "Заданное id не соответствует");
    }

    @Test
    void taskFieldsEquals() {
        Task task = new Task("Test createNewTask", "Test createTask description", Status.NEW);
        taskManager.createTask(task);

        assertEquals(task.getName(), "Test createNewTask" );
        assertEquals(task.getDescription(), "Test createTask description");
        assertEquals(task.getStatus(), Status.NEW);
    }
}
