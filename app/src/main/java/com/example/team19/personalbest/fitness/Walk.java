package com.example.team19.personalbest.fitness;

/**
 * Class Walk
 * Holds information of intentional walks
 */
public class Walk {
    private int steps;  //steps during the walk
    private float distance; //distance covered
    private float speed;    //average speed
    private long startTime;
    private long endTime;

    /**
     * Constructor of Walk
     * Uses startTime to initialize new Walks, all fields initialized to 0
     * @param startTime - startTime of the walk
     */
    public Walk(long startTime) {
        this.startTime = startTime;
        distance = 0;
        speed = 0;
        steps = 0;
    }

    /**
     * Getter of steps
     * @return number of steps during the walk
     */
    public int getSteps() {return steps;}

    /**
     * Getter of distance
     * @return distance covered during the walk, in meters
     */
    public float getDistance() {return distance;}

    /**
     * Getter of speed
     * @return average speed during the walk, in m/s
     */
    public float getSpeed() {return speed;}

    /**
     * Getter of startTime
     * @return startTime of the walk, in millis
     */
    public long StartTime() {return startTime;}

    /**
     * Getter of endTime
     * @return endTime of the walk, in millis
     */
    public long EndTime() {return endTime;}

    /**
     * Setter of steps
     * @param steps - number of steps
     */
    public void setSteps(int steps) {this.steps = steps;}

    /**
     * Setter of distance
     * @param distance - distance covered
     */
    public void setDistance(float distance) {this.distance = distance;}

    /**
     * Setter of speed
     * @param speed - average speed of the walk
     */
    public void setSpeed(float speed) {this.speed = speed;}

    /**
     * Setter of endTime
     * @param endTime - endTime of the walk
     */
    public void setEndTime(long endTime) {this.endTime = endTime;}
}
