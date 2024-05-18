package managertest;

import org.junit.jupiter.api.Test;
import tz5.manager.HistoryManager;
import tz5.manager.InMemoryTaskManager;
import tz5.manager.Managers;
import tz5.manager.TaskManager;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void createManagerShouldReturnNotNull() {

        assertNotNull(taskManager, "Объект инициализирован");
    }

}