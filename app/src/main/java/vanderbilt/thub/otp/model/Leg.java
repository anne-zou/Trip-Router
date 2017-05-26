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

package vanderbilt.thub.otp.model;

import java.util.*;

/**
* One leg of a trip -- that is, a temporally continuous piece of the journey that takes place on a
* particular vehicle (or on foot).
*/

public class Leg {

   /**
    * The date and time this leg begins.
    */
   private long startTime = 0;

   /**
    * The date and time this leg ends.
    */
   private long endTime = 0;

   /**
    * For transit leg, the offset from the scheduled departure-time of the boarding stop in this leg.
    * "scheduled time of departure at boarding stop" = startTime - departureDelay
    */
   private int departureDelay = 0;
   /**
    * For transit leg, the offset from the scheduled arrival-time of the alighting stop in this leg.
    * "scheduled time of arrival at alighting stop" = endTime - arrivalDelay
    */
   private int arrivalDelay = 0;
   /**
    * Whether there is real-time data about this Leg
    */
   private Boolean realTime = false;

   /**
    * Is this a frequency-based trip with non-strict departure times?
    */
   private Boolean isNonExactFrequency = null;

   /**
    * The best estimate of the time between two arriving vehicles. This is particularly important
    * for non-strict frequency trips, but could become important for real-time trips, strict
    * frequency trips, and scheduled trips with empirical headways.
    */
   private Integer headway = null;

   /**
    * The distance traveled while traversing the leg in meters.
    */
   private Double distance = null;

   /**
    * Is this leg a traversing pathways?
    */
   private Boolean pathway = false;

   /**
    * The mode (e.g., <code>Walk</code>) used when traversing this leg.
    */
   private String mode = TraverseMode.WALK.toString();

   /**
    * For transit legs, the route of the bus or train being used. For non-transit legs, the name of
    * the street being traversed.
    */
   private String route = "";

    private String agencyName;

    private String agencyUrl;

    private String agencyBrandingUrl;

    private int agencyTimeZoneOffset;

   /**
    * For transit leg, the route's (background) color (if one exists). For non-transit legs, null.
    */
   private String routeColor = null;

   /**
    * For transit legs, the type of the route. Non transit -1
    * When 0-7: 0 Tram, 1 Subway, 2 Train, 3 Bus, 4 Ferry, 5 Cable Car, 6 Gondola, 7 Funicular
    * When equal or highter than 100, it is coded using the Hierarchical Vehicle Type (HVT) codes from the European TPEG standard
    * Also see http://groups.google.com/group/gtfs-changes/msg/ed917a69cf8c5bef
    */
   private Integer routeType = null;

   /**
    * For transit legs, the ID of the route.
    * For non-transit legs, null.
    */
   private String routeId = null;

   /**
    * For transit leg, the route's text color (if one exists). For non-transit legs, null.
    */
   private String routeTextColor = null;

   /**
    * For transit legs, if the rider should stay on the vehicle as it changes route names.
    */
   private Boolean interlineWithPreviousLeg;


   /**
    * For transit leg, the trip's short name (if one exists). For non-transit legs, null.
    */
   private String tripShortName = null;

   /**
    * For transit leg, the trip's block ID (if one exists). For non-transit legs, null.
    */
   private String tripBlockId = null;

   /**
    * For transit legs, the headsign of the bus or train being used. For non-transit legs, null.
    */
   private String headsign = null;

   /**
    * For transit legs, the ID of the transit agency that operates the service used for this leg.
    * For non-transit legs, null.
    */
   private String agencyId = null;

   /**
    * For transit legs, the ID of the trip.
    * For non-transit legs, null.
    */
   private String tripId = null;

   /**
    * For transit legs, the service date of the trip.
    * For non-transit legs, null.
    */
   private String serviceDate = null;

    /**
     * For transit leg, the route's branding URL (if one exists). For non-transit legs, null.
     */
    private String routeBrandingUrl = null;

    /**
    * The Place where the leg originates.
    */
    private Place from = null;

   /**
    * The Place where the leg begins.
    */
   private Place to = null;

   /**
    * For transit legs, intermediate stops between the Place where the leg originates and the Place where the leg ends.
    * For non-transit legs, null.
    * This field is optional i.e. it is always null unless "showIntermediateStops" parameter is set to "true" in the planner request.
    */

   private List<Place> stop;

   /**
    * The leg's geometry.
    */
   private EncodedPolylineBean legGeometry;

   /**
    * A series of turn by turn instructions used for walking, biking and driving.
    */
   private List<WalkStep> steps;

    //TODO Implement this
//   private List<LocalizedAlert> alerts;

    private String routeShortName;

    private String routeLongName;

    private String boardRule;

    private String alightRule;

    private Boolean rentedBike;

    private Double duration;

    private Boolean transitLeg;


