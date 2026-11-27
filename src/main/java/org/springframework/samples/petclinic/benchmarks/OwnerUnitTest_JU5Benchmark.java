package org.springframework.samples.petclinic.benchmarks;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.DynamicHalt;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@DynamicHalt(model = "oscnn")
public class OwnerUnitTest_JU5Benchmark {

	private Owner owner;

	private Pet existingPet;

	private Pet newPet;

	@Setup(Level.Invocation)
	public void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("Giulia");
		owner.setLastName("Bianchi");
		owner.setAddress("Via Roma 12");
		owner.setCity("Salerno");
		owner.setTelephone("0891234567");

		existingPet = new Pet();
		existingPet.setId(10);
		existingPet.setName("Fido");

		newPet = new Pet();
		newPet.setName("Luna");
	}

	@Benchmark
	public void addPet_WhenPetIsNew_AddsPetToOwner(Blackhole bh) {
		owner.addPet(newPet);
		bh.consume(owner.getPets());
	}

	@Benchmark
	public void addPet_WhenPetHasExistingId_AddsPetToOwner(Blackhole bh) {
		owner.addPet(existingPet);
		bh.consume(owner.getPets());
	}

	@Benchmark
	public void getPetByName_CaseInsensitiveAndIgnoreNewFlag_ReturnsExpectedPet(Blackhole bh) {
		owner.addPet(newPet);
		owner.getPets().add(existingPet);
		bh.consume(owner.getPet("fido", true));
		bh.consume(owner.getPet("luna", true));
		bh.consume(owner.getPet("LUNA", false));
		bh.consume(owner.getPet("luna"));
		bh.consume(owner.getPet("Inesistente"));
	}

	@Benchmark
	public void getPetById_WhenPetExistsAndIsNotNew_ReturnsPet(Blackhole bh) {
		owner.addPet(newPet);
		owner.getPets().add(existingPet);
		bh.consume(owner.getPet(10));
		bh.consume(owner.getPet(999));
	}

	@Benchmark
	public void addVisit_WhenValidPetId_AddsVisitToPet(Blackhole bh) {
		owner.getPets().add(existingPet);
		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2026, 5, 10));
		visit.setDescription("Controllo annuale");
		owner.addVisit(10, visit);
		bh.consume(existingPet.getVisits());
	}

	@Benchmark
	public void addVisit_WhenInvalidArguments_ThrowsIllegalArgumentException(Blackhole bh) {
		owner.getPets().add(existingPet);
		Visit visit = new Visit();
		try {
			owner.addVisit(null, visit);
		}
		catch (IllegalArgumentException e) {
			bh.consume(e);
		}
		try {
			owner.addVisit(10, null);
		}
		catch (IllegalArgumentException e) {
			bh.consume(e);
		}
		try {
			owner.addVisit(999, visit);
		}
		catch (IllegalArgumentException e) {
			bh.consume(e);
		}
	}

}