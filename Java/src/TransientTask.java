public class TransientTask extends Task {
    private int date;
    private float endTime;

    public TransientTask(String name, String type, float startTime, float duration,  int date) {
        super(name, type, startTime, duration);
        this.date = date;
        float temp = startTime + duration;
        if(temp > 24) {
            temp = temp - 24;
            endTime = temp;
        }
        else{
            endTime=temp;
        }
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public float getEndTime(){
        return endTime;
    }
}
