package com.example.team19.personalbest;
import com.example.team19.personalbest.fitness.Walk;

import org.junit.*;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;

public class TestClassWalk {
    Walk walk;
    long startTime;

    @Before
    public void Setup() {
        Calendar cal = Calendar.getInstance();
        startTime = cal.getTimeInMillis();
        walk = new Walk(startTime);
    }

    @Test
    public void testStartTime() {
        assertEquals(startTime, walk.StartTime());
    }

    @Test
    public void testSetAndGetSteps() {
        assertEquals(0, walk.getSteps(),0);
        walk.setSteps(800);
        assertEquals(800, walk.getSteps(),0);
    }

    @Test
    public void testSetAndGetDistance() {
        assertEquals(0, walk.getDistance(), 0);
        walk.setDistance(500);
        assertEquals(500, walk.getDistance(), 0);
    }

    @Test
    public void testSetAndGetSpeed() {
        assertEquals(0, walk.getSpeed(),0);
        walk.setSpeed(2);
        assertEquals(2, walk.getSpeed(),0);
    }

    @Test
    public void testSetAndGetEndTime() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        walk.setEndTime(endTime);
        assertEquals(endTime, walk.EndTime());
    }
}
