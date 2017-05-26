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


import java.util.ArrayList;
import java.util.List;

/**
 * An Itinerary is one complete way of getting from the start location to the end location.
 */
public class Itinerary {

    /**
     * Duration of the trip on this itinerary, in seconds.
     */
    private Long duration = 0L;

    /**
     * Time that the trip departs.
     */
    private long startTime = 0;
    /**
     * Time that the trip arrives.
     */
    private long endTime = 0;

    /**
     * How much time is spent walking, in seconds.
     */
    private long walkTime = 0;
    /**
     * How much time is spent on transit, in seconds.
     */
    private long transitTime = 0;
    /**
     * How much time is spent waiting for transit to arrive, in seconds.
     */
    private long waitingTime = 0;

    /**
     * How far the user has to walk, in meters.
     */
    private Double walkDistance = 0.0;
    
    /**
     * Indicates that the walk limit distance has been exceeded for this itinerary when true.
     */
    private boolean walkLimitExceeded = false;

    /**
     * How much elevation is lost, in total, over the course of the trip, in meters. As an example,
     * a trip that went from the top of Mount Everest straight down to sea level, then back up K2,
     * then back down again would have an elevationLost of Everest + K2.
     */
    private Double elevationLost = 0.0;
    /**
     * How much elevation is gained, in total, over the course of the trip, in meters. See
     * elevationLost.
     */
    private Double elevationGained = 0.0;

    /**
     * The number of transfers this trip has.
     */
    private Integer transfers = 0;

    /*
     * The cost of this trip
     */
//    public Fare fare = new Fare();

    /**
     * A list of Legs. Each Leg is either a walking (cycling, car) portion of the trip, or a transit
     * trip on a particular vehicle. So a trip where the use walks to the Q train, transfers to the
     * 6, then walks to their destination, has four legs.
     */
    private List<Leg> legs = new ArrayList<Leg>();

    /**
     * This itinerary has a greater slope than the user requested (but there are no possible
     * itineraries with a good slope).
     */
    private boolean tooSloped = false;


    public Itinerary() {
    }


    public Itinerary(Long duration, long startTime, long endTime, long walkTime, long transitTime, long waitingTime, Double walkDistance, boolean walkLimitExceeded, Double elevationLost, Double elevationGained, Integer transfers, List<Leg> legs, boolean tooSloped) {
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.walkTime = walkTime;
        this.transitTime = transitTime;
        this.waitingTime = waitingTime;
        this.walkDistance = walkDistance;
        this.walkLimitExceeded = walkLimitExceeded;
        this.elevationLost = elevationLost;
        this.elevationGained = elevationGained;
        this.transfers = transfers;
        this.legs = legs;
        this.tooSloped = tooSloped;
    }

    /**
     * adds leg to array list
     * @param leg
     */
    public void addLeg(Leg leg) {
        if(leg != null)
            legs.add(leg);
    }

    /**
     * remove the leg from the list of legs
     * @param leg object to be removed
     */
    public void removeLeg(Leg leg) {
        if(leg != null) {
            legs.remove(leg);
        }
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public long getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(long walkTime) {
        this.walkTime = walkTime;
    }

    public long getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(long transitTime) {
        this.transitTime = transitTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Double getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(Double walkDistance) {
        this.walkDistance = walkDistance;
    }

    public boolean isWalkLimitExceeded() {
        return walkLimitExceeded;
    }

    public void setWalkLimitExceeded(boolean walkLimitExceeded) {
        this.walkLimitExceeded = walkLimitExceeded;
    }

    public Double getElevationLost() {
        return elevationLost;
    }

    public void setElevationLost(Double elevationLost) {
        this.elevationLost = elevationLost;
    }

    public Double getElevationGained() {
        return elevationGained;
    }

    public void setElevationGained(Double elevationGained) {
        this.elevationGained = elevationGained;
    }

    public Integer getTransfers() {
        return transfers;
    }

    public void setTransfers(Integer transfers) {
        this.transfers = transfers;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public boolean isTooSloped() {
        return tooSloped;
    }

    public void setTooSloped(boolean tooSloped) {
        this.tooSloped = tooSloped;
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


    @Override
    public String toString() {
        return "Itinerary{" + "\n" +
                "duration=" + duration + "\n" +
                ", startTime=" + startTime + "\n" +
                ", endTime=" + endTime + "\n" +
                ", walkTime=" + walkTime + "\n" +
                ", transitTime=" + transitTime + "\n" +
                ", waitingTime=" + waitingTime + "\n" +
                ", walkDistance=" + walkDistance + "\n" +
                ", walkLimitExceeded=" + walkLimitExceeded + "\n" +
                ", elevationLost=" + elevationLost + "\n" +
                ", elevationGained=" + elevationGained + "\n" +
                ", transfers=" + transfers + "\n" +
                ", legs=" + legs + "\n" +
                ", tooSloped=" + tooSloped + "\n" +
                '}';
    }
}
