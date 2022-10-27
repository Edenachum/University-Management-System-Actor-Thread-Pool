package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class CreateStudent extends Action {

    public CreateStudent(){
        this.actionName = "Create Student";
        this.promise = new Promise();
    }
    public void start(){
        ((StudentPrivateState)this.actorState).setStudent(actorId);
        complete(true);
        this.pool.makeActorAvailable(this.actorId);

    }
}
