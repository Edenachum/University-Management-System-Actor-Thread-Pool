package bgu.atd.a1.sim.actions;

import bgu.atd.a1.Action;
import bgu.atd.a1.Promise;
import bgu.atd.a1.sim.Warehouse;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AdministrativeCheck extends Action {
    private List<String> students;
    private String computerType;
    private List<String> conditions;

    public AdministrativeCheck(List<String> students, String computerType, List<String> conditions, CountDownLatch phaseCounter) {
        this.actionName = "Administrative Check";
        this.phaseCounter = phaseCounter;
        this.students = students;
        this.computerType = computerType;
        this.conditions = conditions;
        this.promise = new Promise();
        this.actions = new ArrayList();
    }


    public void start() {
        DepartmentPrivateState privateState = (DepartmentPrivateState) this.actorState;
        privateState.setDepartment(this.actorId);
        privateState.addRecord(this.actionName);
        HashMap<String, Action> studentsHash = new HashMap<>();
        for (String student : students) {
            Action studentAction = new GetGrades();
            this.actions.add(studentAction);
            studentsHash.put(student, studentAction);
        }
        then(this.actions, () -> {
            List<Action> warehouseList = new ArrayList<>();
            this.actions.clear();
            for (Map.Entry<String, Action> set : studentsHash.entrySet()) {
                HashMap<String, String> studentGrades = (HashMap<String, String>) set.getValue().getResult().get();
                Action warehouseAction = new CheckConditions(set.getKey(), studentGrades, this.conditions, this.computerType);
                warehouseList.add(warehouseAction);
                this.actions.add(warehouseAction);
            }
            then(this.actions, () -> {
                        this.actions.clear();
                        HashMap<String, Action> studentHash2 = new HashMap<>();
                        for (Action action : warehouseList) {
                            Pair<String, Long> result = (Pair)action.getResult().get();
                            Action<Boolean> updateStudentSig = new UpdateStudentSig(result.getValue());
                            this.actions.add(updateStudentSig);
                            studentHash2.put(result.getKey(), updateStudentSig);
                        }
                        then(this.actions, () -> {
                            complete(true);
                            phaseCounter.countDown();
                            this.pool.makeActorAvailable(this.actorId);
                        });
                        for (Map.Entry<String, Action> set : studentHash2.entrySet()) {
                            sendMessage(set.getValue(), set.getKey(), new StudentPrivateState());
                        }
                        this.pool.makeActorAvailable(this.actorId);
                    }
            );
            for (Action action : warehouseList) {
                sendMessage(action, "warehouse", new Warehouse());
            }
            this.pool.makeActorAvailable(this.actorId);

        });
        for (Map.Entry<String, Action> set : studentsHash.entrySet()) {
            sendMessage(set.getValue(), set.getKey(), new StudentPrivateState());
        }
        this.pool.makeActorAvailable(this.actorId);

    }
}
