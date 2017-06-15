package google_places_place_search.model;

import java.util.ArrayList;

/**
 * Created by Anne on 6/14/2017.
 */

public class OpeningHours {

    private Boolean open_now;

    private ArrayList<String> weekday_text;

    public Boolean getOpen_now() {
        return open_now;
    }

    public void setOpen_now(Boolean open_now) {
        this.open_now = open_now;
    }

    public ArrayList<String> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(ArrayList<String> weekday_text) {
        this.weekday_text = weekday_text;
    }
}
