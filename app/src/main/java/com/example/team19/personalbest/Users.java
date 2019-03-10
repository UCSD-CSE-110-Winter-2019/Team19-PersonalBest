package com.example.team19.personalbest;

import java.io.Serializable;
import java.util.Map;

public class Users implements Serializable {

    private String Email;
    private Map<String, Integer> Steps;

    public Map<String, Integer> getSteps() {
        return Steps;
    }

    public void setSteps(Map<String, Integer> steps) {
        Steps = steps;
    }

    public Users(){}

    public Users(String Email, Map<String, Integer> Steps) {
        this.Email = Email;
        this.Steps = Steps;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

}
