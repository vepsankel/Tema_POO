import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

class User extends Consumer {
    ArrayList<String> companiesNames = new ArrayList<>();
    HashSet<Job> appliedJobs = new HashSet<>();

    User(){
    }

    User(JSONObject user) {
        this.resume = new Resume(user);

        JSONArray interestedCompanies = user.getJSONArray("interested_companies");
        for (Object company : interestedCompanies) {
            companiesNames.add((String)company);
            Application.getInstance().getCompany((String)company).addNotifier(this);
        }

        JSONArray appliedJobs = new JSONArray();
        if (user.has("applied_jobs"))
            appliedJobs = user.getJSONArray("applied_jobs");
        if (appliedJobs != null){
            for (Object job : appliedJobs) {
                try {
                    Job newJob = Application.getInstance()
                            .getCompany(((JSONObject) job).getString("company_name"))
                            .getDepartment(((JSONObject) job).getString("department"))
                            .getJob(((JSONObject) job).getString("name"));

                    this.appliedJobs.add(newJob);

                } catch (NullPointerException ignored){};

            }

        }
        Application.getInstance().add(this);
        setPassword();
    }

    Employee convert(){
        Employee employee = new Employee();
        employee.resume = this.resume;
        employee.notifications = this.notifications;
        for (Consumer friend: getSocial()) {
            friend.add(employee);
        }
        while (!social.isEmpty()){
            remove(social.get(0));
        }
        Application.getInstance().remove(this);
        Application.getInstance().removeConsumer(this);
        Application.getInstance().addConsumer(employee);
        return employee;
    }

    public void add(String company){
        companiesNames.add(company);
    }

    double getTotalScore(){
        return experienceYears() * 1.5 + meanGPA();
    }

    JSONObject toJSON(){
        JSONObject me = super.toJSON();
        me.put("interested_companies",companiesNames);

        JSONArray appliedJobs = new JSONArray();
        for (Job job : this.appliedJobs) {
            JSONObject appliedJob = new JSONObject();
            appliedJob.put("name",job.name);
            appliedJob.put("company_name",job.companyName);
            appliedJob.put("department",job.department);
            appliedJobs.put(appliedJob);
        }
        me.put("applied_jobs",appliedJobs);
        return me;
    }

}
