package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.List;

public class CheckPrerequisites extends Action {
    private List<String> prerequisitesCourse;

    public CheckPrerequisites (List<String> prerequisitesCourse){
        this.actionName = "Check Prerequisites";
        this.prerequisitesCourse = prerequisitesCourse;
        this.promise = new Promise();
    }

    public void start(){
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;
        privateState.setStudent(this.actorId);
        complete(privateState.areAllPrerequisitesExist(prerequisitesCourse));
        this.pool.makeActorAvailable(this.actorId);
    }
}
