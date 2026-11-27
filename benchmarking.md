# Strategia e Analisi di Microbenchmarking (JMH, JU2JMH e AMBER)

## 1. Metodi sottoposti a Benchmark
Sono stati valutati **12 microbenchmark** sul dominio core di `spring-petclinic` (`org.springframework.samples.petclinic.benchmarks`), combinando **3 benchmark scritti manualmente** (`PetClinicDomainBenchmark`) e **9 convertiti da test JUnit 5** tramite **JU2JMH** (`OwnerUnitTest_JU5Benchmark`, `PetUnitTest_JU5Benchmark`):

* **Benchmark manuali (`PetClinicDomainBenchmark`):**
  * `benchmarkVetGetSpecialtiesSorted`: copia e ordinamento delle specialità di `Vet`.
  * `benchmarkOwnerGetPetByNameWorstCase`: ricerca lineare case-insensitive (`getPet(name, ignoreNew)`) nel caso peggiore.
  * `benchmarkPetAddVisit`: aggiunta di una `Visit` a un `Pet` con reset stato per iterazione (`@Setup(Level.Iteration)`).
* **Benchmark convertiti con JU2JMH:**
  * `OwnerUnitTest_JU5Benchmark` (6): `addPet_WhenPetHasExistingId_AddsPetToOwner`, `addPet_WhenPetIsNew_AddsPetToOwner`, `addVisit_WhenInvalidArguments_ThrowsIllegalArgumentException`, `addVisit_WhenValidPetId_AddsVisitToPet`, `getPetById_WhenPetExistsAndIsNotNew_ReturnsPet`, `getPetByName_CaseInsensitiveAndIgnoreNewFlag_ReturnsExpectedPet`.
  * `PetUnitTest_JU5Benchmark` (3): `addVisit_WhenVisitAdded_StoresVisitInCollection`, `getVisits_WhenNoVisitsAdded_ReturnsEmptyCollection`, `setAndGetProperties_BirthDateAndType_ReturnsExpectedValues`.

## 2. Motivazione della Scelta
I metodi scelti rappresentano le operazioni in-memory più frequenti di PetClinic (scansione collezioni, ordinamento, ricerca entità e validazione). Essendo puri, deterministici e indipendenti da database o contesto Spring, isolano il comportamento del compilatore JIT e i costi di allocazione JVM, evitando *dead-code elimination* tramite valori di ritorno e JMH `Blackhole`.

## 3. Configurazione Warm-up, Misurazione e AMBER
I benchmark sono compilati con `jmh-core` `1.37-extended` ed eseguiti su OpenJDK 17 (`Mode.Throughput`, `ops/ms`, 1 fork, 1 thread) con **AMBER** (`@DynamicHalt(model = "oscnn")`) collegato a `jpt-service` su Docker (`host.docker.internal:50001`). Sono state confrontate due strategie di warm-up:
1. **Baseline AMBER (`1s`):** `@Warmup(time = 1s)`, `@Measurement(iterations = 5, time = 2s)`.
2. **AMBER ridotto (`500ms`):** `-w 500ms` con lo stesso modello `oscnn`.

In entrambi i run AMBER ha attivato l'arresto dinamico all'iterazione 100 (`Cnt = 100`), riducendo la durata totale da ~17 min a ~10 min con `500ms` mantenendo intervalli di confidenza allineati.

## 4. Risultati e Analisi dei Colli di Bottiglia

| Benchmark | AMBER `1s` (ops/ms) | AMBER `500ms` (ops/ms) |
| :--- | :---: | :---: |
| `PetUnitTest.getVisits_Empty` | `49856.88 ± 105.35` | `49840.78 ± 175.11` |
| `PetUnitTest.setAndGetProperties` | `45455.21 ± 113.75` | `45375.89 ± 189.88` |
| `OwnerUnitTest.addPet_IsNew` | `39765.58 ± 157.25` | `39506.80 ± 255.55` |
| `OwnerUnitTest.addPet_ExistingId` | `39592.66 ± 171.81` | `39268.59 ± 297.80` |
| `OwnerUnitTest.getPetById` | `32811.50 ± 187.45` | `33122.28 ± 165.17` |
| `OwnerUnitTest.getPetByName` | `14661.30 ± 45.17` | `16138.65 ± 138.12` |
| `PetUnitTest.addVisit_StoresVisit` | `10611.41 ± 28.09` | `10672.05 ± 37.74` |
| `OwnerUnitTest.addVisit_ValidPetId` | `9412.05 ± 37.72` | `9442.63 ± 31.12` |
| `Domain.benchmarkPetAddVisit` | `2821.26 ± 51.36` | `2824.23 ± 60.26` |
| `Domain.benchmarkOwnerGetPetWorstCase` | `2010.29 ± 4.58` | `2007.03 ± 5.01` |
| `Domain.benchmarkVetGetSpecialtiesSorted` | `1858.28 ± 25.41` | `1898.31 ± 25.21` |
| `OwnerUnitTest.addVisit_InvalidArgs` | `576.61 ± 3.58` | `559.92 ± 5.07` |

* **Colli di bottiglia:** `addVisit_WhenInvalidArguments` (`576.61 ops/ms`) è il più lento per l'istanziazione delle eccezioni e dello stack trace JVM. Nei percorsi nominali, `Vet.getSpecialties()` (`1858.28 ops/ms`) e `Owner.getPet()` worst-case (`2010.29 ops/ms`) sono ~20 volte più lenti degli accessi diretti (`45k–49k ops/ms`) per l'allocazione/ordinamento della `ArrayList` e la scansione lineare $O(n)$.
* **Criticità risolte:** Su `jpt-service` (Docker) è stato isolato il modello PyTorch `OSCNN` in `setup.py` (evitando il crash TensorFlow `random_device could not be read`) e convertiti i pesi con `.double()` in `wrap_oscnn.py` (risolvendo `expected scalar type Double but found Float`).

## 5. Miglioramenti e Conservazione del Codice
`Vet.getSpecialties()` era già stato rifattorizzato sostituendo `PropertyComparator.sort` (reflection Spring) con `Comparator` tipizzato, e `Owner.getPet()` ottimizzato con `equalsIgnoreCase()` evitando allocazioni `.toLowerCase()`. Avendo entrambe le classi contratti formali **OpenJML** (`//@ ensures`, `loop_invariant`), le implementazioni verificate sono state preservate intatte.