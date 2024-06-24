package tz.manager;

import tz.exception.ManagerSaveException;
import tz.model.Epic;
import tz.model.SubTask;
import tz.model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    String header = "id,type,name,status,description,epic";
    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        save();
    }

    @Override
    public Integer createTask(Task newTask) {
        int id = super.createTask(newTask);
        save();
        return id;
    }

    @Override
    public Integer createSubTask(SubTask subTask) {
        int id = super.createSubTask(subTask);
        save();
        return id;
    }

    @Override
    public Integer createEpic(Epic newEpic) {
        int id =  super.createEpic(newEpic);
        save();
        return id;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        save();
        return super.getAllTask();
    }

    @Override
    public ArrayList<SubTask> getAllSubTask() {
        save();
        return super.getAllSubTask();
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        save();
        return super.getAllEpic();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        save();
        return super.getSubTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();

    }

    @Override
    public String toString() {
        return "FileBackedTaskManager{" +
                "file=" + file +
                '}';
    }

    public void save() {
        String fileName = "test.txt";
        try(Writer writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(header);
            writer.write("\n");
        } catch (IOException exception) {
            throw new ManagerSaveException("Не получилось сохранить данные", exception.getCause());
        }
    }
}