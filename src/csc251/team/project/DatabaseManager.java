package csc251.team.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;

public class DatabaseManager {

    // Putting my last name to insure uniquesness across projects
    private final static String databaseName = "CarLotDatabaseMcKitrick";
    private final static String tableName = "Cars";
    // URI for MySQL is "jdbc:mysql"
    private final static String jdbc = "jdbc:mysql";

    private String IP;
    private String port;

    private String user;
    private String password;

    private Connection connection;

    /**
     * Create database
     * Open connection to new database
     * create table to store cars
     * 
     * @param IP       The IP of the MySQl/MariaDB Server, likely be "localhost"
     * @param port     The port of the MySQl/MariaDB Server, likely be "3306"
     * @param user     The user to login as to the MySQl/MariaDB Server
     * @param password The password for user to login to the MySQl/MariaDB Server
     * @return DatabaseManager
     */
    public DatabaseManager(String IP, String port, String user, String password) {
        this.IP = IP;
        this.port = port;
        this.user = user;
        this.password = password;

        createDatabase();

        try {
            connection = DriverManager.getConnection(jdbc + "://" + IP + ":" + port + "/" + databaseName, user,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createTable();

    }

    /**
     * Write car to database
     * Will update information of car with same ID if already in database
     * 
     * @param car The Car object to be written to database
     * @return void
     */
    public void writeCarToDatabase(Car car) {
        try {
            PreparedStatement addCarStatement = connection.prepareStatement(
                    "INSERT INTO Cars " +
                            "(ID, Mileage, MPG, Cost, SalesPrice, Sold, PriceSold, Profit) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "Mileage = IF(@Mileage := VALUES(Mileage), @Mileage, Mileage), " +
                            "MPG = IF(@MPG := VALUES(MPG), @MPG, MPG), " +
                            "Cost = IF(@Cost := VALUES(Cost), @Cost, Cost), " +
                            "SalesPrice = IF(@SalesPrice := VALUES(SalesPrice), @SalesPrice, SalesPrice), " +
                            "Sold = IF(@Sold := VALUES(Sold), @Sold, Sold), " +
                            "PriceSold = IF(@PriceSold := VALUES(PriceSold), @PriceSold, PriceSold), " +
                            "Profit = IF(@Profit := VALUES(Profit), @Profit, Profit);");

            addCarStatement.setString(1, car.getId());
            addCarStatement.setInt(2, car.getMileage());
            addCarStatement.setInt(3, car.getMpg());
            addCarStatement.setDouble(4, car.getCost());
            addCarStatement.setDouble(5, car.getSalesPrice());
            addCarStatement.setBoolean(6, car.isSold());
            addCarStatement.setDouble(7, car.getPriceSold());
            addCarStatement.setDouble(8, car.getProfit());

            addCarStatement.executeUpdate();

            addCarStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read Car objects from database then return them
     * 
     * @return ArrayList<Car> array of Car objects
     */
    public ArrayList<Car> getCarsFromDatabase() {
        ArrayList<Car> cars = new ArrayList<Car>();

        try {

            Statement getCarsStatement = connection.createStatement();
            ResultSet getCarsSet = getCarsStatement.executeQuery("SELECT * FROM Cars;");

            while (getCarsSet.next()) {
                Car car = new Car(getCarsSet.getString("ID"),
                        getCarsSet.getInt("Mileage"),
                        getCarsSet.getInt("MPG"),
                        getCarsSet.getDouble("Cost"),
                        getCarsSet.getDouble("SalesPrice"));

                car.setSold(getCarsSet.getBoolean("Sold"));
                car.setPriceSold(getCarsSet.getDouble("PriceSold"));
                car.setProfit(getCarsSet.getDouble("Profit"));

                cars.add(car);
            }

            getCarsStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    /**
     * Gets cars from database and adds them to the CarLot object
     * This function will overwrite cars already existing in the CarLot with
     * matching IDs
     * 
     * @param cl Carlot object to be written to
     * 
     * @return void
     */
    public void updateCarLot(CarLot cl) {
        ArrayList<Car> cars = getCarsFromDatabase();
        for (int i = 0; i < cars.size(); i++) {
            Car databaseCar = cars.get(i);
            Car carLotCar = cl.findCarByIdentifier(databaseCar.getId());
            if (carLotCar == null) {

                cl.addCar(databaseCar.getId(), databaseCar.getMileage(), databaseCar.getMpg(), databaseCar.getCost(),
                        databaseCar.getSalesPrice());
            } else {

                carLotCar.setMileage(databaseCar.getMileage());
                carLotCar.setMpg(databaseCar.getMpg());
                carLotCar.setCost(databaseCar.getCost());
                carLotCar.setSalesPrice(databaseCar.getSalesPrice());
            }

            if (databaseCar.isSold()) {
                cl.sellCar(databaseCar.getId(), databaseCar.getPriceSold());
            }
        }
    }

    /**
     * This function writes every car to the database via
     * DatabaseManager.writeCarToDatabase()
     * Will update cars in database if the car has matching ID
     * This function works as long as return type of CarLot.getInventory() supports
     * "enhanced for loop"/"for-each loop"
     * 
     * @param cl Carlot object to be read from
     * 
     * @return void
     */
    public void updateDatabase(CarLot cl) {
        for (Car car : cl.getInventory()) {
            writeCarToDatabase(car);
        }
    }

    /**
     * Removes all cars from the carlot database
     * Use this to wipe the database table clean
     * 
     * @return void
     */
    public void emptyDatabase() {
        try {
            Statement deleteAllStatement = connection.createStatement();
            deleteAllStatement.executeUpdate("DELETE FROM " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // create database if not exists
    private void createDatabase() {
        try {
            Connection createDatabaseConnection = DriverManager.getConnection(jdbc + "://" + IP + ":" + port, user,
                    password);

            Statement createTableStatement = createDatabaseConnection.createStatement();
            createTableStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + ";");

            createTableStatement.close();
            createDatabaseConnection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // create car table if not exists
    private void createTable() {
        try {
            Statement createTableStatement = connection.createStatement();
            createTableStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
                    "ID VARCHAR(255) PRIMARY KEY NOT NULL, " +
                    "Mileage INT DEFAULT 0 NOT NULL, " +
                    "MPG INT DEFAULT 0 NOT NULL, " +
                    "Cost DOUBLE DEFAULT 0.0 NOT NULL, " +
                    "SalesPrice DOUBLE DEFAULT 0.0 NOT NULL, " +
                    "Sold BOOLEAN default false NOT NULL, " +
                    "PriceSold DOUBLE DEFAULT 0.0 NOT NULL, " +
                    "Profit DOUBLE DEFAULT 0.0 NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
