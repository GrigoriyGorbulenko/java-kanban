package TZ5.manager;

import TZ5.model.Task;

import java.util.List;

public interface HistoryManager {

    void addToTask(Task task);

    List<Task> getHistory();

}
