package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.Computer;
import bgu.atd.a1.sim.Warehouse;

import java.util.List;

public class AddComputers extends Action {
     private List<Computer> computers;

     public AddComputers(List<Computer> computers){
         this.actionName = "Add Computer";
         this.computers = computers;
         this.promise = new Promise();
     }

    public void start(){
        Warehouse privateState = (Warehouse)this.actorState;
        privateState.setComputerLockers(computers);
        complete(true);
        this.pool.makeActorAvailable(this.actorId);
    }
}
