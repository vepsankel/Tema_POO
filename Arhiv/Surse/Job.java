import org.json.JSONObject;

import java.util.ArrayList;

public class Job {
    String name;
    String companyName;
    String department;

    boolean isOpen;

    private Constraint<Integer> absolvationConstraint;
    private Constraint<Integer> experienceConstraint;
    private Constraint<Double> gradeConstraint;

    ArrayList<User> candidates = new ArrayList<>();
    int noPositions;
    double salary;

    Job(String name, String companyName, String department, int noPositions) {
        this.department = department;
        this.noPositions = noPositions;
        this.companyName = companyName;
        if (noPositions > 0) isOpen = true; else isOpen = false;
        this.name = name;
    }

    Job(String name, String companyName, String department, int noPositions, double salary) {
        this(name, companyName, department, noPositions);
        this.salary = salary;
    }

    Job(JSONObject job, String companyName, String department) {
        this(job.getString("name"), companyName, department, job.getInt("noPositions"), job.getDouble("salary"));

        Double d1 = null;
        Double d2 = null;
        if (! job.isNull("gradeConstraint_min")){
            d1 = job.getDouble("gradeConstraint_min");
        }
        if (! job.isNull("gradeConstraint_max")){
            d2 = job.getDouble("gradeConstraint_max");
        }
        gradeConstraint = new Constraint<>(d1,d2);

        Integer i1 = null;
        Integer i2 = null;
        if (! job.isNull("absolvationConstraint_min")){
            i1 = job.getInt("absolvationConstraint_min");
        }
        if (! job.isNull("absolvationConstraint_max")){
            i2 = job.getInt("absolvationConstraint_max");
        }
        absolvationConstraint = new Constraint<>(i1,i2);

        i1 = null;
        i2 = null;
        if (! job.isNull("experienceConstraint_min")){
            i1 = job.getInt("experienceConstraint_min");
        }
        if (! job.isNull("experienceConstraint_max")){
            i2 = job.getInt("experienceConstraint_max");
        }
        experienceConstraint = new Constraint<>(i1,i2);
    }

    void apply(User user){
        if (!isOpen) return;
        if (!meetsRequirements(user)){
            System.out.println("User "+user.resume.information.name+" applied to "+name+", "+companyName+", but is " +
                    "underqualified");
            return;
        }
        if (noPositions == 0) return;
        user.appliedJobs.add(this);
        this.candidates.add(user);
        Company ourCompany = Application.getInstance().getCompany(companyName);
        ourCompany.addNotifier(user);
        Recruiter recruiter = ourCompany.getRecruiter(user);
        if (recruiter != null){
            System.out.println("User "+user.resume.information.name+" applied to "+name+", "+companyName+", and is" +
                    "qualified. Chosen recruiter is "+recruiter.resume.information.name);
            if (!Test.testIsRunning) recruiter.addToEvaluation(this, user);
            if (Test.testIsRunning) recruiter.evaluate(this, user);
        } else {
            System.out.println("Calling virtual");
            Recruiter.getVirtualRecruiter().evaluate(this, user);
        }

    }

    boolean meetsRequirements(User user){
        if (absolvationConstraint != null && absolvationConstraint.satisfies(user.getGraduationYear())) return false;
        if (experienceConstraint != null && experienceConstraint.satisfies(user.experienceYears())) return false;
        if (gradeConstraint != null && gradeConstraint.satisfies(user.meanGPA())) return false;
        return true;
    }

    public void setAbsolvationConstraint(Constraint<Integer> absolvationConstraint) {
        this.absolvationConstraint = absolvationConstraint;
    }

    public void setExperienceConstraint(Constraint<Integer> experienceConstraint) {
        this.experienceConstraint = experienceConstraint;
    }

    public void setGradeConstraint(Constraint<Double> gradeConstraint) {
        this.gradeConstraint = gradeConstraint;
    }

    void open(){
        isOpen = true;
        Application.getInstance()
                .getCompany(companyName)
                .notify("The Job "+name+" of company "+companyName+" has opened");
    }

    void close(){
        Application.getInstance()
                .getCompany(companyName)
                .notify("The Job "+name+" of company "+companyName+" has closed");
        isOpen = false;
    }

    @Override
    public String toString() {
        return "\n"+companyName + " " + name + " with "+noPositions+" positions and constraints:\n\tAbsolvation "
                +absolvationConstraint+"\n\tExperience "+experienceConstraint+"\n\tGrade "+gradeConstraint;
    }

    public JSONObject getJSON() {
        JSONObject thisJob = new JSONObject();

        thisJob.put("name",name);
        thisJob.put("salary",salary);
        thisJob.put("noPositions",noPositions);
        thisJob.put("isOpen",isOpen);
        thisJob = JsonRW.writeNullable(thisJob,"absolvationConstraint_min",absolvationConstraint.getLower());
        thisJob = JsonRW.writeNullable(thisJob,"absolvationConstraint_max",absolvationConstraint.getUpper());
        thisJob = JsonRW.writeNullable(thisJob,"gradeConstraint_min",gradeConstraint.getLower());
        thisJob = JsonRW.writeNullable(thisJob,"gradeConstraint_max",gradeConstraint.getUpper());
        thisJob = JsonRW.writeNullable(thisJob,"experienceConstraint_min",experienceConstraint.getLower());
        thisJob = JsonRW.writeNullable(thisJob,"experienceConstraint_max",experienceConstraint.getUpper());
        thisJob = JsonRW.writeNullable(thisJob, "department", department);

        return thisJob;
    }

    public Integer getAbsolvationConstraint_min(){
        if (absolvationConstraint != null)
            return absolvationConstraint.getLower();
        return null;
    }

    public Integer getAbsolvationConstraint_max(){
        if (absolvationConstraint != null)
            return absolvationConstraint.getUpper();
        return null;
    }

    public Double getGradeConstraint_min(){
        if (gradeConstraint != null)
            return gradeConstraint.getLower();
        return null;
    }

    public Double getGradeConstraint_max(){
        if (gradeConstraint != null)
            return gradeConstraint.getUpper();
        return null;
    }

    public Integer getExperienceConstraint_min(){
        if (experienceConstraint != null){
            return experienceConstraint.getLower();
        }
        return null;
    }

    public Integer getExperienceConstraint_max(){
        if (experienceConstraint != null){
            return experienceConstraint.getUpper();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Job)) return false;
        try{
            if (!name.equals(((Job) obj).name)) {return false;}
            if (!companyName.equals(((Job) obj).companyName)) {;return false;}
            if (salary != ((Job) obj).salary) {return false;}
            if (!department.equals(((Job) obj).department)) {return false;}
            if (noPositions != ((Job) obj).noPositions) {return false;}
            if (isOpen != ((Job) obj).isOpen) {return false;}
        } catch (NullPointerException e){
            return false;
        }

        return true;
    }
}
