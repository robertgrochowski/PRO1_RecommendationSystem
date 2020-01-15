package com.pro1;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
    @FXML
    Button buildButton;
    @FXML
    Button frequentItemFilterButton;
    @FXML
    Button ruleFilterButton;
    @FXML
    ListView<String> frequentItemsetList;
    @FXML
    ListView<String> rulesList;
    @FXML
    TextField minSupportField;
    @FXML
    TextField minConfidenceField;
    @FXML
    TextField frequentItemsetFilterField;
    @FXML
    TextField ruleItemsetFilterField;
    @FXML
    Label resultsDescriptionLabel;

    File dataFile;
    MyApriori apriori;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void onImportFileButtonPressed(Event ev){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(mainStage.getScene().getWindow());
        if (selectedFile == null)
            return;

        dataFile = selectedFile;
        fileNameText.setText("loading file...");
        top10List.getItems().clear();
        rulesList.getItems().clear();
        frequentItemsetList.getItems().clear();

        try {
            apriori = new MyApriori(dataFile.getAbsolutePath());
            fileNameText.setText(dataFile.getName());

            int i = 1;
            for (Map.Entry<Integer, Integer> entry : apriori.getTopProducts().entrySet()) {
                top10List.getItems().add("#"+(i++)+" PID: ["+entry.getKey() + "]: " + entry.getValue());
            }

            transactionsAmountText.setText(apriori.getTotalTransactions()+"");
            productsAmountText.setText(apriori.getTotalProducts() + "");
        } catch (IOException e) {
            alert(e.getMessage());
            fileNameText.setText("No file loaded...");
        }
    }

    public void onBuildButtonPressed(Event ev) {
        if(apriori == null)
        {
            alert("No data loaded");
            return;
        }

        frequentItemsetList.getItems().clear();
        rulesList.getItems().clear();

        try {
            apriori.setVariables(Double.parseDouble(minSupportField.getText()), Double.parseDouble(minConfidenceField.getText()));

            Stage computingWindow = new Stage();
            computingWindow.setTitle("Connecting to the database");
            computingWindow.setScene(new Scene(FXMLLoader.load(getClass().getResource("fxml/computingWindow.fxml"))));

            computingWindow.initModality(Modality.WINDOW_MODAL);
            computingWindow.initStyle(StageStyle.UNDECORATED);
            computingWindow.initOwner(mainStage.getScene().getWindow());
            computingWindow.show();

            FutureTask<Void> futureTask = new FutureTask<Void>(apriori){
                @Override
                protected void done() {
                    try {
                        if (!isCancelled()) get();

                        Platform.runLater(() -> {

                            for (Set<Integer> itemset : apriori.getFrequentItemSets())
                                frequentItemsetList.getItems().add(itemset.toString());

                            for (Rule rule : apriori.getRules())
                                rulesList.getItems().add(rule.toString());

                            resultsDescriptionLabel.setText("Frequent itemsets found: "+apriori.getFrequentItemSets().size()+", Rules found: "+apriori.getRules().size());
                        });

                    } catch (ExecutionException e) {
                        // Exception occurred, deal with it
                        alert(e.getMessage());
                    } catch (InterruptedException e) {
                        // Shouldn't happen, we're invoked when computation is finished
                        alert(e.getMessage());
                    }
                    finally{
                        Platform.runLater(computingWindow::close);
                    }
                }
            };



            executorService.submit(futureTask);

        } catch (Exception e) {
            alert(e.getMessage());
        }
    }

    public void onFrequentItemsetFilterPressed(Event event) {
        if(frequentItemsetFilterField.getText().length() < 1) {
            frequentItemsetList.getItems().clear();
            for(Set<Integer> itemset : apriori.getFrequentItemSets())
                frequentItemsetList.getItems().add(itemset.toString());

            return;
        }

        try {
            int productId = Integer.parseInt(frequentItemsetFilterField.getText());

            frequentItemsetList.getItems().clear();
            for(Set<Integer> itemset : apriori.getFrequentItemSets()) {
                if (itemset.contains(productId)) {
                    frequentItemsetList.getItems().add(itemset.toString());
                }
            }
        }
        catch (Exception e) {
            alert(e.getMessage());
        }
    }

    public void onRuleFilterPressed(Event event) {
        if(ruleItemsetFilterField.getText().length() < 1) {
            rulesList.getItems().clear();
            for(Rule rule : apriori.getRules())
                rulesList.getItems().add(rule.toString());

            return;
        }

        try {
            int productId = Integer.parseInt(ruleItemsetFilterField.getText());

            rulesList.getItems().clear();
            for (Rule rule : apriori.getRules()) {
                if (rule.getAntecedent().contains(productId)) {
                    rulesList.getItems().add(rule.toString());
                }
            }
        } catch (Exception e) {
            alert(e.getMessage());
        }
    }

    private void alert(String msg) {
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(msg);
            alert.show();
        });
    }
}
