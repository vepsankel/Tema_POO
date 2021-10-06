import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;

public class LogInForm {
    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton registerButton;
    private JButton forgotPasswordButton;
    private JButton logInButton;
    public static JFrame frame;

    public LogInForm() {

        panel1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println(e.getKeyChar());
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println((int)e.getKeyChar());
                if (e.getKeyChar() == (char)10){
                    passwordField1.requestFocusInWindow();
                    System.out.println("focus requested");
                }
            }
        });

        passwordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == (char)10){
                    passwordField1.requestFocusInWindow();
                    logIn();
                }
            }
        });


        LogInForm logInForm = this;
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.setVisible(false);
                new RegisterForm(logInForm);
                frame.revalidate();
            }
        });
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logIn();
            }
        });
        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //System.out.println("Button "+forgotPasswordButton.getText());
                if (forgotPasswordButton.getText().equals("Forgot Password")){
                    forgotPasswordButton.setText("That's sad");
                    return;
                }
                if (forgotPasswordButton.getText().equals("That's sad")){
                    forgotPasswordButton.setText("Consider other buttons");
                    return;
                }
                if (forgotPasswordButton.getText().equals("Consider other buttons")){
                    forgotPasswordButton.setText("I'm getting upset");
                    return;
                }
                if (forgotPasswordButton.getText().equals("I'm getting upset")){
                    forgotPasswordButton.setText("Alas, bye!");
                    forgotPasswordButton.setEnabled(false);
                    return;
                }
            }
        });
    }

    void logIn(){
        System.out.println("Trying to log in with "+textField1.getText() + " "+passwordField1.getText());
        Consumer logged = null;

        if (textField1.getText().equals("admin") && passwordField1.getText().equals("admin")){
            new AdminForm();
            frame.setVisible(false);
        } else{
            logged = Application.getInstance().logIn(textField1.getText(),passwordField1.getText());
        }


        if (logged == null){
            System.out.println("Bad");
        } else {
            if (logged instanceof Manager){
                System.out.println("It's a manager!");
                new ManagerForm((Manager)logged);
                frame.setVisible(false);
            } else

            if (logged instanceof Recruiter){
                System.out.println("It's a recruiter!");
                new RecruiterForm((Recruiter)logged);
                frame.setVisible(false);
            } else

            if (logged instanceof Employee){
                System.out.println("It's an employee");
                frame.setVisible(false);
                new EmployeeForm((Employee)logged);
            } else

            if (logged instanceof User){
                System.out.println("It's user");
                frame.setVisible(false);
                new UserForm(logged);
            }
        }
    }

    public void LogIn(String name, String passwd){
        textField1.setText(name);
        passwordField1.setText(passwd);
        logIn();
    }

    static void close(){
        JsonRW.writeJSON();
        System.exit(0);
    }

    public static void main(String[] args) {
        JsonRW.readJSON();
        initialize();
        placeLogInPanel();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner in = new Scanner(System.in);

                while (true){
                    if (in.hasNext()){
                        if (in.next().equals("save")){
                            System.out.println("Saving");
                            JsonRW.writeJSON();
                        }
                    }
                }
            }
        }).start();
    }

    static void initialize(){
        frame = new JFrame("App");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    static void placeLogInPanel(){
        frame.setSize(400,300);
        frame.add(new LogInForm().panel1);
        frame.revalidate();
    }

    static void show(){
        frame.setVisible(true);
        frame.revalidate();
    }
}
