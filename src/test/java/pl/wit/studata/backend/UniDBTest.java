package pl.wit.studata.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.wit.studata.backend.models.ClassCriterion;
import pl.wit.studata.backend.models.UniClass;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;

/*
 * Testowanie klasy UniDB
 * @author Iryna Mishchenko
 */
public class UniDBTest {
	//Generujemy zmienne prywatne
	private UniDB uniDB;
	private UniGroup ID06IO1;
	private UniGroup ID06TC1;
	private UniStudent bp;
	private UniStudent kw;
	private ClassCriterion kryterium1;
	private UniClass math1;
	private UniClass jp1;
	
	//Inicjalizujemy obiekty przed każdym testem
	@BeforeEach
    public void setUp() {
        uniDB = new UniDB();
        ID06IO1 = new UniGroup("ID06", "IO", "informatyka dzienne, inżeneria");
        ID06TC1 = new UniGroup("ID06", "TC", "informatyka dzienne, technologia chmury");
		bp = new UniStudent("Bazył","Ponury",00000); 
		kw = new UniStudent("Katarzyna","Wesoła",11111); 
		kryterium1 = new ClassCriterion("Praca Domowa", 50);
        math1 = new UniClass("MD1", "Mathematyka Dyskretna 1", Collections.singletonList(kryterium1));
        uniDB.getStudentList().add(bp);
        uniDB.getGroupList().add(ID06IO1);
        uniDB.addClass(math1);
    }
	
	/*
	 * Sprawdzenie poprawnego działania metody dodawania studenta do grupy
	 * */
	@Test
	public void testAssignStudent() {
		//Sprawdzenie metody w wypadku braku na liście studenta oraz grupy
	assertThrows(IllegalArgumentException.class, () -> uniDB.assignStudent(kw, ID06IO1));
    assertThrows(IllegalArgumentException.class, () -> uniDB.assignStudent(bp, ID06TC1));
       //Sprawdzamy czy istnający na liście student zostanie poprawnie dodany do grupy
    uniDB.assignStudent(bp, ID06IO1); // dodajemy studenta do grupy
    assertEquals(ID06IO1, uniDB.getStudentGroupMap().get(bp)); // spawdzamy czy grupa ID06IO1 mieści na liście studenta bp
	}
	/*
	 * Sprawdzenie poprawnego działania metody dodawania przedmiota do listy
	 * */
	@Test
	public void testAddClass() {
		//Sprawdzamy czy istniajacy na liście przedmiot math1 zostanie dodany do listy, oczekujemy blad 
		assertEquals(1, uniDB.getClassList().size()); //Sprawdzamy rozmiar listy przed blednym dodaniem 
		assertThrows(IllegalArgumentException.class, () -> uniDB.addClass(math1));
		//Sprawdzamy czy nie istniający rzedmiot dodany do listy
		jp1 = new UniClass("JP1", "Język Polski 1", Collections.singletonList(kryterium1)); //tworzymy nowy przedmiot
        uniDB.getClassList().add(jp1); // dodajemy przedmiot do listy
        assertEquals(2, uniDB.getClassList().size()); //Sprawdzamy rozmiar listy przedmiotów po dodaniu jp1
	}
/*
 * Metody do sprawdzenia poprawnego działania metody deleteStudent i deleteClass
 * */
	@Test
	public void testDeleteStudent() {
        uniDB.deleteStudent(bp); //usuwamy stydenta bp
        assertFalse(uniDB.getStudentList().contains(bp)); //czy StudentList mieści studenta bp? oczekiwany wynik false
        assertFalse(uniDB.getStudentGroupMap().containsKey(bp));//czy StudentGroupMap mieści studenta bp? oczekiwany wynik false
	}
	
	@Test
	public void testDeleteClass() {
		uniDB.deleteClass(math1); //usuwamy przedmiot math1
        assertFalse(uniDB.getClassList().contains(math1)); //sprawdzamy czy mamy na liście przedmiotów
        }

	/*
	 * Sprawdzamy poprawność działania metody addGradeToStudent
	 * */
	@Test
	public void testAddGradeToStudent() {
        uniDB.addGradeToStudent(bp, math1, kryterium1, 45); // dodajemy pkt za math1 dla studenta bp
        Map<ClassCriterion, Integer> grades = uniDB.getStudentGradesMap().get(bp).get(math1);
        assertEquals(45, grades.get(kryterium1).intValue()); //sparwdzamy czy wartość poprawnie dopisana
	}

