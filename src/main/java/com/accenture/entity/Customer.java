package com.accenture.entity;

import javax.persistence.Entity;

import com.accenture.entity.enums.Role;

@Entity
public class Customer extends Users {

	public Customer() {
		super.setRole(Role.ROLE_CUSTOMER);
	}
	
}
