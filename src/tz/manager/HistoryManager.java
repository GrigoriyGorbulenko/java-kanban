package tz.manager;

import tz.model.Task;

import java.util.List;

public interface HistoryManager {

    void addToTask(Task task);
    void remove(int id);
    List<Task> getHistory();

}
