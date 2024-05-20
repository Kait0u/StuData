package pl.wit.studata.backend.fileio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.*;
/**
 * Klasa testowa do sprawdzenia poprawności zapisu i odczytu danych z plików przy użyciu klasy UniDB.
 * @author Aliaksei Harbuz
 * @author Iryna Mishchenko
 */
public class SerializableTest {
	private UniDB db;
    private UniDB db2;
    private String outPath;

  //Inicjalizujemy obiekty przed każdym testem
    @BeforeEach
    public void setUp() {
		// Tworzenie instancji bazy danych
        db = new UniDB();
        db2 = new UniDB();
		// Ścieżka do pliku wyjściowego
        outPath = "src/test/resources/output.txt";
		// Tworzymy obiekty testowe
        UniGroup GD02PO01 = new UniGroup("GD02PO01", "Grafika", "Grafika 1 rok");
        ClassCriterion kryterium1 = new ClassCriterion("Criterion 1", 50);
        ClassCriterion kryterium2 = new ClassCriterion("Criterion 2", 50);
        List<ClassCriterion> criteria = Arrays.asList(kryterium1,kryterium2);
        UniClass cl = new UniClass("Class 1", "CL1", criteria);
        UniStudent mm = new UniStudent("Mariia", "Muzyczna", 12345);

        db.getStudentList().add(mm);
        db.getGroupList().add( GD02PO01);
        db.getClassList().add(cl);
        db.getClassCriterionMap().put(cl, criteria);
        db.getStudentGroupMap().put(mm,  GD02PO01);
        db.addGradeToStudent(mm, cl, kryterium2, 4);
    }
    
    @Test
    public void testSaveToFile() {
		// Zapisujemy dane do pliku
		try {
			db.saveToFile(outPath);
		}
		catch(Exception ex){
			System.out.print(ex.getMessage());
		}
    }
    
    @Test
    public void testLoadFromFile() {
    	
    	 // Odczytujemy dane z pliku
    			try {
    				db2.loadFromFile(outPath);
    				// Sprawdzenie listy studentów
         	        assertEquals(1, db2.getStudentList().size());
         	        UniStudent s = db2.getStudentList().get(0);
         	        assertEquals("Mariia", s.getFirstName());
         	        assertEquals("Muzyczna", s.getLastName());

        			// Sprawdzenie grupy studentów
         	       assertEquals(1, db2.getGroupList().size());
       	           UniGroup mGr = db2.getGroupList().get(0);
       	           assertEquals("GD02PO01", mGr.getGroupCode());
    	           assertEquals("Grafika", mGr.getSpecialization());
    	           assertEquals("Grafika 1 rok", mGr.getDescription());
    	           
       			// Sprawdzenie listy klas
       	       assertEquals(1, db2.getClassList().size());
       	        UniClass c = db2.getClassList().get(0);
       	        assertEquals("Class 1", c.getClassName());
       	        assertEquals(2, c.getCriteriaList().size());
       	        assertEquals("Criterion 1", c.getCriteriaList().get(0).getCriterionName());
       	        assertEquals("Criterion 2", c.getCriteriaList().get(1).getCriterionName());
    	           
    	        // Sprawdzenie mapy klasa-kryterium
        	        assertEquals(1, db2.getClassCriterionMap().size());
        	        UniClass key = db2.getClassList().get(0);
        	        List<ClassCriterion> criteria = db2.getClassCriterionMap().get(key);
        	        assertEquals("Class 1", key.getClassName());
        	        assertEquals(2, criteria.size());
        	        assertEquals("Criterion 1", criteria.get(0).getCriterionName());
        	        assertEquals(50, criteria.get(0).getMaxPoints());
        	        assertEquals("Criterion 2", criteria.get(1).getCriterionName());
        	        assertEquals(50, criteria.get(1).getMaxPoints());
        	        
        	        //Sprawdzenie mapy student-grupa
          	        assertEquals(1, db2.getStudentGroupMap().size());
        	        UniStudent stud = db2.getStudentList().get(0);
        	        assertEquals("Mariia", stud.getFirstName());
        	        assertEquals("Muzyczna", stud.getLastName());
        	        assertEquals("GD02PO01", mGr.getGroupCode());
        	        assertEquals("Grafika", mGr.getSpecialization());
        	        assertEquals("Grafika 1 rok", mGr.getDescription());
        	        
        			// Sprawdzamy mapy ocen studentów
        	       Map<UniClass, Map<ClassCriterion, Integer>> gradesMap = db2.getStudentGradesMap().get(s);
        	        assertNotNull(gradesMap);
        	        Map<ClassCriterion, Integer> classGrades = gradesMap.get(c);
        	        assertNotNull(classGrades);
        	        assertEquals((Integer) 4, classGrades.get(new ClassCriterion("Criterion 2", 50)));
    			}
    			catch(Exception ex){
    				System.out.println(ex.getMessage());
    			}
    }   
}
