package com.accenture.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.accenture.api.dto.CustomerDto;
import com.accenture.entity.Users;
import com.accenture.exception.CarNotAvailableException;
import com.accenture.exception.InvalidIdException;
import com.accenture.service.CustomerService;

@Controller
@RequestMapping("/admin/customer")
@Secured("ROLE_ADMIN")
public class AdminUserController {

	@Autowired
	private CustomerService customerService;

	@GetMapping
	public String homePage(Model model) {
		model.addAttribute("customers", customerService.showAll());
		return "customer";
	}

	@PostMapping("/addCustomer")
	public String createCustomer(@Valid CustomerDto customerDto, 
								 BindingResult bindingResult, 
								 Model model) {
		customerService.createCustomer(customerDto);
		model.addAttribute("customers", customerService.showAll());
		return "customer";
	}

	@GetMapping("/updatePage/{id}&{name}")
	public String updatePage(@PathVariable("id") String nationalId, 
							 @PathVariable("name") String name, 
							 Model model) {
		model.addAttribute("nationalId", nationalId);
		model.addAttribute("name", name);
		return "updateCustomer";
	}

	@PostMapping("/update")
	public String updateCustomer(@Valid CustomerDto customerDto, 
								 BindingResult bindingResult, 
								 Model model) {
		try {
			customerService.updateCustomer(customerDto);
		} catch (InvalidIdException e) {
		}
		model.addAttribute("customers", customerService.showAll());
		return "customer";
	}

	@GetMapping("/deleteCustomer/{id}")
	public String deleteCustomer(@PathVariable("id") String nationalId, 
								 Model model) {
		try {
			customerService.deleteCustomer(nationalId);
		} catch (CarNotAvailableException e) {
			model.addAttribute("error", e.getMessage());
		}
		model.addAttribute("customers", customerService.showAll());
		return "customer";
	}

	@GetMapping("/search")
	public String searchById(@RequestParam("nationalId") String nationalId, 
							 Model model) {
		if (customerService.searchById(nationalId) == null) {
			model.addAttribute("error", "National ID does not exist");
		} else {
			List<Users> customers = new ArrayList<>();
			customers.add(customerService.searchById(nationalId));
			model.addAttribute("customers", customers);
		}
		return "customer";
	}

	@GetMapping("/details/{id}")
	public String rentedCar(@PathVariable("id") String nationalId, 
							Model model) {
		model.addAttribute("customer", customerService.searchById(nationalId));
		model.addAttribute("cars", customerService.searchCarRented(nationalId));
		return "customerCar";
	}

}
