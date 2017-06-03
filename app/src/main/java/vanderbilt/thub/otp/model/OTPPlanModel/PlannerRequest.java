package vanderbilt.thub.otp.model.OTPPlanModel;

/**
 * Created by chinmaya on 5/16/2017.
 */
public class PlannerRequest {

    private GenericLocation from;
    private GenericLocation to;
    private String modes;

    public PlannerRequest() {}

    public GenericLocation getFrom() {
        return from;
    }

    public void setFrom(GenericLocation from) {
        this.from = from;
    }

    public GenericLocation getTo() {
        return to;
    }

    public void setTo(GenericLocation to) {
        this.to = to;
    }

    public String getModes() {
        return modes;
    }

    public void setModes(String modes) {
        this.modes = modes;
    }
}
