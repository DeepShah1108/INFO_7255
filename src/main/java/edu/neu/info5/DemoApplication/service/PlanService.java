package edu.neu.info5.DemoApplication.service;

public interface PlanService {

    public void createPlan(String key, String value);

    public boolean deletePlan(String key);

    public String readPlan(String key);


}
