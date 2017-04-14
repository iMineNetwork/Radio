package nl.imine.radio.service;

import nl.imine.radio.model.Track;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TrackService extends CrudRepository<Track, Long> {

    List<Track> findAll();

    List<Track> findAllByName(String name);

    List<Track> findAllByArtist(String artist);
}
