import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class Recruiter extends Employee {
    double rating = 5.0;
    ArrayList<ToEvaluate> waitingForEvaluation = new ArrayList<>();
    static Recruiter virtualRecruiter;

    Recruiter(){}

    Recruiter(JSONObject recruiter) {
        super(recruiter,"IT");
        if (recruiter.has("rating")){
            rating = recruiter.getDouble("rating");
        }
        if (recruiter.has("evaluations")) readEvaluations(recruiter.getJSONArray("evaluations"));
        Application.getInstance().getCompany(companyName).add(this);
        setPassword();
    }

    public int evaluate(Job job, User user){
        double score = user.getTotalScore()*rating;

        if (!Application.getInstance().getCompany(job.companyName).getJobs().contains(job))
            return 0;

        if (!job.isOpen)
            return 0;

        if (Application.getInstance().getUser(user.resume.information.getEmail())==null)
            return 0;

        Request<Job, Consumer> request = new Request<Job, Consumer>(job, user, this, score);

        Company myCompany = Application.getInstance().getCompany(job.companyName);
        myCompany.getManager().addRequest(request);
        System.out.println("Request added");

        rating += 0.1;
        return (int) score;
    }

    public ArrayList<ToEvaluate> getWaitingForEvaluation() {
        ArrayList<ToEvaluate> toRemove = new ArrayList<>();
        for (ToEvaluate request : this.waitingForEvaluation) {
            if (request.user == null ||
                    Application.getInstance().getConsumer(request.user.resume.information.getEmail()) == null){
                toRemove.add(request);
            }
        }
        waitingForEvaluation.removeAll(toRemove);
        return waitingForEvaluation;
    }

    public double getRating() {
        return rating;
    }

    void addToEvaluation(Job job, User user){
        waitingForEvaluation.add(new ToEvaluate(job,user,job.companyName));
    }

    void readEvaluations(JSONArray evaluations){
        for (Object toEvaluate : evaluations) {
            this.waitingForEvaluation.add(new ToEvaluate((JSONObject) toEvaluate));
        }
    }

    JSONArray writeEvaluations(){
        JSONArray retVal = new JSONArray();

        for(ToEvaluate evaluate : this.getWaitingForEvaluation()){
            retVal.put(evaluate.toJSON());
        }
        return retVal;
    }

    public void evaluate(ToEvaluate toEvaluate) {
        evaluate(toEvaluate.job, toEvaluate.user);
        removeEvaluation(toEvaluate);
    }

    void removeEvaluation(ToEvaluate toEvaluate){
        if (waitingForEvaluation.remove(toEvaluate)){
            toEvaluate.user.sendNotification("Your request on "+toEvaluate.job.companyName+","+toEvaluate.job.name+" was declined.");
        }
    }

    public static Recruiter getVirtualRecruiter(){

        if (virtualRecruiter != null){
            virtualRecruiter.rating = 1.0;
            return virtualRecruiter;
        }

        Information virtualInformation = new Information("DefaultRecruiter",null,"recruiter@recruiter.recruiter","01.01.1970","robot");
        virtualRecruiter = new Recruiter();
        virtualRecruiter.resume.information = virtualInformation;

        return virtualRecruiter;
    }

    public class Request<K, V> implements Comparable<Request<K, V>> {
        private K key;
        private V value1, value2;
        private Double score;
        public Request(K key, V value1, V value2, Double score) {
            this.key = key;
            this.value1 = value1;
            this.value2 = value2;
            this.score = score;
        }
        public K getKey() {
            return key;
        }
        public V getValue1() {
            return value1;
        }
        public V getValue2() {
            return value2;
        }
        public Double getScore() {
            return score;
        }
        public String toString() {
            return "Key: " + key + " ; Value1: " + value1 + " ; Value2: " + value2 + " ; Score: " + score;
        }

        @Override
        public int compareTo(Request<K, V> kvRequest) {
            return (int) -Math.signum(score - kvRequest.getScore());
        }

        JSONObject toJSON(){
            if (key instanceof Job && value1 instanceof Consumer && value2 instanceof Consumer ){
                JSONObject object = new JSONObject();
                object.put("company",((Job) key).companyName);
                object.put("department",((Job) key).department);
                object.put("name",((Job) key).name);
                object.put("score",score);

                object.put("recruiter",((Consumer) value2).resume.information.getEmail());
                object.put("candidate",((Consumer) value1).resume.information.getEmail());

                return object;
            }

            return null;
        }

        public Request(JSONObject jsonObject){
            try{
                try{
                    key = (K) Application.getInstance()
                            .getCompany(jsonObject.getString("company"))
                            .getDepartment(jsonObject.getString("department"))
                            .getJob(jsonObject.getString("name"));
                } catch (NullPointerException e){
                    return;
                }

                value1 = (V) Application.getInstance().getUser(jsonObject.getString("candidate"));
                if (jsonObject.has("recruiter")) value2 = (V) Application.getInstance().getConsumer(jsonObject.getString("recruiter"));
                else value2 = (V) Recruiter.getVirtualRecruiter();
                score = jsonObject.getDouble("score");
            } catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    }

    class ToEvaluate{
        Job job;
        User user;
        String company;

        ToEvaluate(Job job, User user, String company){
            this.job = job;
            this.user = user;
            this.company = company;
        }

        JSONObject toJSON(){
            JSONObject toJSON = new JSONObject();

            toJSON.put("job", job.name);
            toJSON.put("department", job.department);
            toJSON.put("company", job.companyName);
            toJSON.put("user",user.resume.information.getEmail());

            return toJSON;
        }

        ToEvaluate(JSONObject jsonObject){
            this.job = Application.getInstance()
                    .getCompany(jsonObject.getString("company"))
                    .getDepartment(jsonObject.getString("department"))
                    .getJob(jsonObject.getString("job"));

            this.user = Application.getInstance()
                    .getUser(jsonObject.getString("user"));
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject retVal = super.toJSON().put("rating",rating);
        retVal.put("evaluations",writeEvaluations());
        return retVal;
    }
}
