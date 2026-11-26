
import java.time.LocalDate;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

public class PetClinicJmlDemo {

    public static void main(String[] args) {

        String mode = args.length == 0 ? "valid" : args[0];

        switch (mode) {
            case "valid":
                testValid();
                break;

            case "invalid-id":
                new BaseEntity().setId(-1);
                break;

            case "invalid-name":
                new Person().setFirstName("A".repeat(31));
                break;

            case "invalid-specialty":
                new Vet().addSpecialty(null);
                break;

            case "invalid-visit":
                new Pet().addVisit(null);
                break;

            case "invalid-city":
                new Owner().setCity("A".repeat(81));
                break;

            default:
                throw new IllegalArgumentException("Unknown test: " + mode);
        }

        if (!mode.equals("valid")) {
            System.err.println("FAIL: expected JML violation not detected");
            System.exit(2);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Functional test failed: " + message);
        }
    }

    private static void testValid() {

        System.out.println("=== RAC POSITIVE TESTS ===");

        // BaseEntity
        BaseEntity entity = new BaseEntity();

        check(entity.isNew(), "New entity");

        entity.setId(10);
        check(entity.getId() == 10, "Entity ID");
        check(!entity.isNew(), "Existing entity");

        entity.setId(null);
        check(entity.isNew(), "Reset entity ID");

        System.out.println("[PASS] BaseEntity");

        // NamedEntity
        NamedEntity named = new NamedEntity();

        check("<null>".equals(named.toString()), "Null name");

        named.setName("Fido");
        check("Fido".equals(named.getName()), "Entity name");
        check("Fido".equals(named.toString()), "Entity string");

        System.out.println("[PASS] NamedEntity");

        // Person
        Person person = new Person();

        person.setFirstName("Mario");
        person.setLastName("Rossi");

        check("Mario".equals(person.getFirstName()), "First name");
        check("Rossi".equals(person.getLastName()), "Last name");

        System.out.println("[PASS] Person");

        // Vet
        Vet vet = new Vet();

        vet.setFirstName("Luca");
        vet.setLastName("Verdi");

        check(vet.getNrOfSpecialties() == 0, "Empty specialties");

        Specialty surgery = new Specialty();
        surgery.setName("surgery");

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");

        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);
        vet.addSpecialty(surgery);

        check(vet.getNrOfSpecialties() == 2, "Specialties count");

        check(
            "dentistry".equals(vet.getSpecialties().get(0).getName()),
            "Specialties ordering"
        );

        System.out.println("[PASS] Vet");

        // Pet
        Pet pet = new Pet();

        pet.setId(100);
        pet.setName("Fido");
        pet.setBirthDate(LocalDate.of(2020, 5, 15));

        Visit visit = new Visit();

        pet.addVisit(visit);
        pet.addVisit(visit);

        check(pet.getVisits().contains(visit), "Visit added");
        check(pet.getVisits().size() == 1, "Duplicate visit");

        System.out.println("[PASS] Pet");

        // Owner
        Owner owner = new Owner();

        owner.setId(1);
        owner.setFirstName("Mario");
        owner.setLastName("Rossi");
        owner.setAddress("Via Roma 10");
        owner.setCity("Salerno");
        owner.setTelephone("1234567890");

        owner.addPet(pet);
        owner.addPet(pet);
        owner.addPet(null);

        check(owner.getPets().size() == 1, "Pets count");

        Pet duplicate = new Pet();
        duplicate.setId(100);
        duplicate.setName("Fido");

        owner.addPet(duplicate);

        check(owner.getPets().size() == 1, "Duplicate pet ID");

        check(owner.getPet(100) == pet, "Search by ID");
        check(owner.getPet("FIDO") == pet, "Search by name");
        check(owner.getPet("Fido", true) == pet, "Ignore new pets");
        check(owner.getPet(999) == null, "Unknown pet");

        Visit secondVisit = new Visit();

        owner.addVisit(100, secondVisit);

        check(pet.getVisits().contains(secondVisit), "Owner visit");
        check(owner.getPets().size() == 1, "Unchanged pets count");

        check(owner.toString() != null, "Owner string");

        System.out.println("[PASS] Owner");

        System.out.println();
        System.out.println("ALL POSITIVE TESTS PASSED");
    }
}