	/*
	 * Sprawdzamy poprawność działania metod updateStudents, updateGroups, updateClasses, updateStudentGroupMap, updateStudentGradesMap
	 * */
	@Test
	public void testUpdateStudents() {
        List<UniStudent> newStudentList = new ArrayList<>(); //Tworzymy nową listę studentów
        newStudentList.add(bp); //dodajemy do tej listy studenta bp
        uniDB.updateStudents(newStudentList); //aktualizujemy listę
        assertFalse(uniDB.getStudentList().contains(kw)); //sprawdzamy czy w nowoutworzonej liscie jest student kw, wynik oczekiwany false
        assertTrue(uniDB.getStudentList().contains(bp)); //sprawdzamy czy w nowoutworzonej liscie jest student bp, wynik oczekiwany true
	}

	@Test
	public void testUpdateGroups() {
	List<UniGroup> newGroupList = new ArrayList<>();//Tworzymy nową listę grup
    newGroupList.add(ID06TC1);//dodajemy do tej listy grupę ID06TC1
    uniDB.updateGroups(newGroupList);//aktualizujemy listę
    assertFalse(uniDB.getGroupList().contains(ID06IO1));//sprawdzamy czy w nowoutworzonej liscie jest grupa ID06IO1, wynik oczekiwany false
    assertTrue(uniDB.getGroupList().contains(ID06TC1));//sprawdzamy czy w nowoutworzonej liscie jest grupa ID06TC1, wynik oczekiwany true
	}

	@Test
	public void testUpdateClasses() {
        List<UniClass> newClassList = new ArrayList<>(); //Tworzymy nową listę przedmiotów
        newClassList.add(math1);//dodajemy do tej listy przedmiot math1
        uniDB.updateClasses(newClassList);//aktualizujemy listę
        assertFalse(uniDB.getClassList().contains(jp1));//sprawdzamy czy w nowoutworzonej liscie jest przedmiot jp1, wynik oczekiwany false
        assertTrue(uniDB.getClassList().contains(math1));//sprawdzamy czy w nowoutworzonej liscie jest przedmiot nath1, wynik oczekiwany true
	}

	@Test
	public void testUpdateStudentGroupMap() {
		// Tworzymy nowa mapę przypisań studentów do grup
		Map<UniStudent, UniGroup> newStudentGroupMap = new HashMap<>();
		//Przypisujemy studentów do konkretnych grup
	    newStudentGroupMap.put(bp, ID06IO1);
	    newStudentGroupMap.put(kw, ID06TC1);
	    uniDB.updateStudentGroupMap(newStudentGroupMap);// Aktualizujemy mapę
	    assertEquals(newStudentGroupMap, uniDB.getStudentGroupMap());// Sprawdzemy czy nowa mapa poprawnie zapisana w bazie danych
	}

	@Test
	public void testUpdateClassCriterionMap() {
		 ClassCriterion kryterium2 = new ClassCriterion("Egzamin", 50); // tworzymy nowy kryterium
		    List<ClassCriterion> newCriteriaList = Arrays.asList(kryterium1, kryterium2); // Tworzymy nową listę kryteriów i dodajemy kryterium 1 i 2 do listy
		    Map<UniClass, List<ClassCriterion>> newClassCriterionMap = new HashMap<>(); //tworzymy mapę kryteriów przedmiotów
		    newClassCriterionMap.put(math1, newCriteriaList);
		    uniDB.updateClassCriterionMap(newClassCriterionMap); //Aktualizujemy
		    assertEquals(newClassCriterionMap, uniDB.getClassCriterionMap()); //Sprawdzamy czy nowoutworzona mapa poprawnie zapisana w bd
	}

	@Test
	public void testUpdateStudentGradesMap() {
	    Map<ClassCriterion, Integer> criteriaGrades = new HashMap<>();//Tworzymy mapę ocen kryterium dla przedmiotu
	    criteriaGrades.put(kryterium1, 45);
	    Map<UniClass, Map<ClassCriterion, Integer>> classGrades = new HashMap<>();//Tworzymy mapę ocen przedmiotów dla studenta
	    classGrades.put(math1, criteriaGrades);
	    Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> newStudentGradesMap = new HashMap<>();//Tworzymy mapę ocen studenta
	    newStudentGradesMap.put(bp, classGrades);
	    uniDB.updateStudentGradesMap(newStudentGradesMap); // Aktualizujemy mapy
	    assertEquals(newStudentGradesMap, uniDB.getStudentGradesMap());// Sprawdzemy czy nowa mapa poprawnie zapisana w bazie danych

	}

}
