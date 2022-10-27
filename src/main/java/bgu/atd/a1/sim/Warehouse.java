package bgu.atd.a1.sim;

import bgu.atd.a1.PrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse extends PrivateState {
    private List<Computer> computers;

    public Warehouse() {
        computers = new ArrayList<>();
    }

    public void setComputerLockers(List<Computer> computers) {
        this.computers = computers;
    }

    public Computer getComputer(String computerType) {
        for (Computer computer : computers) {
            if (computer.getComputerType().equals(computerType)) {
                return computer;
            }
        }
        return null;
    }
}
