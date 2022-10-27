package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Unregister extends Action {
    private String student;

    public Unregister(String student, CountDownLatch phaseCounter) {
        this.phaseCounter = phaseCounter;
        this.actionName = "Unregister";
        this.student = student;
        this.promise = new Promise();
        this.actions = new ArrayList<>();
    }

    public void start() {
        CoursePrivateState privateState = (CoursePrivateState) this.actorState;
        privateState.addRecord(this.actionName);
        if (privateState.getRegStudents().contains(student)) {
            privateState.incrementAvailableSpace();
            privateState.decrementRegistered();
            Action<Boolean> studentAction = new RemoveCourseGrade(this.actorId);
            this.actions.add(studentAction);
            then(actions, () -> {
                privateState.getRegStudents().remove(this.student);
                complete(true);
                this.phaseCounter.countDown();
                this.pool.makeActorAvailable(this.actorId);
            });
            sendMessage(studentAction, this.student, new StudentPrivateState());
            this.pool.makeActorAvailable(this.actorId);
        } else {
            complete(false);
            this.phaseCounter.countDown();
            this.pool.makeActorAvailable(this.actorId);
        }
    }
}
