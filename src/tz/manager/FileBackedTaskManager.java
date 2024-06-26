package tz.manager;

import tz.exception.ManagerSaveException;
import tz.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        int id = super.createEpic(newEpic);
        save();
        return id;
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
        return super.getTaskById(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return super.getSubTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
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
        return super.getHistory();
    }

    public Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        Task task;

        if (TypeofTask.TASK.toString().equals(split[1])) {
            task = new Task(name, description, status);
            task.setId(id);
            task.setTypeofTask(TypeofTask.TASK);
            return task;
        } else if (TypeofTask.EPIC.toString().equals(split[1])) {
            task = new Epic(name, description, status);
            task.setId(id);
            task.setTypeofTask(TypeofTask.EPIC);
            return task;
        } else {
            int epicId = Integer.parseInt(split[5]);
            task = new SubTask(name, description, status, epicId);
            task.setId(id);
            task.setTypeofTask(TypeofTask.SUBTASK);
            return task;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            List<String> strings = Files.readAllLines(Paths.get(file.getPath()));
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getHistoryManager(), file);

            for (String line : strings) {
                if (line.isEmpty() || line.isBlank() || line.equals(fileBackedTaskManager.header)) {
                    continue;
                }
                Task task = fileBackedTaskManager.fromString(line);
                switch (task.getTypeofTask()) {
                    case TASK -> fileBackedTaskManager.createTask(task);
                    case EPIC -> fileBackedTaskManager.createEpic((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.createSubTask((SubTask) task);
                    default -> System.out.println("Такого типа задачи нет");
                }
            }
            return fileBackedTaskManager;
        } catch (IOException exception) {
            throw new ManagerSaveException("Данные не восстановлены");
        }
    }

    private void save() {
        String fileName = "fileTaskManager.csv";
        try (Writer writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(header);
            writer.write("\n");
            for (Task task : taskMap.values()) {
                writer.write(task.toString());
                writer.write("\n");
            }
            for (Epic epic : epicMap.values()) {
                writer.write(epic.toString());
                writer.write("\n");
            }
            for (SubTask subTask : subTaskMap.values()) {
                writer.write(subTask.toString() + "," + subTask.getEpicId());
                writer.write("\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Не получилось сохранить данные", exception.getCause());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTaskManager that = (FileBackedTaskManager) o;
        return Objects.equals(file, that.file) && Objects.equals(header, that.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, header);
    }
}