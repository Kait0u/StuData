package pl.wit.studata.backend.fileio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pl.wit.studata.backend.UniDB;
import pl.wit.studata.backend.models.*;

public class SerializableTest {

	@Test
	public void test() {
		UniDB db = new UniDB();
		UniDB db2 = new UniDB();
		
		String outPath = "src/test/resources/output.txt";
		UniGroup gr = new UniGroup("GroupCode 1", "Spec1", "Desc1");
		ClassCriterion cr = new ClassCriterion("Criterion 1", 5);
		ClassCriterion cr2 = new ClassCriterion("Criterion 2", 5);
		List<ClassCriterion> criteria = Arrays.asList(cr, cr2);
		UniClass cl = new UniClass("Class 1", "CL1", criteria);
		UniStudent st = new UniStudent("Name", "Surname", 123);
		
		
		db.getStudentList().add(st);
		db.getGroupList().add(gr);
		db.getClassList().add(cl);
		db.getClassGroupMap().put(cl, Arrays.asList(gr));
		db.getStudentGroupMap().put(st, gr);

		db.addGradeToStudent(st, cl, cr2, 4);
		// write test
		try {
			db.saveToFile(outPath);
		}
		catch(Exception ex){
			System.out.print(ex.getMessage());
		}
		
		
		try {
			db2.loadFromFile(outPath);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		
		// check student-group map
		for(UniStudent stud: db2.getStudentGroupMap().keySet()) {
			System.out.println("Student-Group pair: ");
			System.out.println(stud.getFirstName());
			System.out.println(stud.getLastName());
			
			UniGroup mGr = db2.getStudentGroupMap().get(stud);
			System.out.println(mGr.getGroupCode());
			System.out.println(mGr.getDescription());
			System.out.println(mGr.getSpecialization());
		}
		
		// check class-group map
		for(UniClass key: db2.getClassGroupMap().keySet()) {
			System.out.println("Class-Group pair: ");
			System.out.println(key.getClassName());
			
			for(UniGroup group: db2.getClassGroupMap().get(key)) {
				System.out.println(group.getGroupCode());
				System.out.println(group.getDescription());
				System.out.println(group.getSpecialization());
			}
		}

		for(UniClass c: db2.getClassList()) {
			System.out.println(c.toString());
			System.out.println(c.getClassName());
			for(ClassCriterion c2: c.getCriteriaList()) {
				System.out.println("Criteria: " + c2.getCriterionName());
			}
		}
		
		for(UniStudent s: db2.getStudentList()) {
			System.out.println(s.toString());
			System.out.println(s.getFirstName());
			System.out.println(s.getLastName());
		}
		
		Map<UniClass, Map<ClassCriterion, Integer>> m = db2.getStudentGradesMap().get(st);
		Map<ClassCriterion, Integer> mm = m.get(cl);
		System.out.println(mm.get(cr2));
		
	}

}
