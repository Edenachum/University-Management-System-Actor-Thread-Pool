package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

public class UpdateStudentSig extends Action {
    private long sig;

    public UpdateStudentSig(long sig){
        this.actionName = "Update Student Sig";
        this.sig = sig;
        this.promise = new Promise();
    }

    public void start(){
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;
        privateState.setStudent(actorId);
        privateState.setSignature(this.sig);
        this.complete(true);
        this.pool.makeActorAvailable(this.actorId);

    }
}
