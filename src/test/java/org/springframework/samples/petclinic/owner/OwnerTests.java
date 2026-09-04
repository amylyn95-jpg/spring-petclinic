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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerTests {

	@Test
	void addPetAddsPersistedPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		pet.setName("Buddy");

		owner.addPet(pet);

		assertTrue(owner.getPets().contains(pet));
		assertEquals(1, owner.getPets().size());
	}

	@Test
	void addPetDoesNotAddDuplicatePet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		pet.setName("Buddy");

		owner.addPet(pet);
		owner.addPet(pet);

		assertEquals(1, owner.getPets().size());
	}

	@Test
	void addPetIgnoresNull() {
		Owner owner = new Owner();

		owner.addPet(null);

		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void addPetIgnoresDifferentPetWithSameId() {
		Owner owner = new Owner();
		Pet persistedPet = new Pet();
		persistedPet.setId(5);
		Pet differentPet = new Pet();
		differentPet.setId(5);

		owner.addPet(persistedPet);
		owner.addPet(differentPet);

		assertThat(owner.getPets()).containsExactly(persistedPet);
	}

	@Test
	void addPetAddsDistinctNewPets() {
		Owner owner = new Owner();
		Pet firstPet = new Pet();
		Pet secondPet = new Pet();

		owner.addPet(firstPet);
		owner.addPet(secondPet);

		assertThat(owner.getPets()).containsExactly(firstPet, secondPet);
	}

	@Test
	void getPetByIdFindsMatchingPersistedPetAndSkipsUnknownAndNewPets() {
		Owner owner = new Owner();
		Pet petWithoutId = new Pet();
		Pet persistedPet = new Pet();
		persistedPet.setId(5);
		owner.addPet(petWithoutId);
		owner.addPet(persistedPet);

		assertThat(owner.getPet(5)).isSameAs(persistedPet);
		assertThat(owner.getPet(6)).isNull();
		assertThat(owner.getPet((Integer) null)).isNull();
	}

	@Test
	void getPetByNameMatchesCaseInsensitivelyAndSkipsMissingNames() {
		Owner owner = new Owner();
		Pet petWithoutName = new Pet();
		Pet namedPet = new Pet();
		namedPet.setName("Buddy");
		owner.addPet(petWithoutName);
		owner.addPet(namedPet);

		assertThat(owner.getPet("bUdDy")).isSameAs(namedPet);
		assertThat(owner.getPet("Unknown")).isNull();
		assertThat(owner.getPet((String) null)).isNull();
	}

	@Test
	void getPetByNameCanIgnoreNewPets() {
		Owner owner = new Owner();
		Pet newPet = new Pet();
		newPet.setName("Buddy");
		owner.addPet(newPet);

		assertThat(owner.getPet("Buddy", true)).isNull();
		assertThat(owner.getPet("Buddy", false)).isSameAs(newPet);
	}

	@Test
	void addVisitAddsVisitToPetWithId() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		Visit visit = new Visit();
		owner.addPet(pet);

		owner.addVisit(5, visit);

		assertThat(pet.getVisits()).contains(visit);
	}

	@Test
	void addVisitRejectsNullPetId() {
		Owner owner = new Owner();

		assertThatIllegalArgumentException().isThrownBy(() -> owner.addVisit(null, new Visit()));
	}

	@Test
	void addVisitRejectsNullVisit() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(5);
		owner.addPet(pet);

		assertThatIllegalArgumentException().isThrownBy(() -> owner.addVisit(5, null));
	}

	@Test
	void addVisitRejectsUnknownPetId() {
		Owner owner = new Owner();

		assertThatThrownBy(() -> owner.addVisit(5, new Visit())).isInstanceOf(IllegalArgumentException.class);
	}

}
