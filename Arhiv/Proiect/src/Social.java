import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class Social {
    private JLabel jFriendsCount;
    private JPanel jMyFriends;
    private JTextField jSearchFriendsField;
    private JPanel jFriendList;
    private JComboBox jFriendsSuggestion;
    private JButton addButton;
    private JPanel Social;
    private Consumer logged;

    Social(Consumer logged){
        this.logged = logged;
        setFriends();

        jSearchFriendsField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setFriendsSuggestions(jSearchFriendsField.getText());
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    Consumer friend = Application.getInstance().getConsumer(
                            Objects.requireNonNull(jFriendsSuggestion.getSelectedItem()).toString().substring(
                                    jFriendsSuggestion.getSelectedItem().toString().lastIndexOf(' ')+1
                            )
                    );
                    if (friend != null){
                        logged.add(friend);
                        setFriends();
                    }
                } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(Social, "No such friend found");
                }
            }
        });
    }

    public JPanel getSocial() {
        return Social;
    }

    private void setFriendsSuggestions(String name){
        jFriendsSuggestion.removeAllItems();
        for (Consumer consumer: Application.getInstance().getConsumers()) {
            if (consumer.resume.information.name.contains(name)){
                jFriendsSuggestion.addItem(consumer.resume.information.name + " ; "+consumer.resume.information.getEmail());
            }
        }
    }

    private void setFriends(){
        jFriendsCount.setText("I have "+logged.getSocial().size() + " friends:");
        jFriendList.removeAll();

        JPanel myFriends = new JPanel();
        myFriends.setLayout(new BoxLayout(myFriends,BoxLayout.Y_AXIS));
        for (Consumer friend : logged.getSocial()) {
            JPanel friendRecord = new JPanel();
            friendRecord.setLayout(new BoxLayout(friendRecord,BoxLayout.Y_AXIS));

            JPanel firstLine = new JPanel();
            firstLine.add(new JLabel(friend.resume.information.name));
            firstLine.add(new JLabel(friend.resume.information.getPhone()));
            firstLine.add(new JLabel(friend.resume.information.getEmail()));
            friendRecord.add(firstLine);

            JPanel secondLine = new JPanel();
            secondLine.add(new RemoveFriendButton(logged,friend));
            secondLine.add(new SendMessageButton(logged, friend));
            friendRecord.add(secondLine);

            myFriends.add(friendRecord);

        }

        jFriendList.add(myFriends);
    }

    class RemoveFriendButton extends JButton{
        Consumer friend;

        RemoveFriendButton(Consumer logged, Consumer friend){
            this.friend = friend;
            this.setText("Remove friend");
            addActionListener(actionEvent -> {
                System.out.println("logged "+logged.resume.information.name+" removes "+friend.resume.information.name);
                logged.remove(friend);
                setFriends();
            });
        }
    }

    static class SendMessageButton extends JButton{

        SendMessageButton(Consumer logged, Consumer friend){
            this.setText("Send message");
            this.addActionListener(actionEvent -> {
                JFrame sendMessage = new JFrame("Send message to "+friend.resume.information.name);
                sendMessage.setResizable(false);

                sendMessage.setSize(600,210);
                JPanel textPanel = new JPanel();
                JTextArea messageArea = new JTextArea(10,45);
                messageArea.setLineWrap(true);
                textPanel.add(messageArea);

                JPanel buttons = new JPanel();
                buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
                JButton send = new JButton("Send");
                send.setMinimumSize(new Dimension(80,80));
                send.setMaximumSize(new Dimension(80,80));
                send.setPreferredSize(new Dimension(80,80));
                send.addActionListener(actionEvent12 -> {
                    if (!messageArea.getText().isEmpty())
                        friend.sendNotification("You've got a message from "+logged.resume.information.name+":\n"+messageArea.getText());
                    sendMessage.setVisible(false);
                });
                buttons.add(send);

                JButton discard = new JButton("Discard");
                discard.setMinimumSize(new Dimension(80,80));
                discard.setMaximumSize(new Dimension(80,80));
                discard.setPreferredSize(new Dimension(80,80));
                discard.addActionListener(actionEvent1 -> sendMessage.setVisible(false));
                buttons.add(discard);

                //textPanel.add(new JButton());
                textPanel.add(buttons);
                sendMessage.add(textPanel);

                sendMessage.setVisible(true);
            });
        }
    }
}
