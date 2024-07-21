package tz.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected Status status;
    protected int id;
    protected TypeofTask typeofTask;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.status = status;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        return duration;
    }

    public Long getDurationOfMinutes(Duration duration) {
        return duration.toMinutes();
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {

        return startTime.plusMinutes(duration.toMinutes());
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
                "," + getTypeofTask() +
                "," + getName() +
                "," + getStatus() +
                "," + getDescription() +
                "," + getStartTime() +
                "," + getEndTime() +
                "," + getDuration();
    }
}
