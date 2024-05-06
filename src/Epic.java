import java.util.ArrayList;

public class Epic extends Task {

     ArrayList<Integer> subTaskId = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(ArrayList<Integer> subtaskId) {
        this.subTaskId = subtaskId;
    }


}
