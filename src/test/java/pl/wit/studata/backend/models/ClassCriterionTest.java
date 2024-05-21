package pl.wit.studata.backend.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Klasa testowa sprawdzają poprawność działania Klasy ClassCriterion 
 * 
 * @author Iryna Mishchenko
 */

public class ClassCriterionTest {

	@Test
	public void testValidateScore() {
		/*
		 * Tworzymy obiekt kryteriumTestowy 
		 * Przypisujemy do niego wartości zgodine z konstruktorem
		 * @param string "Praca domowa"
		 * @param int 20 -> max liczba punktów uzyskana za pracę domową 
		 */
		ClassCriterion kryteriumTestowy = new ClassCriterion("Praca domowa", 20);
        assertTrue(kryteriumTestowy.validateScore(20)); //czy liczba jest w przediale od 0 do 50, wynik oczekiwany true
        assertTrue(kryteriumTestowy.validateScore(0)); //czy liczba równa 0, wynik oczekiwany true
        assertTrue(kryteriumTestowy.validateScore(20));//czy liczba równa 20, wynik oczekiwany true
        assertFalse(kryteriumTestowy.validateScore(-1)); //reakcja w wypadku ujemnych pkt, wynink oczekiwany: false 
        assertFalse(kryteriumTestowy.validateScore(21)); //reakcja w wypadku jeżeli liczba pkt większa od maksymalnej liczby, wynink oczekiwany: false
	}

}
