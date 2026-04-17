package cpuscheduler;

import java.util.*;

class FCFS_Scheduler implements Scheduler {

    List<Process> incomingProcesses = new ArrayList<>();
    List<Process> completedProcesses = new ArrayList<>();
    Queue<Process> readyQueue = new LinkedList<>();

    Process currentProcess = null;
    Process lastExecutedProcess = null;
    int currentTime = 0;
    
    private void sortByArrivalTime() {
        incomingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
    }
    @Override
    public void addProcess(Process p) {
        incomingProcesses.add(p);
    }
    @Override
    public void tick() {
        
        if (currentTime == 0 && !incomingProcesses.isEmpty()) {
            sortByArrivalTime();
        }
        // Move arrived processes to ready queue
        Iterator<Process> it = incomingProcesses.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.arrivalTime <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }

        // If CPU is idle → pick next process
        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
        }

        // Execute current process
        if (currentProcess != null) {
            lastExecutedProcess = currentProcess;
            currentProcess.remainingTime--;

            if (currentProcess.remainingTime == 0) {
                // Calculate required metrics
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

    // Useful for GUI
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
    public boolean isFinished() {
        return incomingProcesses.isEmpty()
                && readyQueue.isEmpty()
                && currentProcess == null;
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
