package ewm.common.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Location {
    private Float lat;
    private Float lon;

    public Location() {}

    public Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}