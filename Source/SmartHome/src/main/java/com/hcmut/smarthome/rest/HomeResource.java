package com.hcmut.smarthome.rest;

import static com.hcmut.smarthome.utils.ConstantUtil.ALL_GPIO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.model.Mode;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IHomeService;

@RestController
@RequestMapping(path = "/homes")
@CrossOrigin
public class HomeResource {

	@Autowired
	private IHomeService homeService;

	@Autowired
	private IAuthenticationService authService;

	// TODO : Replace hard-coded USER ID when token is implemented

	@RequestMapping(method = RequestMethod.GET, path = "/{homeId}")
	public ResponseEntity<Home> getHome(@PathVariable int homeId) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Home>(HttpStatus.UNAUTHORIZED);
		}

		Home home = homeService.getHome(authService.getCurrentUserId(), homeId);
		if (home != null) {
			return new ResponseEntity<Home>(home, HttpStatus.OK);
		} else {
			return new ResponseEntity<Home>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{homeId}")
	public ResponseEntity<Void> deleteHome(@PathVariable int homeId) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}

		if (homeService.deleteHome(authService.getCurrentUserId(), homeId)) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.PATCH, path = "/{homeId}")
	public ResponseEntity<Void> updatePartialHome(@PathVariable int homeId,
			@RequestBody Home home) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}

		if (homeService.updatePartialHome(authService.getCurrentUserId(),
				homeId, home))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{homeId}")
	public ResponseEntity<Void> updateHome(@PathVariable int homeId,
			@RequestBody Home home) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}

		if (homeService
				.updateHome(authService.getCurrentUserId(), homeId, home))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> addHome(@RequestBody Home home) {

		int addedHomeId = homeService.addHome(authService.getCurrentUserId(),
				home);
		if (addedHomeId > 0) {
			String URINewAddedObject = String.format("homes/%s", addedHomeId);
			return new ResponseEntity<String>(URINewAddedObject,
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Home>> getHomes() {
		return new ResponseEntity<List<Home>>(
				homeService.getAllHomes(authService.getCurrentUserId()),
				HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/{homeId}/modes/{modeId}")
	public ResponseEntity<Void> deleteMode(@PathVariable int homeId,
			@PathVariable int modeId) {

		if (!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}

		if (homeService.deleteMode(homeId, modeId))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{homeId}/modes/{modeId}")
	public ResponseEntity<Void> updateMode(@PathVariable int homeId,
			@PathVariable int modeId, @RequestBody Mode mode) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		if (homeService.updateMode(homeId, modeId, mode))
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{homeId}/modes")
	public ResponseEntity<String> addMode(@PathVariable int homeId,
			@RequestBody Mode mode) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
		
		int addedModeId = homeService.addMode(homeId, mode);
		if (addedModeId > 0) {
			String URINewAddedObject = String.format("homes/%s/modes/%s",
					homeId, addedModeId);
			return new ResponseEntity<String>(URINewAddedObject,
					HttpStatus.CREATED);
		} else
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{homeId}/modes")
	public ResponseEntity<List<Mode>> getModes(@PathVariable int homeId) {
		
		if(!authService.isAccessable(homeId)) {
			return new ResponseEntity<List<Mode>>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Mode>>(homeService.getAllModes(homeId),
				HttpStatus.OK);
	}

	/**
	 * Get all gpio
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/all-gpios")
	public ResponseEntity<List<Integer>> getAllGpio() {
		return new ResponseEntity<List<Integer>>(ALL_GPIO, HttpStatus.OK);
	}

}