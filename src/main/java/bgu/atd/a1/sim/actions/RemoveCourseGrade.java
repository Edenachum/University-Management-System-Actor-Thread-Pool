package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class RemoveCourseGrade extends Action {
    private String courseName;

    public RemoveCourseGrade(String courseName){
        this.actionName = "Remove Course Grade";
        this.courseName = courseName;
        this.promise = new Promise();
    }

    public void start(){
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;
        privateState.setStudent(actorId);
        privateState.getGrades().remove(this.courseName);
        complete(true);
        this.pool.makeActorAvailable(this.actorId);
    }
}
