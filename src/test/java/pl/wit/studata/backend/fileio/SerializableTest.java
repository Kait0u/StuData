package pl.wit.studata.backend.fileio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		UniClass cl = new UniClass("Class 1", criteria);
		UniStudent st = new UniStudent("Name", "Surname", 123);
		
		db.getStudentList().add(st);
		db.getGroupList().add(gr);
		db.getClassList().add(cl);
//		db.getGroupStudentMap().put(gr, Arrays.asList(st));
		db.getClassGroupMap().put(cl, Arrays.asList(gr));
		
		// write test
		try {
			FileOutputStream fs = new FileOutputStream(outPath);
			DataOutputStream out = new DataOutputStream(fs);
			
			db.saveToFile(out);
		}
		catch(Exception ex){
			System.out.print(ex.getMessage());
		}
		
		
		try {
			FileInputStream fs = new FileInputStream(outPath);
			DataInputStream in = new DataInputStream(fs);
			
			db2.loadFromFile(in);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		for(UniGroup g: db2.getGroupList()) {
			System.out.println("UniGroup: ");
			System.out.println(g.toString());
			System.out.println(g.getGroupCode());
			System.out.println(g.getDescription());
			System.out.println(g.getSpecialization());
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
		
	}

}
