package com.accenture.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.accenture.entity.Car;

public interface CarRepository extends CrudRepository<Car, Long> {
	
	public Car findByRegistrationNumber(String registrationNumber);
	
	public List<Car> findByRentalsStartDateBetween(Date startDate, Date endDate);
	
}
