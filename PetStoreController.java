package pet.store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {
	
	@Autowired
	private PetStoreService petStoreService;
	
	@PostMapping("/pet_store")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreData insertPetStore(@RequestBody PetStoreData petStoreData) {
		log.info("Creating pet store {}", petStoreData);
		
		return petStoreService.savePetStore(petStoreData);
	}
	
	@PutMapping("/pet_store/{petStoreID}")
	public PetStoreData updatePetStore(@PathVariable Long petStoreID,
			@RequestBody PetStoreData petStoreData) {
		
		petStoreData.setPetStoreID(petStoreID);
		log.info("Updating pet store {} with ID= {} ", petStoreData, petStoreID );
		
		return petStoreService.savePetStore(petStoreData);
	}
	
	@GetMapping
	public List<PetStoreData> retrieveAllPetStores(){
		log.info("Retrieving All Pet Stores.");
		
		return petStoreService.retrieveAllPetStores();
	}
	
	
	@GetMapping("/pet_store/{petStoreID}")
	public PetStoreData retrievePetStoreByID(@PathVariable Long petStoreID) {
				
		return petStoreService.retrievePetStoreByID(petStoreID);
		
	}
	
	@DeleteMapping("/pet_store/{petStoreID}")
	public Map<String, String> deletePetStoreByID(@PathVariable Long petStoreID) {
		log.info("Deleting pet store with ID= ", petStoreID);
		
		petStoreService.deletePetStoreByID(petStoreID);
		
		return Map.of("message", "Deletion of pet store with ID= " + petStoreID + " was successful.");
	}
	
	@PostMapping("/pet_store/{petStoreID}/employee")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreEmployee insertEmployee(@PathVariable Long petStoreID, @RequestBody PetStoreEmployee petStoreEmployee) {
		log.info("Adding Employee to Pet Store with ID= {}", petStoreID);
		
		return petStoreService.saveStoreEmployee(petStoreID, petStoreEmployee);
	}
	
	@PostMapping("/pet_store/{petStoreID}/customer")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreCustomer insertCustomer(@PathVariable Long petStoreID, @RequestBody PetStoreCustomer petStoreCustomer) {
		log.info("Adding Customer to Pet Store with ID= {}", petStoreID);
		
		return petStoreService.savePetStoreCustomer(petStoreID, petStoreCustomer);
		
	}
}
