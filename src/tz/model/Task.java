package tz.model;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected Status status;
    protected int id;
    protected TypeofTask typeofTask;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int taskId) {
        this.id = taskId;
    }

    public TypeofTask getTypeofTask() {
        return typeofTask;
    }

    public void setTypeofTask(TypeofTask typeofTask) {
        this.typeofTask = typeofTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        return id +
                "," + getTypeofTask().toString() +
                "," + getName() +
                "," + getStatus() +
                "," + getDescription();
    }
}
