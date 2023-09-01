package com.example.mongoDb.demo.service;

import com.example.mongoDb.demo.collection.Person;
import com.example.mongoDb.demo.repository.PersonRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Person person) {
        return personRepository.save(person).getPersonId().toString();
    }

    @Override
    public List<Person> getPersonSTartWith(String name) {
        return personRepository.findByFirstNameStartsWith(name);
    }

    @Override
    public void deletePerson(int id) {
        personRepository.deleteById(id);
    }

    @Override
    public List<Person> getPersonListWithAge(Integer min, Integer max) {
        return personRepository.findByAgeBetweenMinAndMax(min,max);
    }

    @Override
    public Page<Person> search(Integer min, Integer max, String name, String city, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();
        if(name != null && !name.isEmpty()){
            criteria.add(Criteria.where("firstName").regex(name, "i"));
        }
        if(min != null && max != null){
            criteria.add(Criteria.where("age").gte(min).lt(max));
        }
        if(city != null && !city.isEmpty()){
            criteria.add(Criteria.where("addresses.address1").is(city));
        }
        if(!criteria.isEmpty() && criteria != null){
            query.addCriteria(new Criteria().orOperator(criteria.toArray(new Criteria[0])));
        }
        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query,Person.class),
                pageable, () -> mongoTemplate.count(query.skip(0).limit(0),Person.class));
        return people;
    }

    @Override
    public List<Document> getOlderPersonByCity() {
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"age");
        GroupOperation groupOperation = Aggregation.group("address.address1")
                .first(Aggregation.ROOT)
                .as("oldestPerson");

        Aggregation aggregation = Aggregation.newAggregation(unwindOperation,sortOperation,groupOperation);
        List<Document> oldestPersonData = mongoTemplate.aggregate(aggregation,Person.class,Document.class).getMappedResults();
        return oldestPersonData;
    }

    @Override
    public List<Document> getPopulationByCity() {
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        GroupOperation groupOperation = Aggregation.group("addresses.address1")
                .count().as("popCount");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC,"popCount");
        ProjectionOperation projectionOperation =
                Aggregation.project()
                        .andExpression("_id").as("city")
                        .andExpression("popCount").as("count")
                        .andExclude("_id");
        Aggregation aggregation = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);
        List<Document> mappedResults = mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        return  mappedResults;
    }
}
