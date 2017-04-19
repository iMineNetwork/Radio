package nl.imine.radio.controller;

import nl.imine.radio.model.Track;
import nl.imine.radio.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/delete")
public class DeleteController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private IndexController indexController;

    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable long id){
        if(trackService.exists(id)){
            ModelAndView modelAndView = new ModelAndView("delete");
            modelAndView.addObject("track", trackService.findOne(id));
            return modelAndView;
        } else {
            return indexController.get();
        }
    }

    @PostMapping("/{id}")
    public ModelAndView post(@PathVariable long id, String confirmation){
        if("Delete".equals(confirmation)){
            trackService.delete(id);
        }
        return indexController.get();
    }
}
