package bgu.atd.a1.sim.privateStates;

import java.util.HashMap;
import java.util.List;


import bgu.atd.a1.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState {

    private HashMap<String, String> grades;
    private long signature;
    private String Student;

    /**
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     */
    public StudentPrivateState() {
        this.grades = new HashMap<>();

    }
    public HashMap<String, String> getGrades() {
        return grades;
    }

    public long getSignature() {
        return signature;
    }

    public boolean areAllPrerequisitesExist(List<String> prerequisites) {
        for (String prerequisite : prerequisites) {
            if (!grades.containsKey(prerequisite)) {
                return false;
            }
        }
        return true;
    }
    public void addCourseGrade (String course, String grade){
    	grades.put(course,grade);
	}

    public void setSignature(long signature) {
        this.signature = signature;
    }

    public void setStudent(String student) {
        Student = student;
    }
}
