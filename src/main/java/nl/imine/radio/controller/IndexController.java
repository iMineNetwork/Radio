package nl.imine.radio.controller;

import nl.imine.radio.model.Track;
import nl.imine.radio.service.TrackFileService;
import nl.imine.radio.service.TrackService;
import nl.imine.radio.validator.NBSFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackFileService trackFileService;

    @Autowired
    private NBSFileValidator nbsFileValidator;

    @GetMapping
    public ModelAndView get() {
        ModelAndView modelAndView = new ModelAndView("index");
        List<Track> tracks = trackService.findAll();
        Collections.sort(tracks);
        modelAndView.addObject("tracks", tracks);
        return modelAndView;
    }

    @PostMapping
    public ModelAndView post(HttpServletResponse response,
                             @RequestParam String name,
                             @RequestParam String artist,
                             @RequestParam MultipartFile file) throws IOException {
        if(nbsFileValidator.validate(file.getInputStream())){
            Track track = new Track();
            track.setName(name);
            track.setArtist(artist);
            trackService.save(track);

            trackFileService.save(track.getId(), file.getInputStream());

            ModelAndView modelAndView = get();
            modelAndView.addObject("uploadStatus", String.format("Succesfully uploaded \"%s\" by \"%s\"", name, artist));
            return modelAndView;
        } else {
            ModelAndView modelAndView = get();
            modelAndView.addObject("uploadStatus", String.format("NBS Validation failed. Please verify the file type", name, artist));
            System.out.println("aaa");
            return modelAndView;
        }
    }
}
