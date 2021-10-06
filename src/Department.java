import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class Department {
    String name;
    ArrayList<Employee> employees;
    ArrayList<Job> jobs;
    private Company company;

    private Department(){};

    Department(Company company){
        this.company = company;
        employees = new ArrayList<>();
        jobs = new ArrayList<>();
    }

    public static Department getDepartment(String type, Company company){
        switch (type){
            case "IT": return new IT(company);
            case "Finance": return new Finance(company);
            case "Marketing": return new Marketing(company);
            case "Management": return new Management(company);
            default: throw new IllegalArgumentException();
        }
    }

    public abstract double getTotalSalaryBudget();

    public ArrayList<Job> getJobs(){
        ArrayList<Job> retval = new ArrayList<>();
        for (Job job : this.jobs) {
            if (job.isOpen && job.noPositions > 0) retval.add(job);
        }
        return retval;
    }

    public ArrayList<Job> getAllJobs(){
        return jobs;
    }


    public Job getJob(String name){
        for (Job job: jobs) {
            if (job.name.equals(name)) return job;
        }
        return null;
    }

    boolean isJobNameUnique(String name){
        int repet = 0;
        for (Job job : jobs){
            if (job.name.equals(name))
                repet++;
        }
        return repet<=1;
    }


    void add(Employee employee){
        employees.add(employee);
    }

    void remove(Employee employee){
        if (employees.remove(employee))
            employee.fired();
    }

    void remove(Job job){
        int index = jobs.indexOf(job);
        System.out.println("Index = "+index);

        if (index != -1){
            Job thisJob = jobs.get(index);
            thisJob.close();
            jobs.remove(thisJob);
        }
    }

    void removeJobs(){
        while (jobs.size() > 0) {
            jobs.get(0).close();
            jobs.remove(0);
        }
    }

    public void add(Job job){
        if (!jobs.contains(job)) {
            jobs.add(job);
            System.out.println("Job added");
            company.notify("Company "+company.name+" added an interesting job! Check it out!");
            if (job.isOpen && job.noPositions > 0)
                company.notify("Company "+company.name+" has recently added a new job"+ job.name+" in "+this.name+" Department. Go chek it out!");
        }
        else System.out.println("Job already exists!");
    }

    ArrayList<Employee> getEmployees(){
        return employees;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Department "+name+" has JOBS:\n" + jobs + "\nDepartment "+name+" has employees:\n"+employees;
    }

    Employee getEmployee(Information info)
    {
        for (Employee employee: employees) {
            if (employee.resume.information.equals(info)) return employee;
        }
        return null;
    }

    JSONObject getJSON(){
        JSONObject department = new JSONObject();
        department.put("type",name);
        JSONArray jobs = new JSONArray();

        for (Job job: this.jobs) {
            jobs.put(job.getJSON());
        }
        department.put("jobs",jobs);
        return department;
    }
}
