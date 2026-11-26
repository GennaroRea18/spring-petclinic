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

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.NamedEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Wick Dynex
 */
// @formatter:off
@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	//@ spec_public
	private /*@ nullable @*/ LocalDate birthDate;

	@ManyToOne
	@JoinColumn(name = "type_id")
	//@ spec_public
	private /*@ nullable @*/ PetType type;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "pet_id")
	@OrderBy("date ASC")
	//@ spec_public
	private final Set<Visit> visits = new LinkedHashSet<>();

	//@ public invariant visits != null;

	//@ public behavior
	//@ ensures this.id == null;
	//@ ensures this.name == null;
	//@ ensures this.birthDate == null;
	//@ ensures this.type == null;
	//@ ensures this.visits != null;
	//@ signals_only RuntimeException;
	//@ pure
	public Pet() {
		super();
		this.birthDate = null;
		this.type = null;
	}

	//@ public normal_behavior
	//@ assignable this.birthDate;
	//@ ensures this.birthDate == birthDate;
	public void setBirthDate(/*@ nullable @*/ LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	//@ public normal_behavior
	//@ ensures \result == this.birthDate;
	//@ spec_pure
	public /*@ nullable @*/ LocalDate getBirthDate() {
		return this.birthDate;
	}

	//@ public normal_behavior
	//@ ensures \result == this.type;
	//@ spec_pure
	public /*@ nullable @*/ PetType getType() {
		return this.type;
	}

	//@ public normal_behavior
	//@ assignable this.type;
	//@ ensures this.type == type;
	public void setType(/*@ nullable @*/ PetType type) {
		this.type = type;
	}

	//@ public normal_behavior
	//@ ensures \result == this.visits;
	//@ ensures \result != null;
	//@ spec_pure
	public Collection<Visit> getVisits() {
		return this.visits;
	}

	//@ public behavior
	//@ requires visit != null;
	//-RAC@ assignable this.visits.values;
	//+RAC@ assignable this.visits.objectState;
	//@ ensures visits.contains(visit);
	//@ ensures visits.size() >= \old(visits.size());
	//@ ensures (\bigint) visits.size() <=
	//@     (\bigint) \old(visits.size()) + 1;
	//@ signals_only RuntimeException;
	public void addVisit(Visit visit) {
		getVisits().add(visit);
	}

}
// @formatter:on