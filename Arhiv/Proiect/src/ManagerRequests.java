import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerRequests {
    private JPanel panel1;
    private JPanel jManagerRequests;
    Manager logged;
    MyCompany myCompany;

    ManagerRequests(MyCompany myCompany, Manager logged){
        this.logged = logged;
        this.myCompany = myCompany;
        jManagerRequests.setLayout(new BoxLayout(jManagerRequests, BoxLayout.Y_AXIS));
    }

    void setRequests(){
        jManagerRequests.removeAll();
        for (Recruiter.Request<Job, Consumer> request : logged.getRequests()) {
            JPanel ManagerRequest = new JPanel();
            ManagerRequest.setLayout(new BoxLayout(ManagerRequest,BoxLayout.Y_AXIS));
            ManagerRequest.add(new JLabel(
                    "User "+request.getValue1().resume.information.name +
                    " was evaluated by "+request.getValue2().resume.information.name +
                    " with resultin score of "+request.getScore()+". She wants to apply to "+request.getKey().name+","
                            +request.getKey().department));
            JButton acceptButton = new JButton("Accept");
            acceptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    logged.accept(request);
                    myCompany.setDepartments();
                }
            });
            JButton declineButton = new JButton("Decline");
            declineButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    logged.remove(request);
                    myCompany.setDepartments();
                }
            });
            jManagerRequests.add(acceptButton);
            jManagerRequests.add(declineButton);
            jManagerRequests.add(ManagerRequest);
        }
    }

    public JPanel getjManagerRequests() {
        setRequests();
        return jManagerRequests;
    }
}
