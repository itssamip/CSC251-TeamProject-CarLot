package csc251.team.project;

import java.util.ArrayList;
import java.util.Collections;

public class CarLot {
	private ArrayList<Car> inventory;
	private int numberOfCars = 0;
	private int capacity = 0;
	
	public CarLot() { 
		this(100); 
	}
	
	public CarLot(int capacity) {
		this.capacity = capacity;
		this.inventory = new ArrayList<>(capacity);
	}
	
	public void addCar(String id, int mileage, int mpg, double cost, double salesPrice) {
	    if (inventory.size() < capacity) {
	        inventory.add(new Car(id, mileage, mpg, cost, salesPrice));
	    }
	}
	
	public ArrayList<Car> getInventory() {
	    return new ArrayList<>(inventory);
	}

	
	public Car findCarByIdentifier(String identifier) {
        for (Car car : inventory) {
            if (car.getId().equals(identifier)) {
                return car;
            }
        }
        return null;
    }
	
	public void sellCar(String identifier, double priceSold) throws IllegalArgumentException {
		Car aCar = this.findCarByIdentifier(identifier);
		if (aCar != null) {
			aCar.sellCar(priceSold);
		} else {
			throw new IllegalArgumentException("No car with identifier " + identifier);
		}
	}
	
	public ArrayList<Car> getCarsInOrderOfEntry() {
        return new ArrayList<>(inventory);
    }
	
	public ArrayList<Car> getCarsSortedByMPG() {
		  Collections.sort(inventory, (Car c1, Car c2) -> c2.compareMPG(c1));
		  return inventory;
		}

	
	public Car getCarWithBestMPG() {
        Car bestCar = null;
        int bestMpg = -1;
        for (Car car : inventory) {
            if (car.getMpg() > bestMpg) {
                bestMpg = car.getMpg();
                bestCar = car;
            }
        }
        return bestCar;
    }
	
	public Car getCarWithHighestMileage() {
		Car rtn = null;
		int highestMileage = -1;
		for (Car car : inventory) {
            if (car.getMileage() > highestMileage) {
                highestMileage = car.getMileage();
                rtn = car;
            }
        }
		return rtn;
	}
	
	public double getAverageMpg() {
        double totalMpg = 0.0;
        for (Car car : inventory) {
            totalMpg += car.getMpg();
        }
        return totalMpg / inventory.size();
    }

    public double getTotalProfit() {
        double totalProfit = 0.0;
        for (Car car : inventory) {
            if (car.isSold()) {
                totalProfit += car.getProfit();
            }
        }
        return totalProfit;
    }
	
}
