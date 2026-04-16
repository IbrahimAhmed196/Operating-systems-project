/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package priority.sheduling;
import java.util.* ;

/**
 *
 * @author dell
 */
public class PrioritySheduling {
static class Process {
        String id;
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int priority;          // lower number = higher priority
        int startTime   = -1;
        int finishTime  = 0;
        int waitingTime = 0;
        int turnaroundTime = 0;
 
        Process(String id, int arrivalTime, int burstTime, int priority) {
            this.id            = id;
            this.arrivalTime   = arrivalTime;
            this.burstTime     = burstTime;
            this.remainingTime = burstTime;
            this.priority      = priority;
        }
    }
 

    //  Gantt chart entry
  
    static class GanttEntry {
        String processId;
        int start, end;
        GanttEntry(String pid, int s, int e) { processId = pid; start = s; end = e; }
    }
 

    //  NON-PREEMPTIVE Priority Scheduling
 
    static void nonPreemptive(List<Process> processes) {
 
        System.out.println("║   NON-PREEMPTIVE PRIORITY SCHEDULING     ║");

 
        // Work on copies
        List<Process> procs = deepCopy(processes);
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));
 
        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> done     = new ArrayList<>();
 
        int time = 0;
        int completed = 0;
        int n = procs.size();
 
        while (completed < n) {
            // Collect all processes that have arrived
            List<Process> ready = new ArrayList<>();
            for (Process p : procs) {
                if (p.arrivalTime <= time && p.remainingTime > 0) ready.add(p);
            }
 
            if (ready.isEmpty()) {
                time++;
                continue;
            }
 
            // Pick the highest-priority (lowest number) process
            ready.sort(Comparator.comparingInt(p -> p.priority));
            Process current = ready.get(0);
 
            if (current.startTime == -1) current.startTime = time;
 
            gantt.add(new GanttEntry(current.id, time, time + current.burstTime));
            time += current.burstTime;
 
            current.finishTime     = time;
            current.remainingTime  = 0;
            current.turnaroundTime = current.finishTime - current.arrivalTime;
            current.waitingTime    = current.turnaroundTime - current.burstTime;
 
            done.add(current);
            completed++;
        }
 
        printResults(done, gantt);
    }
 

    //  PREEMPTIVE Priority Scheduling
   
    static void preemptive(List<Process> processes) {
             System.out.println("║     PREEMPTIVE PRIORITY SCHEDULING       ║");
  
 
        List<Process> procs = deepCopy(processes);
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));
 
        List<GanttEntry> gantt = new ArrayList<>();
        List<Process> done = new ArrayList<>();
 
        int n = procs.size();
        int completed = 0;
        int time = 0;
        Process lastRun = null;
        int sliceStart = 0;
 
        while (completed < n) {
            // All arrived and still have work
            List<Process> ready = new ArrayList<>();
            for (Process p : procs) {
                if (p.arrivalTime <= time && p.remainingTime > 0) ready.add(p);
            }
 
            if (ready.isEmpty()) {
                if (lastRun != null) {
                    gantt.add(new GanttEntry(lastRun.id, sliceStart, time));
                    lastRun = null;
                }
                time++;
                continue;
            }
 
            ready.sort(Comparator.comparingInt(p -> p.priority));
            Process current = ready.get(0);
 
            // Record gantt slice when CPU switches process
            if (lastRun != current) {
                if (lastRun != null) gantt.add(new GanttEntry(lastRun.id, sliceStart, time));
                sliceStart = time;
                lastRun = current;
            }
 
            if (current.startTime == -1) current.startTime = time;
 
            current.remainingTime--;
            time++;
 
            if (current.remainingTime == 0) {
                current.finishTime     = time;
                current.turnaroundTime = current.finishTime - current.arrivalTime;
                current.waitingTime    = current.turnaroundTime - current.burstTime;
                done.add(current);
                completed++;
                gantt.add(new GanttEntry(current.id, sliceStart, time));
                lastRun = null;
            }
        }
 
        printResults(done, gantt);
    }
 
  
    //  Print helpers
   
    static void printResults(List<Process> done, List<GanttEntry> gantt) {
  
        System.out.println(" Gantt Chart ");
        StringBuilder top = new StringBuilder("  |");
        StringBuilder bot = new StringBuilder("  |");
 
        // Merge consecutive same-process entries for cleaner display
        List<GanttEntry> merged = mergeGantt(gantt);
 
        for (GanttEntry g : merged) {
            String label = " " + g.processId + " ";
            int width = Math.max(label.length(), 4);
            top.append(center(label, width)).append("|");
            bot.append(center(String.valueOf(g.start), width)).append(" ");
        }
        if (!merged.isEmpty()) {
            bot.append(merged.get(merged.size() - 1).end);
        }
 
        System.out.println(top);
        System.out.println(bot);
  
 
        // ── Per-process table ────────────────────
        System.out.printf("%-8s %-10s %-10s %-10s %-12s %-14s %-14s%n",
                "Process", "Arrival", "Burst", "Priority", "Start", "Finish", "Turnaround");
        System.out.println("─".repeat(80));
 
        double totalWT = 0, totalTAT = 0;
        done.sort(Comparator.comparing(p -> p.id));
        for (Process p : done) {
            System.out.printf("%-8s %-10d %-10d %-12d %-12d %-14d %-14d%n",
                    p.id, p.arrivalTime, p.burstTime, p.priority,
                    p.startTime, p.finishTime, p.turnaroundTime);
            totalWT  += p.waitingTime;
            totalTAT += p.turnaroundTime;
        }
 
        System.out.println("─".repeat(80));
        System.out.printf("%-8s %-10s %-10s %-12s %-12s %-14s %-14s%n",
                "", "", "", "", "", "", "");
        System.out.printf("  ➤  Average Waiting Time    : %.2f%n", totalWT  / done.size());
        System.out.printf("  ➤  Average Turnaround Time : %.2f%n%n", totalTAT / done.size());
    }
 
    static List<GanttEntry> mergeGantt(List<GanttEntry> raw) {
        if (raw.isEmpty()) return raw;
        List<GanttEntry> merged = new ArrayList<>();
        GanttEntry cur = new GanttEntry(raw.get(0).processId, raw.get(0).start, raw.get(0).end);
        for (int i = 1; i < raw.size(); i++) {
            GanttEntry g = raw.get(i);
            if (g.processId.equals(cur.processId) && g.start == cur.end) {
                cur.end = g.end;
            } else {
                merged.add(cur);
                cur = new GanttEntry(g.processId, g.start, g.end);
            }
        }
        merged.add(cur);
        return merged;
    }
 
    static String center(String s, int width) {
        int pad = width - s.length();
        int left = pad / 2, right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
 
    static List<Process> deepCopy(List<Process> src) {
        List<Process> copy = new ArrayList<>();
        for (Process p : src) copy.add(new Process(p.id, p.arrivalTime, p.burstTime, p.priority));
        return copy;
    }
 
    // ─────────────────────────────────────────
    //  Input helpers
    // ─────────────────────────────────────────
    static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("   Please enter a valid integer.");
            }
        }
    }
 
    static List<Process> readProcesses(Scanner sc) {
        int n = readInt(sc, "Enter number of processes: ");
        List<Process> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.println("\n  ── Process P" + i + " ──");
            int at  = readInt(sc, "  Arrival Time : ");
            int bt  = readInt(sc, "  Burst Time   : ");
            int pri = readInt(sc, "  Priority     : ");
            list.add(new Process("P" + i, at, bt, pri));
        }
        return list;
    }
 
    
    //  Main

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
 

        System.out.println("       CPU PRIORITY SCHEDULER            ");
        System.out.println("  (lower priority number = higher priority)");
        
 
        boolean running = true;
        while (running) {
            System.out.println("\n Select Scheduling Mode");
            System.out.println("│  1. Non-Preemptive Priority              │");
            System.out.println("│  2. Preemptive Priority                  │");
            System.out.println("│  3. Run Both (compare)                   │");
        
 
            int choice = readInt(sc, "Your choice: ");
 
            if (choice == 0) { System.out.println("notvalid"); break; }
            if (choice < 1 || choice > 3) { System.out.println("Invalid choice."); continue; }
 
            System.out.println("\nEnter process details:");
            List<Process> processes = readProcesses(sc);
 
            switch (choice) {
                case 1 -> nonPreemptive(processes);
                case 2 -> preemptive(processes);
                case 3 -> { nonPreemptive(processes); preemptive(processes); }
            }
 
            System.out.print("\nRun another simulation? (y/n): ");
            running = sc.nextLine().trim().equalsIgnoreCase("y");
        }
 
        sc.close();
    }
}
 