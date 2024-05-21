package pl.wit.studata.backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.wit.studata.backend.fileio.Serializable;
import pl.wit.studata.backend.fileio.Serializer;
import pl.wit.studata.backend.models.ClassCriterion;
import pl.wit.studata.backend.models.UniClass;
import pl.wit.studata.backend.models.UniGroup;
import pl.wit.studata.backend.models.UniStudent;

/**
 * Klasa reprezentująca bazę danych
 * 
 * @author Karol Wojtyra
 * @author Aliaksei Harbuz
 */
public class UniDB {

	// Zmienne
	private List<UniStudent> studentList;
	private List<UniGroup> groupList;
	private List<UniClass> classList;
	private Map<UniStudent, UniGroup> studentGroupMap;
	private Map<UniClass, List<ClassCriterion>> classCriterionMap;
	private Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentGradesMap; // Mapa ocen studentów

	/**
	 * Konstruktor bezparametryczny.
	 */
	public UniDB() {
		this.studentList = new ArrayList<>();
		this.groupList = new ArrayList<>();
		this.classList = new ArrayList<>();
		this.studentGroupMap = new HashMap<>();
		this.classCriterionMap = new HashMap<>();
		this.studentGradesMap = new HashMap<>();
	}

	/**
	 * Metoda dodania studenta do grupy
	 */
	public void assignStudent(UniStudent student, UniGroup group) {
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
	public void addClass(UniClass uniClass) {
		if (!classList.contains(uniClass)) {
			classList.add(uniClass);
		} else {
			throw new IllegalArgumentException("Ten przedmiot jest już dodany do bazy danych");
		}
	}

	/*
	 * Usuwanie studenta z wszystkich list
	 */
	public void deleteStudent(UniStudent student) {
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
	public void deleteClass(UniClass uniClass) {
		if (classList.contains(uniClass)) {
			classList.remove(uniClass);
		}
		if (classCriterionMap.containsKey(uniClass)) {
			classCriterionMap.remove(uniClass);
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

		studentGroupMap.entrySet().removeIf(entry -> !this.studentList.contains(entry.getKey()));
		studentGradesMap.entrySet().removeIf(entry -> !this.studentList.contains(entry.getKey()));

	}

	public void updateGroups(List<UniGroup> newGroupList) {
		this.groupList = new LinkedList<>(newGroupList);

		// Znajdź, kto jest w grupie, której już nie ma.
		Set<UniStudent> keysToRemove = new HashSet<>();
		for (Map.Entry<UniStudent, UniGroup> entry : studentGroupMap.entrySet()) {
			if (!this.groupList.contains(entry.getValue()))
				keysToRemove.add(entry.getKey());
		}

		// Usuń nieważne klucze.
		for (UniStudent key : keysToRemove)
			this.studentGroupMap.remove(key);
	}

	public void updateClasses(List<UniClass> newClassList) {
		this.classList = new LinkedList<>(newClassList);

		// Usuń nieważne klucze.
		classCriterionMap.entrySet().removeIf(entry -> !this.classList.contains(entry.getKey()));

		for (UniStudent student : studentGradesMap.keySet()) {
			Map<UniClass, Map<ClassCriterion, Integer>> val = studentGradesMap.get(student);
			val.entrySet().removeIf(entry -> !this.classList.contains(entry.getKey()));
		}

	}

	public void updateStudentGroupMap(Map<UniStudent, UniGroup> newStudentGroupMap) {
		this.studentGroupMap = new HashMap<>(newStudentGroupMap);
	}

	public void updateClassCriterionMap(Map<UniClass, List<ClassCriterion>> newClassCriterionMap) {
		this.classCriterionMap = new HashMap<>(newClassCriterionMap);

		for (UniStudent student : studentGradesMap.keySet()) {
			Map<UniClass, Map<ClassCriterion, Integer>> val = studentGradesMap.get(student);
			for (UniClass key : val.keySet()) {
				Map<ClassCriterion, Integer> mark = val.get(key);
				mark.entrySet().removeIf(entry -> !classCriterionMap.get(key).contains(entry.getKey()));
			}
		}
	}

	public void updateStudentGradesMap(
			Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> newStudentGradesMap) {
//		this.studentGradesMap = new HashMap<>(newStudentGradesMap);
		this.studentGradesMap = newStudentGradesMap;
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

	public Map<UniClass, List<ClassCriterion>> getClassCriterionMap() {
		return classCriterionMap;
	}

	// zapis i odczyt z pliku

	/**
	 * Metoda pobierająca listę studentów ze strumienia.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadStudentsFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for (int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if (s instanceof UniStudent)
				studentList.add((UniStudent) s);
			else
				throw new Exception("Object type isn't UniStudent");
		}
	}

	/**
	 * Metoda pobierająca listę grup ze strumienia.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadGroupsFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for (int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if (s instanceof UniGroup)
				groupList.add((UniGroup) s);
			else
				throw new Exception("Object type isn't UniGroup");
		}
	}

	/**
	 * Metoda pobierająca listę przedmiotów ze strumienia.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadClassesFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for (int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if (s instanceof UniClass)
				classList.add((UniClass) s);
			else
				throw new Exception("Object type isn't UniClass");
		}
	}

	/**
	 * Metoda pobierająca cryteria, przydzielone do przedmiotów, ze strumienia i
	 * zapisująca ich do classCriterionMap. Potrzebuje wypełnionej listy przedmiotów
	 * classList.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadClassCriterionMap(DataInputStream din) throws Exception {
		int mapLen = din.readInt();
		for (int i = 0; i < mapLen; ++i) {
			UniClass key = UniClass.loadMapRef(din, classList);
			List<ClassCriterion> list = new ArrayList<ClassCriterion>();
			int listLen = din.readInt();
			for (int j = 0; j < listLen; ++j) {
				ClassCriterion val = (ClassCriterion) Serializer.loadObj(din);
				if (val != null) {
					list.add(val);
				}
			}
			if (key != null) {
				classCriterionMap.put(key, list);
			}
		}
	}

	private void saveClassCriterionMap(DataOutputStream dout) throws Exception {
		dout.writeInt(classCriterionMap.keySet().size());
		for (UniClass key : classCriterionMap.keySet()) {
			key.saveMapElem(dout);
			List<ClassCriterion> criteria = classCriterionMap.get(key);
			dout.writeInt(criteria.size());

			for (ClassCriterion gr : criteria) {
				gr.saveToFile(dout);
			}
		}
	}

	/**
	 * Metoda pobierająca przydział studentów do grup ze strumienia i zapisująca go
	 * do studentGroupMap. Potrzebuje wypełnionych list studentów i grup
	 * (studentList oraz groupList).
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadStudentGroupMap(DataInputStream din) throws Exception {
		int mapLen = din.readInt();
		for (int i = 0; i < mapLen; ++i) {
			UniStudent key = UniStudent.loadMapRef(din, studentList);
			UniGroup gr = UniGroup.loadMapRef(din, groupList);
			if (key != null && gr != null) {
				studentGroupMap.put(key, gr);
			}
		}
	}

	/**
	 * Metoda zapisująca zawartość studentGroupMap do strumienia. Metoda nie
	 * zapisuje całych obiektów UniStudent i UniGroup, tylko odpowiednio ich
	 * studentId i groupCode.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	private void saveStudentGroupMap(DataOutputStream dout) throws Exception {
		dout.writeInt(studentGroupMap.size());
		for (UniStudent key : studentGroupMap.keySet()) {
			key.saveMapElem(dout);
			UniGroup gr = studentGroupMap.get(key);
			gr.saveMapElem(dout);
		}
	}

	/**
	 * Metoda zapisująca zawartość classCriterionMap do strumienia. Metoda nie
	 * zapisuje całych obiektów UniClass, tylko ich nazwy.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	private void loadStudentGradesMap(DataInputStream din) throws Exception {
        synchronized (studentGradesMap) {
            int mapLen = din.readInt();
            for (int i = 0; i < mapLen; ++i) {
                UniStudent key = UniStudent.loadMapRef(din, studentList);
                Map<UniClass, Map<ClassCriterion, Integer>> classes = new HashMap<>();
                int clMapLen = din.readInt();
                for (int j = 0; j < clMapLen; ++j) {
                    UniClass clKey = UniClass.loadMapRef(din, classList);
                    Map<ClassCriterion, Integer> mark = new HashMap<>();
                    if(clKey != null) {
                        int mrkMapLen = din.readInt();
                        for (int k = 0; k < mrkMapLen; ++k) {
                            ClassCriterion cc = ClassCriterion.loadMapRef(din, clKey.getCriteriaList());
                            int val = din.readInt();
                            if (cc != null) {
                                mark.put(cc, val);
                            }
                        }
                        classes.put(clKey, mark);
                    }
                }
                if (key != null) {
                    studentGradesMap.put(key, classes);
                }
            }
        }
    }

	/**
	 * Metoda zapisująca zawartość studentGradesMap do strumienia. Metoda nie
	 * zapisuje całych obiektów UniStudent, UniClass lub ClassCriterion, tylko
	 * odpowiednio ich studentId, className oraz criterionName.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	private void saveStudentGradesMap(DataOutputStream dout) throws Exception {
		dout.writeInt(studentGradesMap.size());
		for (UniStudent key : studentGradesMap.keySet()) {
			key.saveMapElem(dout);
			Map<UniClass, Map<ClassCriterion, Integer>> classes = studentGradesMap.get(key);
			dout.writeInt(classes.size());

			for (UniClass cl : classes.keySet()) {
				cl.saveMapElem(dout);
				Map<ClassCriterion, Integer> mark = classes.get(cl);
				dout.writeInt(mark.size());

				for (ClassCriterion cc : mark.keySet()) {
					cc.saveMapElem(dout);
					dout.writeInt(mark.get(cc));
				}
			}
		}
	}

	/**
	 * Metoda zapisująca zawartość studentList do strumienia.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	public void saveStudentList(DataOutputStream dout) throws Exception {
		int listLen = studentList.size();
		dout.writeInt(listLen);
		for (UniStudent s : studentList) {
			s.saveToFile(dout);
		}
	}

	/**
	 * Metoda zapisująca zawartość groupList do strumienia.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	public void saveGroupList(DataOutputStream dout) throws Exception {
		int listLen = groupList.size();
		dout.writeInt(listLen);
		for (UniGroup s : groupList) {
			s.saveToFile(dout);
		}
	}

	/**
	 * Metoda zapisująca zawartość classList do strumienia.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	public void saveClassList(DataOutputStream dout) throws Exception {
		int listLen = classList.size();
		dout.writeInt(listLen);
		for (UniClass s : classList) {
			s.saveToFile(dout);
		}
	}

	/**
	 * Metoda zapisująca bazę danych do pliku. Struktura wynikowego pliku: <br>
	 * studentList - groupList - classList - classCriterionMap - studentGroupMap -
	 * studentGradesMap.
	 * 
	 * @param path Ścieżka do pliku wyjściowego.
	 * @throws Exception
	 */
	public void saveToFile(String path) throws Exception {
		FileOutputStream fs = new FileOutputStream(path);
		DataOutputStream out = new DataOutputStream(fs);
		saveToFile(out);
	}

	/**
	 * Metoda zapisująca bazę danych do strumienia. Struktura wynikowego pliku: <br>
	 * studentList - groupList - classList - classCriterionMap - studentGroupMap -
	 * studentGradesMap.
	 * 
	 * @param dout Strumień wyjściowy.
	 * @throws Exception
	 */
	public synchronized void saveToFile(DataOutputStream dout) throws Exception {
		saveStudentList(dout);
		saveGroupList(dout);
		saveClassList(dout);

		saveClassCriterionMap(dout);
		saveStudentGroupMap(dout);
		saveStudentGradesMap(dout);

	}

	/**
	 * Metoda wypełniająca bazę danych na podstawie zawartości pliku. Struktura
	 * pliku wejściowego: <br>
	 * studentList - groupList - classList - classCriterionMap - studentGroupMap -
	 * studentGradesMap.
	 * 
	 * @param path Ścieżka do pliku wyjściowego.
	 * @throws Exception
	 */
	public synchronized void loadFromFile(String path) throws Exception {
		FileInputStream fs = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fs);
		loadFromFile(in);
	}

	/**
	 * Metoda wypełniająca bazę danych na podstawie zawartości strumienia. Struktura
	 * danych wejściowych: <br>
	 * studentList - groupList - classList - classCriterionMap - studentGroupMap -
	 * studentGradesMap.
	 * 
	 * @param din Strumień wejściowy.
	 * @throws Exception
	 */
	public synchronized void loadFromFile(DataInputStream din) throws Exception {
		loadStudentsFromFile(din);
		loadGroupsFromFile(din);
		loadClassesFromFile(din);

		loadClassCriterionMap(din);
		loadStudentGroupMap(din);
		loadStudentGradesMap(din);
	}
}
