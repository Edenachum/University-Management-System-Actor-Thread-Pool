package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CloseCourseSub extends Action {

    public CloseCourseSub() {
        this.actionName = "Close Course Sub";
        this.promise = new Promise();
        this.actions = new ArrayList<>();
    }

    public void start() {
        CoursePrivateState privateState = (CoursePrivateState) this.actorState;
        privateState.setAvailableSpaces(-1);
        privateState.setRegistered(0);
        privateState.clearRegStudents();
        if (privateState.getRegStudents().size() > 0) {
            HashMap<String, Action> studentsHash = new HashMap<>();
            for (String student : privateState.getRegStudents()) {
                Action<Boolean> studentAction = new RemoveCourseGrade(this.actorId);
                this.actions.add(studentAction);
                studentsHash.put(student, studentAction);
            }
            then(this.actions, () -> {
                complete(true);
                this.pool.makeActorAvailable(this.actorId);
            });
            for (Map.Entry<String, Action> set : studentsHash.entrySet()) {
                sendMessage(set.getValue(), set.getKey(), new StudentPrivateState());
            }
            this.pool.makeActorAvailable(this.actorId);
        }
        else{
            complete(true);
            this.pool.makeActorAvailable(this.actorId);
        }


    }
}
