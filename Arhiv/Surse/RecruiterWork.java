import javax.swing.*;
import java.awt.*;

public class RecruiterWork {
    private JPanel panel1;
    private JPanel jNeedEvaluations;
    private JPanel jMyWork;
    private JPanel jWork;
    private JLabel jRating;
    Recruiter logged;

    RecruiterWork(Recruiter logged){
        this.logged = logged;
        jWork.setLayout(new BoxLayout(jWork,BoxLayout.Y_AXIS));
        jMyWork.add(new EmployeeWork(logged).getjEmployeeWork());
        jRating.setText("My rating is "+logged.getRating());
        jNeedEvaluations.setLayout(new BoxLayout(jNeedEvaluations,BoxLayout.Y_AXIS));
        setEvaluationRequests();
    }

    public JPanel getjWork() {
        return jWork;
    }

    void setEvaluationRequests(){
        jNeedEvaluations.removeAll();
        for (Recruiter.ToEvaluate request: logged.getWaitingForEvaluation()) {
            jNeedEvaluations.add(new EvaluationRequest(this, request).getjRequest());
        }
        jRating.setText("My rating is "+logged.getRating());
    }
}
