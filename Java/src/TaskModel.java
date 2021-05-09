import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskModel {
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<TransientTask> transientTasks = new ArrayList<>();
    private ArrayList<AntiTask> antiTasks = new ArrayList<>();
    private ArrayList<RecurringTask> recurringTasks = new ArrayList<>();

    public void editTask(Task task) {

    }

    public void deleteTask(Task task) {

    }

    public Task locateTask(String taskName) {

        return null;
    }

    public void createTransientTask(String name, String type, float startTime, float duration, int date) {

        if(verifyTransientDate(new TransientTask(name, type, startTime, duration, date)))
            transientTasks.add(new TransientTask(name, type, startTime, duration, date));
    }

    public void createAntiTask() {

    }

    public void createRecurringTask(String name, String type, float startTime, float duration, int startDate, int endDate, int frequency) {
        if(verifyRecurringDate(new RecurringTask(name,type, startTime, duration, startDate, endDate, frequency)))
            recurringTasks.add(new RecurringTask(name,type, startTime, duration, startDate, endDate, frequency));
    }

    /**
     * Outputs the day of the week as an integer.
     * 0 for Sunday, 1 for Monday, 2 for Tuesday, 3 for Wednesday, 4 for Thursday, 5 for Friday, 6 for Saturday.
     *
     * @param date is an int in the form YYYYMMDD
     * @return Day of the week as integer
     */
    private int getDayOfWeek(int date) {
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

    private boolean verifyName(String taskName) {
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
                    float currentTaskStartTime = task.getStartTime();
                    float currentTaskEndTime = task.getEndTime();

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
        }
        //checking for daily frequency
        else{
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
        }
        return true;
    }
}
