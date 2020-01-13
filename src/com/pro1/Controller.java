package com.pro1;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class Controller {

    @FXML
    AnchorPane mainStage;

    @FXML
    Button importFileButton;
    @FXML
    Label fileNameText;
    @FXML
    Label productsAmountText;
    @FXML
    Label transactionsAmountText;
    @FXML
    ListView<String> top10List;

    File dataFile;


    public void onImportFileButtonPressed(Event ev){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(mainStage.getScene().getWindow());
        if (selectedFile != null) {
            dataFile = selectedFile;
            fileNameText.setText(selectedFile.getName());
        }

        top10List.getItems().add("Dupa");
        top10List.getItems().add("Dupa2");
    }
}
