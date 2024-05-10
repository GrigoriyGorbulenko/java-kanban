import java.sql.SQLOutput;
import java.util.ArrayList;

public class Main {

        public static void main(String[] args) {

            Task job = new Task("Обычная задача", "Сходить в магазин", Status.NEW);

            Task job2 = new Task("Обычная задача", "Сходить в кино", Status.NEW);
            TaskManager taskManager = new TaskManager();
            taskManager.createTask(job);
            taskManager.createTask(job2);
            SubTask subJob = new SubTask("Подзадача номер 1", "Уборка", Status.NEW);
            SubTask subJob1 = new SubTask("Подзадача номер 2", "Готовка", Status.NEW);
            Epic epicJob = new Epic("Большая задача номер 1", "Подготовка праздника");
            taskManager.createEpic(epicJob);
            subJob.setEpicId(epicJob.getId());
            subJob1.setEpicId(epicJob.getId());

            taskManager.createSubTask(subJob);
            taskManager.createSubTask(subJob1);

            epicJob.setSubTaskId(new ArrayList<>() {
                {
                    add(subJob.getId());
                    add(subJob1.getId());
                }
            });
            taskManager.updateEpic(epicJob);

            Epic epicJob2 = new Epic("Большая задача номер 2", "День рождение", Status.NEW);
            taskManager.createEpic(epicJob2);
            SubTask subJob3 = new SubTask("Подзадача номер 3", "Поход за подарком", Status.NEW);
            subJob3.setEpicId(epicJob2.getId());
            taskManager.createSubTask(subJob3);

            epicJob2.setSubTaskId(new ArrayList<>() {
                {
                    add(subJob3.getId());
                }
            });

            System.out.println(taskManager.getAllEpic());
            System.out.println(taskManager.getAllSubTask());
            System.out.println(taskManager.getAllTask());

            job.setStatus(Status.DONE);
            taskManager.updateTask(job);
            System.out.println(job);

            subJob.setStatus(Status.IN_PROGRESS);
            subJob1.setStatus(Status.DONE);
            taskManager.updateSubTask(subJob);
            taskManager.updateSubTask(subJob1);

            System.out.println(epicJob);

            taskManager.deleteSubTaskById(subJob.getId());

            System.out.println(epicJob);

            System.out.println(epicJob.getSubTaskId());

        }
}
