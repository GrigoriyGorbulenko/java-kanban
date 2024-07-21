package tz.manager;

import tz.model.Epic;
import tz.model.SubTask;
import tz.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    Integer createTask(Task newTask);

    Integer createSubTask(SubTask subTask);

    Integer createEpic(Epic newEpic);

    ArrayList<Task> getAllTask();

    ArrayList<SubTask> getAllSubTask();

    ArrayList<Epic> getAllEpic();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    List<Task> getPrioritizedTasks();

    List<SubTask> getSubTasksByEpic(int epicId);
}
