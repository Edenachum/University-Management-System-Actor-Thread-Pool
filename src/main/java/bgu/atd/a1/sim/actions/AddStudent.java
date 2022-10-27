package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class AddStudent extends Action {
    private String department;
    private String student;

    public AddStudent (String department, String student, CountDownLatch phaseCounter){
        this.phaseCounter = phaseCounter;
        this.actionName = "Add Student";
        this.department = department;
        this.student = student;
        this.promise = new Promise();
        this.actions = new ArrayList();
    }
    public void start(){
        DepartmentPrivateState privateState = (DepartmentPrivateState)this.actorState;
        privateState.setDepartment(this.actorId);
        privateState.addRecord(this.actionName);
        Action<Boolean> createStudent = new CreateStudent();
        this.actions.add(createStudent);
        then(actions,()->{
            privateState.addStudent(this.student);
            complete(true);
            phaseCounter.countDown();
            this.pool.makeActorAvailable(this.department);
        });
        sendMessage(createStudent,this.student,new StudentPrivateState());
        this.pool.makeActorAvailable(this.department);
    }
}
