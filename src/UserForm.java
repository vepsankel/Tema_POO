import javax.swing.*;

public class UserForm {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField jFullName;
    private JTextField jDateOfBirth;
    private JTextField jGenre;
    private JTextField jPhone;
    private JTextField jEmail;
    private JPanel jExperiences;

    UserForm(Consumer logged) {

        JFrame frame = new JFrame(logged.resume.information.name);
        frame.setSize(900,800);
        tabbedPane1.removeAll();
        tabbedPane1.add("Notifications", new Notifications(logged).getjNotifications());
        tabbedPane1.add("Personal Information", new PersonalInformation(logged).getjPersonalInformation());
        tabbedPane1.add("Social", new Social(logged).getSocial());
        tabbedPane1.add("User work", new UserWork((User) logged).getWork());
        frame.add(tabbedPane1);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener());

        //tabbedPane1.add(new PersonalInformation(logged));

        frame.revalidate();

    }
}
