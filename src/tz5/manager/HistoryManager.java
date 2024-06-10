package tz5.manager;

import tz5.model.Task;

import java.util.List;

public interface HistoryManager {

    void addToTask(Task task);
    void remove(int id);
    List<Task> getHistory();

}
