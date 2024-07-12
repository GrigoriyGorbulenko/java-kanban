import org.junit.jupiter.api.BeforeEach;
import tz.manager.HistoryManager;
import tz.manager.InMemoryTaskManager;
import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz.model.Status.NEW;

 class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    TaskManager taskManager;

     @Override
     InMemoryTaskManager createTaskManager() {
         return new InMemoryTaskManager(Managers.getHistoryManager());
     }

     @BeforeEach
     public void beforeEachTaskManager() {
         taskManager = Managers.getDefault();
     }

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
    void addEpicIntoEpicShouldReturnEpicId() {
        Epic epic = new Epic("Большая задача", "Уборка");
        Epic epic1 = new Epic("Большая задача2", "Уборка");
        taskManager.createEpic(epic1);
        final Integer epicId = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        epic.addSubTaskId(savedEpic.getId());
        epic.addSubTaskId(epic1.getId());
        assertNotNull(taskManager.getEpicById(epic.getSubTaskId().get(0)));
        assertNotNull(taskManager.getEpicById(epic.getSubTaskId().get(1)));
    }

    @Test
    void setEpicIdForSubTaskWithIdSubtaskShouldReturnSubtask() {
        Epic epic = new Epic("Большая задача", "Уборка");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Test createSubTask", "Test createSubTask description", Status.NEW);
        subTask.setEpicId(epic.getId());
        taskManager.createSubTask(subTask);
        subTask.setEpicId(subTask.getId());
        assertNotNull(taskManager.getEpicById(1), "Эпика не существует");
        assertNotNull(taskManager.getSubTaskById(subTask.getEpicId()), "Подзадачи не существует");
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

        assertEquals(task.getName(), "Test createNewTask");
        assertEquals(task.getDescription(), "Test createTask description");
        assertEquals(task.getStatus(), Status.NEW);
    }

    @Test
    void deleteSubtaskAlsoDeleteIdInEpic() {
        Epic epic = new Epic("Test ", "");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTest", "", epic.getId());
        taskManager.createSubTask(subTask);
        assertEquals(1, epic.getSubTaskId().size(), "У эпика нет подзадача");
        taskManager.deleteSubTaskById(subTask.getId());
        assertEquals(0, epic.getSubTaskId().size(), "У есть подзадача");
    }

    @Test
    void setNameTaskChangeTrue() {
        Task task = new Task("Test", "Test add ", NEW);
        taskManager.createTask(task);
        assertEquals("Test", task.getName(), "Имя не соответствует");
        task.setName("Test1");
        assertEquals("Test1", task.getName(), "Имя не соответствует");
    }
}
