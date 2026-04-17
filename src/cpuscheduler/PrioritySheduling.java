/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cpuscheduler;
import java.util.* ;

/**
 *
 * @author dell
 */
class PrioritySheduling implements Scheduler {
    List<Process> incomingProcesses = new ArrayList<>();
    List<Process> completedProcesses = new ArrayList<>();
    List<Process> readyQueue = new ArrayList<>();

    Process currentProcess = null;
    Process lastExecutedProcess = null;
    int currentTime = 0;
    boolean preemptive;

    public PrioritySheduling(boolean preemptive) {
        this.preemptive = preemptive;
    }

    @Override
    public void addProcess(Process p) {
        incomingProcesses.add(p);
    }

    @Override
    public void tick() {

        // Move arrived processes
        Iterator<Process> it = incomingProcesses.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.arrivalTime <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }

        // Sort by priority (lower number = higher priority)
        readyQueue.sort(Comparator.comparingInt(p -> p.priority));

        if (preemptive && currentProcess != null && !readyQueue.isEmpty()) {
            Process next = readyQueue.get(0);
            if (next.priority < currentProcess.priority) {
                readyQueue.add(currentProcess);
                currentProcess = null;
            }
        }

        // Pick next process
        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.remove(0);
        }

        // Execute
        if (currentProcess != null) {
            lastExecutedProcess = currentProcess;
            currentProcess.remainingTime--;

            if (currentProcess.remainingTime == 0) {
                currentProcess.finishTime = currentTime + 1;
                currentProcess.turnaroundTime =
                        currentProcess.finishTime - currentProcess.arrivalTime;
                currentProcess.waitingTime =
                        currentProcess.turnaroundTime - currentProcess.burstTime;

                completedProcesses.add(currentProcess);
                currentProcess = null;
            }
        } else {
            lastExecutedProcess = null;
        }

        currentTime++;
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
        return incomingProcesses.isEmpty()
                && readyQueue.isEmpty()
                && currentProcess == null;
    }

    @Override
    public List<Process> getAllProcesses() {
        List<Process> all = new ArrayList<>();
        all.addAll(incomingProcesses);
        all.addAll(readyQueue);
        all.addAll(completedProcesses);
        if (currentProcess != null) all.add(currentProcess);
        return all;
    }

    @Override
    public double getAverageWaitingTime() {
        return completedProcesses.stream()
                .mapToInt(p -> p.waitingTime)
                .average().orElse(0);
    }

    @Override
    public double getAverageTurnaroundTime() {
        return completedProcesses.stream()
                .mapToInt(p -> p.turnaroundTime)
                .average().orElse(0);
    }
    
    @Override
    public int getCurrentTime() {
        return currentTime;
    }
}
 