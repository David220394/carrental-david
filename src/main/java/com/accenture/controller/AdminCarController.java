package com.accenture.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.api.dto.CarDto;
import com.accenture.entity.Car;
import com.accenture.exception.CarRentedException;
import com.accenture.exception.InvalidRegistrationNumber;
import com.accenture.exception.NoRentalFoundException;
import com.accenture.service.CarService;

@Controller
@RequestMapping("/admin/car")
@Secured("ROLE_ADMIN")
public class AdminCarController {

	@Autowired
	private CarService carService;

	@GetMapping("/show")
	public String showAll(Model model) {
		model.addAttribute("cars", carService.showAll());
		return "showAll";
	}

	@PostMapping("/insertCar")
	public String createCar(Model modal, 
							@ModelAttribute("carDto") CarDto carDto) 
	{
		carService.addCar(carDto);
		modal.addAttribute("cars", carService.showAll());
		return "showAll";
	}

	@GetMapping("/search")
	public String searchByRegistrationNumber(@RequestParam("registrationNumber") String registrationNumber,
											Model model) 
	{
		List<Car> cars = new ArrayList<>(); 
		try {
			cars.add(carService.findByRegistrationNumber(registrationNumber));
			model.addAttribute("cars", cars);
		} catch (InvalidRegistrationNumber e) {
			model.addAttribute("error", e.getMessage());
		}
		return "showAll";
	}

	@PostMapping(value = "/filter", params = "available")
	public String showAvailable(@RequestParam("sdate") String sdate, 
								@RequestParam("edate") String edate, 
								Model model) 
	{
		if (carService.availableCar(sdate, edate).isEmpty()) {
			model.addAttribute("error", "No car is available for this period");
		}
		model.addAttribute("cars", carService.availableCar(sdate, edate));
		model.addAttribute("sdate", sdate);
		model.addAttribute("edate", edate);
		model.addAttribute("available", "available");
		return "showAll";
	}

	@PostMapping(value = "/filter", params = "rented")
	public String rentedCar(@RequestParam("sdate") String sdate, 
							@RequestParam("edate") String edate, 
							Model model) 
	{
		try {
			model.addAttribute("cars", carService.rentedCar(sdate, edate));
		} catch (NoRentalFoundException e) {
			model.addAttribute("error", "No car was/is rented for this period");
		}
		return "showAll";
	}

	@GetMapping("/updatePage/{id}")
	public String updatePage(@PathVariable("id") String registrationNumber, 
							 Model model) 
	{
		model.addAttribute("regNum", registrationNumber);
		return "updateCar";
	}

	@PostMapping("/update")
	public String updateCar(@RequestParam("pricePerDay") double pricePerDay,
							@RequestParam("regNum") String registrationNumber, 
							Model model) 
	{
		carService.updateCar(pricePerDay, registrationNumber);
		model.addAttribute("cars", carService.showAll());
		return "showAll";
	}

	@GetMapping("/deleteCar/{id}")
	public String deleteCar(@PathVariable("id") String registrationNumber, 
							Model model) {
		try {
			carService.deleteCar(registrationNumber);
		} catch (CarRentedException e) {
			model.addAttribute("error", e.getMessage());
		}
		model.addAttribute("cars", carService.showAll());
		return "showAll";
	}

	@PostMapping("/csv")
	public String importCSV(@RequestParam("csv") MultipartFile file, 
							Model model) {

		try {
			carService.importCSV(file.getInputStream());
		} catch (NumberFormatException | IOException e) {
			model.addAttribute("error", "File Not Found");
		}
		model.addAttribute("cars", carService.showAll());
		return "showAll";
	}

}
