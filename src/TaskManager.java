import java.util.HashMap;
import java.util.Objects;

public class TaskManager {

    private HashMap<Integer, Task> task = new HashMap<>();
    private HashMap<Integer, SubTask> subTask = new HashMap<>();
    private HashMap<Integer, Epic> epic = new HashMap<>();

    private int nextId = 1;


    public int createTask(Task newTask) {
        newTask.setId(nextId++);
        this.task.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int createSubTask(SubTask subTask) {
        subTask.setId(nextId++);
        this.subTask.put(subTask.getId(), subTask);

        return subTask.getId();
    }

    public int createEpic(Epic newEpic) {
        newEpic.setId(nextId++);
        this.epic.put(newEpic.getId(), newEpic);

        syncTasks(newEpic);
        if (newEpic.getSubTaskId().isEmpty()) {
            newEpic.setStatus(Status.NEW);
        }
        return newEpic.getId();
    }

    private void syncTasks(Epic newEpic) {
        int check = 0;
        for (Integer subtaskId : newEpic.getSubTaskId()) {
            SubTask newSubTask = subTask.get(subtaskId);
            newSubTask.setEpicId(newEpic.getId());
            if (subTask.get(subtaskId).getStatus() == Status.IN_PROGRESS) {
                newEpic.setStatus(Status.IN_PROGRESS);
            } else if (subTask.get(subtaskId).getStatus() == Status.DONE) {
                check++;
            }
        }
        if (check == newEpic.getSubTaskId().size() ) {
            newEpic.setStatus(Status.DONE);
        } else if( check > 0 && check < newEpic.getSubTaskId().size()) {
            newEpic.setStatus(Status.IN_PROGRESS);
        }
    }

    public  HashMap<Integer, Task> getAllTask() {
        return task;

    }

    public HashMap<Integer, SubTask>  getAllSubTask() {
        return subTask;
    }

    public HashMap<Integer, Epic>  getAllEpic() {
        return epic;
    }

    public void removeAllTask() {
        task.clear();
    }

    public void removeAllSubTask() {
        subTask.clear();
    }

    public void removeAllEpic() {
        epic.clear();
    }

    public Task getTaskById(int newId) {
        return task.get(newId);
    }

    public SubTask getSubTaskById(int newId) {
        return subTask.get(newId);
    }

    public Epic getEpicById(int newId) {
        return epic.get(newId);
    }


    public void updateTask(Task updateTask) {
        task.put(updateTask.getId(), updateTask);
    }

    public void updateSubTask(SubTask updateSubTask) {
        subTask.put(updateSubTask.getId(), updateSubTask);
        Epic updateEpic = epic.get(updateSubTask.getEpicId());
        syncTasks(updateEpic);
    }

    public void updateEpic(Epic updateEpic) {
        epic.put(updateEpic.getId(), updateEpic);
        syncTasks(updateEpic);
    }

    public void deleteTaskById(int newId) {
        task.remove(newId);
    }

    public void deleteSubTaskById(int newId) {
        subTask.remove(newId);
    }

    public void deleteEpicById(int newId) {
        epic.remove(newId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskManager that = (TaskManager) o;
        return nextId == that.nextId &&
                Objects.equals(task, that.task) &&
                Objects.equals(epic, that.epic) &&
                Objects.equals(subTask, that.subTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, epic, subTask, nextId);
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "task=" + task +
                ", epic=" + epic +
                ", subTask=" + subTask +
                ", nextId=" + nextId +
                '}';
    }
}
