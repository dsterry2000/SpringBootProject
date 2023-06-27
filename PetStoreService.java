package pet.store.service;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.dao.PetStoreDao;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	
	@Autowired
	private PetStoreDao petStoreDao;
	
	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreID = petStoreData.getPetStoreID(); 
		PetStore petStore = findOrCreatePetStore(petStoreID);
		copyPetStoreFields(petStore, petStoreData);
		
		PetStore dbPetStore = petStoreDao.save(petStore);
		return new PetStoreData(dbPetStore);
	}

	private PetStore findOrCreatePetStore(Long petStoreID) {
		PetStore petStore;
		
		if(Objects.isNull(petStoreID)) {
			petStore = new PetStore();
		}else {
			petStore = findPetStoreByID(petStoreID);
		}
		return petStore;
	}

	private PetStore findPetStoreByID(Long petStoreID) {
		
		return petStoreDao.findById(petStoreID).orElseThrow(
				()-> new NoSuchElementException("Pet Store with ID=" + petStoreID + " does not exist."));
		
	}
	
	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData ) {
		petStore.setPetStoreID(petStoreData.getPetStoreID());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorephone(petStoreData.getPetStorephone());
	}

}
