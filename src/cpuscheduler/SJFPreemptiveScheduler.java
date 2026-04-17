package cpuscheduler;

import java.util.*;

class SJFPreemptiveScheduler implements Scheduler {
    List<Process> incomingProcesses = new ArrayList<>();
    List<Process> completedProcesses = new ArrayList<>();
    PriorityQueue<Process> readyQueue;
    Process currentProcess = null;
    Process lastExecutedProcess = null;
    int currentTime = 0;

    public SJFPreemptiveScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
    }
    
    @Override
    public void addProcess(Process p) {
        incomingProcesses.add(p);
    }
    
    @Override
    public void tick() {
        Iterator<Process> it = incomingProcesses.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.arrivalTime <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }

        if (currentProcess != null) {
            readyQueue.add(currentProcess);
            currentProcess = null;
        }

        if (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
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
                .average()
                .orElse(0);
    }
    
    @Override
    public double getAverageTurnaroundTime() {
        return completedProcesses.stream()
                .mapToInt(p -> p.turnaroundTime)
                .average()
                .orElse(0);
    }
    
    @Override
    public int getCurrentTime() {
        return currentTime;
    }
}