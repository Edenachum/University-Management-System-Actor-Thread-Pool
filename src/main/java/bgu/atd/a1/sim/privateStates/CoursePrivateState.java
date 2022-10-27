package bgu.atd.a1.sim.privateStates;

import java.util.ArrayList;
import java.util.List;


import bgu.atd.a1.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState {

    private String Course;
    private long availableSpots;
    private int registered;
    private List<String> regStudents;
    private List<String> prerequisites;

    /**
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail.
     */
    public CoursePrivateState() {
        super();
        this.registered = 0;
        this.regStudents = new ArrayList<>();
        this.prerequisites = new ArrayList<>();

    }

    public long getAvailableSpaces() {
        return availableSpots;
    }

    public int getRegistered() {
        return registered;
    }

    public List<String> getRegStudents() {
        return regStudents;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setAvailableSpaces(long availableSpaces) {
        this.availableSpots = availableSpaces;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public void addRegStudents(String student) {
        regStudents.add(student);
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setCourseName(String courseName) {
        this.Course = courseName;
    }

    public boolean isAvailableSpace() {
        return this.availableSpots > 0;
    }
    public boolean isCourseClose(){return this.availableSpots==-1;}
    public void decrementAvailableSpace (){this.availableSpots--;}
    public void incrementAvailableSpace() {this.availableSpots++;}
    public void decrementRegistered (){this.registered--;}
    public void incrementRegistered (){this.registered++;}
    public void clearRegStudents (){ this.regStudents.clear();}


}
