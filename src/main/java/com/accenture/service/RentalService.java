package com.accenture.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.api.dto.RentalDto;
import com.accenture.entity.Car;
import com.accenture.entity.Customer;
import com.accenture.entity.Rental;
import com.accenture.entity.Users;
import com.accenture.exception.AlreadyRentingException;
import com.accenture.exception.CarNotAvailableException;
import com.accenture.exception.InvalidDateException;
import com.accenture.repository.CarRepository;
import com.accenture.repository.CustomerRepository;
import com.accenture.repository.RentRepository;
import com.accenture.utility.UtilClass;

@Service
@Transactional
public class RentalService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private RentRepository rentRepository;

	// Find Rented Car
	public List<Car> rentedCar(Date start, Date end) {
		return carRepository.findByRentalsStartDateBetween(start, end);
	}

	// Release Car
	public void releaseCar(String nationalId, String registrationNumber, String start) {
		Users user = customerRepository.findByNationalId(nationalId);
		Car car = carRepository.findByRegistrationNumber(registrationNumber);
		Date sdate = null;
		try {
			sdate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
		} catch (ParseException e) {
		}
		Rental rentaldb = rentRepository.findByUsersAndCarAndStartDate(user, car, sdate);
		rentaldb.setReturned(true);
		rentRepository.save(rentaldb);
	}

	// Rent A Car
	public void rentCar(RentalDto rentalDto)
			throws CarNotAvailableException, AlreadyRentingException, ParseException, InvalidDateException {
		Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(rentalDto.getStartDate());
		Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(rentalDto.getEndDate());
		LocalDateTime now = LocalDateTime.now();
		Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
		Date todaysDate = Date.from(instant);
		if (sdate.before(edate) && sdate.after(todaysDate)) {
			Customer user = customerRepository.findByNationalId(rentalDto.getUsers());
			List<Rental> rentals = rentRepository.findByUsers(user);
			for (Rental rental : rentals) {
				if (!((sdate.before(rental.getStartDate()) && edate.before(rental.getStartDate()))
						|| (sdate.after(rental.getEndDate()) && edate.after(rental.getEndDate())))) {
					throw new AlreadyRentingException("A car is aleady rented for this period");
				}
			}
			Car car = carRepository.findByRegistrationNumber(rentalDto.getCar());
			List<Car> cars = rentedCar(sdate, edate);
			for (Car cardb : cars) {
				if (cardb.equals(car)) {
					throw new AlreadyRentingException("A car is aleady rented for this period");
				}
			}
			Rental rental = new Rental();
			rental.setUsers(user);
			rental.setCar(car);
			rental.setStartDate(sdate);
			rental.setEndDate(edate);
			rental.setReturned(false);
			rental.setRentedPricePerDay(car.getPricePerDay());
			rentRepository.save(rental);
		} else {
			throw new InvalidDateException("Invalid Date Entry");
		}
	}

	// Show All Rental
	public List<RentalDto> showAll() {
		List<Rental> rentals = new ArrayList<>();
		List<RentalDto> rentalDtos = new ArrayList<>();
		rentRepository.findAll().forEach(rentals::add);
		for (Rental rental : rentals) {
			rentalDtos.add(UtilClass.convertRentaltoDto(rental));
		}
		return rentalDtos;
	}

	// Show all Rental for a Specific User
	public List<RentalDto> showRentalByUser(Users user) {
		List<Rental> rentals = rentRepository.findByUsers(user);
		List<RentalDto> rentalDtos = new ArrayList<>();
		for (Rental rental : rentals) {
			rentalDtos.add(UtilClass.convertRentaltoDto(rental));
		}
		return rentalDtos;
	}

}
