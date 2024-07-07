package tz.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class Epic extends Task {

    private ArrayList<Integer> subTaskId = new ArrayList<>();

    protected LocalDateTime endTime;

    public Epic(String name, String description, Status status, ArrayList<Integer> subTaskId) {
        super(name, description, status);
        this.subTaskId = subTaskId;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(ArrayList<Integer> subtaskId) {
        this.subTaskId = subtaskId;
    }

    public void addSubTaskId(SubTask subTask) {
        subTaskId.add(subTask.getId());
    }

    public void addSubTaskId(int id) {
        subTaskId.add(id);
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
