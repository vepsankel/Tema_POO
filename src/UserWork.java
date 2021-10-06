import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class UserWork {
    private JTextField textField1;
    private JComboBox comboBox1;
    private JButton addToMyInterestsButton;
    private JPanel jUserWork;
    private JPanel jOffers;
    private JPanel jIndirectOffers;
    private JButton unfollowButton;
    private JLabel jMySubscriptions;
    private JPanel jAlreadyApplied;
    private User user;

    UserWork(User user){
        this.user = user;
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                setCompaniesSuggestions(textField1.getText());
            }
        });
        addToMyInterestsButton.addActionListener(actionEvent -> {
            try{
                if (!user.companiesNames.contains(Objects.requireNonNull(comboBox1.getSelectedItem()).toString())){
                    user.add(comboBox1.getSelectedItem().toString());
                }
            } catch (NullPointerException e){
                JOptionPane.showMessageDialog(jUserWork, "No company selected");
            }
            setInterestedCompanies();
            setOffers();
        });
        jOffers.setLayout(new BoxLayout(jOffers, BoxLayout.Y_AXIS));
        jAlreadyApplied.setLayout( new BoxLayout(jAlreadyApplied, BoxLayout.Y_AXIS));

        System.out.println(jOffers.getComponentCount());

        setOffers();
        setInterestedCompanies();
        setAlreadyApplied();

        System.out.println(jOffers.getComponentCount());
        unfollowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                user.companiesNames.remove(comboBox1.getSelectedItem());
                setOffers();
            }
        });
    }

    private void setCompaniesSuggestions(String suggestions){
        comboBox1.removeAllItems();
        for (Company company: Application.getInstance().getCompanies()) {
            if (company.getName().contains(suggestions)){
                comboBox1.addItem(company.getName());
            }
        }
    }

    void setOffers(){
        jOffers.removeAll();
        jIndirectOffers.removeAll();

        for (String companyName : user.companiesNames) {
            Company company = Application.getInstance().getCompany(companyName);
            if (company != null){
                for (Job job : company.getJobs()) {
                    if (job.meetsRequirements(user) && !user.appliedJobs.contains(job)){
                        jOffers.add(new JobOffer(this, user, job).getOffer());
                    } else if (!user.appliedJobs.contains(job)){
                        jIndirectOffers.add(new JobOffer(this, user, job).getIndirectOffer());
                    }
                }
            }
        }
        jOffers.setSize(500,500);
        jOffers.revalidate();
        jIndirectOffers.revalidate();
        jOffers.repaint();
        jIndirectOffers.repaint();
    }

    void setInterestedCompanies(){
        boolean firstWritten = false;

        for (String company : user.companiesNames) {
            if (!firstWritten){
                jMySubscriptions.setText(company);
                firstWritten = true;
            }
            else
                jMySubscriptions.setText(jMySubscriptions.getText()+", "+company);
        }
    }

    void setAlreadyApplied(){
        jAlreadyApplied.removeAll();

        for (Job job : user.appliedJobs) {
            JLabel text = new JLabel();
            text.setText("Company "+job.companyName + " on position "
                    + job.name + " with salary "+ job.salary);
            jAlreadyApplied.add(text);
        }
    }

    JPanel getWork(){
        return jUserWork;
    }
}
