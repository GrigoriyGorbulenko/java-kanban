package managertest;

import org.junit.jupiter.api.Test;
import tz.manager.Managers;
import tz.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void createManagerShouldReturnNotNull() {

        assertNotNull(taskManager, "Объект инициализирован");
    }

}