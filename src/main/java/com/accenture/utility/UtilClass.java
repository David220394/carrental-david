package com.accenture.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.accenture.api.dto.RentalDto;
import com.accenture.entity.Car;
import com.accenture.entity.Rental;

@Component
public class UtilClass {

	public static RentalDto convertRentaltoDto(Rental rental) {
		RentalDto rentalDto = new RentalDto();
		if (rental.getUsers() != null) {
			rentalDto.setUsers(rental.getUsers().getNationalId());
		} else {
			rentalDto.setUsers("Customer was deleted");
		}
		if (rental.getCar() != null) {
			rentalDto.setCar(rental.getCar().getRegistrationNumber());
		} else {
			rentalDto.setCar("Car was deleted");
		}
		rentalDto.setStartDate(rental.getStartDate().toString());
		rentalDto.setEndDate(rental.getEndDate().toString());
		rentalDto.setReturned(rental.isReturned());
		rentalDto.setTotalAmount(calculateAmount(rental));
		return rentalDto;
	}

	public static Double calculateAmount(Rental rental) {
		long dateDiff = (rental.getEndDate().getTime() - rental.getStartDate().getTime()) / (24 * 60 * 60 * 1000);
		double sum = (dateDiff * rental.getRentedPricePerDay());
		return sum;
	}

	public static Double calculateAmount(Car car, String start, String end) {
		Date sdate = null;
		Date edate = null;
		try {
			sdate = new SimpleDateFormat("yyyy-MM-dd").parse(start);
			edate = new SimpleDateFormat("yyyy-MM-dd").parse(end);
		} catch (ParseException e) {
		}
		long dateDiff = (edate.getTime() - sdate.getTime()) / (24 * 60 * 60 * 1000);
		double sum = (dateDiff * car.getPricePerDay());
		return sum;
	}

}
