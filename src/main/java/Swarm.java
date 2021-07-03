public class Swarm {

    private double x;
    private double y;
    private double bestKnownX;
    private double velocity;

    public Swarm() {
    }

    public Swarm(double x, double y, double bestKnownX) {
        this.x = x;
        this.y = y;
        this.bestKnownX = bestKnownX;
    }

    public Swarm(double x, double y, double bestKnownX, double velocity) {
        this.x = x;
        this.y = y;
        this.bestKnownX = bestKnownX;
        this.velocity = velocity;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getBestKnownX() {
        return bestKnownX;
    }

    public void setBestKnownX(double bestKnownX) {
        this.bestKnownX = bestKnownX;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
