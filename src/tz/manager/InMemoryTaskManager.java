package tz.manager;
import tz.exception.ConflictTimeException;
import tz.exception.NotFoundException;
import tz.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicMap = new HashMap<>();

    protected final Set<Task> tasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int nextId = 1;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public Integer createTask(Task newTask) {
        if (getConflictTime(newTask)) {
            throw new ConflictTimeException("В указанное время уже есть задача");
        }
        newTask.setId(nextId++);
        newTask.setTypeofTask(TypeofTask.TASK);
        this.taskMap.put(newTask.getId(), newTask);
        updateTasksSet();
        return newTask.getId();
    }

    @Override
    public Integer createSubTask(SubTask subTask) {
        if (getConflictTime(subTask)) {
            throw new ConflictTimeException("В указанное время уже есть задача");
        }
        subTask.setId(nextId++);
        subTask.setTypeofTask(TypeofTask.SUBTASK);
        this.subTaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        epic.addSubTaskId(subTask);
        syncTasks(epic);
        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subTask.getStartTime())) {
            epic.setStartTime(subTask.getStartTime());
        }
        if (epic.getEndTime() == null || epic.getEndTime().isBefore(subTask.getEndTime())) {
            epic.setEndTime(subTask.getEndTime());
        }
        if (epic.getDuration() == null) {
            epic.setDuration((subTask.getDuration()));
        } else {
            epic.setDuration(epic.getDuration().plus(subTask.getDuration()));
        }
        updateTasksSet();
        return subTask.getId();
    }

    @Override
    public Integer createEpic(Epic newEpic) {
        newEpic.setId(nextId++);
        newEpic.setTypeofTask(TypeofTask.EPIC);
        this.epicMap.put(newEpic.getId(), newEpic);
        updateTasksSet();
        return newEpic.getId();
    }

    @Override
    public ArrayList<Task> getAllTask() {
        if (taskMap.isEmpty()) {
            throw new NotFoundException("Задачи не найдены");
        }
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {
        if (subTaskMap.isEmpty()) {
            throw new NotFoundException("Задачи не найдены");
        }
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        if (epicMap.isEmpty()) {
            throw new NotFoundException("Задачи не найдены");
        }
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        updateTasksSet();
    }

    @Override
    public void removeAllSubTasks() {
        subTaskMap.clear();
        getAllEpic().forEach(epic -> {
            epic.setSubTaskId(new ArrayList<>());
            epic.setStatus(Status.NEW);
        });
        updateTasksSet();
    }

    @Override
    public void removeAllEpics() {
        epicMap.clear();
        subTaskMap.clear();
        updateTasksSet();
    }


    @Override
    public Task getTaskById(int id) {
        if (taskMap.get(id) == null) {
            throw new NotFoundException("Задачи с указаным id не найдено");
        }
        historyManager.addToTask(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTaskMap.get(id) == null) {
            throw new NotFoundException("Задачи с указаным id не найдено");
        }
            historyManager.addToTask(subTaskMap.get(id));
            return subTaskMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicMap.get(id) == null) {
            throw new NotFoundException("Задачи с указаным id не найдено");
        }
            historyManager.addToTask(epicMap.get(id));
            return epicMap.get(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
         return subTaskMap.values().stream()
                .filter(subTask -> subTask.getEpicId() == epicId)
                .toList();
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
        updateTasksSet();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        syncTasks(epic);
        updateTasksSet();
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        syncTasks(epic);
        updateTasksSet();
    }

    @Override
    public void deleteTaskById(int id) {
        taskMap.remove(id);
        historyManager.remove(id);
        updateTasksSet();
    }

    @Override
    public void deleteSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getEpicId());
        subTaskMap.remove(id);
        historyManager.remove(id);
        epic.getSubTaskId().removeIf(idSubTask -> idSubTask == id);
        syncTasks(epic);
        updateEpicTime(epic);
        updateTasksSet();
    }

    @Override
    public void deleteEpicById(int id) {
        epicMap.get(id).getSubTaskId().forEach(idSubTask -> {
            historyManager.remove(idSubTask);
            subTaskMap.remove(idSubTask);
        });
        epicMap.remove(id);
        historyManager.remove(id);
        updateTasksSet();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksSet.stream().toList();
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

    private boolean getConflictTime(Task task) {
        return getPrioritizedTasks().stream()
                .anyMatch(setTask -> !setTask.getEndTime().isBefore(task.getStartTime()) &&
                        !task.getEndTime().isBefore(setTask.getStartTime()));
    }

    private void updateTasksSet() {
        tasksSet.clear();
        tasksSet.addAll(taskMap.values().stream()
                .filter(task -> task.getStartTime().toLocalTime() != null)
                .toList());

        tasksSet.addAll(subTaskMap.values().stream()
                .filter(subTask -> subTask.getStartTime().toLocalTime() != null)
                .toList());
    }

    private void updateEpicTime(Epic newEpic) {

        List<SubTask> subTaskList = newEpic.getSubTaskId().stream()
                .map(this::getSubTaskById)
                .toList();

        LocalDateTime startTime = subTaskList.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subTaskList.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subTaskList.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        newEpic.setStartTime(startTime);
        newEpic.setEndTime(endTime);
        newEpic.setDuration(duration);
    }
}
