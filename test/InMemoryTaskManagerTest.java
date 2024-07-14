import tz.manager.InMemoryTaskManager;
import tz.manager.Managers;

import tz.model.Epic;
import tz.model.SubTask;
import tz.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz.model.Status.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

     @Override
     protected InMemoryTaskManager createTaskManager() {
         return new InMemoryTaskManager(Managers.getHistoryManager());
     }

    @Test
    void addEpicIntoEpicShouldReturnEpicId() {
        Epic epic = new Epic("Большая задача", "Уборка");
        Epic epic2 = new Epic("Большая задача2", "Уборка");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        epic2.setStartTime(LocalDateTime.now().plusMinutes(15));
        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        epic.addSubTaskId(savedEpic.getId());
        epic.addSubTaskId(epic2.getId());
        assertNotNull(taskManager.getEpicById(epic.getSubTaskId().get(0)));
        assertNotNull(taskManager.getEpicById(epic.getSubTaskId().get(1)));
    }

    @Test
    void setEpicIdForSubTaskWithIdSubtaskShouldReturnSubtask() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(2), epic.getId());
        taskManager.createSubTask(subTask);
        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпика не существует");
        assertNotNull(taskManager.getSubTaskById(subTask.getId()), "Подзадачи не существует");
    }

    @Test
    void createDifferentTaskAndFindByIdShouldReturnTrue() {
        Task task = new Task("Test ", " ", NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(2));
        taskManager.createTask(task);
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(2), epic.getId());
        taskManager.createSubTask(subTask);


        assertEquals(task.getClass(), Task.class, "Не соответствует классу");
        assertEquals(epic.getClass(), Epic.class, "Не соответствует классу");
        assertEquals(subTask.getClass(), SubTask.class, "Не соответствует классу");

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не соответствует своему Id");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Задача не соответствует своему Id");
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()), "Задача не соответствует своему Id");

        assertEquals(task.getId(), taskManager.getTaskById(1).getId(), "Заданное id не соответствует");
        assertEquals(epic.getId(), taskManager.getEpicById(2).getId(), "Заданное id не соответствует");
        assertEquals(subTask.getId(), taskManager.getSubTaskById(3).getId(), "Заданное id не соответствует");
    }

    @Test
    void taskFieldsEquals() {
        Task task = new Task("Test", "Test createTask description", NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(2));
        taskManager.createTask(task);

        assertEquals(task.getName(), "Test", "Имя задачи не соответствуе");
        assertEquals(task.getDescription(), "Test createTask description", "Описание задачи не соответствуе");
        assertEquals(task.getStatus(), NEW, "Статус задачи не соответствуе");
    }

    @Test
    void deleteSubtaskAlsoDeleteIdInEpic() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusMinutes(10));
        SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now().plusMinutes(25), Duration.ofMinutes(2), epic.getId());
        taskManager.createSubTask(subTask);
        assertEquals(1, epic.getSubTaskId().size(), "У эпика нет подзадача");
        taskManager.deleteSubTaskById(subTask.getId());
        assertEquals(0, epic.getSubTaskId().size(), "У есть подзадача");
    }

    @Test
    void setNameTaskChangeTrue() {
        Task task = new Task("Test", "Test createTask description", NEW, LocalDateTime.now().plusMinutes(1), Duration.ofMinutes(2));
        taskManager.createTask(task);
        assertEquals("Test", task.getName(), "Имя не соответствует");
        task.setName("Test1");
        assertEquals("Test1", task.getName(), "Имя не соответствует");
    }

     @Test
     public void checkStatusEpicWhenAllSubtaskNew() {
         Epic epic = new Epic("Test createEpic", "Test createEpic description");
         taskManager.createEpic(epic);
         epic.setStartTime(LocalDateTime.now().plusMinutes(10));
         SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
         SubTask subTask2 = new SubTask("Test2 ", " ", NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
         taskManager.createSubTask(subTask);
         taskManager.createSubTask(subTask2);
         assertSame(epic.getStatus(), NEW);
     }

     @Test
     public void checkStatusEpicWhenAllSubtaskDone() {
         Epic epic = new Epic("Test createEpic", "Test createEpic description");
         taskManager.createEpic(epic);
         epic.setStartTime(LocalDateTime.now().plusMinutes(10));
         SubTask subTask = new SubTask("Test ", " ", DONE, LocalDateTime.now(), Duration.ofMinutes(2), 1);
         SubTask subTask2 = new SubTask("Test2 ", " ", DONE, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
         taskManager.createSubTask(subTask);
         taskManager.createSubTask(subTask2);
         assertSame(epic.getStatus(), DONE);
     }

     @Test
     public void checkStatusEpicWhenSubtaskDoneAndSubtaskNew() {
         Epic epic = new Epic("Test createEpic", "Test createEpic description");
         taskManager.createEpic(epic);
         epic.setStartTime(LocalDateTime.now().plusMinutes(10));
         SubTask subTask = new SubTask("Test ", " ", NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
         SubTask subTask2 = new SubTask("Test2 ", " ", DONE, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
         taskManager.createSubTask(subTask);
         taskManager.createSubTask(subTask2);
         assertSame(epic.getStatus(), IN_PROGRESS);
     }

     @Test
     public void checkStatusEpicWhenAllSubtaskInProgress() {
         Epic epic = new Epic("Test createEpic", "Test createEpic description");
         taskManager.createEpic(epic);
         epic.setStartTime(LocalDateTime.now().plusMinutes(10));
         SubTask subTask = new SubTask("Test ", " ", IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(2), 1);
         SubTask subTask2 = new SubTask("Test2 ", " ", IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 1);
         taskManager.createSubTask(subTask);
         taskManager.createSubTask(subTask2);
         assertSame(epic.getStatus(), IN_PROGRESS);
     }
}
