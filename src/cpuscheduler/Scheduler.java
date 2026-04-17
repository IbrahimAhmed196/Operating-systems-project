package cpuscheduler;

import java.util.List;

public interface Scheduler {
    void addProcess(Process p);
    void tick(); // Advances time by 1 unit and processes the logic
    Process getCurrentProcess(); // Returns who is on the CPU right now
    boolean isFinished(); 
    List<Process> getAllProcesses(); // To update your JavaFX Table
    double getAverageWaitingTime();
    double getAverageTurnaroundTime();
    Process getLastExecutedProcess();
    int getCurrentTime();
}
