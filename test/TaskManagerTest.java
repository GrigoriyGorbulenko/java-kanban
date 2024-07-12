import tz.manager.TaskManager;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    abstract T createTaskManager();
}
