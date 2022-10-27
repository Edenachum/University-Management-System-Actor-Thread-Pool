package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;

import java.util.concurrent.CountDownLatch;

public class AddSpaces extends Action {
    long increment;
    public AddSpaces (long increment, CountDownLatch phaseCounter){
        this.actionName = "Add Spaces";
        this.phaseCounter = phaseCounter;
        this.increment = increment;
        this.promise = new Promise();
    }

    public void start(){
        CoursePrivateState privateState = (CoursePrivateState)this.actorState;
        privateState.addRecord(this.actionName);
        if(!privateState.isCourseClose()){
            privateState.setAvailableSpaces(privateState.getAvailableSpaces() + increment);
            complete(true);
        } else {
            complete(false);
        }
        this.phaseCounter.countDown();
        this.pool.makeActorAvailable(this.actorId);
    }
}
