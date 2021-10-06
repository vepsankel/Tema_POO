import javax.swing.*;

public class ManagerForm {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    JFrame form;

    ManagerForm(Manager logged){
        tabbedPane1.removeAll();
        tabbedPane1.add("Personal Information",new PersonalInformation(logged).getjPersonalInformation());
        tabbedPane1.add("My Company",new MyCompany(Application.getInstance().getCompany(logged.companyName)).getjMyCompany());
        tabbedPane1.add("Social",new Social(logged).getSocial());
        tabbedPane1.add("Notifications",new Notifications(logged).getjNotifications());
        form = new JFrame(logged.resume.information.name);
        form.setSize(900,800);
        form.add(tabbedPane1);
        form.setVisible(true);
        form.addWindowListener(new WindowListener());
    }
}
