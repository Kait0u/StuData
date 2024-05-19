package pl.wit.studata.backend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private Map<UniClass, List<UniGroup>> classGroupMap;
	private Map<UniStudent, Map<UniClass, Map<ClassCriterion, Integer>>> studentGradesMap; // Mapa ocen studentów

	/**
	 * Konstruktor bezparametryczny.
	 */
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
	public void assignStudent(UniStudent student, UniGroup group) {
		// Sprawdzenie, czy student i grupa istnieją w bazie danych
		if (studentList.contains(student) && groupList.contains(group)) {
			//	 Dodanie przypisania studenta do grupy
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
		
		// Znajdź, kto jest w grupie, której już nie ma.
		Set<UniStudent> keysToRemove = new HashSet<>();
		for (Map.Entry<UniStudent, UniGroup> entry: studentGroupMap.entrySet()) {
			if (!this.groupList.contains(entry.getValue()))
				keysToRemove.add(entry.getKey());
		}
		
		// Usuń nieważne klucze.
		for (UniStudent key: keysToRemove)
			this.studentGroupMap.remove(key);
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
	
	
	// zapis i odczyt z pliku
	private void loadStudentsFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for(int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if(s instanceof UniStudent) studentList.add((UniStudent)s);
			else throw new Exception("Object type isn't UniStudent");
		}
	}
	
	private void loadGroupsFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for(int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if(s instanceof UniGroup) groupList.add((UniGroup)s);
			else throw new Exception("Object type isn't UniGroup");
		}
	}
	
	private void loadClassesFromFile(DataInputStream din) throws Exception {
		int listLen = din.readInt();
		for(int i = 0; i < listLen; ++i) {
			Serializable s = Serializer.loadObj(din);
			if(s instanceof UniClass) classList.add((UniClass)s);
			else throw new Exception("Object type isn't UniClass");
		}
	}
	
	private void loadClassGroupMap(DataInputStream din) throws Exception {
		int mapLen = din.readInt();
		for(int i = 0; i < mapLen; ++i) {
			UniClass key = UniClass.loadMapRef(din, classList);
			List<UniGroup> list = new ArrayList<UniGroup>();
			int listLen = din.readInt();
			for(int j = 0; j < listLen; ++j) {
				UniGroup val = UniGroup.loadMapRef(din, groupList);
				if(val != null) {
					list.add(val);
				}
			}
			if(key != null) {
				classGroupMap.put(key, list);
			}
		}
	}
	
	private void saveClassGroupMap(DataOutputStream dout) throws Exception {
		dout.writeInt(classGroupMap.keySet().size());
		for(UniClass key: classGroupMap.keySet()) {
			key.saveMapElem(dout);
			List<UniGroup> groups = classGroupMap.get(key);
			dout.writeInt(groups.size());
			
			for(UniGroup gr: groups) {
				gr.saveMapElem(dout);
			}
		}
	}
	
	private void loadStudentGroupMap(DataInputStream din) throws Exception {
		int mapLen = din.readInt();
		for(int i = 0; i < mapLen; ++i) {
			UniStudent key = UniStudent.loadMapRef(din, studentList);
			UniGroup gr = UniGroup.loadMapRef(din, groupList);
			if(key != null && gr != null) {
				studentGroupMap.put(key, gr);
			}
		}
	}
	
	private void saveStudentGroupMap(DataOutputStream dout) throws Exception {
		dout.writeInt(studentGroupMap.size());
		for(UniStudent key: studentGroupMap.keySet()) {
			key.saveMapElem(dout);
			UniGroup gr = studentGroupMap.get(key);
			gr.saveMapElem(dout);
		}
	}
	
	private void loadStudentGradesMap(DataInputStream din) throws Exception {
		int mapLen = din.readInt();
		for(int i = 0; i < mapLen; ++i) {
			UniStudent key = UniStudent.loadMapRef(din, studentList);
			Map<UniClass, Map<ClassCriterion, Integer>> classes = new HashMap<>();
			int clMapLen = din.readInt();
			for(int j = 0; j < clMapLen; ++j) {
				UniClass clKey = UniClass.loadMapRef(din, classList);
				Map<ClassCriterion, Integer> mark = new HashMap<>();
				int mrkMapLen = din.readInt();
				for(int k = 0; k < mrkMapLen; ++k) {
					ClassCriterion cc = ClassCriterion.loadMapRef(din, clKey.getCriteriaList());
					int val = din.readInt();
					if(cc != null) {
						mark.put(cc, val);
					}
				}
				if(clKey != null) {
					classes.put(clKey, mark);
				}
			}
			if(key != null) {
				studentGradesMap.put(key, classes);
			}
		}
	}
	
	private void saveStudentGradesMap(DataOutputStream dout) throws Exception {
		dout.writeInt(studentGradesMap.size());
		for(UniStudent key: studentGradesMap.keySet()) {
			key.saveMapElem(dout);
			Map<UniClass, Map<ClassCriterion, Integer>> classes = studentGradesMap.get(key);
			dout.writeInt(classes.size());
			
			for(UniClass cl: classes.keySet()) {
				cl.saveMapElem(dout);
				Map<ClassCriterion, Integer> mark = classes.get(cl);
				dout.writeInt(mark.size());
				
				for(ClassCriterion cc: mark.keySet()) {
					cc.saveMapElem(dout);
					dout.writeInt(mark.get(cc));
				}
			}
		}
	}
	
	public void saveStudentList(DataOutputStream dout) throws Exception{
		int listLen = studentList.size();
		dout.writeInt(listLen);
		for(UniStudent s: studentList) {
			s.saveToFile(dout);
		}
	}
	
	public void saveGroupList(DataOutputStream dout) throws Exception{
		int listLen = groupList.size();
		dout.writeInt(listLen);
		for(UniGroup s: groupList) {
			s.saveToFile(dout);
		}
	}
	
	public void saveClassList(DataOutputStream dout) throws Exception{
		int listLen = classList.size();
		dout.writeInt(listLen);
		for(UniClass s: classList) {
			s.saveToFile(dout);
		}
	}
	
	public void saveToFile(String path) throws Exception{
		FileOutputStream fs = new FileOutputStream(path);
		DataOutputStream out = new DataOutputStream(fs);
		saveToFile(out);
	}
	
	public void saveToFile(DataOutputStream dout) throws Exception{
		saveStudentList(dout);
		saveGroupList(dout);
		saveClassList(dout);
		
		
		saveClassGroupMap(dout);
		saveStudentGroupMap(dout);
		saveStudentGradesMap(dout);
			
	}
	
	public void loadFromFile(String path) throws Exception{
		FileInputStream fs = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fs);
		loadFromFile(in);
	}
	
	public void loadFromFile(DataInputStream din) throws Exception{
		loadStudentsFromFile(din);
		loadGroupsFromFile(din);
		loadClassesFromFile(din);
		

		loadClassGroupMap(din);
		loadStudentGroupMap(din);
		loadStudentGradesMap(din);
	}
}
