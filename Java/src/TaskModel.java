import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskModel {
    private ArrayList<Task> taskList = new ArrayList<>();

    public void editTask(Task task){

    }

    public void deleteTask(Task task){

    }

    public Task locateTask(String taskName){

        return null;
    }

    public void createTransientTask(){

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

        return Math.floorMod((int) (day + Math.floor(2.6 * month - 0.2) - (2 * century) + year + Math.floor(year / 4.0) + Math.floor(century / 4.0)), 7);
    }

}
