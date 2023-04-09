package com.example.webservice.controllers;

import com.example.webservice.dao.TriangleDAO;
import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.models.Triangle;
import com.example.webservice.operations.CalculatingOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/view")
public class TriangleViewController {

    @Autowired
    CalculatingOperation calculatingOperation;
    @Autowired
    TriangleDAO triangleDAO;

    @GetMapping("/calculating")
    public String newTriangle(Model model){
        Triangle triangle = new Triangle();
        triangleDAO.addTriangle(triangle);
        model.addAttribute("triangle", triangleDAO.getLastTriangle());
        return "index";
    }

    @RequestMapping(value = "/calculating", method = RequestMethod.POST, params = "area")
    public String getArea(Model model, @ModelAttribute("triangle") @Valid Triangle triangle,
                          BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return "index";
        triangleDAO.addTriangle(triangle);
        var entity = new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC());
        model.addAttribute("area", calculatingOperation.ComputeArea(entity));
        return "index";
    }

    @RequestMapping(value = "/calculating", method = RequestMethod.POST, params = "perimeter")
    public String getPerimeter(Model model, @ModelAttribute("triangle") @Valid Triangle triangle,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return "index";
        triangleDAO.addTriangle(triangle);
        var entity = new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC());
        model.addAttribute("area", calculatingOperation.ComputeArea(entity));
        model.addAttribute("perimeter", calculatingOperation.ComputePerimeter(entity));
        return "index";
    }
}
