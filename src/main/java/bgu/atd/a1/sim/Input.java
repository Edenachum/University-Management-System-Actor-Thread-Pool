package bgu.atd.a1.sim;


import com.google.gson.annotations.SerializedName;

public class Input {
    public class Computer {
        @SerializedName("Type")
        String type;
        @SerializedName("Sig Success")
        long sigSuccess;
        @SerializedName("Sig Fail")
        long sigFail;

    }

    public class Action {
        @SerializedName("Action")
        String action;
        @SerializedName("Department")
        String department;
        @SerializedName("Course")
        String course;
        @SerializedName("Space")
        long space;
        @SerializedName("Prerequisites")
        String[] prerequisites;
        @SerializedName("Student")
        String student;
        @SerializedName("Grade")
        String[] grade;
        @SerializedName("Conditions")
        String[] conditions;
        @SerializedName("Students")
        String[] students;
        @SerializedName("Computer")
        String computer;
        @SerializedName("Preferences")
        String [] preferences;
    }

    int threads;
    @SerializedName("Computers")
    Computer[] computers;
    @SerializedName("Phase 1")
    Action[] phase1;
    @SerializedName("Phase 2")
    Action[] phase2;
    @SerializedName("Phase 3")
    Action[] phase3;


}
