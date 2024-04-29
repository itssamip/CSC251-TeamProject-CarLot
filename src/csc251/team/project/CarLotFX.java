package csc251.team.project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

public class CarLotFX extends Application {

    private CarLot carLot;
    private ObservableList<Car> carList;

    @Override
    public void start(Stage primaryStage) {
        carLot = new CarLot();
        carList = FXCollections.observableArrayList(carLot.getInventory());

        // Create buttons
        Button btnAddCar = new Button("Add Car");
        Button btnCalculateProfit = new Button("Calculate Profit");
        Button btnSellCar = new Button("Sell Car");

        // Create input fields
        TextField txCarId = new TextField();
        TextField txMileage = new TextField();
        TextField txMPG = new TextField();
        TextField txCost = new TextField();
        TextField txSalesPrice = new TextField();

        // Create labels
        Label lblCarId = new Label("Car ID:");
        Label lblMileage = new Label("Mileage:");
        Label lblMPG = new Label("MPG:");
        Label lblCost = new Label("Cost:");
        Label lblSalesPrice = new Label("Sales Price:");

        // Layout components using GridPane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        gridPane.add(lblCarId, 0, 0);
        gridPane.add(txCarId, 1, 0);
        gridPane.add(lblMileage, 0, 1);
        gridPane.add(txMileage, 1, 1);
        gridPane.add(lblMPG, 0, 2);
        gridPane.add(txMPG, 1, 2);
        gridPane.add(lblCost, 0, 3);
        gridPane.add(txCost, 1, 3);
        gridPane.add(lblSalesPrice, 0, 4);
        gridPane.add(txSalesPrice, 1, 4);

        // Create button box
        HBox buttonBox = new HBox(btnAddCar, btnCalculateProfit, btnSellCar);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(5));

        // Create the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(buttonBox);
        borderPane.setCenter(gridPane);

        // Add TableView to display car inventory
        TableView<Car> tableView = new TableView<>();
        tableView.setItems(carList);

        TableColumn<Car, String> colCarId = new TableColumn<>("Car ID");
        colCarId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Car, Integer> colMileage = new TableColumn<>("Mileage");
        colMileage.setCellValueFactory(new PropertyValueFactory<>("mileage"));

        TableColumn<Car, Integer> colMPG = new TableColumn<>("MPG");
        colMPG.setCellValueFactory(new PropertyValueFactory<>("mpg"));

        TableColumn<Car, Double> colCost = new TableColumn<>("Cost");
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<Car, Double> colSalesPrice = new TableColumn<>("Sales Price");
        colSalesPrice.setCellValueFactory(new PropertyValueFactory<>("salesPrice"));

        TableColumn<Car, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().isSold() ? "SOLD" : "AVAILABLE";
            return new SimpleStringProperty(status);
        });

        tableView.getColumns().addAll(colCarId, colMileage, colMPG, colCost, colSalesPrice, colStatus);

        borderPane.setBottom(tableView);

        // Add functionality to buttons
        btnAddCar.setOnAction(e -> {
            String id = txCarId.getText();
            int mileage = Integer.parseInt(txMileage.getText());
            int mpg = Integer.parseInt(txMPG.getText());
            double cost = Double.parseDouble(txCost.getText());
            double salesPrice = Double.parseDouble(txSalesPrice.getText());

            carLot.addCar(id, mileage, mpg, cost, salesPrice);
            carList.setAll(carLot.getInventory()); // Update TableView
            clearFields(txCarId, txMileage, txMPG, txCost, txSalesPrice);
        });

        btnCalculateProfit.setOnAction(e -> {
            double totalProfit = carLot.getTotalProfit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Total Profit");
            alert.setHeaderText(null);
            alert.setContentText("Total Profit: $" + String.format("%.2f", totalProfit));
            alert.showAndWait();
        });

        btnSellCar.setOnAction(e -> {
            Car carToMarkAsSold = tableView.getSelectionModel().getSelectedItem();
            if (carToMarkAsSold != null) {
                carToMarkAsSold.setSold(true); // Mark the selected car as sold
                tableView.refresh(); // Refresh TableView to reflect the change
                clearFields(txCarId, txMileage, txMPG, txCost, txSalesPrice);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Car Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a car to sell.");
                alert.showAndWait();
            }
        });

        // Create the scene and set it to the stage
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setTitle("Car Lot Inventory Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
