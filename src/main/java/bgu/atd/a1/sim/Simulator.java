/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.atd.a1.sim;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.*;

import bgu.atd.a1.Action;
import bgu.atd.a1.ActorThreadPool;
import bgu.atd.a1.PrivateState;

import bgu.atd.a1.sim.actions.*;
import bgu.atd.a1.sim.privateStates.CoursePrivateState;
import bgu.atd.a1.sim.privateStates.DepartmentPrivateState;
import bgu.atd.a1.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.concurrent.CountDownLatch;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	
	public static ActorThreadPool actorThreadPool;
	private static Input input = null;

	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
		actorThreadPool.start();
		CountDownLatch phase1ToPhase2 = new CountDownLatch(input.phase1.length);
		CountDownLatch phase2ToPhase3 = new CountDownLatch(input.phase2.length);
		CountDownLatch phase3ToEnd = new CountDownLatch(input.phase3.length);
		List<Computer> computers = new ArrayList<>();
		for (Input.Computer computer: input.computers){
			Computer computer1 = new Computer(computer.type,computer.sigFail,computer.sigSuccess);
			computers.add(computer1);
		}
		AddComputers addComputersAction = new AddComputers(computers);
		actorThreadPool.submit(addComputersAction, "warehouse", new Warehouse());

		for (Input.Action action: input.phase1){
				submitAction(action, phase1ToPhase2);
		}

		try {
			if(!actorThreadPool.getShouldTerminate())
				phase1ToPhase2.await();
			for (Input.Action action: input.phase2){
				submitAction(action, phase2ToPhase3);
			}
			if(!actorThreadPool.getShouldTerminate())
				phase2ToPhase3.await();
			for (Input.Action action: input.phase3){
				submitAction(action, phase3ToEnd);
			}
			if(!actorThreadPool.getShouldTerminate())
				phase3ToEnd.await();
			end();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public static void submitAction (Input.Action action, CountDownLatch phaseCounter){

		if (action.action.equals("Open Course")) {
			Action<Boolean> openCourseAction = new OpenCourse(action.department, action.course, action.space, Arrays.asList(action.prerequisites), phaseCounter);
			actorThreadPool.submit(openCourseAction, action.department, new DepartmentPrivateState());
		} else if (action.action.equals("Add Student")) {
			Action<Boolean> addStudent = new AddStudent(action.department, action.student, phaseCounter);
			actorThreadPool.submit(addStudent, action.department, new DepartmentPrivateState());
		} else if (action.action.equals("Participate In Course")) {
			Action<Boolean> participatingInCourse = new ParticipatingInCourse(action.student, action.course, action.grade, phaseCounter,true);
			actorThreadPool.submit(participatingInCourse, action.course, new CoursePrivateState());
		} else if(action.action.equals("Unregister")){
			Action<Boolean> unregister = new Unregister(action.student,phaseCounter);
			actorThreadPool.submit(unregister,action.course,new CoursePrivateState());
		} else if (action.action.equals("Close Course")){
			Action<Boolean> closeACourse = new CloseACourse(action.course,phaseCounter);
			actorThreadPool.submit(closeACourse,action.department,new DepartmentPrivateState());
		} else if (action.action.equals("Add Spaces")){
			Action<Boolean> addSpaces = new AddSpaces(action.space,phaseCounter);
			actorThreadPool.submit(addSpaces,action.course,new CoursePrivateState());
		}else if(action.action.equals("Administrative Check")){
			Action<Boolean> administrativeCheck = new AdministrativeCheck(Arrays.asList(action.students),action.computer,Arrays.asList(action.conditions),phaseCounter);
			actorThreadPool.submit(administrativeCheck,action.department,new DepartmentPrivateState());
		}else if(action.action.equals("Register With Preferences")){
			Action<Boolean> regAction = new RegisterWithPreferences(action.student,action.preferences,action.grade,phaseCounter);
			actorThreadPool.submit(regAction,action.student,new StudentPrivateState());
		}

	}
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		actorThreadPool = myActorThreadPool;
	}
	
	/**
	* shut down the simulation
	* returns list of private states
	*/

	public static HashMap<String,PrivateState> end(){
		try{
			actorThreadPool.shutdown();
		}catch (InterruptedException e){}
		Map<String, PrivateState> SimulationResult = actorThreadPool.getActors();
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HashMap<>(SimulationResult);

	}
	

	public static void main(String [] args){
		Gson gson = new Gson();
		HashMap<String, Object>  hash = new HashMap<>();
		try (Reader reader = new FileReader(args[0])) {
			input = gson.fromJson(reader, Input.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		attachActorThreadPool(new ActorThreadPool(input.threads));
		start();
	}
}
