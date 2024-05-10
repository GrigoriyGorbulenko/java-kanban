import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {

    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private int nextId = 1;

    public int createTask(Task newTask) {
        newTask.setId(nextId++);
        this.taskMap.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int createSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        this.subTaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        syncTasks(epic);
        return subTask.getId();
    }

    public int createEpic(Epic newEpic) {
        newEpic.setId(nextId++);
        this.epicMap.put(newEpic.getId(), newEpic);

        return newEpic.getId();
    }

    public ArrayList<Task> getAllTask() {
    return new ArrayList<>(taskMap.values());
    }

    public ArrayList<SubTask> getAllSubTask() {

        return new ArrayList<>(subTaskMap.values());
    }

    public ArrayList<Epic>  getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    public void removeAllTasks() {
        taskMap.clear();
    }

    public void removeAllSubTasks() {
        subTaskMap.clear();
        for (Epic epic : getAllEpic()) {
            epic.setSubTaskId(new ArrayList<>());
            epic.setStatus(Status.NEW);
        }
    }

    public void removeAllEpics() {
        epicMap.clear();
        subTaskMap.clear();
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicMap.get(id);
    }

    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        Epic epic= epicMap.get(subTask.getEpicId());
        syncTasks(epic);
    }

    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        syncTasks(epic);
    }

    public void deleteTaskById(int id) {
        taskMap.remove(id);
    }

    public void deleteSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getEpicId());
        subTaskMap.remove(id);
        epic.getSubTaskId().removeIf(idSubTask -> idSubTask == id);
        syncTasks(epic);
    }

    public void deleteEpicById(int id) {
        for (Integer idSubTask : epicMap.get(id).getSubTaskId()) {
            subTaskMap.remove(idSubTask);
        }
        epicMap.remove(id);
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
            } if(subTaskMap.get(subtaskId).getStatus() == Status.NEW) {
                checkNew++;
            }
        }
        if (checkDone == newEpic.getSubTaskId().size() && checkDone > 0 ) {
            newEpic.setStatus(Status.DONE);
        } else if( checkDone > 0 && checkDone < newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.IN_PROGRESS);
        } else if (checkNew == newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.NEW);
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "task=" + taskMap +
                ", epic=" + epicMap +
                ", subTask=" + subTaskMap +
                ", nextId=" + nextId +
                '}';
    }
}
