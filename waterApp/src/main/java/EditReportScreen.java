/*
 Created by Corey Caskey on 4/24/2017
 */

import java.util.Map;
import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.text.*;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.layout.HBox;


import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.collections.FXCollections;

import java.util.ArrayList;

import java.io.*;


public class EditReportScreen extends StackPane {

	private ImageView rain = new ImageView(
        new Image("https://rain.today/Pix/rain.jpg"));

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    private Button editWaterType = new Button("Confirm Water Type Change");
    private Button editWaterCondition = new Button("Confirm Water Condition Change");

    private Label location = new Label("Edit the Report Location");
    private Label waterType = new Label("Edit the Water Type");
    private Label waterCondition = new Label("Edit the Water Condition");

    private TextField locationText = new TextField(ReportList.getCurrentReport().getLocation());
    private TextField waterTypeText = new TextField(ReportList.getCurrentReport().getWaterType().toString());
    private TextField waterConditionText = new TextField(ReportList.getCurrentReport().getWaterCondition().toString());

    private ListView<WaterType> waterTypes = new ListView<>(
            FXCollections.observableArrayList(WaterType.BOTTLED, WaterType.WELL,
                WaterType.STREAM, WaterType.LAKE, WaterType.SPRING, WaterType.OTHER));

    private ListView<WaterCondition> waterConditions = new ListView<>(
            FXCollections.observableArrayList(WaterCondition.WASTE, WaterCondition.TREATABLE_MUDDY,
                WaterCondition.TREATABLE_CLEAR, WaterCondition.POTABLE));

	public EditReportScreen(Stage primaryStage) {

        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

        locationText.setPromptText("Enter the location here.");

        rain.setFitHeight(800);
        rain.setFitWidth(800);

        HBox buttons = new HBox(5.5);
        buttons.getChildren().add(cancel);
        buttons.getChildren().add(save);
        buttons.setAlignment(Pos.CENTER);

        VBox options = new VBox(5.5);
        options.getChildren().add(location);
        options.getChildren().add(locationText);
        options.getChildren().add(waterType);
        options.getChildren().add(waterTypeText);
        options.getChildren().add(this.getWaterTypeList());
        options.getChildren().add(editWaterType);
        options.getChildren().add(waterCondition);
        options.getChildren().add(waterConditionText);
        options.getChildren().add(this.getWaterConditionList());
        options.getChildren().add(editWaterCondition);
        options.setAlignment(Pos.CENTER);

        GridPane reportView = new GridPane();
        reportView.setAlignment(Pos.CENTER);
        reportView.setVgap(20);

        reportView.add(options, 0, 0);
        reportView.add(buttons, 0, 1);

        GridPane.setHalignment(options, HPos.CENTER);
        GridPane.setHalignment(buttons, HPos.CENTER);

        locationText.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                this.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });

		this.getChildren().add(rain);
		this.getChildren().add(reportView);

        editWaterType.setOnAction(e -> {
            waterTypeText.setText(this.getWaterTypeList().getSelectionModel().getSelectedItem().toString());
        });

        editWaterCondition.setOnAction(e -> {
            waterConditionText.setText(this.getWaterConditionList().getSelectionModel().getSelectedItem().toString());
        });

        cancel.setOnAction(e -> {
            primaryStage.setScene(new Scene(new ReportTableScreen(primaryStage)));
            primaryStage.show();
        });

        save.setOnAction(e -> {
            Report report = ReportList.getCurrentReport(); // gets current report
            Report editedReport = null;
            if (this.getWaterTypeList().getSelectionModel().getSelectedItem() == null
                || this.getWaterConditionList().getSelectionModel().getSelectedItem() == null) {
                editedReport = new Report(report.getReportNumber(), locationText.getText().toString(),
                    "", this.getWaterTypeEnum(waterTypeText.getText().toString()),
                    this.getWaterConditionEnum(waterConditionText.getText().toString()));
            } else {
                editedReport = new Report(report.getReportNumber(), locationText.getText().toString(),
                    "", this.getWaterTypeList().getSelectionModel().getSelectedItem(),
                    this.getWaterConditionList().getSelectionModel().getSelectedItem());
            }
            Map<Integer, Report> copyReportList = ReportList.getReportList();
            copyReportList.remove(report.getReportNumber());
            ReportList.setReportList(copyReportList);
            ReportList.addReport(editedReport);

            // The name of the file to open.
            String fileName = "reports.txt";

            try {
                // Assume default encoding.
                FileWriter fileWriter =
                    new FileWriter(fileName);

                // Always wrap FileWriter in BufferedWriter.
                BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

                // Note that write() does not automatically
                // append a newline character.
                bufferedWriter.flush();

                for (Report aReport: ReportList.getReportListValues()) {
                    bufferedWriter.write(aReport.getReportNumber() + "\n");
                    bufferedWriter.write(aReport.getLocation() + "\n");
                    bufferedWriter.write(aReport.getAddress() + "\n");
                    bufferedWriter.write(aReport.getWaterType().toString() + "\n");
                    bufferedWriter.write(aReport.getWaterCondition().toString() + "\n");
                    bufferedWriter.write("-----------------------" + "\n");
                }

                // Always close files.
                bufferedWriter.close();
            }
            catch(IOException ex) {
                System.out.println(
                    "Error writing to file '"
                    + fileName + "'");
                // Or we could just do this:
                // ex.printStackTrace();
            }

            primaryStage.setScene(new Scene(new ReportTableScreen(primaryStage)));
            primaryStage.show();
        });

		// primaryStage.setResizable(false);
  //       primaryStage.setScene(scene);
  //       primaryStage.show();
	}

        /**
    * return a ListView of WaterTypes representing the list of
    * available account types
    * @return listview of WaterType
    */
    public ListView<WaterType> getWaterTypeList() {
        waterTypes.setMaxHeight(120);
        waterTypes.setMaxWidth(300);
        waterTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return waterTypes;
    }

        /**
    * return a ListView of WaterConditions representing the list of
    * available account types
    * @return listview of WaterConditions
    */
    public ListView<WaterCondition> getWaterConditionList() {
        waterConditions.setMaxHeight(120);
        waterConditions.setMaxWidth(300);
        waterConditions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return waterConditions;
    }

    public WaterType getWaterTypeEnum(String value) {
        if (value.equals("Bottled")) {
            return WaterType.BOTTLED;
        } else if (value.equals("Well")) {
            return WaterType.WELL;
        } else if (value.equals("Stream")) {
            return WaterType.STREAM;
        } else if (value.equals("Lake")) {
            return WaterType.LAKE;
        } else if (value.equals("Spring")) {
            return WaterType.SPRING;
        } else {
            return WaterType.OTHER;
        }
    }

    public WaterCondition getWaterConditionEnum(String value) {
        if (value.equals("Waste")) {
            return WaterCondition.WASTE;
        } else if (value.equals("Treatable_muddy")) {
            return WaterCondition.TREATABLE_MUDDY;
        } else if (value.equals("Treatable_clear")) {
            return WaterCondition.TREATABLE_CLEAR;
        } else {
            return WaterCondition.POTABLE;
        }
    }



}
