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

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects
 * needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
// @formatter:off
@MappedSuperclass
public class BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@ spec_public
	private /*@ nullable @*/ Integer id;
	//-RAC@ in modelString;

	//@ public invariant id == null || id.intValue() >= 0;

	//@ public normal_behavior
	//@ ensures this.id == null;
	//@ pure
	public BaseEntity() {
		this.id = null;
	}

	//@ public normal_behavior
	//@ ensures \result == id;
	//@ spec_pure
	public /*@ nullable @*/ Integer getId() {
		return id;
	}

	//@ public normal_behavior
	//@ requires id == null || id.intValue() >= 0;
	//@ assignable this.id;
	//@ ensures this.id == id;
	public void setId(/*@ nullable @*/ Integer id) {
		this.id = id;
	}

	//@ public normal_behavior
	//@ ensures \result == (this.id == null);
	//@ spec_pure
	public boolean isNew() {
		return this.id == null;
	}

}
// @formatter:on