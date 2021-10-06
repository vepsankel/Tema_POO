import javax.swing.*;
import java.awt.*;

public class Notification {
    private JPanel panel1;
    private JButton jRead;
    private JTextArea jContent;
    private JPanel jNotification;

    Notification(Notifications notifications, String text){
        jContent.setText(text);

        jRead.addActionListener(actionEvent -> {
            notifications.consumer.removeNotifications(text);
            notifications.setNotifications();
        });

        jContent.setOpaque(false);
    }

    JPanel getNotification() {
        jNotification.setMaximumSize(new Dimension(1800, 70));
        return jNotification;
    }
}
