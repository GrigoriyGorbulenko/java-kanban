package TZ5.manager;
import TZ5.model.Task;
import java.util.ArrayList;
import java.util.List;

import static TZ5.model.Status.NEW;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final static int MAX_HISTORY_SIZE = 10;

    @Override
    public void addToTask(Task task) {
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
