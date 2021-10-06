import javax.swing.*;
import javax.swing.border.Border;

public class MyJob {
    JPanel panel1;
    public JTextField jName;
    JButton editButton;
    JTextField jSalary;
    JTextField jGradeRestraint_min;
    JTextField jGradeRestraint_max;
    JTextField jExperienceRestraint_min;
    JTextField jExperienceRestraint_max;
    JTextField jAbsolvationRestraint_min;
    JTextField jAbsolvationRestraint_max;
    JPanel jJob;
    JTextField jNoPos;
    //JButton availableButton;
    private JButton jRemoveButton;
    private JCheckBox jAvailable;
    private Job job;
    private String oldName;

    private MyJob(MyCompanyDepartment myCompanyDepartment, JFrame newJobFrame){
        componentsSetEditable(true);
        editButton.setText("Save");
        editButton.addActionListener(actionEvent -> {

            if (jName.isEditable()){

                editButton.setText("Edit");
                componentsSetEditable(false);
                componentSetBorder(null);

                boolean correct = correctInputs();

                if (correct && myCompanyDepartment.department.getJob(jName.getText()) != null){
                    Job newJob = new Job(
                            jName.getText(),
                            myCompanyDepartment.company.name,
                            myCompanyDepartment.department.name,
                            Integer.parseInt(jNoPos.getText()),
                            Double.parseDouble(jSalary.getText())
                    );

                    newJob.setExperienceConstraint(
                            new Constraint<>(
                                    parseIntOrNull(jExperienceRestraint_min.getText()),
                                    parseIntOrNull(jExperienceRestraint_max.getText())
                            )
                    );
                    newJob.setAbsolvationConstraint(
                            new Constraint<>(
                                    parseIntOrNull(jAbsolvationRestraint_min.getText()),
                                    parseIntOrNull(jAbsolvationRestraint_max.getText())
                            )
                    );
                    newJob.setGradeConstraint(
                            new Constraint<>(
                                    parseDoubleOrNull(jGradeRestraint_min.getText()),
                                    parseDoubleOrNull(jGradeRestraint_max.getText())
                            )
                    );

                    newJob.isOpen = jAvailable.isSelected() && newJob.noPositions != 0;

                    myCompanyDepartment.addNewJob(newJob);
                    myCompanyDepartment.setJobs();
                    newJobFrame.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(panel1,"Job already exists ot dates are incorrect");
                }

            } else {
                editButton.setText("Save");

                componentsSetEditable(true);
                Border standartBorder = new JTextField().getBorder();
                componentSetBorder(standartBorder);
                myCompanyDepartment.setJobs();
            }
        });

    }

    private Integer parseIntOrNull(String string){
        Integer rez;
        try {
            rez = Integer.parseInt(string);
        } catch (NumberFormatException e){
            return null;
        }

        if (string.equals("-")) return null;

        return rez;
    }

    private Double parseDoubleOrNull(String string){
        Double rez;
        try {
            rez = Double.parseDouble(string);
        } catch (NumberFormatException e){
            return null;
        }

        return rez;
    }

