package pl.wit.studata.backend;

import java.util.ArrayList;
import java.util.HashMap;
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
	private Map<UniGroup, List<UniStudent>> groupStudentMap;
	private Map<UniClass, List<UniGroup>> classGroupMap;
	private Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentGradesMap; // Mapa ocen studentów

	// Konstruktor
	public UniDB() {
		this.studentList = new ArrayList<>();
		this.groupList = new ArrayList<>();
		this.classList = new ArrayList<>();
		this.groupStudentMap = new HashMap<>();
		this.classGroupMap = new HashMap<>();
		this.studentGradesMap = new HashMap<>();
	}

	/**
	 * Metoda dodania studenta do grupy
	 */
	private void assignStudent(UniGroup group, UniStudent student, Map<UniGroup, List<UniStudent>> groupStudentMap) {
		// Sprawdzenie, czy grupa istnieje w mapie
		if (groupStudentMap.containsKey(group)) {
			// Pobranie listy studentów dla danej grupy
			List<UniStudent> studentsInGroup = groupStudentMap.get(group);
			// Dodanie studenta do listy
			studentsInGroup.add(student);
			// Aktualizacja mapy
			groupStudentMap.put(group, studentsInGroup);
		} else {
			List<UniStudent> newStudentsInGroup = new ArrayList<>();
			newStudentsInGroup.add(student);
			// Dodanie nowej listy do mapy
			groupStudentMap.put(group, newStudentsInGroup);
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

	public Map<UniGroup, List<UniStudent>> getGroupStudentMap() {
		return groupStudentMap;
	}

	public Map<UniClass, List<UniGroup>> getClassGroupMap() {
		return classGroupMap;
	}
}
