package com.accenture.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.accenture.api.dto.CustomerDto;
import com.accenture.entity.Car;
import com.accenture.entity.Customer;
import com.accenture.entity.Rental;
import com.accenture.entity.Users;
import com.accenture.entity.enums.Role;
import com.accenture.entity.enums.Sex;
import com.accenture.exception.CarNotAvailableException;
import com.accenture.exception.InvalidIdException;
import com.accenture.repository.CustomerRepository;
import com.accenture.repository.RentRepository;
import com.accenture.repository.UserRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RentRepository rentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	// Create a Customer
	public void createCustomer(CustomerDto customerDto) {
		Customer customer = new Customer();
		customer.setName(customerDto.getName());
		customer.setNationalId(customerDto.getNationalId());
		customer.setPassword(encoder.encode(customerDto.getPassword()));
		try {
			customer.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(customerDto.getDateOfBirth()));
		} catch (ParseException e) {
		}
		if (customerDto.getSex().equalsIgnoreCase("FEMALE")) {
			customer.setSex(Sex.F);
		} else {
			customer.setSex(Sex.M);
		}
		customer.setRole(Role.ROLE_CUSTOMER);
		customerRepository.save(customer);
	}

	// Update Customer
	public void updateCustomer(CustomerDto customers) throws InvalidIdException {
		Customer customerdb = customerRepository.findByNationalId(customers.getNationalId());
		if (customerdb != null) {
			customerdb.setName(customers.getName());
			customerdb.setNationalId(customers.getNationalId());
			if (customers.getPassword() != null) {
				customerdb.setPassword(encoder.encode(customers.getPassword()));
			}
			if (customers.getDateOfBirth() != null) {
				try {
					customerdb.setDateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse(customers.getDateOfBirth()));
				} catch (ParseException e) {
				}
			}
			if (customers.getSex().equalsIgnoreCase("FEMALE")) {
				customerdb.setSex(Sex.F);
			} else {
				customerdb.setSex(Sex.M);
			}
			customerRepository.save(customerdb);
		} else {
			throw new InvalidIdException("Customer not found");
		}
	}

	// Delete a Customer
	public void deleteCustomer(String nationalId) throws CarNotAvailableException {
		Customer customerdb = customerRepository.findByNationalId(nationalId);

		if (rentRepository.findByUsersAndReturned(customerdb, false).isEmpty()) {
			customerRepository.delete(customerdb);
		} else {
			throw new CarNotAvailableException("Customer cannot be deleted, he/she is renting a car");
		}
	}

	// Show all customer
	public List<Customer> showAll() {
		List<Customer> customers = new ArrayList<>();
		customerRepository.findAll().forEach(customers::add);
		return customers;
	}

	// Search by National Id
	public Users searchById(String nationalId) {
		return userRepository.findByNationalId(nationalId);

	}

	// Search rented car related to customer
	public List<Car> searchCarRented(String nationalId) {
		List<Car> cars = new ArrayList<>();
		Users user = customerRepository.findByNationalId(nationalId);
		List<Rental> rentals = rentRepository.findByUsers(user);
		for (Rental rental : rentals) {
			if (rental.getCar() != null) {
				cars.add(rental.getCar());
			}
		}
		return cars;
	}

	@PostConstruct
	public void initprod() {
		
		if (userRepository.findByNationalId("admin") == null) {
			Users user = new Users();
			user.setNationalId("admin");
			user.setName("Brandon");
			user.setPassword(encoder.encode("admin"));
			user.setRole(Role.ROLE_ADMIN);
			userRepository.save(user);
			System.out.println("initialize");
		}
		
		if(customerRepository.findByNationalId("D123456789") == null) {
			Customer customer = new Customer();
			customer.setNationalId("D123456789");
			  customer.setName("Brandon");
			  customer.setPassword(encoder.encode("qwerty"));
			  customer.setRole(Role.ROLE_CUSTOMER);
				customerRepository.save(customer);
		}
	}

	 

}
