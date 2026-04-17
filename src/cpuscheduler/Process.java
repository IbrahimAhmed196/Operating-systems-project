package cpuscheduler;

class Process {
    public String id;
    public int arrivalTime;
    public int burstTime;
    public int remainingTime;
    public int priority; 
    public int quantum;
    public int startTime = -1;
    public int finishTime = 0;
    public int waitingTime = 0;
    public int turnaroundTime = 0;

    public Process(String id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }
}