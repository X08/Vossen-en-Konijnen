package main;


/**
 * Main class to operate first
 * 
 * @author Ieme, Jermo, Yisong
 * @version 2012.01.29
 */ 
public class MainProgram
{
    private static Simulator simulator;

    /**
     * Main methode
     */ 
    public static void main(String[] args) {
        setSimulator(new Simulator());
    }

    public static Simulator getSimulator() {
        return simulator;
    }

    public static void setSimulator(Simulator simulator) {
        MainProgram.simulator = simulator;
    }
}