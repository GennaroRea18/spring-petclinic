
/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Oliver Drotbohm
 * @author Wick Dynex
 */

// @formatter:off
@Entity
@Table(name = "owners")
public class Owner extends Person {

    @Column
	@NotBlank
	@Size(max = 255)
	//@ spec_public
	private /*@ nullable @*/ String address;
	//-RAC@ in modelString;

	@Column
	@NotBlank
	@Size(max = 80)
	//@ spec_public
	private /*@ nullable @*/ String city;
	//-RAC@ in modelString;

	@Column
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")
	//@ spec_public
	private /*@ nullable @*/ String telephone;
	//-RAC@ in modelString;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	//@ spec_public
	private final List<Pet> pets = new ArrayList<>();

    // Class invariants

	//@ public invariant pets != null;
	//@ public invariant address == null || address.length() <= 255;
	//@ public invariant city == null || city.length() <= 80;

	/*-RAC@
	@ public normal_behavior
	@ reads \everything;
	@ ensures \result == \string.of(value, 10);
	@ public static model helper spec_pure \string decimalChars(int value) {
	@     String text = Integer.toString(value);
	@     return text.chars;
	@ }
	@*/

	/*-RAC@
	@ public represents modelString =
	@     "Owner[id=".chars
	@     + (id == null ? "null".chars : decimalChars(id.intValue()))
	@     + ", new=".chars
	@     + (id == null ? "true".chars : "false".chars)
	@     + ", lastName=".chars
	@     + (lastName == null ? "null" : lastName).chars
	@     + ", firstName=".chars
	@     + (firstName == null ? "null" : firstName).chars
	@     + ", address=".chars
	@     + (address == null ? "null" : address).chars
	@     + ", city=".chars
	@     + (city == null ? "null" : city).chars
	@     + ", telephone=".chars
	@     + (telephone == null ? "null" : telephone).chars
	@     + "]".chars;
	@*/

    // Constructor

    //@ public normal_behavior
    //@ ensures this.id == null;
    //@ ensures this.firstName == null;
    //@ ensures this.lastName == null;
    //@ ensures this.address == null;
    //@ ensures this.city == null;
    //@ ensures this.telephone == null;
    //@ ensures this.pets != null;
    //@ pure
    public Owner() {
        super();
        this.address = null;
        this.city = null;
        this.telephone = null;
    }

    // Address

    //@ public normal_behavior
    //@ ensures \result == this.address;
    //@ spec_pure
    public /*@ nullable @*/ String getAddress() {
        return this.address;
    }

    //@ public normal_behavior
    //@ requires address == null || address.length() <= 255;
    //@ assignable this.address;
    //@ ensures this.address == address;
    public void setAddress(/*@ nullable @*/ String address) {
        this.address = address;
    }

    // City

    //@ public normal_behavior
    //@ ensures \result == this.city;
    //@ spec_pure
    public /*@ nullable @*/ String getCity() {
        return this.city;
    }

    //@ public normal_behavior
    //@ requires city == null || city.length() <= 80;
    //@ assignable this.city;
    //@ ensures this.city == city;
    public void setCity(/*@ nullable @*/ String city) {
        this.city = city;
    }

    // Telephone

    //@ public normal_behavior
    //@ ensures \result == this.telephone;
    //@ spec_pure
    public /*@ nullable @*/ String getTelephone() {
        return this.telephone;
    }

    //@ public normal_behavior
    //@ assignable this.telephone;
    //@ ensures this.telephone == telephone;
    public void setTelephone(/*@ nullable @*/ String telephone) {
        this.telephone = telephone;
    }

    // Pets

    //@ public normal_behavior
    //@ ensures \result != null;
    //@ ensures \result == this.pets;
    //@ spec_pure
    public List<Pet> getPets() {
        return this.pets;
    }

    //@ public behavior
    //@ ensures pets.size() >= \old(pets.size());
    //@ ensures (\bigint) pets.size() <= (\bigint) \old(pets.size()) + 1;
    //@ ensures pet == null ==> pets.size() == \old(pets.size());
    //@ ensures pet != null && \old(pets.contains(pet))
    //@     ==> pets.size() == \old(pets.size());
    //@ signals_only RuntimeException;
    public void addPet(/*@ nullable @*/ Pet pet) {

        if (pet == null) {
            return;
        }

        if (getPets().contains(pet)) {
            return;
        }

        List<Pet> currentPets = getPets();

        //@ loop_invariant 0 <= i && i <= currentPets.size();
		//-RAC@ loop_writes i;
		//@ decreases currentPets.size() - i;
        for (int i = 0; i < currentPets.size(); i++) {

            Pet existingPet = currentPets.get(i);

            if (!existingPet.isNew() && !pet.isNew()
                    && Objects.equals(existingPet.getId(), pet.getId())) {
                return;
            }
        }

        getPets().add(pet);
    }

