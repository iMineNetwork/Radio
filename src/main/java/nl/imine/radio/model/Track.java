package nl.imine.radio.model;

import java.util.UUID;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
public class Track implements Comparable<Track> {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    private UUID id;
    private String name;
    private String artist;

    public Track() {
    }

    public Track(UUID id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int compareTo(Track other) {
        int artistCompare = this.getArtist().compareToIgnoreCase(other.getArtist());
        if(artistCompare == 0) {
            return this.getName().compareToIgnoreCase(other.getName());
        }
        return artistCompare;
    }
}
