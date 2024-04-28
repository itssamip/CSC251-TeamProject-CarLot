package csc251.team.project;

public class CarLotTester {
	public static void main(String[] args) {
		CarLot lot = new CarLot();
		lot.addCar("test1", 10000, 30, 12500.0D, 17500.0D);
		lot.addCar("test2", 10000, 10, 10000D, 10000D);
		lot.addCar("test3", 12000, 20, 12000D, 12000D);
		System.out.println("Inventory: ");
		for (Car car: lot.getInventory()) {
			System.out.println(car);
		}
		System.out.println("Average MPG: " + lot.getAverageMpg());
		System.out.println("Total Profit: " + lot.getTotalProfit());
		System.out.println("Car with the best MPG: " + lot.getCarWithBestMPG());
		System.out.println("Car with the highest mileage: " + lot.getCarWithHighestMileage());
		lot.sellCar("test1", 17000.0D);
		System.out.println("Total profit after selling one car: " + lot.getTotalProfit());

		DatabaseManager dm = new DatabaseManager("localhost", "3306", "John", "password123");
		CarLot cl = new CarLot();

		System.out.println("\nDemonstrate writing to database and reading from it");
		dm.emptyDatabase();
		dm.updateDatabase(lot); // update the database with previous CarLot
		dm.updateCarLot(cl); // write information from database to the new CarLot
		for (Car car : cl.getInventory()) {
            System.out.println(car);
        }

		System.out.println("\nDemonstrate overwriting existing cars in the carlot and database");
		lot.getInventory()[0].setMpg(37); // update an existing Car's mpg in previous CarLot
		dm.writeCarToDatabase(lot.getInventory()[0]); // update the Car's information in the database rather than creating a new Car entry
		dm.updateCarLot(cl); // write information from database to the new CarLot, the MPG of cars[0] will now be 37
		for (Car car : cl.getInventory()) {
            System.out.println(car);
        }

		System.out.println("\nDemonstrate emptying the database");
		dm.emptyDatabase(); // delete all entries from the database
		for (Car car : dm.getCarsFromDatabase()) {
            System.out.println(car);
        }
	}

}
