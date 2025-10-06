package ru.school21.TicTacToe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.school21.TicTacToe.domain.service.TicTacService;

@Controller
@RequestMapping("/table")
public class TableController {
    private final TicTacService ticTacService;

    @Autowired
    public TableController(TicTacService ticTacService) {
        this.ticTacService = ticTacService;
    }

    @GetMapping()
    public String searchTable() {
        return "table/search";
    }

    @PostMapping()
    public String search(@RequestParam("count") int count, Model model) {
        if(count <= 0) {
            model.addAttribute("error", "Нельзя указать число меньше 0");
            return "table/search";
        }
        else{
            return "redirect:table/" + count;
        }
    }

    @GetMapping("/{count}")
    public String table(@PathVariable("count") int count, Model model) {
        model.addAttribute("leaderBoard", ticTacService.getLeaderBoard(count));
        return "table/leaders";
    }
}
