package bgu.atd.a1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far 
 * IMPORTANT: You can not add any field to this class.
 */
public abstract class PrivateState implements Serializable {
	
	// holds the actions' name what were executed
	private List<String> actions;

	public PrivateState (){
		actions = new ArrayList();
	}

	public List<String> getLogger(){
		return actions;
	}
	
	/**
	 * add an action to the records
	 *  
	 * @param actionName
	 */
	public void addRecord(String actionName){
		actions.add(actionName);
	}
	
	
}
