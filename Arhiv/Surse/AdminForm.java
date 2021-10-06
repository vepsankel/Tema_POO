import javax.swing.*;

public class AdminForm {
    private JPanel panel1;
    private JPanel jContent;
    private JTextArea textArea1;

    AdminForm(){
        for (Company company : Application.getInstance().getCompanies()) {
            textArea1.append(company.toString());
        }
        for (User user : Application.getInstance().getListUsers()) {
            textArea1.append(user.toString());
        }

        JFrame admin = new JFrame("Admin");
        admin.setVisible(true);
        admin.setSize(600,800);
        admin.add(panel1);
        admin.addWindowListener(new WindowListener());
    }
}
