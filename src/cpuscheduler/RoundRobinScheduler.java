/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cpuscheduler;
import java.util.*;
/**
 *
 * @author IbrahimAhmedBadr
 */
public class RoundRobinScheduler implements Scheduler {
    private List<Process> processes = new ArrayList<>();
    private Queue<Process> readyQueue = new LinkedList<>();

    private Set<String> inQueue = new HashSet<>();

    private Process currentProcess = null;
    private Process lastExecutedProcess = null;
    private int time = 0;

    private int quantum;
    private int remainingQuantum = 0;

    public RoundRobinScheduler(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process p) {
        processes.add(p);
    }

    private void addArrivedProcesses() {
        for (Process p : processes) {
            if (p.arrivalTime <= time &&
                p.remainingTime > 0 &&
                !inQueue.contains(p.id) &&
                p != currentProcess) {

                readyQueue.add(p);
                inQueue.add(p.id);
            }
        }
    }

    @Override
    public void tick() {

        addArrivedProcesses();

        if (currentProcess == null) {
            currentProcess = readyQueue.poll();

            if (currentProcess != null) {
                remainingQuantum = quantum;
            }
        }

        if (currentProcess == null) {
            lastExecutedProcess = null;
            time++;
            return;
        }

        lastExecutedProcess = currentProcess;
        currentProcess.remainingTime--;
        remainingQuantum--;
        time++;

        addArrivedProcesses();

        if (currentProcess.remainingTime == 0) {
            currentProcess.finishTime = time;
            currentProcess.turnaroundTime =
                    currentProcess.finishTime - currentProcess.arrivalTime;

            currentProcess.waitingTime =
                    currentProcess.turnaroundTime - currentProcess.burstTime;

            lastExecutedProcess = currentProcess;
            currentProcess = null;
        }
        else if (remainingQuantum == 0) {
            readyQueue.add(currentProcess);
            currentProcess = null;
        }
    }

    @Override
    public Process getCurrentProcess() {
        return currentProcess;
    }

    @Override
    public Process getLastExecutedProcess() {
        return lastExecutedProcess;
    }

    @Override
    public boolean isFinished() {
        for (Process p : processes) {
            if (p.remainingTime > 0) return false;
        }
        return true;
    }

    @Override
    public List<Process> getAllProcesses() {
        return processes;
    }

    @Override
    public double getAverageWaitingTime() {
        double sum = 0;
        for (Process p : processes) sum += p.waitingTime;
        return processes.isEmpty() ? 0 : sum / processes.size();
    }

    @Override
    public double getAverageTurnaroundTime() {
        double sum = 0;
        for (Process p : processes) sum += p.turnaroundTime;
        return processes.isEmpty() ? 0 : sum / processes.size();
    }
    
    @Override
    public int getCurrentTime() {
        return time;
    }
}
