import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeWork {
    private JPanel panel1;
    private JButton jIFire;
    private JLabel jText;
    private JPanel jEmployeeWork;
    Employee employee;

    EmployeeWork(Employee employee){
        this.employee = employee;
        jText.setText("I am currently working at "+employee.getCurrentCompany()+" company. My salary is "+employee.getSalary());
        jIFire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (employee.getCurrentCompany() != null)
                    Application.getInstance().getCompany(employee.getCurrentCompany()).remove(employee);
                LogInForm.close();
            }
        });
    }

    public JPanel getjEmployeeWork() {
        return jEmployeeWork;
    }

}
