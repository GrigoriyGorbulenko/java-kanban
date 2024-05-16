package TZ5.manager;
import TZ5.model.Epic;
import TZ5.model.Status;
import TZ5.model.SubTask;
import TZ5.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private int nextId = 1;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public Integer createTask(Task newTask) {
        newTask.setId(nextId++);
        this.taskMap.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    @Override
    public Integer createSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        this.subTaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        syncTasks(epic);
        return subTask.getId();
    }

    @Override
    public Integer createEpic(Epic newEpic) {
        newEpic.setId(nextId++);
        this.epicMap.put(newEpic.getId(), newEpic);

        return newEpic.getId();
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {

        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTaskMap.clear();
        for (Epic epic : getAllEpic()) {
            epic.setSubTaskId(new ArrayList<>());
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void removeAllEpics() {
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addToTask(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.addToTask(subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.addToTask(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        syncTasks(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        syncTasks(epic);
    }

    @Override
    public void deleteTaskById(int id) {
        taskMap.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getEpicId());
        subTaskMap.remove(id);
        epic.getSubTaskId().removeIf(idSubTask -> idSubTask == id);
        syncTasks(epic);
    }

    @Override
    public void deleteEpicById(int id) {
        for (Integer idSubTask : epicMap.get(id).getSubTaskId()) {
            subTaskMap.remove(idSubTask);
        }
        epicMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void syncTasks(Epic newEpic) {
        int checkDone = 0;
        int checkNew = 0;
        for (Integer subtaskId : newEpic.getSubTaskId()) {
            SubTask newSubTask = subTaskMap.get(subtaskId);
            newSubTask.setEpicId(newEpic.getId());
            if (subTaskMap.get(subtaskId).getStatus() == Status.IN_PROGRESS) {
                newEpic.setStatus(Status.IN_PROGRESS);
            } else if (subTaskMap.get(subtaskId).getStatus() == Status.DONE) {
                checkDone++;
            }
            if (subTaskMap.get(subtaskId).getStatus() == Status.NEW) {
                checkNew++;
            }
        }
        if (checkDone == newEpic.getSubTaskId().size() && checkDone > 0) {
            newEpic.setStatus(Status.DONE);
        } else if (checkDone > 0 && checkDone < newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.IN_PROGRESS);
        } else if (checkNew == newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.NEW);
        }
    }

    @Override
    public String toString() {
        return "TZ5.manager.InMemoryTaskManager{" +
                "taskMap=" + taskMap +
                ", subTaskMap=" + subTaskMap +
                ", epicMap=" + epicMap +
                ", nextId=" + nextId +
                ", historyManager=" + historyManager +
                '}';
    }
}
