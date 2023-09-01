package com.example.mongoDb.demo.repository;

import com.example.mongoDb.demo.collection.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person,Integer> {
    List<Person> findByFirstNameStartsWith(String name);

    @Query(value = "{ 'age' : { $gt : ?0, $lt : ?1}}",fields = "{'addresses' : 0}")
    List<Person> findByAgeBetweenMinAndMax(Integer min, Integer max);
}
