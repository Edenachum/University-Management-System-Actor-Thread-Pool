package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.Computer;
import bgu.atd.a1.sim.Warehouse;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class CheckConditions extends Action {
    private String studentName;
    private HashMap<String,String> studentGrades;
    private List<String> courses;
    private String computerType;

    public CheckConditions(String studentName,HashMap<String,String> studentGrades, List<String> courses, String computerType){
        this.actionName = "Check Conditions";
        this.studentName = studentName;
        this.studentGrades = studentGrades;
        this.courses = courses;
        this.computerType = computerType;
        this.promise = new Promise();
    }

    public void start(){
        Warehouse privateState = (Warehouse)this.actorState;
        Computer computer = privateState.getComputer(this.computerType);
        long sig = computer.checkAndSign(this.courses,this.studentGrades);
        complete(new Pair<String,Long>(this.studentName,sig));

        this.pool.makeActorAvailable(this.actorId);
    }
}
