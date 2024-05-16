package TZ5.model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskId = new ArrayList<>();

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


}
