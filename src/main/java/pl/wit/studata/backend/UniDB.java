package pl.wit.studata.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.wit.studata.backend.models.ClassCriterion;
import pl.wit.studata.backend.models.UniClass;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;

public class UniDB {

	// Zmienne
	private List<UniStudent> studentList;
	private List<UniGroup> groupList;
	private List<UniClass> classList;
	private Map<UniStudent, UniGroup> studentGroupMap;
	private Map<UniClass, List<UniGroup>> classGroupMap;
	private Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentGradesMap; // Mapa ocen studentów

	// Konstruktor
	public UniDB() {
		this.studentList = new ArrayList<>();
		this.groupList = new ArrayList<>();
		this.classList = new ArrayList<>();
		this.studentGroupMap = new HashMap<>();
		this.classGroupMap = new HashMap<>();
		this.studentGradesMap = new HashMap<>();
	}

	/**
	 * Metoda dodania studenta do grupy
	 */
	private void assignStudent(UniStudent student, UniGroup group) {
		// Sprawdzenie, czy student i grupa istnieją w bazie danych
		if (studentList.contains(student) && groupList.contains(group)) {
			// Dodanie przypisania studenta do grupy
			studentGroupMap.put(student, group);
		} else {
			// Można dodać odpowiednią obsługę błędów, np. rzucenie wyjątku
			throw new IllegalArgumentException("Student lub grupa nie istnieje w bazie danych.");
		}
	}

	/*
	 * Metoda dodania nowego przedmiotu do listy przedmiotów
	 */
	private void addClass(UniClass uniClass) {
		if (!classList.contains(uniClass)) {
			classList.add(uniClass);
		} else {
			throw new IllegalArgumentException("Ten przedmiot jest już dodany do bazy danych");
		}
	}

	/*
	 * Usuwanie studenta z wszystkich list
	 */
	private void deleteStudent(UniStudent student) {
		if (studentList.contains(student)) {
			studentList.remove(student);
		}
		if (studentGroupMap.containsKey(student)) {
			studentGroupMap.remove(student);
		}
		if (studentGradesMap.containsKey(student)) {
			studentGradesMap.remove(student);
		}
	}

	/*
	 * Metoda usuwania przedmiotu z wszystkich list
	 */
	private void deleteClass(UniClass uniClass) {
		if (classList.contains(uniClass)) {
			classList.remove(uniClass);
		}
		if (classGroupMap.containsKey(uniClass)) {
			classGroupMap.remove(uniClass);
		}
	}

	/**
	 * Metoda wystawiająca studentowi ocenę z danego przedmiotu w danym kryterium
	 */
	public void addGradeToStudent(UniStudent student, UniClass uniClass, ClassCriterion criterion, int points) {
		// Sprawdzenie, czy student istnieje w bazie danych
		if (studentList.contains(student)) {
			// Sprawdzenie, czy przedmiot istnieje w bazie danych
			if (classList.contains(uniClass)) {
				// Sprawdzenie, czy kryterium istnieje w przedmiocie
				if (uniClass.getCriteriaList().contains(criterion)) {
					// Sprawdzenie, czy liczba punktów mieści się w zakresie [0, maksimum]
					if (points >= 0 || points <= criterion.getMaxPoints()) {
						// Pobranie mapy ocen studenta
						Map<UniClass, Map<ClassCriterion, Integer>> studentGrades = studentGradesMap
								.getOrDefault(student, new HashMap<>());

						// Pobranie mapy ocen dla danego przedmiotu
						Map<ClassCriterion, Integer> classGrades = studentGrades.getOrDefault(uniClass,
								new HashMap<>());

						// Dodanie oceny do mapy ocen dla danego kryterium
						classGrades.put(criterion, points);

						// Aktualizacja mapy ocen dla danego przedmiotu
						studentGrades.put(uniClass, classGrades);

						// Aktualizacja mapy ocen studenta
						studentGradesMap.put(student, studentGrades);
					}
				}
			}
		}

	}

	/*
	 * Metody aktualizujące poszczególne listy
	 */
	public void updateStudents(List<UniStudent> newStudentList) {
		this.studentList = new LinkedList<>(newStudentList);
	}

	public void updateGroups(List<UniGroup> newGroupList) {
		this.groupList = new LinkedList<>(newGroupList);
	}

	public void updateClasses(List<UniClass> newClassList) {
		this.classList = new LinkedList<>(newClassList);
	}

	public void updateStudentGroupMap(Map<UniStudent, UniGroup> newStudentGroupMap) {
		this.studentGroupMap = new HashMap<>(newStudentGroupMap);
	}

	public void updateClassGroupMap(Map<UniClass, List<UniGroup>> newClassGroupMap) {
		this.classGroupMap = new HashMap<>(newClassGroupMap);
	}

	public void updateStudentGradesMap(
			Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> newStudentGradesMap) {
		this.studentGradesMap = new HashMap<>(newStudentGradesMap);
	}

	// gettery i settery
	public List<UniStudent> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<UniStudent> studentList) {
		this.studentList = studentList;
	}

	public List<UniGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<UniGroup> groupList) {
		this.groupList = groupList;
	}

	public List<UniClass> getClassList() {
		return classList;
	}

	public void setClassList(List<UniClass> classList) {
		this.classList = classList;
	}

	public Map<UniStudent, UniGroup> getStudentGroupMap() {
		return studentGroupMap;
	}

	public Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> getStudentGradesMap() {
		return studentGradesMap;
	}

	public Map<UniClass, List<UniGroup>> getClassGroupMap() {
		return classGroupMap;
	}
}
