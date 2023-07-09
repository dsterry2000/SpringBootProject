package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	
	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;
	
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
	
		
	@Transactional(readOnly = false)
	public PetStoreEmployee saveStoreEmployee(Long petStoreID, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreByID(petStoreID);
		Long employeeID = petStoreEmployee.getEmployeeID();
		Employee employee = findOrCreateEmployee(petStoreID, employeeID);
		
		copyEmployeeFields(employee, petStoreEmployee);
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		Employee dbEmployee = employeeDao.save(employee);
		
		return new PetStoreEmployee(dbEmployee);
	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeID(petStoreEmployee.getEmployeeID());
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
		
	}

	private Employee findOrCreateEmployee(Long petStoreID, Long employeeID) {
		Employee employee;
		
		if(Objects.isNull(employeeID)) {					
			employee = new Employee();
		}else
			employee = findEmployeeByID(petStoreID, employeeID);
		
		return employee;
	}

	private Employee findEmployeeByID(Long petStoreID, Long employeeID) {
		
		Employee employee = employeeDao.findById(employeeID).orElseThrow(
				()-> new NoSuchElementException("Employee with ID=" + employeeID + " does not exist."));
		
		if (employee.getPetStore().getPetStoreID() != petStoreID){		  
			throw new IllegalArgumentException(
					"Employee with ID= " + employeeID + " doesn't work at pet store" +
			"with ID= " + petStoreID);
		}
		
		return employee;
	}
	
	@Transactional(readOnly = false)
	public PetStoreCustomer savePetStoreCustomer(Long petStoreID, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreByID(petStoreID);
		Long customerID = petStoreCustomer.getCustomerID();
		Customer customer = findOrCreateCustomer(petStoreID, customerID);
		
		copyCustomerFields(customer, petStoreCustomer);
		
		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);
		
		Customer dbCustomer = customerDao.save(customer);
		
		return new PetStoreCustomer(dbCustomer);
	}
	
	private Customer findOrCreateCustomer(Long petStoreID, Long customerID) {
		
		Customer customer;
		
		if(Objects.isNull(customerID)) {
			customer = new Customer();
		}else
			customer = findCustomerByID(petStoreID, customerID);
		
		return customer;
	}

	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerID(petStoreCustomer.getCustomerID());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
	}
	
	private Customer findCustomerByID(Long petStoreID, Long customerID) {
				
		Customer customer = customerDao.findById(customerID).orElseThrow(
				() -> new NoSuchElementException("Customer with ID= " +customerID + " does not exist"));
		
		boolean found = false;
		for(PetStore petStore : customer.getPetStores()) {
			if(petStore.getPetStoreID() == petStoreID) {
				found = true;
				break;
			}
			if(!found) {
			throw new IllegalArgumentException("Customer with ID= " + customerID
					+ "is not a customer at pet store with ID= " + petStoreID);
			}			
			
		}
		
		return customer;	
		
	}
	
	@Transactional
	public List<PetStoreData> retrieveAllPetStores() {
				
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> result = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			
			psd.getCustomers().clear();
			psd.getEmployees().clear();
			
			result.add(psd);
		}
		return result;
	}

	@Transactional
	public PetStoreData retrievePetStoreByID(Long petStoreID) {
		
		PetStore petStore = findPetStoreByID(petStoreID);
		PetStoreData psd = new PetStoreData(petStore);
		
		return psd;
	}

	public void deletePetStoreByID(Long petStoreID) {
		
		PetStore petStore = findPetStoreByID(petStoreID);
		petStoreDao.delete(petStore);
		
	}

}
