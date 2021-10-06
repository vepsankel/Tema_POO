import javax.swing.*;
import java.awt.*;

public class MyWorker {
    private JPanel jWorker;
    private JLabel jName;
    private JButton fireButton;
    private JTextField jSalaryTextField;
    private JLabel jSalary;
    private JButton jTransfer;
    private JComboBox comboBox1;

    MyWorker(Employee employee, MyCompanyDepartment myCompanyDepartment){
        jName.setText(employee.resume.information.name);
        jSalaryTextField.setText(String.valueOf(employee.salary));

        System.out.println(employee.resume.information.name);
        System.out.println(employee.getCurrentCompany());

        for (Department department :
                Application.getInstance().getCompany(employee.getCurrentCompany()).getDepartments())
        {
            if (!myCompanyDepartment.department.name.equals(department.name))
                comboBox1.addItem(department.name);
        }

        fireButton.addActionListener(actionEvent -> {
            fireButton.setEnabled(false);
            System.out.println("Remove pressed on "+employee.resume.information.name);
            System.out.println(employee.getCurrentCompany());
            Application.getInstance().getCompany(employee.getCurrentCompany()).remove(employee);
            myCompanyDepartment.myCompany.setDepartments();
            fireButton.setEnabled(true);
        });
        jTransfer.addActionListener(actionEvent -> {
            if (comboBox1.getSelectedItem() != null){
                Company employeesCompany = Application.getInstance().getCompany(employee.getCurrentCompany());
                employeesCompany.move(employee, employeesCompany.getDepartment((String) comboBox1.getSelectedItem()));
            }
            myCompanyDepartment.myCompany.setDepartments();
        });
    }

    JPanel getjWorker() {
        jWorker.setMaximumSize(new Dimension(1800, 40));
        return jWorker;
    }
}
