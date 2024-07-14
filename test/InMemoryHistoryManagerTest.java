import tz.manager.Managers;
import tz.manager.TaskManager;
import tz.model.Epic;
import tz.model.Status;
import tz.model.SubTask;
import tz.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();


    @Test
    void addTaskToHistoryShouldReturnNotNull() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        Task task2 = new Task("Test2 ", " ", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        assertNotNull(taskManager.getHistory(), "История пустая.");
        assertEquals(taskManager.getHistory().getFirst(), task, "Не соответствует задаче по инцдексу");
    }

    @Test
    void addTaskShouldReturnNotNull() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2), 1);
        taskManager.createSubTask(subTask);
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        assertNotNull(taskManager.getHistory(), "История пуста");
    }

    @Test
    void deleteTaskShouldReturnNull() {
        Task task = new Task("Test ", " ", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getHistory().getFirst(), "Таска не удалена");

        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Test ", " ", Status.NEW, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(2), 2);
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