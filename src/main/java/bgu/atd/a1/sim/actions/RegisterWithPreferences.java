package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class RegisterWithPreferences extends Action {
    private String student;
    private String[] preferences;
    private String[] grades;

    public RegisterWithPreferences(String student, String[] preferences, String[] grades, CountDownLatch phaseCounter){
        this.actionName = "Register With Preferences";
        this.student = student;
        this.preferences = preferences;
        this.grades = grades;
        this.phaseCounter = phaseCounter;
        this.promise = new Promise();
        this.actions = new ArrayList();
    }

    public void start(){
        StudentPrivateState privateState = (StudentPrivateState)this.actorState;
        privateState.setStudent(this.student);
        privateState.addRecord(this.actionName);
        recFun(0);
    }

    private void recFun(int index){
        String[] gradeOfCourse = {this.grades[index]};
        ParticipatingInCourse courseActor = new ParticipatingInCourse(this.student,this.preferences[index],gradeOfCourse,new CountDownLatch(0),false);
        this.actions.add(courseActor);
        then(this.actions,()->{
            Boolean result = (Boolean)courseActor.getResult().get();
            if(result){
                complete(true);
                this.phaseCounter.countDown();
                this.pool.makeActorAvailable(this.actorId);
            }else{
                if(this.preferences.length -1 == index){
                    complete(false);
                    this.phaseCounter.countDown();
                    this.pool.makeActorAvailable(this.actorId);
                }
                else{
                    recFun(index+1);
                }
            }
        });
        sendMessage(courseActor,this.preferences[index],new CoursePrivateState());
        this.pool.makeActorAvailable(this.actorId);
    }


}
