
package cpuscheduler;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;


public class FXMLDocumentController implements Initializable {
    private Scheduler scheduler;
    private Timeline timeline;
    private int processCounter = 1;
    private List<Process> initialProcesses = new ArrayList<>();
    private Label lastBlock = null;
    private Process lastProcess = null;
    private String lastGanttId = null;
    private static final String IDLE_ID = "__IDLE__";
    private int lastGanttTime = 0;
    private Map<String, String> processColors = new HashMap<>();
    private String[] colors = {"#FF6B6B","#4ECDC4","#FFD93D","#6C5CE7","#00B894"};
    private int colorIndex = 0;
    private boolean isPaused = false;
    
    @FXML private ComboBox<String> schedulerType;
    @FXML private Label qrrfield;
    @FXML private TextField qrrtext;
    @FXML private TextField priorityField;
    @FXML private Label priorityLabel;
    @FXML private Label arrivalLabel;
    @FXML private Label burstLabel;
    @FXML private Button addProcessBtn;
    @FXML private Button startBtn;
    @FXML private Label statusLabel;
    @FXML private RadioButton radioStatic;
    @FXML private RadioButton radioDynamic;
    @FXML private Button pauseBtn;
    @FXML private ToggleGroup simType;
    @FXML private TextField arrivalField;
    @FXML private TextField burstField;
    @FXML private TableView<Process> tableView;
    @FXML private Label avgWaitingLabel;
    @FXML private Label avgTurnaroundLabel;
    @FXML private Label timerLabel;
    @FXML private HBox ganttBox; 
    @FXML private Pane timeBox;
    @FXML private TableColumn<Process, String> colProcess;
    @FXML private TableColumn<Process, Integer> colArrival;
    @FXML private TableColumn<Process, Integer> colBurst;
    @FXML private TableColumn<Process, Integer> colRemaining;
    @FXML private TableColumn<Process, Integer> colPriority;
    @FXML private TableColumn<Process, Integer> colWaiting;
    @FXML private TableColumn<Process, Integer> colTurnaround;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        schedulerType.getItems().clear();
        schedulerType.getItems().addAll(
                "FCFS",
                "SJF(Preemptive)",
                "SJF(Non Preemptive)",
                "Priority(Preemptive)",
                "Priority(non Preemptive)",
                "Round Robin"
        );
        schedulerType.setPromptText("Select scheduler");
        schedulerType.getSelectionModel().clearSelection();

        startBtn.setDisable(true);
        addProcessBtn.setDisable(true);
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
        pauseBtn.setDisable(true);
        statusLabel.setText("");
        statusLabel.setVisible(false);
        colPriority.setVisible(false);
        qrrtext.setVisible(false);
        qrrtext.setManaged(false);
        qrrfield.setVisible(false);
        qrrfield.setManaged(false);
        priorityField.setVisible(false);
        priorityField.setManaged(false);
        priorityLabel.setVisible(false);
        priorityLabel.setManaged(false);
        arrivalLabel.setVisible(false);
        arrivalLabel.setManaged(false);
        arrivalField.setVisible(false);
        arrivalField.setManaged(false);
        burstLabel.setVisible(false);
        burstLabel.setManaged(false);
        burstField.setVisible(false);
        burstField.setManaged(false);

