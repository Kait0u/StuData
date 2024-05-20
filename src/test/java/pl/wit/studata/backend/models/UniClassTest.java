package pl.wit.studata.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/*
 * Dodanie kryterium do listy
 * @author Iryna Mishchenko
 */

class UniClassTest {

	@Test
	void addCriterionTest() {
		ClassCriterion kryterium1 = new ClassCriterion("Praca domowa", 50); // Tworzymy kryterium "Praca domowa", maksymalna liczba do uzyskania 50 pkt
        List<ClassCriterion> criteriaList = new ArrayList<>(); //Tworzymy listę kryterium
		UniClass uniClass = new UniClass("Mathematics", "MATH1", criteriaList); //Tworzymy i nadajemy znaczenia obiektu przedmiot
		assertEquals(0, uniClass.getCriteriaList().size()); //Potwierdzenie że lista pusta
		uniClass.addCriterion(kryterium1); //Dodajemy kryterium1 do listy
		assertEquals(1, uniClass.getCriteriaList().size()); //Sprawdzamy rozmiar listy po dodaniu kryterium
        assertTrue(uniClass.getCriteriaList().contains(kryterium1));//Sprawdzamy czy lista mieści kryterium1
	}

}
