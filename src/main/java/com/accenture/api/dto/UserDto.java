package com.accenture.api.dto;

import java.util.Set;

public class UserDto {

	private String nationalId;

	private String password;

	private String role;

	private String name;

	private String sex;

	private String dateOfBirth;

	Set<String> rentals;

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Set<String> getRentals() {
		return rentals;
	}

	public void setRentals(Set<String> rentals) {
		this.rentals = rentals;
	}

}
