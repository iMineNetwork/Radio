package nl.imine.radio.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public interface TrackFileService {

    InputStream findOne(UUID id) throws IOException;

    void save(UUID id, InputStream inputStream);

    boolean exists(UUID id);

    long findSize(UUID id);
}
