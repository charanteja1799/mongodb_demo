package com.example.mongoDb.demo.controller;

import com.example.mongoDb.demo.collection.Person;
import com.example.mongoDb.demo.service.PersonService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("")
    public String save(@RequestBody Person person){
        return personService.save(person);
    }

    @PostMapping("/getPersonList")
    public List<Person> getPersonStartWith(@RequestParam("name") String name){
        return personService.getPersonSTartWith(name);
    }

    @DeleteMapping("/{id}")
    public void deletePerson (@PathVariable int id){
        personService.deletePerson(id);
    }

    @GetMapping("/age")
    public List<Person> getPersonWithAge(@RequestParam("min") Integer min,@RequestParam("max") Integer max){
        return personService.getPersonListWithAge(min, max);
    }

    @GetMapping("/search")
    public Page<Person> searchPagination(@RequestParam(required = false) Integer min,
                                         @RequestParam(required = false) Integer max,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String city,
                                         @RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        return personService.search(min,max,name,city,pageable);

    }

    @GetMapping("/oldestPerson")
    public List<Document> getOldestPerson(){
        return personService.getOlderPersonByCity();
    }

    @GetMapping("/populationByCity")
    public List<Document> getPopuluationByCity(){
        return personService.getPopulationByCity();
    }

}
