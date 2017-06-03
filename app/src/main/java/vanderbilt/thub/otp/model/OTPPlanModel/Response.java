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

package vanderbilt.thub.otp.model.OTPPlanModel;



public class Response {


    //TODO Implement this
//    public HashMap<String, String> requestParameters;

    private TripPlan plan;


    //TODO Implement this
//    private PlannerError error = null;
//

    //TODO Implement this
//    /** Debugging and profiling information */
//    public DebugOutput debugOutput = null;

    public Response() {}

    public Response(TripPlan plan) {
        this.plan = plan;
    }

    // NOTE: the order the getter methods below is semi-important, in that Jersey will use the
    // same order for the elements in the JS or XML serialized response. The traditional order
    // is request params, followed by plan, followed by errors.

    /** The actual trip plan. */
    public TripPlan getPlan() {
        return plan;
    }

    public void setPlan(TripPlan plan) {
        this.plan = plan;
    }


    
}