    /**
     * Return the Pet with the given name,
     * or null if none found for this Owner.
     */

    //@ public behavior
	//@ assignable \nothing;
    //@ ensures \result == null || pets.contains(\result);
    //@ signals_only RuntimeException;
    public /*@ nullable @*/ Pet getPet(
            /*@ nullable @*/ String name) {

        return getPet(name, false);
    }

    /**
     * Return the Pet with the given id,
     * or null if none found for this Owner.
     */

    //@ public behavior
	//@ assignable \nothing;
	//@ ensures \result == null || pets.contains(\result);
	//@ ensures \result != null ==> !\result.isNew();
	//@ ensures \result != null ==> \result.getId() != null;
	//@ ensures \result != null ==> id != null;
	//@ ensures \result != null ==> 
	//@     \result.getId().intValue() == id.intValue();
	//@ signals_only RuntimeException;
	public /*@ nullable @*/ Pet getPet(
			/*@ nullable @*/ Integer id) {

		if (id == null) {
			return null;
		}

		List<Pet> currentPets = getPets();

		//@ loop_invariant 0 <= i && i <= currentPets.size();
		//-RAC@ loop_writes i;
		//@ decreases currentPets.size() - i;
		for (int i = 0; i < currentPets.size(); i++) {

			Pet pet = currentPets.get(i);

			/*@ nullable @*/ Integer compId = pet.getId();

			if (compId != null
					&& compId.intValue() == id.intValue()) {
				return pet;
			}
		}

		return null;
	}

    /**
     * Return the Pet with the given name,
     * optionally ignoring new pets.
     */

    //@ public behavior
	//@ assignable \nothing;
	//@ ensures \result == null || pets.contains(\result);
	//@ ensures \result != null && ignoreNew ==> !\result.isNew();
	//@ ensures \result != null ==> \result.getName() != null;
	//@ ensures \result != null ==> name != null;
	//@ signals_only RuntimeException;
	public /*@ nullable @*/ Pet getPet(
			/*@ nullable @*/ String name, boolean ignoreNew) {

		if (name == null) {
			return null;
		}

		List<Pet> currentPets = getPets();

		//@ loop_invariant 0 <= i && i <= currentPets.size();
		//-RAC@ loop_writes i;
		//@ decreases currentPets.size() - i;
		for (int i = 0; i < currentPets.size(); i++) {

			Pet pet = currentPets.get(i);

			/*@ nullable @*/ String compName = pet.getName();

			if (compName != null && compName.equalsIgnoreCase(name)) {

				if (!ignoreNew || !pet.isNew()) {
					return pet;
				}
			}
		}

		return null;
	}

    //@ also
	//@ public normal_behavior
	//@ ensures \result != null;
	//@ assignable \nothing;
	@Override
	public String toString() {

		/*@ nullable @*/ Integer ownerId = getId();
		/*@ nullable @*/ String ownerFirstName = getFirstName();
		/*@ nullable @*/ String ownerLastName = getLastName();

		return "Owner[id="
			.concat(ownerId == null
				? "null" : Integer.toString(ownerId.intValue()))
			.concat(", new=")
			.concat(ownerId == null ? "true" : "false")
			.concat(", lastName=")
			.concat(ownerLastName == null ? "null" : ownerLastName)
			.concat(", firstName=")
			.concat(ownerFirstName == null ? "null" : ownerFirstName)
			.concat(", address=")
			.concat(address == null ? "null" : address)
			.concat(", city=")
			.concat(city == null ? "null" : city)
			.concat(", telephone=")
			.concat(telephone == null ? "null" : telephone)
			.concat("]");
	}

    /**
     * Adds the given Visit to the Pet
     * with the given identifier.
     */

    //@ public behavior
	//@ requires petId != null;
	//@ requires visit != null;
	//@ ensures pets.size() == \old(pets.size());
	//@ signals_only RuntimeException;
	public void addVisit(Integer petId, Visit visit) {

		//@ ghost int initialPetCount = pets.size();

		if (petId == null) {
			throw new IllegalArgumentException(
				"Pet identifier must not be null!");
		}

		if (visit == null) {
			throw new IllegalArgumentException(
				"Visit must not be null!");
		}

		/*@ nullable @*/ Pet pet = getPet(petId);

		if (pet == null) {
			throw new IllegalArgumentException(
				"Invalid Pet identifier!");
		}

		//@ assert pets.size() == initialPetCount;

		if ((Object) this.pets == (Object) pet.getVisits()) {
			throw new IllegalStateException(
				"Pets and visits must be distinct collections!");
		}

		//@ assert (Object) this.pets != (Object) pet.visits;

		pet.addVisit(visit);

		//@ assert pets.size() == initialPetCount;
	}

}
// @formatter:on
