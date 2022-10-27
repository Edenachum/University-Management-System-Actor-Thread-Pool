package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CloseACourse extends Action {
    private String courseName;

    public CloseACourse(String CourseName, CountDownLatch phaseCounter) {
        this.actionName = "Close Course";
        this.phaseCounter = phaseCounter;
        this.courseName = CourseName;
        this.promise = new Promise();
        this.actions = new ArrayList<>();
    }

    public void start() {
        DepartmentPrivateState privateState = (DepartmentPrivateState) this.actorState;
        privateState.setDepartment(this.actorId);
        actorState.addRecord(this.actionName);
        if(privateState.getCourseList().contains(this.courseName)) {
            Action<Boolean> courseAction = new CloseCourseSub();
            this.actions.add(courseAction);
            then(this.actions, () -> {
                privateState.getCourseList().remove(this.courseName);
                complete(true);
                this.phaseCounter.countDown();
                this.pool.makeActorAvailable(this.actorId);
            });
            sendMessage(courseAction, this.courseName, new CoursePrivateState());
            this.pool.makeActorAvailable(this.actorId);
        } else {
            complete(false);
            this.phaseCounter.countDown();
            this.pool.makeActorAvailable(this.actorId);
        }

    }
}
