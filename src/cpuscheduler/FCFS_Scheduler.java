import java.util.*;

class FCFS_Scheduler {

    List<Process> incomingProcesses = new ArrayList<>();
    List<Process> completedProcesses = new ArrayList<>();
    Queue<Process> readyQueue = new LinkedList<>();

    Process currentProcess = null;
    int currentTime = 0;

    public void addProcess(Process p) {
        incomingProcesses.add(p);
    }

    public void tick() {

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
            currentProcess.remainingTime--;

            if (currentProcess.remainingTime == 0) {
                // Calculate required metrics
                currentProcess.completionTime = currentTime + 1;
                currentProcess.turnaroundTime =
                        currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime =
                        currentProcess.turnaroundTime - currentProcess.burstTime;

                completedProcesses.add(currentProcess);
                currentProcess = null;
            }
        }

        currentTime++;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    // Useful for GUI
    public List<Process> getAllProcesses() {
        List<Process> all = new ArrayList<>();
        all.addAll(incomingProcesses);
        all.addAll(readyQueue);
        all.addAll(completedProcesses);
        if (currentProcess != null) all.add(currentProcess);
        return all;
    }

    public boolean isFinished() {
        return incomingProcesses.isEmpty()
                && readyQueue.isEmpty()
                && currentProcess == null;
    }
    
    public double getAverageWaitingTime() {
        return completedProcesses.stream()
                .mapToInt(p -> p.waitingTime)
                .average()
                .orElse(0);
    }

    public double getAverageTurnaroundTime() {
        return completedProcesses.stream()
                .mapToInt(p -> p.turnaroundTime)
                .average()
                .orElse(0);
    }
    
}
