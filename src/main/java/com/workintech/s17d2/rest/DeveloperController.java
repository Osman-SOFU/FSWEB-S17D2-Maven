package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
    private final Taxable taxable;
    public Map<Integer, Developer> developers = new HashMap<>();

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers.put(1, new JuniorDeveloper(1, "Ali", 5000));
        developers.put(2, new MidDeveloper(2, "Veli", 7000));
        developers.put(3, new SeniorDeveloper(3, "Ayşe", 10000));
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Burada yanıt kodunu 201 olarak ayarlıyoruz
    public Developer createDeveloper(@RequestBody Developer developer) {
        int id = developers.size() + 1;
        Developer newDeveloper;

        switch (developer.getExperience()) {
            case JUNIOR -> newDeveloper = new JuniorDeveloper(id, developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxable.getSimpleTaxRate() / 100));
            case MID -> newDeveloper = new MidDeveloper(id, developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxable.getMiddleTaxRate() / 100));
            case SENIOR -> newDeveloper = new SeniorDeveloper(id, developer.getName(),
                    developer.getSalary() - (developer.getSalary() * taxable.getUpperTaxRate() / 100));
            default -> throw new IllegalStateException("Geçersiz deneyim seviyesi");
        }

        developers.put(id, newDeveloper);
        return newDeveloper;
    }


    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer developer) {
        if (developers.containsKey(id)) {
            developers.put(id, developer);
            return developer;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteDeveloper(@PathVariable int id) {
        developers.remove(id);
        return "Developer ID " + id + " silindi.";
    }
}