        schedulerType.setOnAction(e -> updateInputVisibility());
        schedulerType.getOnAction().handle(null);
        simType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateInputVisibility();
        });
        ganttBox.setMinWidth(Region.USE_PREF_SIZE);
        timeBox.setMinWidth(Region.USE_PREF_SIZE);
        colProcess.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().id));

        colArrival.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().arrivalTime).asObject());

        colBurst.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().burstTime).asObject());

        colRemaining.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().remainingTime).asObject());

        colPriority.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().priority).asObject());

        colWaiting.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().waitingTime).asObject());

        colTurnaround.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().turnaroundTime).asObject());

        colProcess.setSortable(false);
        colArrival.setSortable(false);
        colBurst.setSortable(false);
        colRemaining.setSortable(false);
        colPriority.setSortable(false);
        colWaiting.setSortable(false);
        colTurnaround.setSortable(false);
    }

    private void updateInputVisibility() {
        String selectedScheduler = schedulerType.getValue();
        boolean schedulerSelected = selectedScheduler != null && !selectedScheduler.isEmpty();
        boolean isPriority = "Priority(Preemptive)".equals(selectedScheduler)
                || "Priority(non Preemptive)".equals(selectedScheduler);
        boolean isRoundRobin = "Round Robin".equals(selectedScheduler);
        boolean isDynamic = radioDynamic.isSelected();

        startBtn.setDisable(!schedulerSelected);
        addProcessBtn.setDisable(!schedulerSelected);
        colPriority.setVisible(isPriority);

        burstLabel.setVisible(schedulerSelected);
        burstLabel.setManaged(schedulerSelected);
        burstField.setVisible(schedulerSelected);
        burstField.setManaged(schedulerSelected);

        arrivalLabel.setVisible(schedulerSelected && !isDynamic);
        arrivalLabel.setManaged(schedulerSelected && !isDynamic);
        arrivalField.setVisible(schedulerSelected && !isDynamic);
        arrivalField.setManaged(schedulerSelected && !isDynamic);

        qrrfield.setVisible(schedulerSelected && isRoundRobin);
        qrrfield.setManaged(schedulerSelected && isRoundRobin);
        qrrtext.setVisible(schedulerSelected && isRoundRobin);
        qrrtext.setManaged(schedulerSelected && isRoundRobin);

        priorityLabel.setVisible(schedulerSelected && isPriority);
        priorityLabel.setManaged(schedulerSelected && isPriority);
        priorityField.setVisible(schedulerSelected && isPriority);
        priorityField.setManaged(schedulerSelected && isPriority);
    }
    
    @FXML
    private void handleStart() {
        createScheduler();
        if (scheduler == null) return;

        resetUIOnly();
        for (Process p : initialProcesses) {
            resetProcessMetrics(p);
            scheduler.addProcess(p);
        }

        schedulerType.setDisable(true);
        radioStatic.setDisable(true);
        radioDynamic.setDisable(true);
        startBtn.setDisable(true);
        burstField.setDisable(true);
        arrivalField.setDisable(true);
        priorityField.setDisable(true);
        if (qrrtext.isVisible()) qrrtext.setDisable(true);
        addProcessBtn.setDisable(true);

        if (radioStatic.isSelected()) {
            runStatic();
        } else {
            pauseBtn.setDisable(false);
            pauseBtn.setVisible(true);
            pauseBtn.setManaged(true);
            pauseBtn.setText("Pause");
            isPaused = false;
            runDynamic();
        }
    }
    
    @FXML
    private void handleAddProcess() {
        Integer burst = parseInt(burstField, "Burst");
        if (burst == null) return;
        int priority = 0;
        if (priorityField.isVisible()) {
            Integer p = parseInt(priorityField, "Priority");
            if (p == null) return;
            priority = p;
        }
        Process p;
        if (scheduler == null) {
            if (radioStatic.isSelected()) {
                Integer arrival = parseInt(arrivalField, "Arrival");
                if (arrival == null) return;
                p = new Process("P" + processCounter++, arrival, burst, priority);
            } else {
                p = new Process("P" + processCounter++, 0, burst, priority);
            }
            initialProcesses.add(p);
        } else {
            if (!radioDynamic.isSelected()) {
                showError("Cannot add process after static simulation started");
                return;
            }
            if (!isPaused) {
                showError("Pause simulation before adding process");
                return;
            }
            int time = scheduler.getCurrentTime();
            p = new Process("P" + processCounter++, time, burst, priority);
            scheduler.addProcess(p);
            initialProcesses.add(p);
        }
        if ("Round Robin".equals(schedulerType.getValue())) {
            qrrtext.setDisable(true);
        }
        updateTable();
    }
    
    private void runStatic() {
        while (!scheduler.isFinished()) {
            scheduler.tick();
            Process executed = scheduler.getLastExecutedProcess();
            updateGantt(executed, scheduler.getCurrentTime());
        }
        updateUI();
    }
    
    private void runDynamic() {
        pauseBtn.setDisable(false);
        timeline = new Timeline(new KeyFrame(
            Duration.seconds(1), e -> {
            scheduler.tick();
            Process executed = scheduler.getLastExecutedProcess();
            updateGantt(executed, scheduler.getCurrentTime());
            timerLabel.setText("Time: " + scheduler.getCurrentTime());
            updateUI();
            if (executed == null) {
                statusLabel.setText("Simulation idle: pause and add a process.");
                statusLabel.setVisible(true);
            } else {
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    @FXML
    private void handlePause() {
        if (timeline == null) return;
        if (!isPaused) {
            timeline.pause();
            pauseBtn.setText("Resume");
            isPaused = true;
            addProcessBtn.setDisable(false);
            burstField.setDisable(false);
            priorityField.setDisable(!priorityField.isVisible());
        } else {
            timeline.play();
            pauseBtn.setText("Pause");
            isPaused = false;
            addProcessBtn.setDisable(true);
            burstField.setDisable(true);
            priorityField.setDisable(true);
        }
    }
    @FXML
    private void handleReset() {
        if (timeline != null) timeline.stop();
        scheduler = null;
        initialProcesses.clear();
        processCounter = 1;
        isPaused = false;
        pauseBtn.setText("Pause");
        pauseBtn.setDisable(true);
        schedulerType.setDisable(false);
        radioStatic.setDisable(false);
        radioDynamic.setDisable(false);
        startBtn.setDisable(schedulerType.getSelectionModel().isEmpty());
        burstField.setDisable(false);
        arrivalField.setDisable(false);
        priorityField.setDisable(false);
        qrrtext.setDisable(false);
        addProcessBtn.setDisable(schedulerType.getSelectionModel().isEmpty());
        resetUIOnly();
        statusLabel.setText("");
        statusLabel.setVisible(false);
        updateInputVisibility();
    }
    
    private void resetUIOnly() {
        tableView.getItems().clear();
        ganttBox.getChildren().clear();
        timeBox.getChildren().clear();
        timerLabel.setText("Time: 0");
        avgWaitingLabel.setText("Average Waiting Time: 0.0");
        avgTurnaroundLabel.setText("Average Turnaround Time: 0.0");
        lastBlock = null;
        lastProcess = null;
        lastGanttId = null;
        lastGanttTime = 0;
        processColors.clear();
        colorIndex = 0;
    }
    
    private void updateGantt(Process executedProcess, int time) {
        int BLOCK_WIDTH = 30;

        String currentId = executedProcess == null ? IDLE_ID : executedProcess.id;
        String labelText = executedProcess == null ? "idle" : executedProcess.id;

        int startTime = time - 1;
        boolean firstBlock = timeBox.getChildren().isEmpty();
        if (firstBlock) {
            Label initialTime = new Label(String.valueOf(startTime));
            initialTime.setLayoutX(0);
            initialTime.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
            timeBox.getChildren().add(initialTime);
            lastGanttTime = startTime;
        }

        boolean continuingSameProcess = currentId.equals(lastGanttId);

        if (continuingSameProcess) {
            lastBlock.setMinWidth(lastBlock.getMinWidth() + BLOCK_WIDTH);
            Label lastTimeLabel = (Label) timeBox.getChildren().get(timeBox.getChildren().size() - 1);
            lastTimeLabel.setText(String.valueOf(time));
            double currentX = 0;
            for (int i = 0; i < ganttBox.getChildren().size() - 1; i++) {
                currentX += ((Region)ganttBox.getChildren().get(i)).getMinWidth();
            }
            currentX += lastBlock.getMinWidth();
            lastTimeLabel.setLayoutX(currentX - 4);
            lastGanttTime = time;
            return;
        }

        int gapUnits = startTime - lastGanttTime;
        if (gapUnits > 0) {
            Label idleGap = new Label();
            idleGap.setMinWidth(gapUnits * BLOCK_WIDTH);
            idleGap.setPrefHeight(30);
            idleGap.setStyle("-fx-background-color: transparent;");
            ganttBox.getChildren().add(idleGap);
        }

        Label block = new Label(labelText);
        block.setMinWidth(BLOCK_WIDTH);
        block.setPrefHeight(30);
        block.setStyle("-fx-background-color:" + (executedProcess == null ? "#d3d3d3" : getColor(executedProcess.id)) +
                       ";-fx-border-color:black;-fx-alignment:center;-fx-font-weight:bold;");
        ganttBox.getChildren().add(block);

        double endX = 0;
        for (javafx.scene.Node node : ganttBox.getChildren()) {
            endX += ((Region)node).getMinWidth();
        }

        Label timeLabel = new Label(String.valueOf(time));
        timeLabel.setStyle("-fx-font-size: 11;");
        timeLabel.setLayoutX(endX - 4);
        timeBox.getChildren().add(timeLabel);

        lastBlock = block;
        lastGanttId = currentId;
        lastGanttTime = time;
        lastProcess = executedProcess;
    }
    
    private void updateUI() {
        updateTable();
        updateAverages();
    }
    
    private void updateTable() {
        tableView.getItems().clear();
        if (scheduler != null) {
            refreshProcessMetrics(initialProcesses, scheduler.getCurrentTime());
            tableView.getItems().addAll(initialProcesses);
        } else {
            tableView.getItems().addAll(initialProcesses);
        }
    }
    
    private void refreshProcessMetrics(List<Process> processes, int currentTime) {
        for (Process p : processes) {
            if (p.finishTime > 0) {
                continue;
            }
            if (p.arrivalTime > currentTime) {
                p.waitingTime = 0;
                p.turnaroundTime = 0;
                continue;
            }
            int executedTime = p.burstTime - p.remainingTime;
            p.waitingTime = Math.max(0, currentTime - p.arrivalTime - executedTime);
            p.turnaroundTime = currentTime - p.arrivalTime;
        }
    }
    
    private void updateAverages() {
        if (scheduler == null) return;

        avgWaitingLabel.setText("Average Waiting Time: " + scheduler.getAverageWaitingTime());
        avgTurnaroundLabel.setText("Average Turnaround Time: " + scheduler.getAverageTurnaroundTime());
    }
    
    private void resetProcessMetrics(Process p) {
        p.remainingTime = p.burstTime;
        p.finishTime = 0;
        p.waitingTime = 0;
        p.turnaroundTime = 0;
        p.startTime = -1;
    }

    private Integer parseInt(TextField f, String name) {
        try {
            int v = Integer.parseInt(f.getText());
            if (v < 0) throw new Exception();
            return v;
        } catch (Exception e) {
            showError(name + " must be non-negative");
            return null;
        }
    }
    
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg);
        a.showAndWait();
    }
    
    private String getColor(String id) {
    if (!processColors.containsKey(id)) {
        processColors.put(id, colors[colorIndex++ % colors.length]);
    }
    return processColors.get(id);
    }
    
    private void createScheduler() {
        switch (schedulerType.getValue()) {
            case "FCFS" -> scheduler = new FCFS_Scheduler();
            case "SJF(Preemptive)" -> scheduler = new SJFPreemptiveScheduler();
            case "SJF(Non Preemptive)" -> scheduler = new SJFNonPreemptiveScheduler();
            case "Priority(Preemptive)" -> scheduler = new PrioritySheduling(true);
            case "Priority(non Preemptive)" -> scheduler = new PrioritySheduling(false);
            case "Round Robin" -> {
                Integer q = parseInt(qrrtext, "Quantum");
                if (q == null) return;
                scheduler = new RoundRobinScheduler(q);
            }
        }
    }

}
