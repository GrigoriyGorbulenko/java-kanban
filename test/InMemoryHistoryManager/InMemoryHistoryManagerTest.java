package InMemoryHistoryManager;

import TZ5.manager.HistoryManager;
import TZ5.manager.Managers;
import TZ5.manager.TaskManager;
import TZ5.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static TZ5.model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
    TaskManager taskManager = Managers.getDefault();
    final int taskId = taskManager.createTask(task);
    HistoryManager historyManager = Managers.getHistoryManager();

    @Test
    void addToTask() {
        historyManager.addToTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}