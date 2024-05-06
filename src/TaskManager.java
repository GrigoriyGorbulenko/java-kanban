import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskManager {

    private  HashMap<Integer, Task> task = new HashMap<>();
    private  HashMap<Integer, SubTask> subTask= new HashMap<>();
    private  HashMap<Integer, Epic> epic= new HashMap<>();

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

        return newEpic.getId();
    }

    private void syncTasks(Epic newEpic) {
        for (Integer subtaskId : newEpic.getSubTaskId()) {
            SubTask newSubTask = subTask.get(subtaskId);
            newSubTask.epicId = newEpic.getId();
        }
    }

    public List<Integer> getAllTask() {
        ArrayList<Integer> taskIdList = new ArrayList<>();
        for(Task one : task.values()) {
          taskIdList.add(one.getId());
        }
        return taskIdList;
    }

    public List<Integer> getAllSubTask() {
        ArrayList<Integer> subTaskIdList = new ArrayList<>();
        for(SubTask one : subTask.values()) {
            subTaskIdList.add(one.getId());
        }
        return subTaskIdList;
    }

    public List<Integer> getAllEpic() {
        ArrayList<Integer> epicIdList = new ArrayList<>();
        for(Epic one : epic.values()) {
            epicIdList.add(one.getId());
        }
        return epicIdList;
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


    public SubTask getSubTaskById(int newId) {
        SubTask check = null;
        for(SubTask one : subTask.values()) {
            if(one.getId()== newId)
                check = one;
        }
        return check;
    }

    public Task getEpicById(int newId) {
        Epic check = null;
        for(Epic one : epic.values()) {
            if(one.getId() == newId)
                check = one;
        }
        return check;
    }

    public Task getTaskById(int newId) {
        Task check = null;
        for(Task one : task.values()) {
            if(one.id == newId)
                check = one;
        }
        return check;
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
