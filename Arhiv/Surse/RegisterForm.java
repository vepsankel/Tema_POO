import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegisterForm extends JFrame {
    private JPanel panel1;
    private JTextField jFullName;
    private JTextField jDateOfBirth;
    private JTextField jGenre;
    private JTextField jPhone;
    private JTextField jEmail;
    private JButton restorePasswdButton;
    private JCheckBox iAmEmployedCheckBox;
    private JButton backButton;
    private JCheckBox jIAmManagerAndCheckBox;
    private JTextField jMyCompanyTextField;
    private JComboBox jCompaniesComboBox;
    private JButton registerButton;
    private JLabel ErrorsLabel;
    private JLabel RestorePasswordLabel;
    private JPasswordField jPassword;
    private JPasswordField jRepeatPassword;
    private JButton jSearchCompaniesButton;
    private JTextField jSearchCompaniesTextField;
    private JComboBox jDepartments;
    private JTextField jPosition;
    private JTextField jStartWorkingDate;
    private JTextField jStartManagingDate;
    static JFrame registerForm;

    public RegisterForm(LogInForm logInForm) {
        registerForm = new JFrame("Regiter");
        registerForm.addWindowListener(new WindowListener());

        registerForm.setSize(800,520);
        registerForm.add(panel1);
        registerForm.setVisible(true);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                registerForm.setVisible(false);
                LogInForm.show();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean correct = verifyFields();
                if (correct) {
                    if (!sendRegistrationRequest()){
                        setError("Could not register! User Already exists!");
                    } else {
                        registerForm.setVisible(false);
                        logInForm.LogIn(jEmail.getText(), jPassword.getText());
                    }

                } else  {
                    setError("Incorrect dates");
                }
            }
        });
        RestorePasswordLabel.setVisible(false);
        restorePasswdButton.setVisible(false);
        jCompaniesComboBox.setEnabled(false);
        jSearchCompaniesButton.setEnabled(false);
        jSearchCompaniesTextField.setEnabled(false);
        jMyCompanyTextField.setEnabled(false);
        jStartManagingDate.setEnabled(false);
        jDepartments.setEnabled(false);
        jPosition.setEnabled(false);
        jStartWorkingDate.setEnabled(false);

        jSearchCompaniesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setCompanies(jSearchCompaniesTextField.getText());
            }
        });

        iAmEmployedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (!iAmEmployedCheckBox.isSelected()){
                    jCompaniesComboBox.removeAllItems();
                    jCompaniesComboBox.setEnabled(false);
                    jSearchCompaniesTextField.setEnabled(false);
                    jSearchCompaniesButton.setEnabled(false);
                    jDepartments.setEnabled(false);
                    jStartWorkingDate.setEnabled(false);
                    jPosition.setEnabled(false);
                } else {
                    jIAmManagerAndCheckBox.setSelected(false);
                    jMyCompanyTextField.setEnabled(false);
                    jStartManagingDate.setEnabled(false);
                    setCompanies();
                    jCompaniesComboBox.setEnabled(true);
                    jSearchCompaniesTextField.setEnabled(true);
                    jSearchCompaniesButton.setEnabled(true);
                    jDepartments.setEnabled(true);
                    jStartWorkingDate.setEnabled(true);
                    jPosition.setEnabled(true);
                }
            }
        });
        jIAmManagerAndCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jIAmManagerAndCheckBox.isSelected()){
                    iAmEmployedCheckBox.setSelected(false);
                    jMyCompanyTextField.setEnabled(true);
                    jStartManagingDate.setEnabled(true);
                } else {
                    jMyCompanyTextField.setEnabled(false);
                    jStartManagingDate.setEnabled(false);
                }
            }
        });

        ErrorsLabel.setVisible(false);
        jCompaniesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jDepartments.removeAllItems();
                if (jCompaniesComboBox.getSelectedItem() == null) return;
                for (Department department : Application.getInstance().getCompany(jCompaniesComboBox.getSelectedItem().toString()).getDepartments())
                {
                    jDepartments.addItem(department.name);
                }
            }
        });
    }

    private boolean sendRegistrationRequest() {
        boolean registered = false;
        if (!iAmEmployedCheckBox.isSelected() && !jIAmManagerAndCheckBox.isSelected()){
            registered = Application.getInstance().registerUser(
                    jEmail.getText(),
                    jPassword.getText(),
                    jFullName.getText(),
                    jPhone.getText(),
                    jGenre.getText(),
                    jDateOfBirth.getText()
            );
        }

        if (iAmEmployedCheckBox.isSelected()){
            registered = Application.getInstance().registerEmployee(
                    jCompaniesComboBox.getSelectedItem().toString(),
                    jDepartments.getSelectedItem().toString(),
                    jPosition.getText(),
                    jStartWorkingDate.getText(),
                    jEmail.getText(),
                    jPassword.getText(),
                    jFullName.getText(),
                    jPhone.getText(),
                    jGenre.getText(),
                    jDateOfBirth.getText()
            );
        }

        if (jIAmManagerAndCheckBox.isSelected()){
            registered = Application.getInstance().registerManager(
                    jMyCompanyTextField.getText(),
                    jStartManagingDate.getText(),
                    jEmail.getText(),
                    jPassword.getText(),
                    jFullName.getText(),
                    jPhone.getText(),
                    jGenre.getText(),
                    jDateOfBirth.getText()
            );
        }

        System.out.println("Registered = "+registered);
        return registered;
    }

    private void setCompanies(){
        setCompanies(null);
    }

    private void setCompanies(String search) {
        //ArrayList<Company> companies = Application.getCurrentApplication().getCompanies();
        //String[] companies = new String[Application.getCurrentApplication().getCompanies().size()];

        jCompaniesComboBox.removeAllItems();

        for (Company company: Application.getInstance().getCompanies()) {
            System.out.println(company.name);

            JLabel newCompany = new JLabel(company.name);
            newCompany.setVisible(true);

            if (search != null && company.name.contains(search)){
                jCompaniesComboBox.addItem(company.name);
            } else if (search == null ){
                jCompaniesComboBox.addItem(company.name);
            }
        }
    }

    boolean verifyFields(){
        boolean rezult = true;

        setError(null);

        if (!isGoodName(jFullName.getText())){
            if (rezult) setError("Name is not optional");
            rezult = false;
            jFullName.setBackground(new Color(255, 204, 204));
        } else {
            jFullName.setBackground(Color.WHITE);
        }

        if (jGenre.getText().equals("")){
            if (rezult) setError("Genre is not optional");
            rezult = false;
            jGenre.setBackground(new Color(255, 204, 204));
        } else {
            jGenre.setBackground(Color.WHITE);
        }

        if (!isGoodDateOfBirth(jDateOfBirth.getText())){
            if (rezult) setError("Date of birth is not optional");
            rezult = false;
            jDateOfBirth.setBackground(new Color(255, 204, 204));
        } else {
            jDateOfBirth.setBackground(Color.WHITE);
        }

        if (!isGoodEmail(jEmail.getText())){
            if (rezult) setError("Email is not optional");
            rezult = false;
            jEmail.setBackground(new Color(255, 204, 204));
        } else {
            jEmail.setBackground(Color.WHITE);
        }

        if (!areGoodPasswords(jPassword.getText(), jRepeatPassword.getText())){
            jPassword.setBackground(new Color(255, 204, 204));
            jRepeatPassword.setBackground(new Color(255, 204, 204));
            rezult = false;
        } else {
            jPassword.setBackground(Color.WHITE);
            jRepeatPassword.setBackground(Color.WHITE);
        }

        if (jIAmManagerAndCheckBox.isSelected()){
            if (jMyCompanyTextField.getText().isEmpty()){
                setError("You cannot manage that company");
                rezult = false;
            }

            if (!InformationChecker.isGoodDate(jStartManagingDate.getText())){
                setError("Managing date is incorrect");
                rezult = false;
            }
        }

        if (iAmEmployedCheckBox.isSelected()){
            if (jCompaniesComboBox.getSelectedItem()==null){
                setError("You must select your company");
                rezult = false;
            }

            if (!isGoodDateOfBirth(jStartWorkingDate.getText())){
                setError("Date of your working experience is incorrect");
                rezult = false;
            }

            if (jDepartments.getSelectedItem() == null){
                setError("This department does not exist");
                rezult = false;
            }

            if (jPosition.getText().equals("")){
                setError("You have to indicate your position");
                rezult = false;
            }
        }

        return rezult;
    }

    private boolean areGoodPasswords(String pswd, String rppswd) {
        if (pswd.length() < 6 ){
            setError("Password must be at least 6 characters long");
            return false;
        }

        if (!pswd.equals(rppswd)){
            setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean isGoodEmail(String email) {
        boolean result = true;

        final Pattern rfc2822 = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
        );

        if (!rfc2822.matcher(email).matches()) {
            setError("Invalid address");
            return false;
        }

        return result;
    }

    boolean isGoodDateOfBirth(String text) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try{
            LocalDate date = LocalDate.parse(text, dtf);
            if (date.isBefore(LocalDate.of(1900,1,1)) || date.isAfter(LocalDate.now())){
                setError("Enter valid date");
                return false;
            }
        } catch (DateTimeParseException e){
            setError("Date is not in dd.mm.yyyy formate");
            return false;
        }

        return true;
    }

    private boolean isGoodName(String fullName) {
        boolean rezult = true;
        if (fullName.equals("")){
            return false;
        }
        return rezult;
    }

    void setError(String message){

        if (message == null){
            ErrorsLabel.setText("Fill all fields before continuing");
            ErrorsLabel.setForeground(Color.BLACK);
            return;
        }

        if (ErrorsLabel.getForeground() == Color.RED) return;

        ErrorsLabel.setText(message);
        ErrorsLabel.setForeground(Color.RED);
        ErrorsLabel.setVisible(true);
    }



    public JPanel getPanel1() {
        return panel1;
    }
}
