package com.accenture.controller;

import java.text.ParseException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.accenture.api.dto.RentalDto;
import com.accenture.exception.AlreadyRentingException;
import com.accenture.exception.CarNotAvailableException;
import com.accenture.exception.InvalidDateException;
import com.accenture.service.CarService;
import com.accenture.service.CustomerService;
import com.accenture.service.RentalService;

@Controller
@RequestMapping("/admin/rental")
@Secured("ROLE_ADMIN")
public class AdminRentController {

	@Autowired
	private RentalService rentalService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CarService carService;

	@GetMapping
	public String showAll(Model model) {
		model.addAttribute("rentals", rentalService.showAll());
		model.addAttribute("users", customerService.showAll());
		model.addAttribute("cars", carService.showAll());
		return "rental";
	}

	@GetMapping("/{registrationNumber}&{sdate}&{edate}")
	public String showAll(@PathVariable("registrationNumber") String registrationNumber,
						  @PathVariable("sdate") String sdate, 
						  @PathVariable("edate") String edate, 
						  Model model) {
		model.addAttribute("sdate", sdate);
		model.addAttribute("edate", edate);
		model.addAttribute("cars", registrationNumber);
		model.addAttribute("allocate", "allocate");
		model.addAttribute("rentals", rentalService.showAll());
		model.addAttribute("users", customerService.showAll());
		return "rental";
	}

	@PostMapping("/addRental")
	public String addRental(@Valid RentalDto rentalDto, 
							BindingResult bindingResult, 
							Model model) {

		try {
			rentalService.rentCar(rentalDto);
		} catch (ParseException | AlreadyRentingException | CarNotAvailableException | InvalidDateException e) {
			model.addAttribute("invalid", e.getMessage());
		}

		model.addAttribute("rentals", rentalService.showAll());
		model.addAttribute("users", customerService.showAll());
		model.addAttribute("cars", carService.showAll());
		return "rental";
	}

	@GetMapping("/release/{nationalId}&{registrationNumber}&{sdate}")
	public String releaseCar(@PathVariable("nationalId") String nationalId,
							 @PathVariable("registrationNumber") String registrationNumber, 
							 @PathVariable("sdate") String sdate,
							 Model model) {
		rentalService.releaseCar(nationalId, registrationNumber, sdate);
		model.addAttribute("rentals", rentalService.showAll());
		model.addAttribute("users", customerService.showAll());
		model.addAttribute("cars", carService.showAll());
		return "rental";
	}

}
