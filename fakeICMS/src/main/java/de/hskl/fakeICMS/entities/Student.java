package de.hskl.fakeICMS.entities;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private String name;
    private long matrikelnummer;
    private Map<String, Note> noten = new HashMap<>(); //<Veranstaltung, Note>

    public Student(String name, long matrikelnummer) {
        this.name = name;
        this.matrikelnummer = matrikelnummer;
    }

    public Student() {}

    public String getName() {
        return name;
    }

    public long getMatrikelnummer() {
        return matrikelnummer;
    }

    public Map<String, Note> getNoten() {
        return noten;
    }
}
