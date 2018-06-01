package com.accenture.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name = "CAR_TBL")
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CAR_ID")
	private Long id;

	@Column(name = "REGISTRATION_NUMBER")
	private String registrationNumber;

	@Column(name = "MODEL")
	private String model;

	@Column(name = "YEAR")
	private Integer year;

	@Column(name = "PRICE_PER_DAY")
	private Double pricePerDay;

	@OneToMany(mappedBy = "car")
	private Set<Rental> rentals;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Double getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(Double pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public Set<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	//Set all rentals null on delete
	@PreRemove
	private void preRemove() {
		for (Rental rental : rentals) {
			rental.setCar(null);
		}
	}

}
