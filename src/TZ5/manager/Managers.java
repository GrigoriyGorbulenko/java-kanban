package TZ5.manager;

import TZ5.manager.HistoryManager;
import TZ5.manager.InMemoryHistoryManager;
import TZ5.manager.InMemoryTaskManager;
import TZ5.manager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getHistoryManager());
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }


}
