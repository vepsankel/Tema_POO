import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyCompanyDepartment {
    private JPanel panel1;
    private JTextField jDepartmentNameTextField;
    private JPanel jDepartment;
    private JButton jRemoveDepartment;
    private JButton jTransformDepartment;
    private JButton jEditDepartment;
    private JPanel jMyWorkers;
    private JPanel jJobs;
    private JButton addJobButton;
    private JButton calculateBudget;
    private JLabel jBudget;
    private JPanel jFunctionPanel;
    Department department;
    Company company;
    MyCompany myCompany;

    MyCompanyDepartment(MyCompany myCompany,Department department){
        this.department = department;
        this.company = myCompany.company;
        this.myCompany = myCompany;

        jDepartmentNameTextField.setText(department.name);
        jDepartmentNameTextField.setEditable(false);
        jTransformDepartment.setVisible(false);

        MyCompanyDepartment me = this;

        setWorkers();
        setJobs();
        addJobButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MyJob.newJob(me);
            }
        });
        calculateBudget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jBudget.setText(String.valueOf(department.getTotalSalaryBudget()));
            }
        });
        jRemoveDepartment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (company.contains(department)) company.remove(department);
                myCompany.setDepartments();
            }
        });
    }

    void setWorkers(){
        jMyWorkers.removeAll();
        jMyWorkers.setLayout(null);
        jMyWorkers.setAlignmentX(Component.TOP_ALIGNMENT);
        jMyWorkers.setLayout(new BoxLayout(jMyWorkers,BoxLayout.Y_AXIS));
        for (Employee employee : department.getEmployees()) {
            jMyWorkers.add(new MyWorker(employee, this).getjWorker());
        }
        jMyWorkers.repaint();
        setBudget();
    }

    void setJobs(){
        jJobs.removeAll();
        jJobs.setLayout(new BoxLayout(jJobs, BoxLayout.Y_AXIS));

        System.out.println(department.getAllJobs().size());
        for (Job job : department.getAllJobs()){
            jJobs.add(new MyJob(job, this).getjJob());
        }

        jJobs.repaint();
        jJobs.revalidate();
        setBudget();
    }

    void setBudget(){
        jBudget.setText(String.valueOf(department.getTotalSalaryBudget()));
    }

    public JPanel getjDepartment() {
        return jDepartment;
    }

    public void addNewJob(Job newJob) {
        department.add(newJob);
        setJobs();
    }
}
