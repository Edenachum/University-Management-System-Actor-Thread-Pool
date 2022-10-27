package bgu.atd.a1.sim;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Computer implements Serializable {

	String computerType;
	long failSig;
	long successSig;
	
	public Computer(String computerType, long failSig, long successSig)
	{
		this.computerType = computerType;
		this.failSig = failSig;
		this.successSig = successSig;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, String> coursesGrades){
		for (String courseName: courses){
			if(!coursesGrades.containsKey(courseName)||coursesGrades.get(courseName) =="-"|| Integer.parseInt(coursesGrades.get(courseName))< 56){
				return failSig;
			}
		}
		return successSig;
	}
	public String getComputerType() {
		return computerType;
	}
}
