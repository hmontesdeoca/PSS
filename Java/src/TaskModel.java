import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TaskModel {
    private ArrayList<Task> taskList = new ArrayList<>();
    ArrayList<TransientTask> transientTasks = new ArrayList<>();
    ArrayList<AntiTask> antiTasks = new ArrayList<>();
    ArrayList<RecurringTask> recurringTasks = new ArrayList<>();

    // List of task types
    public static final List<String> transientTypes = Collections.unmodifiableList(Arrays.asList("Visit", "Shopping", "Appointment"));
    public static final List<String> recurringTypes = Collections.unmodifiableList(Arrays.asList("Class", "Study", "Sleep", "Exercise", "Work", "Meal"));
    public static final List<String> antiTypes = Collections.unmodifiableList(Arrays.asList("Cancellation"));

    public ArrayList<Task> getTaskList()
    {
        return taskList;
    }
    public ArrayList<TransientTask> getTransientTasksList()
    {
        return transientTasks;
    }
    public ArrayList<RecurringTask> getRecurringTaskList()
    {
        return recurringTasks;
    }

    public void editTask(Task task) {}

    public void deleteTask(String taskName) {
        Task result = locateTask(taskName);

        if (result != null) {
            // Deleting a recurring task
            if (result instanceof RecurringTask) {
                recurringTasks.remove(result);  // Might have to cast this to remove properly? Not sure

                // Also delete any anti-tasks associated with deleted recurring task
                AntiTask antiTask = getMatchingAntiTask((RecurringTask) result);
                if (antiTask != null) {
                    antiTasks.remove(antiTask);
                }
            }

            // Deleting an anti-task
            else if (result instanceof AntiTask) {
                // First find the recurring task associated with this anti-task
                RecurringTask associatedRecTask = getMatchingRecTask((AntiTask) result);

                // No conflict recurring tasks or transient tasks. verifyRecurringDate should return true if the passed recurring task doesn't clash with any recurring tasks or transient tasks
                if (verifyRecurringDate(associatedRecTask)) {
                    antiTasks.remove(result);
                }

                else {
                    System.out.println("ERROR: Cannot delete anti-task");
                }
            }

            // Deleting a transient task
            else {
                transientTasks.remove(result);
            }
        }

        // result should be null if there is no match
        else {
            System.out.println("Task to delete does not exist");
        }

    }

    /**
     * Return a recurring task associated with the given anti-task, return null if no match
     *
     * @param task is an AntiTask object
     * @return RecurringTask associated with the anti-task
     */
    private RecurringTask getMatchingRecTask(AntiTask task) {
        for (RecurringTask recTask : recurringTasks) {
            // If this recurring task is daily
            if (recTask.getFrequency() == 1) {
                if (recTask.getStartTime() == task.getStartTime()) {
                    return recTask;
                }
            }

            // Recurring task is weekly
            else {
                // First check if day matches
                if (getDayOfWeek(recTask.getStartDate()) == getDayOfWeek(task.getDate())) {
                    // Match if time is also the same
                    if (recTask.getStartTime() == task.getStartTime()) {
                        return recTask;
                    }
                }
            }
        }

        // No match found
        return null;
    }

    /**
     * Return a single anti-task associated with a given recurring task if found, return null if no match.
     * Note that there can be multiple anti-tasks associated for one recurring task.
     *
     * @param task is a RecurringTask object
     * @return AntiTask associated with the recurring task
     */
    private AntiTask getMatchingAntiTask(RecurringTask task) {
        for (AntiTask antitask : antiTasks) {
            // For daily recurring tasks
            if (task.getFrequency() == 1) {
                // Check if start time matches
                if (antitask.getStartTime() == task.getStartTime()) {
                    return antitask;
                }
            }

            // For weekly recurring tasks
            else {
                // First check if day of week matches
                if (getDayOfWeek(antitask.getDate()) == getDayOfWeek(task.getStartDate())) {
                    // Check if start time matches
                    if (antitask.getStartTime() == task.getStartTime()) {
                        return antitask;
                    }
                }
            }
        }

        // No match found
        return null;
    }

    public Task locateTask(String taskName) {
        // Look through all of our tasks and return the task if name matches
        for (TransientTask transientTask : transientTasks) {
            // Ignore lowercase/uppercase for now
            if (transientTask.getName().equalsIgnoreCase(taskName)) return transientTask;
        }

        for (RecurringTask recurringTask : recurringTasks) {
            if (recurringTask.getName().equalsIgnoreCase(taskName)) return recurringTask;
        }

        for (AntiTask antiTask : antiTasks) {
            if (antiTask.getName().equalsIgnoreCase(taskName)) return antiTask;
        }

        // Figure out what we want to return if task is not found
        return null;
    }

    public void createTransientTask(String name, String type, float startTime, float duration, int date) {
        TransientTask transientTask = new TransientTask(name, type, startTime, duration, date);
        if(verifyTransientDate(transientTask)) {
            transientTasks.add(transientTask);
            taskList.add(transientTask);
        }
    }

    public void createAntiTask(String name, String type, float startTime, float duration, int date) {
        AntiTask task = new AntiTask(name, type, startTime, duration, date);
        if (verifyAntiDate(task)) {
            antiTasks.add(task);
            taskList.add(task);
        }
    }

    public void createRecurringTask(String name, String type, float startTime, float duration, int startDate, int endDate, int frequency) {
        RecurringTask rec = new RecurringTask(name, type, startTime, duration, startDate, endDate, frequency);
        if(verifyRecurringDate(rec)) {
            recurringTasks.add(rec);
            taskList.add(rec);
        }
    }

    /**
     * Outputs the day of the week as an integer.
     * 0 for Sunday, 1 for Monday, 2 for Tuesday, 3 for Wednesday, 4 for Thursday, 5 for Friday, 6 for Saturday.
     *
     * @param date is an int in the form YYYYMMDD
     * @return Day of the week as integer
     */
    public static int getDayOfWeek(int date) {
        int day = date % 100;
        date /= 100;
        int month = (date % 100);
        date /= 100;
        int year = date % 100;
        date /= 100;
        int century = date;

        // Subtract 1 from year if month is Jan or Feb
        if (month <= 2) {
            year--;
        }

        // Need March to be 1, Jan to be 11, Feb to be 12, etc
        month -= 2;
        if (month < 0) {
            month += 12;
        }

        return Math.floorMod((int) (day + Math.floor(2.6 * month - 0.2) -
                (2 * century) + year + Math.floor(year / 4.0) + Math.floor(century / 4.0)), 7);
    }

    public boolean verifyName(String taskName) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getName().equals(taskName))
                return false;
        }
        return true;
    }

    private boolean verifyTransientDate(TransientTask task) {
        // Check if task to be added clashes with any transient tasks
        for (int i = 0; i < transientTasks.size(); i++) {
            TransientTask currentTask = transientTasks.get(i);
            if (currentTask.getDate() == task.getDate()) {
                float currentTaskStartTime = currentTask.getStartTime();
                float currentTaskEndTime = currentTask.getEndTime();

                //This disqualifies same start time, and the task to be added is in the duration of another task
                if (task.getStartTime() >= currentTaskStartTime && task.getStartTime() < currentTaskEndTime) {
                    System.out.println("conflict with: "+ currentTask.getName());
                    return false;
                }

                //is the task has a duration that bleeds onto another task
                if (task.getEndTime() > currentTaskStartTime && task.getEndTime() <= currentTaskEndTime) {
                    System.out.println("conflict with: "+ currentTask.getName());
                    return false;
                }

                // Case where task to be added starts before current task and ends after current task
                if (task.getStartTime() <= currentTaskStartTime && task.getEndTime() >= currentTaskEndTime) {
                    System.out.println("conflict with: "+ currentTask.getName());
                    return false;
                }
            }
        }

        // Check if task to be added clashes with any recurring tasks
        for (RecurringTask recTask : recurringTasks) {
            //if the task is weekly
            if(recTask.getFrequency()==7){

                // Check if this recurring task occurs on the same day as task to be added
                if (getDayOfWeek(recTask.getStartDate()) == getDayOfWeek(task.getDate())) {

                    // Check if this recurring task overlaps with the task to be added
                    if ((task.getStartTime() >= recTask.getStartTime() && task.getStartTime() < recTask.getEndTime())
                            || (task.getEndTime() > recTask.getStartTime() && task.getEndTime() <= recTask.getEndTime())
                            || (task.getStartTime() <= recTask.getStartTime() && task.getEndTime() >= recTask.getEndTime())) {

                        // Check if there is an anti-task that cancels out this overlapping recurring task
                        for (AntiTask antiTask : antiTasks) {
                            // First check if anti-task date matches the task to be added
                            if (antiTask.getDate() == task.getDate()) {
                                // Check if this anti-task matches the recurring task. If it matches, this recurring task shouldn't block the task we are trying to add.
                                if (antiTask.getStartTime() == recTask.getStartTime()) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                        // No anti-task found to cancel this blocking recurring task
                        return false;
                    }
                }
            }
            //if the task is daily
            else{
                //checking if it falls in the range of the recurring task INCLUDING SAME DAY ("<= or >=")
                if(task.getDate() <= recTask.getEndDate() && task.getDate() >= recTask.getStartDate()){
                    //check if they have any overlapping times
                    float currentTaskStartTime = recTask.getStartTime();
                    float currentTaskEndTime = recTask.getEndTime();

                    //This disqualifies same start time, and the task to be added is in the duration of another task
                    if ((task.getStartTime() >= recTask.getStartTime() && task.getStartTime() < recTask.getEndTime())
                            || (task.getEndTime() > recTask.getStartTime() && task.getEndTime() <= recTask.getEndTime())
                            || (task.getStartTime() <= recTask.getStartTime() && task.getEndTime() >= recTask.getEndTime()))  {
                        for (AntiTask antiTask : antiTasks) {
                            // First check if anti-task date matches the task to be added
                            if (antiTask.getDate() == task.getDate()) {
                                // Check if this anti-task matches the recurring task. If it matches, this recurring task shouldn't block the task we are trying to add.
                                if (antiTask.getStartTime() == recTask.getStartTime()) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                        return false;
                    }
                }

            }
        }

        // No blocking overlapping transient or recurring tasks
        return true;
    }

    private boolean verifyAntiDate(AntiTask task) {
        // Can't have duplicate anti-tasks
        for (AntiTask antiTask : antiTasks) {
            if (antiTask.getDate() == task.getDate() && antiTask.getStartTime() == task.getStartTime()) {
                return false;
            }
        }

        // Need a matching recurring task to create anti-task
        for (RecurringTask recTask : recurringTasks) {

            // (For weekly recurring tasks) Find a recurring task that falls on the same day of the week as this anti-task
            // (For daily recurring tasks) Find recurring task that's occurring during anti-task's date
            if ((recTask.getFrequency() == 7 && getDayOfWeek(recTask.getStartDate()) == getDayOfWeek(task.getDate()))
                    || (recTask.getFrequency() == 1 && (task.getDate() >= recTask.getStartDate() && task.getDate() <= recTask.getEndDate()))) {

                // Now check if start times and duration match
                if (recTask.getStartTime() == task.getStartTime() && recTask.getDuration() == task.getDuration()) {
                    return true;
                }
            }
        }

        // No matching recurring task found for this anti-task
        return false;
    }

    public void printArrays(){
        System.out.println("TRANSIENT TASKS");
        System.out.println("----------------");
        for(TransientTask task : transientTasks)
            System.out.println(task.getName());
        System.out.println();
        System.out.println("RECURRING TASKS");
        System.out.println("----------------");
        for(RecurringTask task : recurringTasks)
            System.out.println(task.getName());
        System.out.println();
    }

    private boolean verifyRecurringDate(RecurringTask task) {
        //checking for weekly frequency
        if(task.getFrequency()==7){
            // Check for conflict with transient tasks
            for (int i = 0; i < transientTasks.size(); i++) {
                TransientTask currentTask = transientTasks.get(i);
                //check if any transient tasks have the same day of the week as this recurring task
                //AND check if the task does not end before another task
                if (getDayOfWeek(task.getStartDate()) == getDayOfWeek(currentTask.getDate()) &&
                        (currentTask.getDate() < task.getEndDate() && currentTask.getDate() > task.getEndDate())) {
                    //check if they have any overlapping times
                    float currentTaskStartTime = currentTask.getStartTime();
                    float currentTaskEndTime = currentTask.getEndTime();

                    //This disqualifies same start time, and the task to be added is in the duration of another task
                    if (task.getStartTime() >= currentTaskStartTime && task.getStartTime() < currentTaskEndTime)
                        return false;

                    //is the task has a duration that bleeds onto another task
                    if (task.getEndTime() > currentTaskStartTime && task.getEndTime() <= currentTaskEndTime)
                        return false;

                    // Case where task to be added starts before current task and ends after current task
                    if (task.getStartTime() <= currentTaskStartTime && task.getEndTime() >= currentTaskEndTime) {
                        return false;
                    }
                }
            }

            // Check for conflict with other recurring tasks
            for (RecurringTask recTask : recurringTasks) {
                // First check if they occur on same day
                if (getDayOfWeek(recTask.getStartDate()) == getDayOfWeek(task.getStartDate())) {
                    // Now check if the dates overlap
                    if (datesOverlap(recTask, task)) {
                        // Now check for time overlap (subroutine?)
                        if ((task.getStartTime() >= recTask.getStartTime() && task.getStartTime() < recTask.getEndTime())
                                || (task.getEndTime() > recTask.getStartTime() && task.getEndTime() <= recTask.getEndTime())
                                || (task.getStartTime() <= recTask.getStartTime() && task.getEndTime() >= recTask.getEndTime())) {
                            // There is a time overlap, check if there is an anti-task that makes this ok
                            if (getMatchingAntiTask(recTask) == null) {  // if no anti-task
                                return false;
                            }
                        }
                    }
                }
            }
        }

        //checking for daily frequency
        else{
            // Check for conflict with transient tasks
            for (int i = 0; i < transientTasks.size(); i++){
                TransientTask currentTask = transientTasks.get(i);
                //checking if it falls in the range of the recurring task INCLUDING SAME DAY ("<= or >=")
                if(currentTask.getDate() <= task.getEndDate() && currentTask.getDate() >= task.getStartDate()){
                    //check if they have any overlapping times
                    float currentTaskStartTime = currentTask.getStartTime();
                    float currentTaskEndTime = currentTask.getEndTime();

                    //This disqualifies same start time, and the task to be added is in the duration of another task
                    if (task.getStartTime() >= currentTaskStartTime && task.getStartTime() < currentTaskEndTime)
                        return false;
                    System.out.println("1st cond checked");
                    //is the task has a duration that bleeds onto another task
                    if (task.getEndTime() > currentTaskStartTime && task.getEndTime() <= currentTaskEndTime)
                        return false;
                    System.out.println("2nd cond checked");

                    // Case where task to be added starts before current task and ends after current task
                    if (task.getStartTime() <= currentTaskStartTime && task.getEndTime() >= currentTaskEndTime) {
                        return false;
                    }
                    System.out.println("3rd cond checked");
                }
            }

            // Check for conflict with other recurring tasks
            for (RecurringTask recTask : recurringTasks) {
                // First check if dates overlap
                if (datesOverlap(recTask, task)) {
                    // Now check if times overlap (move to subroutine?)
                    if ((task.getStartTime() >= recTask.getStartTime() && task.getStartTime() < recTask.getEndTime())
                            || (task.getEndTime() > recTask.getStartTime() && task.getEndTime() <= recTask.getEndTime())
                            || (task.getStartTime() <= recTask.getStartTime() && task.getEndTime() >= recTask.getEndTime())) {
                        // Times overlap, see if there is an anti-task that makes this ok
                        if (getMatchingAntiTask(recTask) == null) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Return true if there is a date overlap between the two given recurring tasks, false otherwise
     * @param task1 is the first RecurringTask object
     * @param task2 is the second RecurringTask object
     * @return boolean true if there is overlap, false if not
     */
    private boolean datesOverlap(RecurringTask task1, RecurringTask task2) {
        // task2 starts before task1 but ends during task1
        if (task2.getStartDate() <= task1.getStartDate() && task2.getEndDate() <= task1.getEndDate()) {
            return true;
        }

        // task2 starts during task1 and ends after task1
        else if (task2.getStartDate() >= task1.getStartDate() && task2.getEndDate() >= task1.getEndDate()) {
            return true;
        }

        // task2 starts and ends during task1
        else if (task2.getStartDate() >= task1.getStartDate() && task2.getEndDate() <= task1.getEndDate()) {
            return true;
        }

        // task1 starts and ends during task2
        else if (task1.getStartDate() >= task2.getStartDate() && task1.getEndDate() <= task2.getEndDate()) {
            return true;
        }

        // No overlap
        return false;
    }
    /**
     * Remove all tasks from our schedule
     */
    public void resetSchedule() {
        transientTasks.clear();
        recurringTasks.clear();
        antiTasks.clear();
    }

    /**
     * Read the schedule from a JSON file
     */
    public void loadSchedule() {
        // Possibly call resetSchedule() here if we're loading a new one?

        JSONParser parser = new JSONParser();

        // Get file name from user
        String fileName = getFileName();

        try {
            // Read and verify that file exists
            FileReader fileReader = new FileReader(fileName);

            // Convert json to array
            JSONArray jsonArray = (JSONArray) parser.parse(fileReader);

            // Add all tasks from file to TaskModel's lists, according for each task type
            jsonArray.forEach(obj -> {
                JSONObject task = (JSONObject) obj;

                // Determine what type of task this is through the 'type' attribute
                if (transientTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");
                    float duration = (float) (double) task.get("Duration");
                    int date = (int) (long) task.get("Date");

                    createTransientTask(name, type, startTime, duration, date);
                } else if (recurringTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");  // Need to cast twice from Object -> long -> float for some reason
                    float duration = (float) (double) task.get("Duration");
                    int startDate = (int) (long) task.get("StartDate");
                    int endDate = (int) (long) task.get("EndDate");
                    int frequency = (int) (long) task.get("Frequency");

                    createRecurringTask(name, type, startTime, duration, startDate, endDate, frequency);
                } else if (antiTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");
                    float duration = (float) (double) task.get("Duration");
                    int date = (int) (long) task.get("Date");

                    createAntiTask(name, type, startTime, duration, date);
                } else {
                    System.out.println("ERROR: Type may not be valid?");
                }
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write schedule to JSON file
     */
    public void exportSchedule() {
        // Get valid file name from user
        String fileName = getFileName();

        try {
            // Open file
            FileWriter fileWriter = new FileWriter(fileName);

            JSONArray jsonArray = new JSONArray();

            // Iterate through all transient tasks
            for (TransientTask task : transientTasks) {
                // Convert TransientTask object to a JSON object
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", task.getName());
                jsonObject.put("Type", task.getType());
                jsonObject.put("Date", task.getDate());
                jsonObject.put("StartTime", task.getStartTime());
                jsonObject.put("Duration", task.getDuration());

                // Add to JSON array
                jsonArray.add(jsonObject);
            }

            // Write all recurring tasks
            for (RecurringTask task : recurringTasks) {
                // Convert RecurringTask object to a JSON object
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", task.getName());
                jsonObject.put("Type", task.getType());
                jsonObject.put("StartDate", task.getStartDate());
                jsonObject.put("StartTime", task.getStartTime());
                jsonObject.put("Duration", task.getDuration());
                jsonObject.put("EndDate", task.getEndDate());
                jsonObject.put("Frequency", task.getFrequency());

                // Add to JSON array
                jsonArray.add(jsonObject);
            }

            // Write all anti-tasks
            for (AntiTask task : antiTasks) {
                // Convert AntiTask object to a JSON object
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Name", task.getName());
                jsonObject.put("Type", task.getType());
                jsonObject.put("Date", task.getDate());
                jsonObject.put("StartTime", task.getStartTime());
                jsonObject.put("Duration", task.getDuration());

                // Add to JSON array
                jsonArray.add(jsonObject);
            }

            // Write JSON array to the file
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        // Implement code to grab string from GUI here?
        String userInput = "Set2.json"; //test value for now

        // Validate that file is JSON
        String extension = userInput.split("[.]")[1];
        if (extension.equals("json")) {
            return userInput;
        } else {
            System.out.println("ERROR: invalid file extension");
            return null;
        }
    }
}
