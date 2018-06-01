package com.accenture.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.entity.Car;
import com.accenture.entity.Rental;
import com.accenture.entity.Users;

@Repository
public interface RentRepository extends CrudRepository<Rental, Long> {
	
	List<Rental> findByUsers(Users user);
	
	Rental findByUsersAndCarAndStartDate(Users users, Car car, Date sdate);
	
	List<Rental> findByUsersAndReturned(Users user, boolean returned);
	
	List<Rental> findByCarAndReturned(Car car, boolean returned);
}
