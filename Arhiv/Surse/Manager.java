import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

class Manager extends Employee {
    private SortedSet<Recruiter.Request<Job, Consumer>> requests = new TreeSet<>();

    Manager(){
        super();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject me = super.toJSON();
        me.put("requests",writeRequests());
        return me;
    }

    JSONArray writeRequests(){
        JSONArray retVal = new JSONArray();
        for (Recruiter.Request request: requests) {
            retVal.put(request.toJSON());
        }
        return retVal;
    }

    public SortedSet<Recruiter.Request<Job, Consumer>> getRequests() {
        for (Recruiter.Request<Job, Consumer> request : requests) {
            if (Application.getInstance().getUser(request.getValue1().resume.information.getEmail()) == null)
                requests.remove(request);
        }
        return requests;
    }

    public void remove(Recruiter.Request request){
        requests.remove(request);
        if ((Consumer)request.getValue1() != null){
            if (Application.getInstance().getUser(((Consumer) request.getValue1()).resume.information.getEmail()) == null){
                ((Consumer)request.getValue1()).sendNotification("You were declined on "+request.getKey().toString());
                System.out.println("Removing "+request.getKey());
                ((User)request.getValue1()).appliedJobs.remove((Job)request.getKey());
            }
        }
    }

    Manager(JSONObject manager) {
        super(manager, null);
        Application.getInstance().getCompany(getCurrentCompany()).setManager(this);
        if (manager.has("requests")){
            for (Object request : manager.getJSONArray("requests")) {
                if (request != JSONObject.NULL){
                    Recruiter.Request<Job,Consumer> newRequest = new Recruiter().new Request<>((JSONObject) request);
                    if (newRequest.getValue1() != null && newRequest.getValue2() != null && newRequest.getScore() != null)
                    requests.add(newRequest);
                }
            }
        }
        setPassword();
    }

    void addRequest(Recruiter.Request<Job, Consumer> request){
        requests.add(request);
    }

    void accept(Recruiter.Request<Job, Consumer> request){
        Job job = request.getKey();
        System.out.println("Accepted "+job.name+job.isOpen);
        User goodCandidate = (User) request.getValue1();
        System.out.println(goodCandidate);

        if (job.noPositions > 0 && job.isOpen){
            if (Application.getInstance().remove(goodCandidate)){
                System.out.println("Removing was good!");

                Employee employee = goodCandidate.convert();
                employee.accepted(job);
                employee.sendNotification("Congratulations! You were accepted at job "+job.name);
                Company myCompany = Application.getInstance().getCompany(job.companyName);
                myCompany.add(employee, job.department);

                requests.remove(request);
                job.noPositions--;

                if (job.noPositions == 0) {
                    job.close();
                    myCompany.notify("Your followed company "+companyName+" has closed 1 of its jobs: "+job.name);
                }
            }
        }

    }

    void process(Job job){

        Iterator<Recruiter.Request<Job, Consumer>> iter = requests.iterator();

        while (job.noPositions > 0 && iter.hasNext()){
            Recruiter.Request request = iter.next();
            User goodCandidate = (User) request.getValue1();

            if (request.getKey() == job && Application.getInstance().remove(goodCandidate)){

                Employee employee = goodCandidate.convert();
                employee.accepted(job);
                employee.sendNotification("Congratulations! You were accepted at job "+job.name);
                Company myCompany = Application.getInstance().getCompany(job.companyName);
                myCompany.add(employee, job.department);

                requests.remove(request);
                job.noPositions--;
            }
        }

        if (job.noPositions == 0) {
            job.isOpen = false;
            Application.getInstance().getCompany(getCurrentCompany())
                    .notify("Your followed company "+companyName+" has closed 1 of its jobs: "+job.name);
        }
    }
}
