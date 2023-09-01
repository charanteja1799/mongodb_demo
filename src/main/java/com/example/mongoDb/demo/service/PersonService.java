package com.example.mongoDb.demo.service;

import com.example.mongoDb.demo.collection.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {
    String save(Person person);

    List<Person> getPersonSTartWith(String name);

    void deletePerson(int id);

    List<Person> getPersonListWithAge(Integer min, Integer max);

    Page<Person> search(Integer min, Integer max, String name, String city, Pageable pageable);

    List<Document> getOlderPersonByCity();

    List<Document> getPopulationByCity();
}
