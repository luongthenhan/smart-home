package com.hcmut.smarthome.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.hcmut.smarthome.utils.ConstantUtil.ALL_GPIO;

@CrossOrigin
@RestController
public class GpioResource {
	
	@RequestMapping(method = RequestMethod.GET, path = "/allGpio")
	public ResponseEntity<List<Integer>> getAllGpio() {
		return new ResponseEntity<List<Integer>>(ALL_GPIO, HttpStatus.OK);
	}

}
