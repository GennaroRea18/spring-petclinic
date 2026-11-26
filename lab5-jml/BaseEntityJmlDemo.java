import org.springframework.samples.petclinic.model.BaseEntity;

public class BaseEntityJmlDemo {

    public static void main(String[] args) {

        BaseEntity entity = new BaseEntity();

        // Test 1: New entity
        System.out.println("Test 1 - New entity");
        System.out.println("ID: " + entity.getId());
        System.out.println("isNew: " + entity.isNew());

        assert entity.getId() == null;
        assert entity.isNew();

        // Test 2: Valid ID
        System.out.println("\nTest 2 - Valid ID");
        entity.setId(10);

        System.out.println("ID: " + entity.getId());
        System.out.println("isNew: " + entity.isNew());

        assert entity.getId() == 10;
        assert !entity.isNew();

        // Test 3: Boundary value
        System.out.println("\nTest 3 - Boundary value");
        entity.setId(0);

        System.out.println("ID: " + entity.getId());

        assert entity.getId() == 0;

        // Test 4: Null ID
        System.out.println("\nTest 4 - Null ID");
        entity.setId(null);

        System.out.println("ID: " + entity.getId());
        System.out.println("isNew: " + entity.isNew());

        assert entity.getId() == null;
        assert entity.isNew();

        // Test 5: Invalid ID
        if (args.length > 0 && args[0].equals("bad")) {
            System.out.println("\nTest 5 - Invalid ID");
            entity.setId(-1);
        }

        System.out.println("\nExecution completed.");
    }

}