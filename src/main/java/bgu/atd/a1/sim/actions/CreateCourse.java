package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;

import java.util.List;


public class CreateCourse extends Action {


    private String courseName;
    private long space;
    private List<String> prerequisites;

    public CreateCourse(String courseName, long space, List<String> prerequisites) {
        this.actionName = "Create Course";
        this.courseName = courseName;
        this.space = space;
        this.prerequisites = prerequisites;
        this.promise = new Promise();
    }

    @Override
    protected void start() {
        CoursePrivateState privateState = ((CoursePrivateState) this.actorState);
        privateState.setAvailableSpaces(this.space);
        privateState.setPrerequisites(this.prerequisites);
        privateState.setCourseName(this.courseName);
        complete(true);
        pool.makeActorAvailable(this.courseName);
    }
}
