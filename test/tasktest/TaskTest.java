package tasktest;

import tz5.model.Epic;
import tz5.model.Status;
import tz5.model.SubTask;
import tz5.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task = new Task("Обычная задача5", "Сходить в магазин", Status.NEW);
    Task task2 = new Task("Обычная задача5", "Сходить в магазин", Status.NEW);
    Task task3 = new SubTask("Подзадача номер 1", "Подмести пол", Status.NEW);
    Task task4 = new SubTask("Подзадача номер 1", "Подмести пол", Status.NEW);
    Task task5 = new Epic("Большая задача номер 1", "Уборка", Status.NEW);
    Task task6 = new Epic("Большая задача номер 1", "Уборка", Status.NEW);

    int id = task.getId();
    int id2 = task2.getId();

    int id3 = task3.getId();
    int id4 = task4.getId();

    int id5 = task5.getId();
    int id6 = task6.getId();

    @Test
    public void shouldReturnEqualsTaskId(){
        assertEquals(id, id2);
        assertEquals(task, task2);
    }

    @Test
    public void shouldReturnEqualsSubTaskId(){
        assertEquals(id3, id4);
        assertEquals(task3, task4);
    }

    @Test
    public void shouldReturnEqualsEpicId(){
        assertEquals(id5, id6);
        assertEquals(task5, task6);
    }
}