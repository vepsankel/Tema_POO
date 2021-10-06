import javax.swing.*;

public class Notifications {
    JPanel panel1;
    private JLabel jNotificationMessage;
    private JPanel jNotifications;
    private JPanel jNotificationsTab;
    Consumer consumer;

    Notifications(Consumer consumer){
        this.consumer = consumer;
        jNotifications.setLayout(new BoxLayout(jNotifications, BoxLayout.Y_AXIS));
        setNotifications();
    }

    void setNotifications(){
        jNotificationMessage.setText("I have "+consumer.getNotifications().size()+" notifications");
        jNotifications.removeAll();
        for (String notification : consumer.getNotifications()) {
            jNotifications.add(new Notification(this, notification).getNotification());
        }
        jNotifications.repaint();
    }

    JPanel getjNotifications(){
        return jNotificationsTab;
    }
}
