/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author IbrahimAhmedBadr
 */
public class FXMLDocumentController implements Initializable {

    @FXML private ComboBox<String> schedulerType;
    @FXML private Label qrrfield;
    @FXML private TextField qrrtext;
    @FXML private TextField priorityField;
    @FXML private Label priorityLabel;
    @FXML private RadioButton radioStatic;
    @FXML private RadioButton radioDynamic;
    @FXML private Button pauseBtn;
    @FXML private ToggleGroup simType;

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
        schedulerType.getSelectionModel().select(3);
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
        schedulerType.setOnAction(e -> {
            String selectedScheduler = schedulerType.getValue();
            if ("Round Robin".equals(selectedScheduler)){
                qrrtext.setVisible(true);
                qrrtext.setManaged(true);
                qrrfield.setVisible(true);
                qrrfield.setManaged(true);
            } else {
                qrrtext.setVisible(false);
                qrrtext.setManaged(false);
                qrrfield.setVisible(false);
                qrrfield.setManaged(false);
            }
            if ("Priority(Preemptive)".equals(selectedScheduler)||"Priority(non Preemptive)".equals(selectedScheduler)) {
                priorityField.setVisible(true);
                priorityField.setManaged(true);
                priorityLabel.setVisible(true);
                priorityLabel.setManaged(true);
            } else {
                priorityField.setVisible(false);
                priorityField.setManaged(false);
                priorityLabel.setVisible(false);
                priorityLabel.setManaged(false);
                priorityField.clear();
            }
        });
        schedulerType.getOnAction().handle(null);
        simType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (radioStatic.isSelected()) {
                pauseBtn.setVisible(false);
                pauseBtn.setManaged(false);
            }
            else if (radioDynamic.isSelected()) {
                pauseBtn.setDisable(true);
                pauseBtn.setVisible(true);
                pauseBtn.setManaged(true);
            }
        });
    }
}
