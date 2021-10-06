import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EmployeeForm {
    private JTabbedPane tabbedPane1;

    public JScrollPane getjPersonalInformation() {
        return jPersonalInformation;
    }

    private JScrollPane jPersonalInformation;
    private Employee logged;
    JFrame frame;

    public EmployeeForm(Employee logged) {
        this.logged = logged;

        frame = new JFrame(logged.resume.information.name);
        frame.setSize(900,800);
        tabbedPane1.removeAll();
        tabbedPane1.add("Personal Information", new PersonalInformation(logged).getjPersonalInformation());
        tabbedPane1.add("Work",new EmployeeWork(logged).getjEmployeeWork());
        tabbedPane1.add("Social", new Social(logged).getSocial());
        tabbedPane1.add("Notifications",new Notifications(logged).getjNotifications());
        frame.add(tabbedPane1);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener());

    }


}
