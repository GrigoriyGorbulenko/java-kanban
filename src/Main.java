import java.util.ArrayList;

public class Main {

        public static void main(String[] args) {

            Task job = new Task("Обычная задача", "Сходить в магазин", Status.NEW);

            Task job1 = new Task("Обычная задача", "Сходить в кино", Status.NEW);
            TaskManager taskManager = new TaskManager();
            taskManager.createTask(job);
            taskManager.createTask(job1);


            SubTask subJob = new SubTask("Подзадача номер 1", "Уборка", Status.NEW);
            SubTask subJob1 = new SubTask("Подзадача номер 2", "Готовка", Status.NEW);


            Epic epicJob = new Epic("Большая задача номер 1", "Подготовка праздника", Status.NEW);
            epicJob.setName("Большая задача номер 1");
            epicJob.subTaskId.add(3);
            epicJob.subTaskId.add(4);
            taskManager.createSubTask(subJob);
            taskManager.createSubTask(subJob1);

            taskManager.createEpic(epicJob);

            SubTask subJob2 = new SubTask("Подзадача номер 3", "Поход за подарком", Status.NEW);
            taskManager.createSubTask(subJob2);
            Epic epicJob2 = new Epic("Большая задача номер 2", "День рождение", Status.NEW);
            epicJob2.subTaskId.add(6);

            taskManager.createEpic(epicJob2);

            System.out.println("Обычная задача " + job.getId() );
            System.out.println("Обычная задача " + job1.getId() );

            System.out.println("Подзадача номер 1 " + subJob.getId() + " " + subJob.getEpicId());
            System.out.println("Подзадача номер 2 " + subJob1.getId() + " " + subJob1.getEpicId());
            System.out.println("Большая задача номер 1 " + epicJob.getId() + " " + epicJob.getSubTaskId());

            System.out.println("Подзадача номер 3 " + subJob2.getId() + " " + subJob1.getEpicId());
            System.out.println("Большая задача номер 2 " + epicJob2.getId() + " " + epicJob2.getSubTaskId());

            System.out.println(taskManager.getAllTask());
            System.out.println(taskManager.getAllSubTask());
            System.out.println(taskManager.getAllEpic());
            System.out.println(taskManager.getTaskById(2));
            System.out.println(taskManager.getSubTaskById(6));
            System.out.println(taskManager.getEpicById(5));
            taskManager.deleteTaskById(1);
            //System.out.println(taskManager.getAllTask());
            System.out.println(taskManager.getAllSubTaskByEpic(5));





    }


}
