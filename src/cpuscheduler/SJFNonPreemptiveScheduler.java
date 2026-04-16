import java.util.*;

class SJFNonPreemptiveScheduler {
    List<Process> incomingProcesses = new ArrayList<>();
    PriorityQueue<Process> readyQueue;
    Process currentProcess = null;
    int currentTime = 0;

    public SJFNonPreemptiveScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));
    }

    public void addProcess(Process p) {
        incomingProcesses.add(p);
    }

    public void tick() {
        Iterator<Process> it = incomingProcesses.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            if (p.arrivalTime <= currentTime) {
                readyQueue.add(p);
                it.remove();
            }
        }

        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
        }

        if (currentProcess != null) {
            currentProcess.remainingTime--;
            
            if (currentProcess.remainingTime == 0) {
                currentProcess = null;
            }
        }
        
        currentTime++;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }
}