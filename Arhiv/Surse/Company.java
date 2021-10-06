import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Company {
    String name;
    Manager manager;
    private ArrayList<Recruiter> recruiters;
    private ArrayList<Consumer> toNotify;

    public ArrayList<Department> getDepartments() {
        return departments;
    }

    private ArrayList<Department> departments;

    private Company(){
        recruiters = new ArrayList<>();
        departments = new ArrayList<>();
        toNotify = new ArrayList<>();

        Application.getInstance().add(this);
    }

    Company(JSONObject jsonCompany){
        this();
        this.name = jsonCompany.getString("name");
        for (Object department : (JSONArray)(jsonCompany).get("departments")) {
            JSONObject jsonDepartment = (JSONObject) department;
            add(Department.getDepartment(jsonDepartment.getString("type"),this));

            for (Object job : (JSONArray) jsonDepartment.get("jobs")){
                String departmentType = jsonDepartment.getString("type");
                getDepartment(departmentType).add(new Job((JSONObject) job, name,departmentType));
            }
        }
    }

    public void setName(){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    Company(String name){
        this();
        this.name = name;
    }

    public void addJob(Job job, String department){
        if (department != null){
            Department dep;
            if ((dep = getDepartment(department)) != null){
                dep.add(job);
            }
        }

        notify("Company "+name+" has recently added a new job "+job.name+". Go check it!");
    }

    void setManager(Manager manager) {
        this.manager = manager;
    }

    public void add(Department department){
        departments.add(department);
    }

    public void add(Recruiter recruiter){
        recruiters.add(recruiter);
    }

    public void add(Employee employee, Department department){
        department.add(employee);
    }

    void move(Employee employee, Department department){
        String oldDepartment = employee.getCurrentExperience().getDepartment();
        employee.getCurrentExperience().setDepartment(department.name);
        getDepartment(oldDepartment).getEmployees().remove(employee);
        department.add(employee);
    }

    void add(Employee employee, String department){
        if (department != null){
            Department dep;
            if ((dep = getDepartment(department)) != null){
                dep.add(employee);
            }
        }
    }

    void addNotifier(Consumer consumer){
        if (toNotify.contains(consumer)) return;
        toNotify.add(consumer);
    }

    Employee getEmployee(Information information){
        for (Department department: departments) {
            Employee employee = department.getEmployee(information);
            if (employee != null) return employee;
        }
        return null;
    }

    public Manager getManager() {
        return manager;
    }

    public void remove(Employee employee){
        for (Department department:departments) {
            department.remove(employee);
        }
        if (employee instanceof Recruiter) remove((Recruiter) employee);
    }

    public void remove(Department department){
        Employee employee;
        while (department.getEmployees().size()>0) {
            employee = department.getEmployees().get(0);
            department.remove(employee);
            if (employee instanceof Recruiter) remove((Recruiter) employee);
        }

        department.removeJobs();
        departments.remove(department);
    }

    public Department getDepartment(String name){
        for (Department department:departments) {
            if (department.name.equals(name)) return department;
        }
        return null;
    }

    public void remove(Recruiter recruiter){
        recruiters.remove(recruiter);
        if (recruiters.size() > 0){
            recruiters.get(0).getWaitingForEvaluation().addAll(recruiter.getWaitingForEvaluation());
        } else {
            for (Recruiter.ToEvaluate waitingForEvaluation : recruiter.getWaitingForEvaluation()) {
                Recruiter.virtualRecruiter.evaluate(waitingForEvaluation);
            }
        }
    }

    boolean contains(Department department){
        return departments.contains(department);
    }

    boolean contains(String department){
        for (Department dep: departments) {
            if (dep.name.equals(department)) return true;
        }
        return false;
    }

    boolean contains(Recruiter recruiter){
        return recruiters.contains(recruiter);
    }

    Recruiter getRecruiter(User user){
        ArrayList<Integer> degrees = user.getDegreeInFriendship(recruiters);
        int maxDegree = 0;
        double maxRating = 0;
        Recruiter chosenRecruiter = null;

        Iterator<Integer> iter = degrees.iterator();
        int curentIndex = -1;

        while (iter.hasNext()){
            int degree = iter.next();
            curentIndex++;

            if (degree == maxDegree){
                if (recruiters.get(curentIndex).rating > maxRating){
                    maxRating = recruiters.get(curentIndex).rating;
                    chosenRecruiter = recruiters.get(curentIndex);
                }
            }

            if (degree > maxDegree){
                maxDegree = degree;
                maxRating = recruiters.get(curentIndex).rating;
                chosenRecruiter = recruiters.get(curentIndex);
            }

        }

        if (chosenRecruiter == null && recruiters.size() > 0){
            chosenRecruiter = recruiters.get(0);
            for (Recruiter recruiter : recruiters) {
                if (recruiter.getRating() > chosenRecruiter.getRating()) chosenRecruiter = recruiter;
            }
        } else if (chosenRecruiter == null){
            return null;
        }

        return chosenRecruiter;
    }

    @Override
    public String toString() {
        return "\nCompany " + name + " has DEPARTMENTS:\n" + departments + "\n" + name + " has MANAGER:\n "
                + manager + "\n" + name + " has RECRUITERS:\n " + recruiters;
    }

    ArrayList<Job> getJobs(){
        ArrayList<Job> retval = new ArrayList<>();
        for (Department department: departments) {
            retval.addAll(department.jobs);
        }
        return retval;
    }

    public JSONObject getJSON() {
        JSONObject thisCompany = new JSONObject();
        thisCompany.put("name",name);
        JSONArray departments = new JSONArray();

        for (Department department: this.departments) {
            departments.put(department.getJSON());
        }

        thisCompany.put("departments",departments);
        return thisCompany;
    }

    JSONArray writeEmployees(){
        JSONArray employees = new JSONArray();
        for (Department department: departments) {
            for (Employee employee: department.getEmployees()) {
                if (employee instanceof Recruiter) continue;
                employees.put(employee.toJSON());
            }
        }
        return employees;
    }

    public JSONArray writeRecruitersJSON() {
        JSONArray recruiters = new JSONArray();

        for (Recruiter recruiter: this.recruiters) {
            recruiters.put(recruiter.toJSON());
        }

        return recruiters;
    }

    void notify(String message){
        for (Consumer consumer: toNotify) {
            consumer.sendNotification(message);
        }
    }
}
