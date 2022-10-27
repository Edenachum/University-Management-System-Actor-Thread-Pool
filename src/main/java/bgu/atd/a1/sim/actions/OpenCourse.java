package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OpenCourse extends Action<Boolean> {

    private String department;
    private String course;
    private long space;
    private List<String> prerequisites;

    public OpenCourse(String department, String course, long space, List<String> prerequisites, CountDownLatch phaseCounter) {
        this.phaseCounter = phaseCounter;
        this.actionName = "Open Course";
        this.department = department;
        this.course = course;
        this.space = space;
        this.prerequisites = prerequisites;
        this.actions = new ArrayList<>();
        this.promise = new Promise<>();
    }

    @Override
    protected void start() {
        this.actorState.addRecord(this.actionName);
        DepartmentPrivateState privateState = (DepartmentPrivateState) actorState;
        privateState.setDepartment(this.actorId);
        privateState.addCourse(this.course);

        Action<Boolean> createCourse = new CreateCourse(this.course, this.space, this.prerequisites);
        this.actions.add(createCourse);
        then(this.actions, () -> {
            complete(true);
            phaseCounter.countDown();
            pool.makeActorAvailable(this.actorId);
        });
        sendMessage(createCourse, this.course, new CoursePrivateState());
        this.pool.makeActorAvailable(this.actorId);
    }
}
