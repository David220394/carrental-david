package com.accenture.repository;

import org.springframework.data.repository.CrudRepository;

import com.accenture.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Customer findByNationalId(String nationalId);
	
}
