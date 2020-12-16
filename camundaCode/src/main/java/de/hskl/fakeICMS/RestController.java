package de.hskl.fakeICMS;

import de.hskl.fakeICMS.entities.Note;
import de.hskl.fakeICMS.entities.Student;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private Logger logger = Logger.getLogger("fakeICMS");

    private List<Student> students = new ArrayList<>();

    public RestController() {
        students.add(new Student("Anna Bolika", 111111));
        students.add(new Student("Klaus Trophobie", 222222));
    }

    @PostMapping("/students")
    public ResponseEntity<Object> createStudent(@RequestBody Student student) {
        for(Student currentStudent : students) {
            if(currentStudent.getMatrikelnummer() == student.getMatrikelnummer())
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"Student already exists.\"}");
        }

        students.add(student);
        logger.info("Created student " + student.getName() + " (" + student.getMatrikelnummer() + ").");

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{matrikelnummer}").buildAndExpand(student.getMatrikelnummer()).toUri();

        return ResponseEntity.created(uri).body(student);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getStudents() {
        logger.info("Requested students.");
        return ResponseEntity.ok().body(students);
    }

    @GetMapping("/students/{matrikelnummer}")
    public ResponseEntity<Student> getStudent(@PathVariable long matrikelnummer) {
        Student foundStudent = findStudent(matrikelnummer);
        if(foundStudent == null) {
            logger.warning("Searched student " + matrikelnummer + " but none where found.");
            return ResponseEntity.notFound().build();
        }

        logger.info("Student " + matrikelnummer + " has been queried");
        return ResponseEntity.ok().body(foundStudent);
    }

    @PutMapping("/students/{matrikelnummer}/{veranstaltung}")
    public ResponseEntity<Object> createNote(@PathVariable long matrikelnummer, @PathVariable String veranstaltung, @RequestBody Note note) {
        Student foundStudent = findStudent(matrikelnummer);
        if(foundStudent == null) {
            logger.warning("Tried to add a grade to a non existing student " + matrikelnummer);
            return ResponseEntity.notFound().build();
        }

        if(foundStudent.getNoten().containsKey(veranstaltung)) {
            logger.warning("Tried to add a grade to " + matrikelnummer + ". A student who already is graded in " + veranstaltung);
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"Student is already graded in that module.\"}");
        }

        foundStudent.getNoten().put(veranstaltung, note);

        logger.info("Added " + veranstaltung + " with " + note.getNote() + " to " + matrikelnummer);
        return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(foundStudent);
    }

    private Student findStudent(long matrikelnummer) {
        for(Student student : students) {
            if(student.getMatrikelnummer() == matrikelnummer) {
                return student;
            }
        }
        return null;
    }
}
