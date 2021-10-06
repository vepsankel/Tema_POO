import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyCompany {
    private JPanel jMyCompany;
    private JTabbedPane tabbedPane1;
    private JTextField jMyCompanyName;
    private JButton jAddGepartments;
    Company company;

    MyCompany(Company company){
        this.company = company;

        jMyCompanyName.setText(company.name);

        setDepartments();
        jAddGepartments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame addDepartment = new JFrame("Enter department type");
                addDepartment.setSize(200,80);
                JPanel panel = new JPanel();
                JTextField departmentType = new JTextField(10);
                JButton create = new JButton("Create");
                departmentType.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent keyEvent) {

                    }

                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        if (keyEvent.getKeyChar() == (char) 10 ){
                            createDepartment(departmentType, addDepartment);
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent keyEvent) {

                    }
                });

                create.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        createDepartment(departmentType, addDepartment);
                    }
                });
                panel.add(departmentType);
                panel.add(create);
                addDepartment.setVisible(true);
                addDepartment.add(panel);
            }


        });
    }

    private void createDepartment(JTextField departmentType, JFrame addDepartment){
        String department = departmentType.getText();

        try{
            Department newDepartment = Department.getDepartment(department, company);
            if (company.contains(departmentType.getText())){
                JOptionPane.showMessageDialog(jAddGepartments, "Already exists!");
                addDepartment.setVisible(false);
                return;
            }
            company.add(newDepartment);
            addDepartment.setVisible(false);
        } catch (Exception e){
            JOptionPane.showMessageDialog(jAddGepartments, "Wrong type!");
        }

        setDepartments();
    }

    void setDepartments(){
        int selected = tabbedPane1.getSelectedIndex();
        tabbedPane1.removeAll();
        tabbedPane1.add("Requests", new ManagerRequests(this, company.getManager()).getjManagerRequests());

        for (Department department : company.getDepartments()){
            tabbedPane1.add(department.name, new MyCompanyDepartment(this,department).getjDepartment());
        }

        try{
            tabbedPane1.setSelectedIndex(selected);
        } catch (IndexOutOfBoundsException e){
            tabbedPane1.setSelectedIndex(0);
        }

    }

    public JPanel getjMyCompany() {
        return jMyCompany;
    }
}
