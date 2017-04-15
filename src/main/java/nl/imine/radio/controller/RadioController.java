package nl.imine.radio.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.imine.radio.model.Track;
import nl.imine.radio.service.TrackFileService;
import nl.imine.radio.service.TrackService;
import nl.imine.radio.validator.NBSFileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/radio")
public class RadioController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackFileService trackFileService;

    @Autowired
    private NBSFileValidator nbsFileValidator;

    @GetMapping
    public List<Track> getAll() {
        return trackService.findAll();
    }

    @GetMapping("/{id}")
    public Track getTrack(HttpServletResponse response,
                          @PathVariable Long id) throws IOException {
        Track one = trackService.findOne(id);
        return one;
    }

    @GetMapping("/{id}/download")
    public void downloadTrackFile(HttpServletResponse response,
                                  @PathVariable Long id) throws IOException {
        if(trackFileService.exists(id)) {
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(trackFileService.findSize(id));
            response.setHeader("Content-Disposition", "attachment; filename=track_" + id + ".nbs");
            try (OutputStream outputStream = response.getOutputStream()) {
                InputStream inputStream = trackFileService.findOne(id);
                for(int data = inputStream.read(); data > 0; data = inputStream.read()){
                    outputStream.write(data);
                }
            }
        } else {
            response.sendError(404, "The requested track could not be found");
        }
    }

    @PostMapping
    public long addTrack(HttpServletResponse response,
                         @RequestParam(required = true) String name,
                         @RequestParam(required = true) String artist,
                         @RequestParam MultipartFile file) throws IOException {

        if(nbsFileValidator.validate(file.getInputStream())){
            Track track = new Track();
            track.setName(name);
            track.setArtist(artist);
            trackService.save(track);

            trackFileService.save(track.getId(), file.getInputStream());
            return track.getId();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The file was not a valid NBS File");
            return -1;
        }
    }

    @PutMapping("/{id}")
    public Track updateTrack(HttpServletResponse response,
                             @PathVariable long id,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String artist) throws IOException {

        Track track = trackService.findOne(id);
        if (track != null) {
            if (name != null) {
                track.setName(name);
            }
            if (artist != null) {
                track.setArtist(artist);
            }
            trackService.save(track);
            return track;
        } else {
            response.sendError(404, "The requested track could not be found");
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteTrack(HttpServletResponse response,
                            @PathVariable long id) throws IOException {
        if(trackService.exists(id)){
            trackService.delete(id);
            response.sendError(200, "Deleting: " + id);
        } else {
            response.sendError(404, "The requested track could not be found");
        }
    }

    @PostMapping("/import")
    public void importTracks(HttpServletResponse response,
                             @RequestBody String urlString) throws IOException {
        try {
            String decodedString = URLDecoder.decode(urlString, StandardCharsets.UTF_8.name()).split("=")[0];
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

            List<Map<String, String>> content = objectMapper.readValue(new URL(decodedString), new TypeReference<List<HashMap<String, String>>>(){});

            for (Map<String, String> stringStringMap : content) {
                Track track = new Track();
                track.setName(stringStringMap.get("name"));
                track.setArtist(stringStringMap.get("artist"));
                trackService.save(track);

                String fileUrlFormat = decodedString.split("trackList.json")[0] + "%s.nbs";
                trackFileService.save(track.getId(), new URL(String.format(fileUrlFormat, stringStringMap.get("id"))).openStream());

                System.out.println(String.format("Imported \"%s\" by \"%s\"", track.getName(), track.getArtist()));
            }

            response.sendError(HttpServletResponse.SC_OK, "Done importing files");


        } catch (MalformedURLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
