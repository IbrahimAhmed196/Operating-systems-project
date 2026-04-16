import java.util.*;

class SJFPreemptiveScheduler {
    List<Process> incomingProcesses = new ArrayList<>();
    PriorityQueue<Process> readyQueue;
    Process currentProcess = null;
    int currentTime = 0;

    public SJFPreemptiveScheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
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

        if (currentProcess != null) {
            readyQueue.add(currentProcess);
            currentProcess = null;
        }

        if (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
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