import tz.model.Status;
import tz.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task = new Task("Обычная задача5", "Сходить в магазин", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
    Task task2 = new Task("Обычная задача5", "Сходить в магазин", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(2));
    
    int id = task.getId();
    int id2 = task2.getId();

    @Test
    public void shouldReturnEqualsTaskId(){
        assertEquals(id, id2);
        assertEquals(task, task2);
    }
}