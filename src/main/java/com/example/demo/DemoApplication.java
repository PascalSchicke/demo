package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.util.ArrayList;

@SpringBootApplication
@RestController
public class DemoApplication {
	StandardServiceRegistry ssr;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	// GET /hello/name -> Gibt "Hello %s! Schön, dass du da bist!" aus. Entweder mit dem Angegeben Namen oder dem
	// Standardwert "World".
	@GetMapping ("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s! Schön, dass du da bist!", name);
	}


	// POST /students/firstName,age -> Erzeugt einen Studierenden mit dem Angegebenen Vornamen und Alter, sowie dem
	// bereits definierten Nachnamen, der Telefonnummer und der E-Mail-Adresse.
	@PostMapping("/students")
	public String addStudent(@RequestParam(value = "firstName") String firstName,
							 @RequestParam(value = "age") Integer age) {
		Student student = new Student(firstName);
		student.setAge(age);
		student.setLastName("Müller");
		student.setPhoneNumber("1234567890");
		student.setEmailAddress("kevin.mueller@th-brandenburg.de");

		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure().build();

		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();
		SessionFactory factory = meta.getSessionFactoryBuilder().build();
		Session session = factory.openSession();

		session.beginTransaction();
		session.persist(student);
		session.flush();

		return "Erfolgreich in der Datenbank persistiert!";
	}

	// Versuch, bestehende Datensätze in der Datenbank zu aktualisieren.
/*	@PutMapping("/putstudents")
	public String updateStudent(@RequestParam(value = "firstName") String firstName,
								@RequestParam(value = "age") Integer age){
		Student student = Student(firstName);
		student.setAge(age);

		ssr = new StandardServiceRegistryBuilder().configure().build();

		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();
		SessionFactory factory = meta.getSessionFactoryBuilder().build();
		Session session = factory.openSession();

		session.beginTransaction();
		session.update(student);
		session.flush();

		return "Datenbank erfolgreich aktualisisert!";
	}
*/

	// GET /students/id -> Gibt den Studierenden mit der angegebenen ID zurück.
	@GetMapping("/students")
	public String viewStudents(@RequestParam(value = "id", defaultValue = "1") Long id) {

		ArrayList<Student> students = new ArrayList<>( 100);

		ssr = new StandardServiceRegistryBuilder().configure().build();

		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();
		SessionFactory factory = meta.getSessionFactoryBuilder().build();
		Session session = factory.openSession();

		session.beginTransaction();
		Student student = session.load(Student.class, id);
		session.flush();

		String studentObjectMappedToJSONString = null;
		ObjectMapper om = new ObjectMapper();
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		try {
			studentObjectMappedToJSONString = om.writeValueAsString(student);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
			return String.format(studentObjectMappedToJSONString);
	}
}
