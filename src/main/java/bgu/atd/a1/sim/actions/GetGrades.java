package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class GetGrades extends Action {

    public GetGrades(){
        this.actionName = "Get Grade";
        this.promise = new Promise();
    }

    public void start(){
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;
        privateState.setStudent(actorId);
        complete(privateState.getGrades());
        this.pool.makeActorAvailable(this.actorId);
    }
}
