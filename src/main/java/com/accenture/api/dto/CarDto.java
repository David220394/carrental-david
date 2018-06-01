package com.accenture.api.dto;

import java.util.Set;

public class CarDto {

	private String registrationNumber;

	private String model;

	private Integer year;

	private Double pricePerDay;

	private Set<String> rentals;

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(Double pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public Set<String> getRentals() {
		return rentals;
	}

	public void setRentals(Set<String> rentals) {
		this.rentals = rentals;
	}

}
