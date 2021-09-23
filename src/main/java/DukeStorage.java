import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class DukeStorage {
    private static final String TASK_IS_EVENT = "E";
    private static final String TASK_IS_TODO = "T";
    private static final String TASK_IS_DEADLINE = "D";
    private static final String TASK_COMPLETED_INT = "1";
    private static final String TASK_NOT_COMPLETED_INT = "0";

    private ArrayList<Task> tasks = new ArrayList<Task>();

    public DukeStorage() {
        updateDukeStorageOnStartUp();
    }

    private void updateDukeStorageOnStartUp() {
        try {
            File dukeTextFile = new File("../data/duke.txt");
            Scanner dukeTextFileReader = new Scanner(dukeTextFile);
            while (dukeTextFileReader.hasNextLine()) {
                String currentTaskString = dukeTextFileReader.nextLine();
                Task currentTask = getTaskFromDatabase(currentTaskString);
                this.tasks.add(currentTask);
            }
            dukeTextFileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Duke text file not found. Creating a new duke text file.");
            createNewDataDirectory();
        }
    }

    /**
     * @return We return the list of all tasks from duke text file
     * */
    public ArrayList<Task> loadTaskList() {
        return this.tasks;
    }

    private Task getTaskFromDatabase(String taskString) {
        String[] currentTaskSplit = taskString.split(" [|] ");
        String taskIndicator = currentTaskSplit[0];
        String taskIsCompleted = currentTaskSplit[1];
        String taskDescription = currentTaskSplit[2];

        switch (taskIndicator) {
            case TASK_IS_TODO:
                Todo todoReturn = new Todo(taskDescription);
                if (taskIsCompleted.equals(TASK_COMPLETED_INT)) {
                    todoReturn.setTaskAsDone();
                }
                return todoReturn;
            case TASK_IS_DEADLINE:
                String deadlineDate = currentTaskSplit[3];
                Deadline deadlineReturn = new Deadline(taskDescription, deadlineDate);
                if (taskIsCompleted.equals(TASK_COMPLETED_INT)) {
                    deadlineReturn.setTaskAsDone();
                }
                return deadlineReturn;
            case TASK_IS_EVENT:
                String eventDate = currentTaskSplit[3];
                Event eventReturn = new Event(taskDescription, eventDate);
                if (taskIsCompleted.equals(TASK_COMPLETED_INT)) {
                    eventReturn.setTaskAsDone();
                }
                return eventReturn;
        }
        return null;
    }

    private void createNewDataDirectory() {
        File dataDirectory = new File("../data");
        dataDirectory.mkdirs();
        try {
            FileWriter dukeTextFile = new FileWriter("../data/duke.txt");
        } catch (IOException e) {
            System.out.println("Unable to create duke.txt file in data directory");
        }
    }

    private String getTaskCompletionInInt(Task task) {
        String taskCompletionInteger;
        if (task.isCompleted()) {
            taskCompletionInteger = TASK_COMPLETED_INT;
        } else {
            taskCompletionInteger = TASK_NOT_COMPLETED_INT;
        }
        return taskCompletionInteger;
    }

    private String cleanTaskToSave(Task task) {
        String taskStringToSave = "";
        if (task instanceof Todo) {
            Todo taskTodo = (Todo) task;
            taskStringToSave = TASK_IS_TODO + " | " +
                    getTaskCompletionInInt(taskTodo) + " | " +
                    taskTodo.getTaskDescription();
        } else if (task instanceof Event) {
            Event taskEvent = (Event) task;
            taskStringToSave = TASK_IS_EVENT + " | " +
                    getTaskCompletionInInt(task) + " | " +
                    taskEvent.getTaskDescription() + " | " +
                    taskEvent.getEventDate();
        } else {
            Deadline taskDeadline = (Deadline) task;
            taskStringToSave = TASK_IS_DEADLINE + " | " +
                    getTaskCompletionInInt(task) + " | " +
                    taskDeadline.getTaskDescription() + " | " +
                    taskDeadline.getDeadlineDate();
        }
        return taskStringToSave;
    }

    /**
     * @param taskList The tasklist to update duke text file
     * We update the duke text file in ../data/duke.txt everytime
     * the tasklist is updated.
     * */
    public void updateStorage(TaskList taskList) {
        try {
            FileWriter dukeTextFile = new FileWriter("../data/duke.txt");
            for (int i = 0; i < taskList.getNumberOfTask(); i++) {
                Task currentTask = taskList.getTaskFromIndex(i);
                String taskString = cleanTaskToSave(currentTask);
                dukeTextFile.write(taskString);
                dukeTextFile.write("\n");
            }
            dukeTextFile.close();
        } catch (IOException e) {
            System.out.println("Error saving into the duke text file. " +
                    "Data directory not found. \n" +
                    "Creating a new data directory.");
            createNewDataDirectory();
            System.out.println("Done creating new data directory.\n");
            updateStorage(taskList);
        }
    }
}
