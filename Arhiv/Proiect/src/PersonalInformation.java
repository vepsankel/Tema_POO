import org.json.JSONWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PersonalInformation extends JScrollPane {
    private JScrollPane jPersonalInformation;
    private JPanel jPersonalInformationPage;
    private JPanel jExperiences;
    private JPanel Educations;
    private JButton saveButton;
    private JButton editButton;
    private JTextField jFullName;
    private JTextField jPhone;
    private JTextField jGenre;
    private JTextField jDateOfBirth;
    private JTextField jEmail;
    private JPanel jLanguages;
    private JButton exitButton;
    Consumer logged;

    PersonalInformation(Consumer logged){
        this.logged = logged;
        setEditableComponents(false);
        setPersonalInformation();
        setEducation();
        setExperiences();
        this.setVisible(true);
        this.setSize(500,500);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setEditableComponents(true);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean goodPersonalInformation = true;

                if (jFullName.getText().isEmpty()) goodPersonalInformation = false;
                if (jGenre.getText().isEmpty()) goodPersonalInformation = false;
                if (jPhone.getText().isEmpty()) goodPersonalInformation = false;
                if (!InformationChecker.isGoodDate(jDateOfBirth.getText())) goodPersonalInformation = false;

                if (goodPersonalInformation){
                    logged.resume.information.name = jFullName.getText();
                    logged.resume.information.setGenre(jGenre.getText());
                    logged.resume.information.setPhone(jPhone.getText());
                    logged.resume.information.setDateOfBirth(LocalDate.parse(jDateOfBirth.getText(),DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                } else {
                    JOptionPane.showMessageDialog(jPersonalInformation, "Not saved");
                }

                setPersonalInformation();
                setEditableComponents(false);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JsonRW.writeJSON();
                System.exit(0);
            }
        });
    }

    private void setEditableComponents(boolean var) {
        jFullName.setEditable(var);
        jGenre.setEditable(var);
        jPhone.setEditable(var);
        jDateOfBirth.setEditable(var);
    }

    JScrollPane getjPersonalInformation(){
        return jPersonalInformation;
    }


    private void setExperiences() {
        jExperiences.removeAll();
        JPanel experiencesAux = new JPanel();
        experiencesAux.setLayout(new BoxLayout(experiencesAux, BoxLayout.Y_AXIS));
        for (Experience experience : logged.resume.experience){
            experiencesAux.add(new ExperienceJPanel(this, experience));
        }
        jExperiences.add(experiencesAux);
    }

    static class ExperienceJPanel extends JPanel{
        Experience experience;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        PersonalInformation form;
        JTextField companyValue;
        JTextField finishDateValue;
        JTextField startDateValue;
        JTextField positionValue;

        ExperienceJPanel(PersonalInformation form, Experience experience){
            this.form = form;
            this.experience = experience;
            //setLayout(null);
            //setLayout(new GridLayout(2,3));
            GridBagLayout gbl = new GridBagLayout();
            setLayout(gbl);
            setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));

            GridBagConstraints c = new GridBagConstraints();

            c.anchor = GridBagConstraints.NORTH;
            c.fill   = GridBagConstraints.HORIZONTAL;
            c.gridheight = 1;
            c.gridwidth  = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 0, 0, 0);
            c.ipadx = 0;
            c.ipady = 0;
            c.weightx = 0.0;
            c.weighty = 0.0;

            JPanel firstLine = new JPanel();
            JLabel companyLabel = new JLabel("Company");
            companyLabel.setSize(40,25);
            companyValue = new JTextField(experience.company,15);
            firstLine.add(companyLabel);
            firstLine.add(companyValue);

            JPanel firstLineSecondBlock = new JPanel();
            JLabel positionLabel = new JLabel("Position");
            positionLabel.setSize(40,25);
            positionValue = new JTextField(25);
            positionValue.setText(experience.position);
            firstLineSecondBlock.add(positionLabel);
            firstLineSecondBlock.add(positionValue);

            JPanel secondLineSecondBlock = new JPanel();
            JLabel finishDateLabel = new JLabel("Finish date:");
            try{
                finishDateValue = new JTextField(experience.finish.format(dtf),10);
            } catch (NullPointerException e){
                finishDateValue = new JTextField("continuing",10);
            }

            secondLineSecondBlock.add(finishDateLabel);
            secondLineSecondBlock.add(finishDateValue);

            JPanel secondLine = new JPanel();
            JLabel startDateLabel = new JLabel("Start date:");
            startDateValue = new JTextField(experience.start.format(dtf),10);
            secondLine.add(startDateLabel);
            secondLine.add(startDateValue);

            EditExpButton editButton = new EditExpButton(this);

            companyValue.setEditable(false);
            startDateValue.setEditable(false);
            finishDateValue.setEditable(false);
            positionValue.setEditable(false);

            gbl.setConstraints(firstLine, c);
            add(firstLine);

            c.gridy = 0;
            c.gridx = 1;
            gbl.setConstraints(firstLineSecondBlock,c);
            add(firstLineSecondBlock);

            c.gridy = 0;
            c.gridx = 2;
            gbl.setConstraints(editButton,c);
            add(editButton);

            c.gridx = 0;
            c.gridy = 2;
            gbl.setConstraints(secondLine,c);
            add(secondLine);

            c.gridx = 1;
            c.gridy = 2;
            c.insets = new Insets(0,0,0,0);
            gbl.setConstraints(secondLineSecondBlock,c);
            add(secondLineSecondBlock);

            setVisible(true);
        }

        class EditExpButton extends JButton{
            ExperienceJPanel parent;
            boolean canBeEdited = false;

            EditExpButton(ExperienceJPanel jPanel){
                this.parent = jPanel;
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (parent.finishDateValue.getText().equals("continuing")){
                            JOptionPane.showMessageDialog(parent, "Cannot edit continuing experience!");
                            return;
                        }

                        if (canBeEdited){
                            boolean correct = verifyNewExpDates();
                            if (correct){
                                rewriteExperiences();
                            }
                            form.setExperiences();
                            parent.companyValue.setEditable(false);
                            parent.startDateValue.setEditable(false);
                            parent.finishDateValue.setEditable(false);
                            positionValue.setEditable(false);
                            setText("Edit");
                        }
                        if (!canBeEdited){
                            parent.companyValue.setEditable(true);
                            parent.startDateValue.setEditable(true);
                            parent.finishDateValue.setEditable(true);
                            positionValue.setEditable(true);
                            setText("Save");
                        }
                        canBeEdited = !canBeEdited;
                    }
                });
                setText("Edit");
                setSize(200,40);
            }
        }

        boolean verifyNewExpDates(){
            if (companyValue.getText().isEmpty() || positionValue.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Wrond data format!\nNo change applied");
                return false;
            }
            if (!InformationChecker.isGoodDate(startDateValue.getText())){
                JOptionPane.showMessageDialog(this,"Wrong start date format");
                return false;
            }
            if (!InformationChecker.isGoodDate(finishDateValue.getText()) && !finishDateValue.getText().isEmpty()){
                JOptionPane.showMessageDialog(this,"Wrong finish date format");
                return false;
            }
            return true;
        }

        void rewriteExperiences(){
            experience.start = LocalDate.parse(startDateValue.getText(), dtf);
            try{
                experience.finish = LocalDate.parse(finishDateValue.getText(), dtf);
            } catch (Exception e){
                experience.finish = null;
            }

            experience.position = positionValue.getText();
            experience.company = companyValue.getText();

            form.setExperiences();
        }
    }

    private void setPersonalInformation() {
        jFullName.setText(logged.resume.information.name);
        jDateOfBirth.setText(logged.resume.information.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        jGenre.setText(logged.resume.information.getGenre());
        jPhone.setText(logged.resume.information.getPhone());
        jEmail.setText(logged.resume.information.getEmail());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        for (Map.Entry<String, Information.LanguageLevel> entry : logged.resume.information.getLanguages().entrySet()) {
            JPanel line = new JPanel();

            JTextField languageName = new JTextField();
            languageName.setText(entry.getKey());
            line.add(languageName);

            JComboBox<String> languageLevel = new JComboBox<String>();
            languageLevel.addItem("Beginner");
            languageLevel.addItem("Experienced");
            languageLevel.addItem("Advanced");
            System.out.println(entry.getValue());
            languageLevel.setSelectedItem(entry.getValue().toString());

            line.add(languageName);
            line.add(languageLevel);

            jPanel.add(line);
        }

        jLanguages.add(jPanel);
    }

    private void setEducation() {
        Educations.removeAll();
        JPanel educationsAux = new JPanel();
        educationsAux.setLayout(new BoxLayout(educationsAux, BoxLayout.Y_AXIS));
        for (Education education : logged.resume.education){
            educationsAux.add(new EducationJPanel(this,education));
        }
        Educations.add(educationsAux);
    }

    static class EducationJPanel extends JPanel {
        Education education;
        PersonalInformation form;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        static Color color1 = new Color(142, 228, 175);
        static Color color2 = new Color(255, 255, 253);
        static Color lastcreated = color1;
        JTextField institutionValue;
        JTextField levelValue;
        JTextField startDateValue;
        JTextField finishDateValue;
        JTextField gradeValue;

        EducationJPanel(PersonalInformation form, Education education){
            this.education = education;
            this.form = form;
            GridBagLayout gbl = new GridBagLayout();
            setLayout(gbl);
            setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));

            GridBagConstraints c = new GridBagConstraints();

            c.anchor = GridBagConstraints.NORTH;
            c.fill   = GridBagConstraints.HORIZONTAL;
            c.gridheight = 1;
            c.gridwidth  = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 0, 0, 0);
            c.ipadx = 0;
            c.ipady = 0;
            c.weightx = 0.0;
            c.weighty = 0.0;

            JPanel firstLine = new JPanel();
            JLabel institutionLabel = new JLabel("Institution");
            institutionLabel.setSize(40,25);
            institutionValue = new JTextField(education.institutionName,15);
            firstLine.add(institutionLabel);
            firstLine.add(institutionValue);

            JPanel firstLineSecondBlock = new JPanel();
            JLabel levelLabel = new JLabel("Level");
            levelLabel.setSize(40,25);
            levelValue = new JTextField(25);
            levelValue.setText(education.educationLevel);
            firstLineSecondBlock.add(levelLabel);
            firstLineSecondBlock.add(levelValue);

            JPanel secondLineSecondBlock = new JPanel();
            JLabel finishDateLabel = new JLabel("Finish date:");
            try{
                finishDateValue = new JTextField(education.finish.format(dtf),10);
            } catch (NullPointerException e){
                finishDateValue = new JTextField("continuing",10);
            }
            JLabel grade = new JLabel("Grade: ");
            gradeValue = new JTextField(3);
            gradeValue.setText(String.valueOf(education.grade));

            secondLineSecondBlock.add(finishDateLabel);
            secondLineSecondBlock.add(finishDateValue);
            secondLineSecondBlock.add(grade);
            secondLineSecondBlock.add(gradeValue);

            JPanel secondLine = new JPanel();
            JLabel startDateLabel = new JLabel("Start date:");
            startDateValue = new JTextField(education.start.format(dtf),10);
            secondLine.add(startDateLabel);
            secondLine.add(startDateValue);

            EducationJPanel.EditEduButton editButton = new EducationJPanel.EditEduButton(this);

            gradeValue.setEditable(false);
            institutionValue.setEditable(false);
            startDateValue.setEditable(false);
            finishDateValue.setEditable(false);
            levelValue.setEditable(false);
            gradeValue.setEditable(false);

            gbl.setConstraints(firstLine, c);
            add(firstLine);

            c.gridy = 0;
            c.gridx = 1;
            gbl.setConstraints(firstLineSecondBlock,c);
            add(firstLineSecondBlock);

            c.gridy = 0;
            c.gridx = 2;
            gbl.setConstraints(editButton,c);
            add(editButton);

            c.gridx = 0;
            c.gridy = 1;
            gbl.setConstraints(secondLine,c);
            add(secondLine);

            c.gridx = 1;
            c.gridy = 1;
            c.insets = new Insets(0,0,0,0);
            gbl.setConstraints(secondLineSecondBlock,c);
            add(secondLineSecondBlock);



            setBackground(lastcreated);
            institutionLabel.setBackground(lastcreated);
            levelLabel.setBackground(lastcreated);
            startDateLabel.setBackground(lastcreated);
            finishDateLabel.setBackground(lastcreated);
            firstLine.setBackground(lastcreated);
            secondLine.setBackground(lastcreated);
            firstLineSecondBlock.setBackground(lastcreated);
            secondLineSecondBlock.setBackground(lastcreated);

            if (lastcreated.equals(color1)){
                lastcreated = color2;
            } else {
                lastcreated = color1;
            }

            setVisible(true);
        }

        class EditEduButton extends JButton{
            EducationJPanel parent;
            boolean canBeEdited = false;

            EditEduButton(EducationJPanel jEdu){
                this.parent = jEdu;
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if (canBeEdited){
                            boolean correct = verifyNewEduDates();
                            if (correct){
                                rewriteEducation();
                            }
                            form.setEducation();
                            parent.institutionValue.setEditable(false);
                            parent.startDateValue.setEditable(false);
                            parent.finishDateValue.setEditable(false);
                            parent.levelValue.setEditable(false);
                            parent.gradeValue.setEditable(false);
                            setText("Edit");
                        }
                        if (!canBeEdited){
                            parent.institutionValue.setEditable(true);
                            parent.startDateValue.setEditable(true);
                            parent.finishDateValue.setEditable(true);
                            parent.levelValue.setEditable(true);
                            parent.gradeValue.setEditable(true);
                            setText("Save");
                        }

                        canBeEdited = !canBeEdited;
                    }
                });
                setText("Edit");
                setSize(200,40);
            }
        }

        void rewriteEducation(){
            education.start = LocalDate.parse(startDateValue.getText(),dtf);
            try{
                education.finish = LocalDate.parse(finishDateValue.getText(),dtf);
            } catch (Exception e){
                education.finish = null;
            }
            education.educationLevel = levelValue.getText();
            education.grade = Double.parseDouble(gradeValue.getText());
            education.institutionName = institutionValue.getText();

            form.setEducation();
        }

        boolean verifyNewEduDates(){
            if (! InformationChecker.isGoodDate(startDateValue.getText())){
                JOptionPane.showMessageDialog(this, "Wrond data format!\nNo change applied");
                return false;
            }
            if (!InformationChecker.isGoodDate(finishDateValue.getText()) && !finishDateValue.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Wrond data format!\nNo change applied");
                return false;
            }
            if (institutionValue.getText().isEmpty() || levelValue.getText().isEmpty() || gradeValue.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Wrond text format!\nNo change applied");
                return false;
            }
            try{
                Double.valueOf(gradeValue.getText());
            } catch (NumberFormatException e){
                JOptionPane.showMessageDialog(this, "Grade must be double");
                return false;
            }

            return true;
        }
    }
}