    MyJob(Job job, MyCompanyDepartment myCompanyDepartment){
        this.job = job;
        this.oldName = job.name;

        jName.setText(job.name);
        jSalary.setText(String.valueOf(job.salary));

        jAbsolvationRestraint_max.setText(String.valueOf(job.getAbsolvationConstraint_max()).equals("null") ? "-" :
                String.valueOf(job.getAbsolvationConstraint_max()));
        jAbsolvationRestraint_min.setText(String.valueOf(job.getAbsolvationConstraint_min()).equals("null") ? "-" :
                String.valueOf(job.getAbsolvationConstraint_min()));

        jExperienceRestraint_max.setText(String.valueOf(job.getExperienceConstraint_max()).equals("null") ? "-" :
                String.valueOf(job.getExperienceConstraint_max()));
        jExperienceRestraint_min.setText(String.valueOf(job.getExperienceConstraint_min()).equals("null") ? "-" :
                String.valueOf(job.getExperienceConstraint_min()));

        jGradeRestraint_max.setText(String.valueOf(job.getGradeConstraint_max()).equals("null") ? "-" :
                String.valueOf(job.getGradeConstraint_max()));
        jGradeRestraint_min.setText(String.valueOf(job.getGradeConstraint_min()).equals("null") ? "-" :
                String.valueOf(job.getGradeConstraint_min()));

        jNoPos.setText(String.valueOf(job.noPositions));
        jName.setBorder(null);

        editButton.addActionListener(actionEvent -> {
            if (jName.isEditable()){
                editButton.setText("Edit");

                componentsSetEditable(false);
                componentSetBorder(null);

                boolean correct = correctInputs();

                if (correct &&
                        (jName.getText().equals(oldName) || myCompanyDepartment.department.getJob(jName.getText()) == null)
                        ){
                    job.noPositions = Integer.parseInt(jNoPos.getText());
                    job.name = jName.getText();
                    job.isOpen = jAvailable.isSelected();
                    job.salary = Double.parseDouble(jSalary.getText());

                    job.setExperienceConstraint(
                            new Constraint<>(
                                    parseIntOrNull(jExperienceRestraint_min.getText()),
                                    parseIntOrNull(jExperienceRestraint_max.getText())
                            )
                    );
                    job.setAbsolvationConstraint(
                            new Constraint<>(
                                    parseIntOrNull(jAbsolvationRestraint_min.getText()),
                                    parseIntOrNull(jAbsolvationRestraint_max.getText())
                            )
                    );
                    job.setGradeConstraint(
                            new Constraint<>(
                                    parseDoubleOrNull(jGradeRestraint_min.getText()),
                                    parseDoubleOrNull(jGradeRestraint_max.getText())
                            )
                    );
                } else {
                    JOptionPane.showMessageDialog(panel1,"Job already exists");
                }

                myCompanyDepartment.setJobs();
            } else {
                editButton.setText("Save");

                componentsSetEditable(true);
                Border standartBorder = new JTextField().getBorder();
                componentSetBorder(standartBorder);
            }
        });


        jRemoveButton.addActionListener(actionEvent -> {

            System.out.println("REMOVE BUTTON");
            System.out.println(myCompanyDepartment.myCompany.company.getDepartments());
            System.out.println((myCompanyDepartment.myCompany.company.contains(myCompanyDepartment.department)));
            if (myCompanyDepartment.myCompany.company.contains(myCompanyDepartment.department)){
                myCompanyDepartment.department.remove(job);
            }

            myCompanyDepartment.setJobs();
        });
    }

    private void componentsSetEditable(boolean var){
        jNoPos.setEditable(var);
        jName.setEditable(var);
        jSalary.setEditable(var);
        jGradeRestraint_min.setEditable(var);
        jGradeRestraint_max.setEditable(var);
        jExperienceRestraint_min.setEditable(var);
        jExperienceRestraint_max.setEditable(var);
        jAbsolvationRestraint_min.setEditable(var);
        jAbsolvationRestraint_max.setEditable(var);
        jAvailable.setEnabled(var);
    }

    private void componentSetBorder(Border border){
        jNoPos.setBorder(border);
        jSalary.setBorder(border);
        jGradeRestraint_min.setBorder(border);
        jGradeRestraint_max.setBorder(border);
        jExperienceRestraint_min.setBorder(border);
        jExperienceRestraint_max.setBorder(border);
        jAbsolvationRestraint_min.setBorder(border);
        jAbsolvationRestraint_max.setBorder(border);
    }

    private boolean correctInputs(){
        try{
            Double.valueOf(jSalary.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(jName, "Wrong salary modification!");
            return false;
        }

        try{
            if (!jGradeRestraint_min.getText().equals("-"))
                Double.valueOf(jGradeRestraint_min.getText());
            if (!jGradeRestraint_max.getText().equals("-"))
            Double.valueOf(jGradeRestraint_max.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(jName, "Wrong grade constraint modification!");
            return false;
        }

        try{
            if (!jExperienceRestraint_min.getText().equals("-"))
                Integer.valueOf(jExperienceRestraint_min.getText());
            if (!jExperienceRestraint_max.getText().equals("-"))
                Integer.valueOf(jExperienceRestraint_max.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(jName, "Wrong experience constraint modification!");
            return false;
        }

        try{
            if (!jAbsolvationRestraint_min.getText().equals("-"))
                Integer.valueOf(jAbsolvationRestraint_min.getText());
            if (!jAbsolvationRestraint_max.getText().equals("-"))
                Integer.valueOf(jAbsolvationRestraint_max.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(jName, "Wrong absolvation constraint modification!");
            return false;
        }

        try{
            Integer.parseInt(jNoPos.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(jName, "Wrong noPos modification!");
        }

        return true;
    }

    JPanel getjJob() {
        return jJob;
    }

    static void newJob(MyCompanyDepartment myCompanyDepartment){
        JFrame newJob = new JFrame("Enter a new job:");
        MyJob myJob = new MyJob(myCompanyDepartment, newJob);
        JPanel jJob = myJob.jJob;
        newJob.add(jJob);
        newJob.setSize(1000,300);
        newJob.setVisible(true);
    }
}
