import tz5.manager.InMemoryTaskManager;
import tz5.manager.Managers;
import tz5.manager.TaskManager;
import tz5.model.Epic;
import tz5.model.Status;
import tz5.model.SubTask;
import tz5.model.Task;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        taskManager.createEpic(new Epic("Купить дом", ""));
        taskManager.createEpic((new Epic("Переехать за границу", "")));
        taskManager.createSubTask(new SubTask("Взять ипотеку", "", 1));
        taskManager.createSubTask((new SubTask("Выбрать дом", "", 1)));
        taskManager.createSubTask(new SubTask("Выбрать страну", "", 2));
        taskManager.createTask(new Task("Тест", ""));
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(1);
        taskManager.getTaskById(6);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(2);
        taskManager.deleteTaskById(6);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(2);
        System.out.println(taskManager.getHistory().size());
        taskManager.getSubTaskById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);
        System.out.println(taskManager.getHistory());
        taskManager.deleteSubTaskById(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(4);
        taskManager.deleteEpicById(1);

        System.out.println(taskManager.getHistory());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);

            for (Integer i : epic.getSubTaskId() ) {
                System.out.println("--> " + manager.getSubTaskById(i));
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getAllSubTask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
