package edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Itinerary;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Leg;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TripPlan;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.WalkStep;

import java.lang.reflect.Type;

/**
 * Created by chinmaya on 1/08/2017.
 */
public class TPPlannerUtils {



    public static String getJsonString(TripPlan plan){
        Gson gson = new Gson();
        Type type = new TypeToken<TripPlan>() {}.getType();
        String json = gson.toJson(plan, type);
        return json;
    }


    public static void displayPlan(TripPlan plan){
        System.out.println("=======================================================================================================================================================================================");
        if (plan != null && plan.getItineraries() != null){

            for (int i=0; i < plan.getItineraries().size(); i++){
                Itinerary itinerary = plan.getItineraries().get(i);

                System.out.println("\nItinerary " + (i+1) +":-");
                System.out.println("-------------------------------------");
                System.out.println("Total Duration:- " + itinerary.getDuration());
                System.out.println("Start Time:- " + itinerary.getStartTime());
                System.out.println("End Time:- " + itinerary.getEndTime());
                System.out.println("Walk Time:- " + itinerary.getWalkTime());
                System.out.println("Transit Time:- " + itinerary.getTransitTime());
                System.out.println("Waiting Time:- " + itinerary.getWaitingTime());
                System.out.println("Walking Distance:- " + itinerary.getWalkDistance());
                System.out.println("Total Transfers:- " + itinerary.getTransfers() + "\n");

                for (int j=0; j < itinerary.getLegs().size(); j++){
                    Leg leg = itinerary.getLegs().get(j);

                    System.out.println("\n\t\tLeg " + (j+1) +":-");
                    System.out.println("\t\t-------------------------------------");
                    System.out.println("\t\tStart Time:- " + leg.getStartTime());
                    System.out.println("\t\tEnd Time:- " + leg.getEndTime());
                    System.out.println("\t\tDeparture delay:- " + leg.getDepartureDelay());
                    System.out.println("\t\tArrival delay:- " + leg.getArrivalDelay());
                    System.out.println("\t\tDistance:- " + leg.getDistance());
                    System.out.println("\t\tFrom:- " + leg.getFrom());
                    System.out.println("\t\tTo:- " + leg.getTo());
                    System.out.println("\t\tDuration:- " + leg.getDuration());
                    System.out.println("\t\tMode:- " + leg.getMode());


                    if (leg.getSteps() != null){
                        for (int k =0; k < leg.getSteps().size(); k++){
                            WalkStep step = leg.getSteps().get(k);

                            System.out.println("\n\t\t\t\tStep " + (k+1) +":-");
                            System.out.println("\t\t\t\t-------------------------------------");
                            System.out.println("\t\t\t\tLocation:- " + step.getLat() + "," + step.getLon());
                            System.out.println("\t\t\t\tDistance:- " + step.getDistance());
                            System.out.println("\t\t\t\tRelative Direction:- " + step.getRelativeDirection());
                            System.out.println("\t\t\t\tAbsolute Direction:- " + step.getAbsoluteDirection());
                            System.out.println("\t\t\t\tStreet Name:- " + step.getStreetName() + "\n");

                        }
                    }

                    if (leg.getIntermediateStops() != null){
                        for (int l=0; l < leg.getIntermediateStops().size(); l++){
                            Place place = leg.getIntermediateStops().get(l);
                        }
                    }


                }

                System.out.println("=======================================================================================================================================================================================");


            }
        }

    }



}
