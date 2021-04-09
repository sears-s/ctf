package org.jivesoftware.smackx.geoloc.packet;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.TimestampElement;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.jivesoftware.smackx.reference.element.ReferenceElement;

public final class GeoLocation implements Serializable, ExtensionElement {
    public static final String ELEMENT = "geoloc";
    private static final Logger LOGGER = Logger.getLogger(GeoLocation.class.getName());
    public static final String NAMESPACE = "http://jabber.org/protocol/geoloc";
    private static final long serialVersionUID = 1;
    private final Double accuracy;
    private final Double alt;
    private final Double altAccuracy;
    private final String area;
    private final Double bearing;
    private final String building;
    private final String country;
    private final String countryCode;
    private final String datum;
    private final String description;
    private final Double error;
    private final String floor;
    private final Double lat;
    private final String locality;
    private final Double lon;
    private final String postalcode;
    private final String region;
    private final String room;
    private final Double speed;
    private final String street;
    private final String text;
    private final Date timestamp;
    private final String tzo;
    private final URI uri;

    public static class Builder {
        private Double accuracy;
        private Double alt;
        private Double altAccuracy;
        private String area;
        private Double bearing;
        private String building;
        private String country;
        private String countryCode;
        private String datum;
        private String description;
        private Double error;
        private String floor;
        private Double lat;
        private String locality;
        private Double lon;
        private String postalcode;
        private String region;
        private String room;
        private Double speed;
        private String street;
        private String text;
        private Date timestamp;
        private String tzo;
        private URI uri;

        public Builder setAccuracy(Double accuracy2) {
            this.accuracy = accuracy2;
            return this;
        }

        public Builder setAlt(Double alt2) {
            this.alt = alt2;
            return this;
        }

        public Builder setAltAccuracy(Double altAccuracy2) {
            this.altAccuracy = altAccuracy2;
            return this;
        }

        public Builder setArea(String area2) {
            this.area = area2;
            return this;
        }

        public Builder setBearing(Double bearing2) {
            this.bearing = bearing2;
            return this;
        }

        public Builder setBuilding(String building2) {
            this.building = building2;
            return this;
        }

        public Builder setCountry(String country2) {
            this.country = country2;
            return this;
        }

        public Builder setCountryCode(String countryCode2) {
            this.countryCode = countryCode2;
            return this;
        }

        public Builder setDatum(String datum2) {
            this.datum = datum2;
            return this;
        }

        public Builder setDescription(String description2) {
            this.description = description2;
            return this;
        }

        public Builder setError(Double error2) {
            this.error = error2;
            return this;
        }

        public Builder setFloor(String floor2) {
            this.floor = floor2;
            return this;
        }

        public Builder setLat(Double lat2) {
            this.lat = lat2;
            return this;
        }

        public Builder setLocality(String locality2) {
            this.locality = locality2;
            return this;
        }

        public Builder setLon(Double lon2) {
            this.lon = lon2;
            return this;
        }

        public Builder setPostalcode(String postalcode2) {
            this.postalcode = postalcode2;
            return this;
        }

        public Builder setRegion(String region2) {
            this.region = region2;
            return this;
        }

        public Builder setRoom(String room2) {
            this.room = room2;
            return this;
        }

        public Builder setSpeed(Double speed2) {
            this.speed = speed2;
            return this;
        }

        public Builder setStreet(String street2) {
            this.street = street2;
            return this;
        }

        public Builder setText(String text2) {
            this.text = text2;
            return this;
        }

        public Builder setTimestamp(Date timestamp2) {
            this.timestamp = timestamp2;
            return this;
        }

        public Builder setTzo(String tzo2) {
            this.tzo = tzo2;
            return this;
        }

        public Builder setUri(URI uri2) {
            this.uri = uri2;
            return this;
        }

        public GeoLocation build() {
            GeoLocation geoLocation = new GeoLocation(this.accuracy, this.alt, this.altAccuracy, this.area, this.bearing, this.building, this.country, this.countryCode, this.datum, this.description, this.error, this.floor, this.lat, this.locality, this.lon, this.postalcode, this.region, this.room, this.speed, this.street, this.text, this.timestamp, this.tzo, this.uri);
            return geoLocation;
        }
    }

