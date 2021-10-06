import javax.swing.*;

public class RecruiterForm {

    Recruiter logged;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JFrame frame;

    RecruiterForm(Recruiter logged){
        this.logged = logged;

        frame = new JFrame(logged.resume.information.name);
        frame.setSize(900,800);
        tabbedPane1.removeAll();
        tabbedPane1.add("Notifications", new Notifications(logged).getjNotifications());
        tabbedPane1.add("Personal Information", new PersonalInformation(logged).getjPersonalInformation());
        tabbedPane1.add("Social", new Social(logged).getSocial());
        tabbedPane1.add("Recruiter work", new RecruiterWork((Recruiter) logged).getjWork());
        frame.add(tabbedPane1);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener());

        //tabbedPane1.add(new PersonalInformation(logged));

        frame.revalidate();
    }
}
