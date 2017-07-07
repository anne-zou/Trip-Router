/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel;


/** 
* A Place is where a journey starts or ends, or a transit stop along the way.
*/ 
public class Place {

    /** 
     * For transit stops, the name of the stop.  For points of interest, the name of the POI.
     */
    private String name = null;

    /** 
     * The ID of the stop. This is often something that users don't care about.
     */
    private String stopId = null;

    /** 
     * The "code" of the stop. Depending on the transit agency, this is often
     * something that users care about.
     */
    private String stopCode = null;

    /**
      * The code or name identifying the quay/platform the vehicle will arrive at or depart from
      *
    */
    private String platformCode = null;

    /**
     * The longitude of the place.
     */
    private Double lon = null;
    
    /**
     * The latitude of the place.
     */
    private Double lat = null;

    /**
     * The time the rider will arrive at the place.
     */
    private long arrival = 0;

    /**
     * The time the rider will depart the place.
     */
    private long departure = 0;

    private String orig;

    private String zoneId;

    /**
     * For transit trips, the stop index (numbered from zero from the start of the trip
     */
    private Integer stopIndex;

    /**
     * For transit trips, the sequence number of the stop. Per GTFS, these numbers are increasing.
     */
    private Integer stopSequence;

    /**
     * Type of vertex. (Normal, Bike sharing station, Bike P+R, Transit stop)
     * Mostly used for better localization of bike sharing and P+R station names
     */
    private VertexType vertexType;

    /**
     * In case the vertex is of type Bike sharing station.
     */
    private String bikeShareId;


    public Place() {
    }

    public Place(Double lon, Double lat, String name) {
        this.lon = lon;
        this.lat = lat;
        this.name = name;
	    this.vertexType = VertexType.NORMAL;
    }

    public Place(Double lon, Double lat, String name, long arrival, long departure) {
        this(lon, lat, name);
        this.arrival = arrival;
        this.departure = departure;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public long getArrival() {
        return arrival;
    }

    public void setArrival(long arrival) {
        this.arrival = arrival;
    }

    public long getDeparture() {
        return departure;
    }

    public void setDeparture(long departure) {
        this.departure = departure;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(Integer stopIndex) {
        this.stopIndex = stopIndex;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public VertexType getVertexType() {
        return vertexType;
    }

    public void setVertexType(VertexType vertexType) {
        this.vertexType = vertexType;
    }

    public String getBikeShareId() {
        return bikeShareId;
    }

    public void setBikeShareId(String bikeShareId) {
        this.bikeShareId = bikeShareId;
    }


    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", stopId='" + stopId + '\'' +
                ", stopCode='" + stopCode + '\'' +
                ", platformCode='" + platformCode + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", arrival=" + arrival +
                ", departure=" + departure +
                ", orig='" + orig + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", stopIndex=" + stopIndex +
                ", stopSequence=" + stopSequence +
                ", vertexType=" + vertexType +
                ", bikeShareId='" + bikeShareId + '\'' +
                '}';
    }
}
