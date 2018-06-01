package com.accenture.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.accenture.api.dto.RentalDto;
import com.accenture.config.CustomAuthenticationProvider;
import com.accenture.entity.Car;
import com.accenture.entity.Users;
import com.accenture.exception.AlreadyRentingException;
import com.accenture.exception.CarNotAvailableException;
import com.accenture.exception.InvalidDateException;
import com.accenture.exception.InvalidRegistrationNumber;
import com.accenture.service.CarService;
import com.accenture.service.CustomerService;
import com.accenture.service.RentalService;
import com.accenture.utility.UtilClass;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CarService carService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private RentalService rentalService;

	@GetMapping
	public String home(Model model) {
		model.addAttribute("user", customerService.searchById(CustomAuthenticationProvider.getId()));
		model.addAttribute("rentals", rentalService.showRentalByUser(customerService.searchById(CustomAuthenticationProvider.getId())));
		return "customerHome";
	}

	@GetMapping("/available")
	public String showAvailableCar(@RequestParam String sdate, 
								   @RequestParam String edate, 
								   Model model) {
		Users user = customerService.searchById(CustomAuthenticationProvider.getId());
		List<Car> cars = carService.availableCar(sdate, edate);
		model.addAttribute("sdate", sdate);
		model.addAttribute("edate", edate);
		model.addAttribute("cars", cars);
		model.addAttribute("user", user);
		model.addAttribute("available", "available");
		model.addAttribute("rentals", rentalService.showRentalByUser(user));
		return "customerHome";
	}

	@GetMapping("/confirmRent/{registrationNumber}&{sdate}&{edate}")
	public String confirmRental(@PathVariable String registrationNumber, 
							    @PathVariable String sdate,
							    @PathVariable String edate, 
							    Model modal) {
		Users user = customerService.searchById(CustomAuthenticationProvider.getId());
		Car car = null;
		try {
			car = carService.findByRegistrationNumber(registrationNumber);
		} catch (InvalidRegistrationNumber e) {
		}
		modal.addAttribute("car", car);
		modal.addAttribute("sdate", sdate);
		modal.addAttribute("edate", edate);
		modal.addAttribute("user", user);
		modal.addAttribute("totalAmount", UtilClass.calculateAmount(car, sdate, edate));
		return "customerConfirmRental";
	}

	@PostMapping("/rentCar")
	public String rentCar(@RequestParam("nationalId") String nationalId,
						  @RequestParam("registrationNumber") String registrationNumber, 
						  @RequestParam("sdate") String sdate,
						  @RequestParam("edate") String edate, 
						  Model model) {
		RentalDto dto = new RentalDto();
		dto.setUsers(nationalId);
		dto.setCar(registrationNumber);
		dto.setStartDate(sdate);
		dto.setEndDate(edate);
		try {
			rentalService.rentCar(dto);
		} catch (CarNotAvailableException | AlreadyRentingException | ParseException | InvalidDateException e) {
			model.addAttribute("error", e.getMessage());
		}
		model.addAttribute("user", customerService.searchById(CustomAuthenticationProvider.getId()));
		model.addAttribute("rentals", rentalService.showRentalByUser(customerService.searchById(nationalId)));
		return "customerHome";
	}

}
