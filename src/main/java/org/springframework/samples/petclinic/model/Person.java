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
package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Simple JavaBean domain object representing an person.
 *
 * @author Ken Krebs
 */
// @formatter:off
@MappedSuperclass
public class Person extends BaseEntity {

	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	//@ spec_public
	private /*@ nullable @*/ String firstName;
	//-RAC@ in modelString;

	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	//@ spec_public
	private /*@ nullable @*/ String lastName;
	//-RAC@ in modelString;

	//@ public invariant firstName == null || firstName.length() <= 30;
	//@ public invariant lastName == null || lastName.length() <= 30;

	//@ public normal_behavior
	//@ ensures this.id == null;
	//@ ensures this.firstName == null;
	//@ ensures this.lastName == null;
	//@ pure
	public Person() {
		super();
		this.firstName = null;
		this.lastName = null;
	}

	//@ public normal_behavior
	//@ ensures \result == this.firstName;
	//@ spec_pure
	public /*@ nullable @*/ String getFirstName() {
		return this.firstName;
	}

	//@ public normal_behavior
	//@ requires firstName == null || firstName.length() <= 30;
	//@ assignable this.firstName;
	//@ ensures this.firstName == firstName;
	public void setFirstName(/*@ nullable @*/ String firstName) {
		this.firstName = firstName;
	}

	//@ public normal_behavior
	//@ ensures \result == this.lastName;
	//@ spec_pure
	public /*@ nullable @*/ String getLastName() {
		return this.lastName;
	}

	//@ public normal_behavior
	//@ requires lastName == null || lastName.length() <= 30;
	//@ assignable this.lastName;
	//@ ensures this.lastName == lastName;
	public void setLastName(/*@ nullable @*/ String lastName) {
		this.lastName = lastName;
	}

}
// @formatter:on
