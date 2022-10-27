package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class UpdateCourseGrade extends Action {
    private String grade;
    private String courseName;

    public UpdateCourseGrade(String grade, String courseName) {
        this.actionName = "Update Course Grade";
        this.grade = grade;
        this.courseName = courseName;
        this.promise = new Promise();
    }

    public void start() {
        StudentPrivateState privateState = (StudentPrivateState) this.actorState;
        privateState.setStudent(actorId);
        privateState.addCourseGrade(this.courseName, this.grade);
        complete(true);
        this.pool.makeActorAvailable(this.actorId);
    }
}
