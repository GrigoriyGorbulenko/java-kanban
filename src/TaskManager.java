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
        deleteSubTask();
    }

    public void removeAllEpics() {
        epicMap.clear();
        deleteSubTask();
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

    public void updateSubTask(SubTask updateSubTask) {
        subTaskMap.put(updateSubTask.getId(), updateSubTask);
        Epic updateEpic = epicMap.get(updateSubTask.getEpicId());
        syncTasks(updateEpic);
    }

    public void updateEpic(Epic updateEpic) {
        epicMap.put(updateEpic.getId(), updateEpic);
        syncTasks(updateEpic);
    }

    public void deleteTaskById(int id) {
        taskMap.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTaskMap.remove(id);
        syncTasks(getEpicById(getSubTaskById(id).getEpicId()));
    }

    public void deleteEpicById(int id) {
        epicMap.remove(id);
    }

    private void deleteSubTask(){
        for (Epic epic: getAllEpic()) {
            epic.setSubTaskId(new ArrayList<>());
            epic.setStatus(Status.NEW);
        }
    }

    private void syncTasks(Epic newEpic) {
        int check = 0;
        for (Integer subtaskId : newEpic.getSubTaskId()) {
            SubTask newSubTask = subTaskMap.get(subtaskId);
            newSubTask.setEpicId(newEpic.getId());
            if (subTaskMap.get(subtaskId).getStatus() == Status.IN_PROGRESS) {
                newEpic.setStatus(Status.IN_PROGRESS);
            } else if (subTaskMap.get(subtaskId).getStatus() == Status.DONE) {
                check++;
            }
        }
        if (check == newEpic.getSubTaskId().size() ) {
            newEpic.setStatus(Status.DONE);
        } else if( check > 0 && check < newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.IN_PROGRESS);
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
