public class Tube {
    private float top;
    private float bottom;
    private float x;
    private boolean causedPoint = false;

    public Tube(float top, float bottom, float x) {
        this.top = top;
        this.bottom = bottom;
        this.x = x;
    }

    public Tube() {
    }

    public boolean hasCausedPoint() {
        return causedPoint;
    }

    public void setCausedPoint(boolean causedPoint) {
        this.causedPoint = causedPoint;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }
}
