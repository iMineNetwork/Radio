package nl.imine.radio.service.impl;

import nl.imine.radio.service.TrackFileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class TrackFileServiceImpl implements TrackFileService {

    public static final String STORAGE_PATH_FORMAT = "tracks/track_%s.nbs";

    @Override
    public InputStream findOne(UUID id) {
        Path path = Paths.get(String.format(STORAGE_PATH_FORMAT, id));
        if(Files.exists(path)){
            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void save(UUID id, InputStream inputStream) {
        try {
            Path path = Paths.get(String.format(STORAGE_PATH_FORMAT, id));
            Files.createDirectories(path.getParent());
            Files.copy(inputStream, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(UUID id) {
        return Files.exists(Paths.get(String.format(STORAGE_PATH_FORMAT, id)));
    }

    @Override
    public long findSize(UUID id) {
        try {
            return Files.size(Paths.get(String.format(STORAGE_PATH_FORMAT, id)));
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
