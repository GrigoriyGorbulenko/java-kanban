package TaskTest;

import TZ5.model.Epic;
import TZ5.model.Status;
import TZ5.model.SubTask;
import TZ5.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task = new Task("Обычная задача5", "Сходить в магазин", Status.NEW);
    Task task2 = new Task("Обычная задача5", "Сходить в магазин", Status.NEW);

    Task task3 = new SubTask("Подзадача номер 1", "Уборка", Status.NEW);
    Task task4 = new SubTask("Подзадача номер 1", "Уборка", Status.NEW);

    int id = task.getId();
    int id2 = task2.getId();

    int id3 = task3.getId();
    int id4 = task4.getId();




    @Test
    public void shouldReturnEqualsSubtaskId(){
        assertEquals(id, id2);
    }
    @Test
    public void shouldReturnEqualsSubTask(){
        assertEquals(task3, task4);
    }

    @Test
    public void shouldReturnEqualsIdTask(){
        assertEquals(id3, id4);
    }
}