    private GeoLocation(Double accuracy2, Double alt2, Double altAccuracy2, String area2, Double bearing2, String building2, String country2, String countryCode2, String datum2, String description2, Double error2, String floor2, Double lat2, String locality2, Double lon2, String postalcode2, String region2, String room2, Double speed2, String street2, String text2, Date timestamp2, String tzo2, URI uri2) {
        String datum3;
        Double error3;
        Double d = accuracy2;
        this.accuracy = d;
        this.alt = alt2;
        this.altAccuracy = altAccuracy2;
        this.area = area2;
        this.bearing = bearing2;
        this.building = building2;
        this.country = country2;
        this.countryCode = countryCode2;
        if (StringUtils.isNullOrEmpty((CharSequence) datum2)) {
            datum3 = "WGS84";
        } else {
            datum3 = datum2;
        }
        this.datum = datum3;
        this.description = description2;
        if (d != null) {
            error3 = null;
            LOGGER.log(Level.WARNING, "Error and accuracy set. Ignoring error as it is deprecated in favor of accuracy");
        } else {
            error3 = error2;
        }
        this.error = error3;
        this.floor = floor2;
        this.lat = lat2;
        this.locality = locality2;
        this.lon = lon2;
        this.postalcode = postalcode2;
        this.region = region2;
        this.room = room2;
        this.speed = speed2;
        this.street = street2;
        this.text = text2;
        this.timestamp = timestamp2;
        this.tzo = tzo2;
        this.uri = uri2;
    }

    public Double getAccuracy() {
        return this.accuracy;
    }

    public Double getAlt() {
        return this.alt;
    }

    public Double getAltAccuracy() {
        return this.altAccuracy;
    }

    public String getArea() {
        return this.area;
    }

    public Double getBearing() {
        return this.bearing;
    }

    public String getBuilding() {
        return this.building;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getDatum() {
        return this.datum;
    }

    public String getDescription() {
        return this.description;
    }

    public Double getError() {
        return this.error;
    }

    public String getFloor() {
        return this.floor;
    }

    public Double getLat() {
        return this.lat;
    }

    public String getLocality() {
        return this.locality;
    }

    public Double getLon() {
        return this.lon;
    }

    public String getPostalcode() {
        return this.postalcode;
    }

    public String getRegion() {
        return this.region;
    }

    public String getRoom() {
        return this.room;
    }

    public Double getSpeed() {
        return this.speed;
    }

    public String getStreet() {
        return this.street;
    }

    public String getText() {
        return this.text;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getTzo() {
        return this.tzo;
    }

    public URI getUri() {
        return this.uri;
    }

    public String getElementName() {
        return ELEMENT;
    }

    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder((ExtensionElement) this);
        xml.rightAngleBracket();
        xml.optElement("accuracy", (Object) this.accuracy);
        xml.optElement("alt", (Object) this.alt);
        xml.optElement("altaccuracy", (Object) this.altAccuracy);
        xml.optElement("area", this.area);
        xml.optElement("bearing", (Object) this.bearing);
        xml.optElement("building", this.building);
        xml.optElement("country", this.country);
        xml.optElement("countrycode", this.countryCode);
        xml.optElement("datum", this.datum);
        xml.optElement(JingleContentDescription.ELEMENT, this.description);
        xml.optElement("error", (Object) this.error);
        xml.optElement("floor", this.floor);
        xml.optElement("lat", (Object) this.lat);
        xml.optElement("locality", this.locality);
        xml.optElement("lon", (Object) this.lon);
        xml.optElement("postalcode", this.postalcode);
        xml.optElement("region", this.region);
        xml.optElement("room", this.room);
        xml.optElement("speed", (Object) this.speed);
        xml.optElement("street", this.street);
        xml.optElement("text", this.text);
        xml.optElement(TimestampElement.ELEMENT, this.timestamp);
        xml.optElement("tzo", this.tzo);
        xml.optElement(ReferenceElement.ATTR_URI, (Object) this.uri);
        xml.closeElement((NamedElement) this);
        return xml;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GeoLocation from(Message message) {
        return (GeoLocation) message.getExtension(ELEMENT, NAMESPACE);
    }
}
