package com.accenture.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.accenture.entity.enums.Role;
import com.accenture.entity.enums.Sex;

@Entity
@Table(name = "USER_TBL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE")
public class Users {

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NATIONAL_ID", nullable = false, unique = true)
	private String nationalId;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "SEX")
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Column(name = "DATE_OF_BIRTH")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth;

	@OneToMany(mappedBy = "users")

	Set<Rental> rentals;

	public Set<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	//Set rentals null on delete
	@PreRemove
	private void preRemove() {
		for (Rental rental : rentals) {
			rental.setUsers(null);
		}
	}

}
