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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A TripPlan is a set of ways to get from point A to point B at time T.
 */
public class TripPlan {

    /**  The time and date of travel */
    private long date = 0;
    
    /** The origin */
    private Place from = null;
    
    /** The destination */
    private Place to = null;

    /** A list of possible itineraries */
    private List<Itinerary> itineraries = new ArrayList<Itinerary>();

    public TripPlan() { }
    
    public TripPlan(Place from, Place to, Date date) {
        this.from = from;
        this.to = to;
        this.date = new Date().getTime();
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    public void addItinerary(Itinerary itinerary) {
        this.itineraries.add(itinerary);
    }

    @Override
    public String toString() {
        return "TripPlan{" + "\n\n" +
                "date=" + date + "\n" +
                ", from=" + from + "\n" +
                ", to=" + to + "\n" +
                ", itineraries=" + itineraries + "\n" +
                '}';
    }
}
