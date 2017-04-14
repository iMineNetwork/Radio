package nl.imine.radio.controller;

import nl.imine.radio.model.Track;
import nl.imine.radio.service.TrackFileService;
import nl.imine.radio.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackFileService trackFileService;

    @GetMapping
    public ModelAndView get() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("tracks", trackService.findAll());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView post(@RequestParam String name,
                             @RequestParam String artist,
                             @RequestParam MultipartFile file) throws IOException {
        Track track = new Track();
        track.setName(name);
        track.setArtist(artist);
        trackService.save(track);

        trackFileService.save(track.getId(), file.getInputStream());
        return get();
    }
}
