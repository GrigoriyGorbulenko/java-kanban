package inmemoryhistorymanagertest;

import tz5.manager.HistoryManager;
import tz5.manager.Managers;
import tz5.manager.TaskManager;
import tz5.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tz5.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getHistoryManager();

    @Test
    void addTaskToHistoryShouldReturnNotNull() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description", NEW);
        historyManager.addToTask(task);
        historyManager.addToTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(history.getFirst(), task, "Не соответствует задаче по инцдексу");
    }
}