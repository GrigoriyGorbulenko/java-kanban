package tz5.manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getHistoryManager());
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }


}
