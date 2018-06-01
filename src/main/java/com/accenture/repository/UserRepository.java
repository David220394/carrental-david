package com.accenture.repository;

import org.springframework.data.repository.CrudRepository;

import com.accenture.entity.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
	
	Users findByNationalId(String nationalId);
	
	

}
