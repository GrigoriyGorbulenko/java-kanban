package InMemoryTaskManagerTest;

import TZ5.manager.Managers;
import TZ5.manager.TaskManager;
import TZ5.model.Epic;
import TZ5.model.Status;
import TZ5.model.SubTask;
import TZ5.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    @Test
    void createTask() {
        TaskManager taskManager = Managers.getDefault();
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
    void createAll() {
        TaskManager taskManagers = Managers.getDefault();
        Task task = new Task("Test createTask", "Test createTask description", Status.NEW);
        final Integer taskId = taskManagers.createTask(task);
        final Task savedTask = taskManagers.getTaskById(taskId);

        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        final Integer epicId = taskManagers.createEpic(epic);
        final Epic savedEpic = taskManagers.getEpicById(epicId);
        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epicId);
        final Integer subTaskId = taskManagers.createSubTask(subTask);
        final SubTask savedSubTask = taskManagers.getSubTaskById(subTaskId);


        taskManagers.createTask(task);
        taskManagers.createEpic(epic);
        taskManagers.createSubTask(subTask);


        assertEquals(task, savedTask);

        assertEquals(epic, savedEpic);

        assertEquals(subTask, savedSubTask);

        assertEquals(4, task.getId());

        assertEquals(5, epic.getId());

        assertEquals(6, subTask.getId());

        System.out.println(subTask.getId());
    }
}