    public Leg() {
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDepartureDelay() {
        return departureDelay;
    }

    public void setDepartureDelay(int departureDelay) {
        this.departureDelay = departureDelay;
    }

    public int getArrivalDelay() {
        return arrivalDelay;
    }

    public void setArrivalDelay(int arrivalDelay) {
        this.arrivalDelay = arrivalDelay;
    }

    public Boolean getRealTime() {
        return realTime;
    }

    public void setRealTime(Boolean realTime) {
        this.realTime = realTime;
    }

    public Boolean getNonExactFrequency() {
        return isNonExactFrequency;
    }

    public void setNonExactFrequency(Boolean nonExactFrequency) {
        isNonExactFrequency = nonExactFrequency;
    }

    public Integer getHeadway() {
        return headway;
    }

    public void setHeadway(Integer headway) {
        this.headway = headway;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Boolean getPathway() {
        return pathway;
    }

    public void setPathway(Boolean pathway) {
        this.pathway = pathway;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyUrl() {
        return agencyUrl;
    }

    public void setAgencyUrl(String agencyUrl) {
        this.agencyUrl = agencyUrl;
    }

    public String getAgencyBrandingUrl() {
        return agencyBrandingUrl;
    }

    public void setAgencyBrandingUrl(String agencyBrandingUrl) {
        this.agencyBrandingUrl = agencyBrandingUrl;
    }

    public int getAgencyTimeZoneOffset() {
        return agencyTimeZoneOffset;
    }

    public void setAgencyTimeZoneOffset(int agencyTimeZoneOffset) {
        this.agencyTimeZoneOffset = agencyTimeZoneOffset;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }



    public String getRouteTextColor() {
        return routeTextColor;
    }

    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }

    public Boolean getInterlineWithPreviousLeg() {
        return interlineWithPreviousLeg;
    }

    public void setInterlineWithPreviousLeg(Boolean interlineWithPreviousLeg) {
        this.interlineWithPreviousLeg = interlineWithPreviousLeg;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    public String getTripBlockId() {
        return tripBlockId;
    }

    public void setTripBlockId(String tripBlockId) {
        this.tripBlockId = tripBlockId;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getRouteBrandingUrl() {
        return routeBrandingUrl;
    }

    public void setRouteBrandingUrl(String routeBrandingUrl) {
        this.routeBrandingUrl = routeBrandingUrl;
    }

    public Place getFrom() {
        return from;
    }

    public void setFrom(Place from) {
        this.from = from;
    }

    public Place getTo() {
        return to;
    }

    public void setTo(Place to) {
        this.to = to;
    }

    public List<Place> getStop() {
        return stop;
    }

    public void setStop(List<Place> stop) {
        this.stop = stop;
    }

    public EncodedPolylineBean getLegGeometry() {
        return legGeometry;
    }

    public void setLegGeometry(EncodedPolylineBean legGeometry) {
        this.legGeometry = legGeometry;
    }

    public List<WalkStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WalkStep> steps) {
        this.steps = steps;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getBoardRule() {
        return boardRule;
    }

    public void setBoardRule(String boardRule) {
        this.boardRule = boardRule;
    }

    public String getAlightRule() {
        return alightRule;
    }

    public void setAlightRule(String alightRule) {
        this.alightRule = alightRule;
    }

    public Boolean getRentedBike() {
        return rentedBike;
    }

    public void setRentedBike(Boolean rentedBike) {
        this.rentedBike = rentedBike;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Boolean getTransitLeg() {
        return transitLeg;
    }

    public void setTransitLeg(Boolean transitLeg) {
        this.transitLeg = transitLeg;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "startTime=" + startTime + "\n" +
                ", endTime=" + endTime + "\n" +
                ", departureDelay=" + departureDelay + "\n" +
                ", arrivalDelay=" + arrivalDelay + "\n" +
                ", realTime=" + realTime + "\n" +
                ", isNonExactFrequency=" + isNonExactFrequency + "\n" +
                ", headway=" + headway + "\n" +
                ", distance=" + distance + "\n" +
                ", pathway=" + pathway + "\n" +
                ", mode='" + mode + '\'' + "\n" +
                ", route='" + route + '\'' + "\n" +
                ", agencyName='" + agencyName + '\'' + "\n" +
                ", agencyUrl='" + agencyUrl + '\'' + "\n" +
                ", agencyBrandingUrl='" + agencyBrandingUrl + '\'' + "\n" +
                ", agencyTimeZoneOffset=" + agencyTimeZoneOffset + "\n" +
                ", routeColor='" + routeColor + '\'' + "\n" +
                ", routeType=" + routeType + "\n" +
                ", routeId='" + routeId + '\'' + "\n" +
                ", routeTextColor='" + routeTextColor + '\'' + "\n" +
                ", interlineWithPreviousLeg=" + interlineWithPreviousLeg + "\n" +
                ", tripShortName='" + tripShortName + '\'' + "\n" +
                ", tripBlockId='" + tripBlockId + '\'' + "\n" +
                ", headsign='" + headsign + '\'' + "\n" +
                ", agencyId='" + agencyId + '\'' + "\n" +
                ", tripId='" + tripId + '\'' + "\n" +
                ", serviceDate='" + serviceDate + '\'' + "\n" +
                ", routeBrandingUrl='" + routeBrandingUrl + '\'' + "\n" +
                ", from=" + from + "\n" +
                ", to=" + to + "\n" +
                ", stop=" + stop + "\n" +
                ", legGeometry=" + legGeometry + "\n" +
                ", steps=" + steps + "\n" +
                ", routeShortName='" + routeShortName + '\'' + "\n" +
                ", routeLongName='" + routeLongName + '\'' + "\n" +
                ", boardRule='" + boardRule + '\'' + "\n" +
                ", alightRule='" + alightRule + '\'' + "\n" +
                ", rentedBike=" + rentedBike + "\n" +
                ", duration=" + duration + "\n" +
                ", transitLeg=" + transitLeg + "\n" +
                '}';
    }
}
