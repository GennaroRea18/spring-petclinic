package org.springframework.samples.petclinic.benchmarks;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@DynamicHalt(model = "oscnn")
public class PetClinicDomainBenchmark {

	private Owner owner;

	private Vet vet;

	private Pet targetPet;

	@Setup(Level.Iteration)
	public void setup() {
		owner = new Owner();
		for (int i = 0; i < 100; i++) {
			Pet p = new Pet();
			p.setId(i + 1);
			p.setName("Pet-" + i);
			owner.addPet(p);
		}
		targetPet = owner.getPet(1);

		vet = new Vet();
		for (int i = 0; i < 20; i++) {
			Specialty s = new Specialty();
			s.setId(i + 1);
			s.setName("Specialty-" + (20 - i));
			vet.addSpecialty(s);
		}
	}

	@Benchmark
	public Pet benchmarkOwnerGetPetByNameWorstCase() {
		return owner.getPet("Pet-99", false);
	}

	@Benchmark
	public List<Specialty> benchmarkVetGetSpecialtiesSorted() {
		return vet.getSpecialties();
	}

	@Benchmark
	public Pet benchmarkPetAddVisit() {
		Visit v = new Visit();
		v.setDate(LocalDate.now());
		v.setDescription("Routine checkup");
		targetPet.addVisit(v);
		return targetPet;
	}

}