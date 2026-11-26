# Lab 5 – JML Software Verification

## 1. Configurazione e obiettivi

La verifica è stata svolta su Windows con PowerShell, Maven, OpenJML e il solver Z3 5.1.0. Il classpath comprende `target/classes` e le dipendenze in `target/dependency`; `--specs-path` carica `jml-specs` e `src/main/java`. La nostra analisi riguarda sei classi del dominio, scelte per verificare operazioni elementari, ereditarietà, collezioni e cicli.

## 2. Specifiche JML introdotte

- **BaseEntity:** invariante sull'ID (nullo o non negativo), precondizione equivalente per `setId` e postcondizioni di costruttore, getter, setter e `isNew`.
- **NamedEntity:** inizializzazione del nome a null, postcondizioni di getter e setter e rappresentazione astratta di `toString` mediante `modelString`.
- **Person:** invarianti e precondizioni sulla lunghezza massima di nome e cognome (30 caratteri), ammettendo null; postcondizioni di costruttore, getter e setter.
- **Pet:** inizializzazione dei campi, invariante sulla collezione delle visite non nulla e contratti di getter e setter. `addVisit` richiede una visita non nulla e ne garantisce la presenza nella collezione, con incremento della dimensione compreso tra 0 e 1.
- **Vet:** inizializzazione e accesso alla collezione delle specializzazioni, conteggio non negativo, inserimento di specializzazioni non nulle e restituzione di una lista non nulla della stessa dimensione del set.
- **Owner:** invariante sulla lista degli animali non nulla, limiti di 255 e 80 caratteri per indirizzo e città, contratti di inizializzazione e accesso ai campi. `addPet` limita la variazione della dimensione e gestisce null e duplicati. `getPet` non modifica lo stato e vincola gli eventuali risultati all'appartenenza alla lista, all'ID o alle condizioni di ricerca per nome specificate. `addVisit` richiede ID e visita non nulli e preserva il numero di animali. `toString` utilizza `modelString` e il metodo modello `decimalChars`.

Le specifiche comprendono inoltre frame conditions (`assignable`), annotazioni di purezza, `nullable`, `spec_public`, contratti eccezionali e invarianti di ciclo con `loop_writes` e `decreases`.

## 3. Verifica statica (ESC)

L'esecuzione con `--esc --progress --timeout=30` ha validato 40 metodi su 40 (incluso `decimalChars`) e 6 classi su 6, senza esclusioni, errori, timeout o warning. La prima verifica completa riuscita è durata 18,3 s. Vedi [report ESC](lab5-jml/esc-final-report.txt).

Le criticità hanno riguardato visibilità dei campi, valori nulli, invarianti ereditati nei costruttori e specifiche incomplete delle librerie. Per impedire a Spring JavaFormat di alterare le annotazioni JML, il codice è stato protetto con `@formatter:off/on`.

Le specifiche `Set.jml` e `List.jml` modellano la dimensione delle collezioni e, per `List`, le dipendenze di lettura; `LinkedHashSet.jml` specifica il costruttore. In `Vet`, la pipeline Stream è stata sostituita da una copia in `ArrayList` con ordinamento esplicito, specificando la conservazione della dimensione in `sort`. In `Owner` sono stati introdotti cicli indicizzati, controlli sui valori nulli e confronti numerici degli ID. La gestione delle visite ha richiesto controlli Java in sostituzione degli assert di Spring, frame conditions più precise e un controllo difensivo contro l'aliasing. `ToStringCreator` è stato sostituito da concatenazioni esplicite, conservando i campi informativi ma modificando il formato della stringa.

## 4. Verifica dinamica (RAC)

Le sei classi sono state compilate con `--rac` in `target/jml-rac`, separatamente dalle classi Maven. Alcune specifiche astratte (`modelString`, `decimalChars`, data group `values`) non risultavano disponibili a runtime; inoltre, `loop_writes` causava un errore interno di OpenJML. L'impiego delle annotazioni condizionali `-RAC`/`+RAC` ha consentito di preservare le proprietà ESC e completare la compilazione RAC.

[PetClinicJmlDemo.java](lab5-jml/PetClinicJmlDemo.java) è stato eseguito con `openjml-java`, `-ea` e la modalità `assertionerror`. I sei gruppi di test positivi sono stati superati (exit code 0). I cinque test negativi (ID negativo, nome di 31 caratteri, specializzazione nulla, visita nulla e città di 81 caratteri) hanno rilevato `JML precondition is false` (exit code 1), come previsto. Vedi [report RAC](lab5-jml/rac-final-report.txt).

## 5. Risultati e considerazioni

ESC ha validato tutti i metodi selezionati rispetto ai contratti definiti, mentre RAC ha confermato i casi di prova eseguiti. I test Maven e i controlli finali hanno avuto esito positivo. La verifica resta circoscritta alle proprietà specificate e dipende dai contratti di libreria adottati (per `sort` si assume un comparatore privo di effetti esterni). Non dimostra l'ordinamento o la completezza delle ricerche, né RAC controlla tutte le proprietà astratte ESC. L'attività ha evidenziato per il nostro gruppo l'importanza di contratti precisi, frame conditions, invarianti e verifica modulare.
