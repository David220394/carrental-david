package com.accenture.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.api.dto.CarDto;
import com.accenture.entity.Car;
import com.accenture.entity.Rental;
import com.accenture.exception.CarRentedException;
import com.accenture.exception.InvalidRegistrationNumber;
import com.accenture.exception.NoRentalFoundException;
import com.accenture.repository.CarRepository;
import com.accenture.repository.RentRepository;

@Service
@Transactional
public class CarService {

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private RentRepository rentRepository;

	// Rent a Car
	public List<Car> rentedCar(String start, String end) throws NoRentalFoundException {
		Date sdate = null;
		Date edate = null;
		try {
			sdate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			edate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
		}
		if (!carRepository.findByRentalsStartDateBetween(sdate, edate).isEmpty()) {
			return carRepository.findByRentalsStartDateBetween(sdate, edate);
		}
		throw new NoRentalFoundException("No car found for this period");
	}

	// Add a Car
	public void addCar(CarDto carDto) {
		if (carRepository.findByRegistrationNumber(carDto.getRegistrationNumber()) == null) {
			Car car = new Car();
			car.setModel(carDto.getModel());
			car.setYear(carDto.getYear());
			car.setRegistrationNumber(carDto.getRegistrationNumber());
			car.setPricePerDay(carDto.getPricePerDay());
			carRepository.save(car);
		}
	}

	// Show All Car
	public List<Car> showAll() {
		List<Car> cars = new ArrayList<>();
		carRepository.findAll().forEach(cars::add);
		return cars;
	}

	// Update a Car
	public void updateCar(double pricePerDay, String registrationNumber) {
		Car cardb = carRepository.findByRegistrationNumber(registrationNumber);
		cardb.setPricePerDay(pricePerDay);
		carRepository.save(cardb);
	}

	// Delete a Car
	public void deleteCar(String registrationNumber) throws CarRentedException {
		Car car = carRepository.findByRegistrationNumber(registrationNumber);
		if (rentRepository.findByCarAndReturned(car, false).isEmpty()) {
			carRepository.delete(car);
		} else {
			throw new CarRentedException("The car is being rented");
		}
	}

	// Find By RegistrationNumber
	public Car findByRegistrationNumber(String registrationNumber) throws InvalidRegistrationNumber {
		Car car = carRepository.findByRegistrationNumber(registrationNumber);
		if (car != null) {
			return car;
		} else
			throw new InvalidRegistrationNumber("Registration Number Not Valid");
	}

	// Find Available Car
	public List<Car> availableCar(String start, String end) {
		Date sdate = null;
		Date edate = null;
		try {
			sdate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			edate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
		}
		List<Car> avaialbleCars = new ArrayList<>();
		carRepository.findAll().forEach(avaialbleCars::add);
		List<Rental> rentals = new ArrayList<Rental>();
		rentRepository.findAll().forEach(rentals::add);
		for (Rental rental : rentals) {
			if (!((sdate.before(rental.getStartDate()) && edate.before(rental.getStartDate()))
					|| (sdate.after(rental.getEndDate()) && edate.after(rental.getEndDate())))) {
				if (!rental.isReturned()) {
					avaialbleCars.remove(rental.getCar());
				}
			}
		}
		return avaialbleCars;
	}

	//Import CSV
	public void importCSV(InputStream filePath) throws NumberFormatException, IOException {
		Reader fr = new InputStreamReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		String str = null;
		while ((str = br.readLine()) != null) {
			String[] arry = str.split(",");
			CarDto carDto = new CarDto();
			carDto.setRegistrationNumber(arry[0]);
			carDto.setModel(arry[1]);
			carDto.setYear(Integer.parseInt(arry[2]));
			carDto.setPricePerDay(Double.parseDouble(arry[3]));
			addCar(carDto);
		}
		br.close();
		fr.close();
	}
	
}
