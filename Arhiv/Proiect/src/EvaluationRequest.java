import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluationRequest {
    private JLabel jText;
    private JButton evaluateButton;
    private JButton deleteButton;
    private JPanel jRequest;
    RecruiterWork form;

    EvaluationRequest(RecruiterWork form , Recruiter.ToEvaluate toEvaluate){
        this.form = form;

        jText.setText("Userul "+toEvaluate.user.resume.information.name + " applied to job " + toEvaluate.job.name);
        evaluateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (form.logged.getCurrentCompany() == null) return;
                if (!Application.getInstance().getCompany(form.logged.companyName).contains(form.logged))
                    return;
                form.logged.evaluate(toEvaluate);
                form.setEvaluationRequests();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!form.logged.getCurrentCompany().equals(toEvaluate.job.companyName)){
                    return;
                }
                if (!Application.getInstance().getCompany(form.logged.companyName).contains(form.logged))
                    return;
                form.logged.removeEvaluation(toEvaluate);
                toEvaluate.user.appliedJobs.remove(toEvaluate.job);
                form.setEvaluationRequests();
            }
        });
    }

    public JPanel getjRequest() {
        jRequest.setMaximumSize(new Dimension(1800, 150));
        return jRequest;
    }
}
