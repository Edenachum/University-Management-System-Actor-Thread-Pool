package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParticipatingInCourse extends Action {
    private String student;
    private String course;
    private String[] grade;
    private boolean isPhaseCounterRelevant;

    public ParticipatingInCourse(String student, String course, String[] grade, CountDownLatch phaseCounter,boolean isPhaseCounterRelevant){
        this.phaseCounter = phaseCounter;
        this.actionName = "Participate In Course";
        this.student = student;
        this.course = course;
        this.grade = grade;
        this.promise = new Promise();
        this.actions = new ArrayList<>();
        this.isPhaseCounterRelevant = isPhaseCounterRelevant;
    }

    public void start(){
        CoursePrivateState privateState = (CoursePrivateState)actorState;
        privateState.addRecord(this.actionName);
        if(privateState.isAvailableSpace()){
            privateState.decrementAvailableSpace();
            privateState.incrementRegistered();
            Action <Boolean> checkPrerequisites = new CheckPrerequisites(privateState.getPrerequisites());
            this.actions.add(checkPrerequisites);
            then(actions,()->{
                if(checkPrerequisites.getResult().get()){
                    privateState.addRegStudents(this.student);
                    Action<Boolean> updateCourseGrade = new UpdateCourseGrade(this.grade[0],this.course);
                    this.actions.clear();
                    this.actions.add(updateCourseGrade);
                    then(actions,()->{
                        complete(true);
                        if(isPhaseCounterRelevant)
                            phaseCounter.countDown();
                        this.pool.makeActorAvailable(this.actorId);
                    } );
                    sendMessage(updateCourseGrade,this.student,new StudentPrivateState());
                    this.pool.makeActorAvailable(this.actorId);
                }else{
                    privateState.incrementAvailableSpace();
                    privateState.decrementRegistered();
                    complete(false);
                    if(isPhaseCounterRelevant)
                        phaseCounter.countDown();
                    this.pool.makeActorAvailable(this.actorId);
                }
            });
            sendMessage(checkPrerequisites,this.student,new StudentPrivateState());
            this.pool.makeActorAvailable(this.actorId);
        }else{
            complete(false);
            if(isPhaseCounterRelevant)
                phaseCounter.countDown();
            this.pool.makeActorAvailable(this.actorId);
        }

    }
}
