package nl.imine.radio.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface TrackFileService {

    InputStream findOne(long id) throws IOException;

    void save(long id, InputStream inputStream);

    boolean exists(long id);

    long findSize(long id);
}
