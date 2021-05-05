import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskModel {
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<TransientTask> transientTasks = new ArrayList<>();
    private ArrayList<AntiTask> antiTasks = new ArrayList<>();
    private ArrayList<RecurringTask> recurringTasks = new ArrayList<>();

    public void editTask(Task task){

    }

    public void deleteTask(Task task){

    }

    public Task locateTask(String taskName){

        return null;
    }

    public void createTransientTask(String name, String type, float startTime, float duration, int date) {
        Task task = new TransientTask(name, type, startTime, duration, date);
        if()
    }

    public void createAntiTask(){

    }

    public void createRecurringTask(){

    }

    /**
     * Outputs the day of the week as an integer.
     * 0 for Sunday, 1 for Monday, 2 for Tuesday, 3 for Wednesday, 4 for Thursday, 5 for Friday, 6 for Saturday.
     *
     * @param date is an int in the form YYYYMMDD
     * @return Day of the week as integer
     */
    private int getDayOfWeek(int date){
        int day = date % 100;
        date /= 100;
        int month = (date % 100);
        date /= 100;
        int year = date % 100;
        date /= 100;
        int century =  date;

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

    private boolean verifyName(String taskName){
        for(int i =0; i <taskList.size(); i++){
            if(taskList.get(i).getName().equals(taskName))
                return false;
        }
        return true;
    }
    private boolean verifyTransientDate(TransientTask task){
        for(int i =0; i < transientTasks.size(); i++){
            TransientTask currentTask = transientTasks.get(i);
            if(currentTask.getDate() == task.getDate()){
                float currentTaskStartTime = currentTask.getStartTime();
                float currentTaskEndTime = currentTask.getEndTime();

                //This disqualifies same start time, and the task to be added is in the duration of another task
                if(task.getStartTime() >= currentTaskStartTime && task.getStartTime() <= currentTaskEndTime)
                    return false;

                //is the task has a duration that bleeds onto another task
                if(task.getEndTime() >= currentTaskStartTime && task.getEndTime() <= currentTaskEndTime)
                    return false;
            }
        }
        return true;
    }
    private boolean verifyAntiDate(AntiTask task){
        return true;
    }
    private boolean verifyRecurringDate(RecurringTask task){
        return true;
    }
}
