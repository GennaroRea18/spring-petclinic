package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OwnerJmlCoverageTests {

	@Test
	void shouldIgnoreNullAndDuplicatePets() {
		Owner owner = new Owner();
		owner.addPet(null);
		assertTrue(owner.getPets().isEmpty());
		Pet pet = new Pet();
		pet.setId(10);
		owner.addPet(pet);
		owner.addPet(pet);
		Pet duplicate = new Pet();
		duplicate.setId(10);
		owner.addPet(duplicate);
		assertEquals(1, owner.getPets().size());
		Pet anotherPet = new Pet();
		anotherPet.setId(20);
		owner.addPet(anotherPet);
		assertEquals(2, owner.getPets().size());
	}

	@Test
	void shouldFindPetsByIdentifier() {
		Owner owner = new Owner();
		Pet newPet = new Pet();
		owner.addPet(newPet);
		Pet existingPet = new Pet();
		existingPet.setId(100);
		owner.addPet(existingPet);
		assertSame(existingPet, owner.getPet(100));
		assertNull(owner.getPet(999));
		assertNull(owner.getPet((Integer) null));
	}

	@Test
	void shouldFindPetsByNameAndIgnoreNewPets() {
		Owner owner = new Owner();
		Pet unnamedPet = new Pet();
		owner.addPet(unnamedPet);
		Pet newPet = new Pet();
		newPet.setName("Fido");
		owner.addPet(newPet);
		assertSame(newPet, owner.getPet("FIDO"));
		assertNull(owner.getPet("Fido", true));
		Pet existingPet = new Pet();
		existingPet.setId(100);
		existingPet.setName("Fido");
		owner.addPet(existingPet);
		assertSame(existingPet, owner.getPet("Fido", true));
		assertNull(owner.getPet("Unknown"));
		assertNull(owner.getPet((String) null));
	}

	@Test
	void shouldRejectInvalidVisitArguments() {
		Owner owner = new Owner();
		Visit visit = new Visit();
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(null, visit));
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(100, null));
		assertThrows(IllegalArgumentException.class, () -> owner.addVisit(999, visit));
	}

	@Test
	void shouldAddVisitWithoutChangingPets() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(100);
		owner.addPet(pet);
		Visit visit = new Visit();
		owner.addVisit(100, visit);
		assertTrue(pet.getVisits().contains(visit));
		assertEquals(1, owner.getPets().size());
	}

	@Test
	void shouldRepresentOwnerWithNullFields() {
		Owner owner = new Owner();
		String result = owner.toString();
		assertNotNull(result);
		assertTrue(result.contains("id=null"));
		assertTrue(result.contains("new=true"));
		assertTrue(result.contains("firstName=null"));
		assertTrue(result.contains("lastName=null"));
		assertTrue(result.contains("address=null"));
		assertTrue(result.contains("city=null"));
		assertTrue(result.contains("telephone=null"));
	}

	@Test
	void shouldRepresentOwnerWithPopulatedFields() {
		Owner owner = new Owner();
		owner.setId(100);
		owner.setFirstName("Mario");
		owner.setLastName("Rossi");
		owner.setAddress("Via Roma 10");
		owner.setCity("Salerno");
		owner.setTelephone("1234567890");
		String result = owner.toString();
		assertTrue(result.contains("id=100"));
		assertTrue(result.contains("new=false"));
		assertTrue(result.contains("firstName=Mario"));
		assertTrue(result.contains("lastName=Rossi"));
		assertTrue(result.contains("address=Via Roma 10"));
		assertTrue(result.contains("city=Salerno"));
		assertTrue(result.contains("telephone=1234567890"));
	}

}
