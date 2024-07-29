package org.zerock.niceadmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/todo")
@Log4j2
public class TodoController {

    //  TODO 페이지
    @GetMapping("/list")
    public String todoList(Model model) {
        return "todo/list";
    }

}
