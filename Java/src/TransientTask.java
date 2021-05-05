public class TransientTask extends Task {
    private int date;

    public TransientTask(String name, String type, float startTime, float duration, int date) {
        super(name, type, startTime, duration);
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
