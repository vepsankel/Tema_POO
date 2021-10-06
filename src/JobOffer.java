import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JobOffer {
    private JLabel jCompanyName;
    private JLabel jNoPos;
    private JLabel jPosition;
    private JLabel jSalary;
    private JButton applyButton;
    private JPanel jJobOffer;
    private JLabel jMessage;
    Job job;
    User user;
    UserWork userWork;

    public JobOffer(UserWork userWork, User user, Job job) {
        this.userWork = userWork;
        this.user = user;
        this.job = job;
        jCompanyName.setText(job.companyName);
        jNoPos.setText(String.valueOf(job.noPositions));
        jPosition.setText(String.valueOf(job.name));
        jSalary.setText(String.valueOf(job.salary));
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean good = true;
                if (Application.getInstance().getUser(user.resume.information.getEmail())==null) good = false;
                if (!user.appliedJobs.contains(job) && Application.getInstance().getCompany(job.companyName).getDepartment(job.department).getJob(job.name) != null)
                    if (good) job.apply(user);

                userWork.setOffers();
                userWork.setInterestedCompanies();
                userWork.setAlreadyApplied();
            }
        });
    }

    JPanel getOffer(){
        return jJobOffer;
    }

    JPanel getIndirectOffer(){
        boolean messageStarted = false;
        jMessage.setText("To apply at this job you need ");
        if (job.getExperienceConstraint_min() != null){
            if (!messageStarted) jMessage.setText("To apply at this job you need ");
            jMessage.setText(jMessage.getText() + job.getExperienceConstraint_min() + " years of experience");
        }
        if (job.getGradeConstraint_min() != null && job.getGradeConstraint_min() != 0){
            if (!messageStarted) jMessage.setText("To apply at this job you need ");
            jMessage.setText(jMessage.getText() +", grade "+ job.getGradeConstraint_min());
        }
        if (!messageStarted) jMessage.setText("To apply at this job you need ");
        jMessage.setText(jMessage.getText() + " to be in ["+job.getAbsolvationConstraint_min()
                +";"+job.getAbsolvationConstraint_max()+"].");
        applyButton.setEnabled(false);
        return jJobOffer;
    }
}
