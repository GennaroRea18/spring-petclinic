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
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@DynamicHalt(model = "oscnn")
public class PetUnitTest_JU5Benchmark {

	private Pet pet;

	@Setup(Level.Invocation)
	public void setUp() {
		pet = new Pet();
		pet.setId(1);
		pet.setName("Micio");
	}

	@Benchmark
	public void getVisits_WhenNoVisitsAdded_ReturnsEmptyCollection(Blackhole bh) {
		bh.consume(pet.getVisits().isEmpty());
	}

	@Benchmark
	public void addVisit_WhenVisitAdded_StoresVisitInCollection(Blackhole bh) {
		Visit visit = new Visit();
		visit.setDate(LocalDate.of(2026, 3, 15));
		visit.setDescription("Vaccinazione");
		pet.addVisit(visit);
		bh.consume(pet.getVisits());
	}

	@Benchmark
	public void setAndGetProperties_BirthDateAndType_ReturnsExpectedValues(Blackhole bh) {
		PetType cat = new PetType();
		cat.setId(2);
		cat.setName("cat");
		LocalDate birthDate = LocalDate.of(2023, 8, 20);
		pet.setType(cat);
		pet.setBirthDate(birthDate);
		bh.consume(pet.getType());
		bh.consume(pet.getType().getName());
		bh.consume(pet.getBirthDate());
	}

}