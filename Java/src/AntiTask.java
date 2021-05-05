public class AntiTask extends Task {
    private int date;

    public AntiTask(String name, String type, float startTime, float duration, int date) {
        super(name, type, startTime, duration);
        this.date = date;
    }

    public int getDate() {
        return this.date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
