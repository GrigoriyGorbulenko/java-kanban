package inmemoryhistorymanagertest;


import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.SubTask;
import tz.model.Task;
import org.junit.jupiter.api.Test;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();


    @Test
    void addTaskToHistoryShouldReturnNotNull() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description", NEW);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        assertNotNull(taskManager.getHistory(), "История пустая.");
        assertEquals(taskManager.getHistory().getFirst(), task, "Не соответствует задаче по инцдексу");
    }

    @Test
    void addTaskShouldReturnNotNull() {
        Epic epic = new Epic("Test ", "");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTest", "", epic.getId());
        taskManager.createSubTask(subTask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        assertNotNull(taskManager.getHistory(), "История пуста");
    }

    @Test
    void deleteTaskShouldReturnNull() {
        Task task = new Task("Test ", "Test add ", NEW);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getHistory().getFirst(), "Таска не удалена");

        Epic epic = new Epic("Test ", "");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTest", "", epic.getId());
        taskManager.createSubTask(subTask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        assertNotNull(taskManager.getHistory(), "История пуста");
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getHistory().getLast(), "В истории остались таски");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManagerTest that = (InMemoryHistoryManagerTest) o;
        return Objects.equals(taskManager, that.taskManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskManager);
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManagerTest{" +
                "taskManager=" + taskManager +
                '}';
    }
}