package ru.school21.morrasha2.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.school21.morrasha2.domain.model.Person;
import ru.school21.morrasha2.domain.service.PersonDetailsService;

import java.util.UUID;

@Controller
@RequestMapping("/info")
public class PersonController {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonController(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @GetMapping()
    public String personInfo(Model model) {
        model.addAttribute("person", new Person());
        return "info/search";
    }

    @PostMapping()
    public String search(@RequestParam("id") String stringId, Model model) {
        try{
            UUID id = UUID.fromString(stringId);
            return "redirect:/info/" + id;
        }
        catch (Exception e) {
            model.addAttribute("error", "Пользователь с таким id не найден");
            return "info/search";
        }
    }

    @GetMapping("/{id}")
    public String personInfo(@PathVariable UUID id, Model model) {
        model.addAttribute("person", personDetailsService.getPerson(id));
        return "info/person";
    }

}
