import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public abstract class Consumer implements Comparable<Consumer>{
    Resume resume = new Resume(new Information(), new TreeSet<>(), new TreeSet<>());
    ArrayList<Consumer> social = new ArrayList<>();
    BufferedInputStream bis;
    ArrayList<String> notifications = new ArrayList<>();

    Consumer(){};

    Consumer(JSONObject consumer){
        this.resume = new Resume(consumer);
        if (consumer.has("notifications")){
            for (Object object : consumer.getJSONArray("notifications")) {
                notifications.add((String) object);
            }
        }

        Application.getInstance().addConsumer(this);
    }

    public void add (Education education){
        resume.education.add(education);
    }

    public void add (Experience experience){
        resume.experience.add(experience);
    }

    public void set (Information information){ resume.information = information; }

    Experience getCurrentExperience(){
        for (Experience experience: resume.experience) {
            if (experience.finish == null) return experience;
        }
        return null;
    }

    public ArrayList<Consumer> getSocial() {
        return social;
    }

    Integer getGraduationYear(){
        for (Education edu: resume.education) {
            if (edu.educationLevel.equals("college")){
                if (edu.finish == null) return null;
                return edu.finish.getYear();
            }
        }
        return null;
    }

    public void add (Consumer consumer){
        if (!social.contains(consumer)) {
            social.add(consumer);
            consumer.add(this);
        }
    }

    public void remove (Consumer consumer){
        if (social.remove(consumer)){
            consumer.remove(this);
        }
    }

    public int getDegreeInFriendship(Consumer consumer){
        ArrayList<Consumer> consumers;
        ArrayList<Integer> degrees;

        Pair<ArrayList<Consumer>,ArrayList<Integer>> pair = getAllDegreesOfFriendship();
        consumers = pair.getKey();
        degrees = pair.getValue();

        int index = consumers.indexOf(consumer);
        if (index == -1) return -1;
        return degrees.get(index);
    }

    public ArrayList<Integer> getDegreeInFriendship(ArrayList<Recruiter> recruiters){
        ArrayList<Consumer> consumers;
        ArrayList<Integer> degrees;

        Pair<ArrayList<Consumer>,ArrayList<Integer>> pair = getAllDegreesOfFriendship();
        consumers = pair.getKey();
        degrees = pair.getValue();

        ArrayList<Integer> ret = new ArrayList<>();

        for (Consumer consumer: recruiters ) {
            int index = consumers.indexOf(consumer);
            if (index == -1) ret.add(-1); else
            ret.add(degrees.get(index));
        }

        return ret;
    }

    private Pair<ArrayList<Consumer>, ArrayList<Integer>> getAllDegreesOfFriendship(){
        ArrayList<Consumer> consumers = new ArrayList<>();
        ArrayList<Integer> degrees = new ArrayList<>();

        consumers.add(this);
        degrees.add(0);

        for (int i = 0 ; i < consumers.size() ; i++){
            Consumer currentConsumer = consumers.get(i);
            Integer currentDegree = degrees.get(i);

            for (Consumer friend : currentConsumer.social){
                if (!consumers.contains(friend)){
                    consumers.add(friend);
                    degrees.add(currentDegree + 1);
                }
            }
        }

        return new Pair<>(consumers,degrees);
    }

    Double meanGPA(){
        int numOfEdu = 0;
        double sum = 0;

        for ( Education education:resume.education) {
            if (education.finish != null){
               sum += education.grade;
               numOfEdu++;
            }
        }
        if (numOfEdu == 0) return null;
        return (sum / numOfEdu);
    }

    Integer experienceYears(){
        Period totalExp = Period.of(0,0,0);

        for (Experience exp: resume.experience) {
            LocalDate finish;
            finish = Objects.requireNonNullElseGet(exp.finish, LocalDate::now);

            LocalDate start = exp.start;

            Period difference = Period.between(start,finish);
            totalExp = totalExp.plus(difference);
        }

        totalExp = totalExp.normalized();

        int totalExpYears = totalExp.getYears();
        if (totalExp.getMonths() >= 3){
            totalExpYears++;
        }

        return totalExpYears;
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        retVal.append(resume);
        retVal.append("\nIs friend with:");
        for (Consumer friend: social) {
            retVal.append("\n").append(friend.resume.information.name);
        }
        return retVal.toString();
    }

    JSONObject toJSON(){
        JSONObject me = resume.writeResume();
        if (notifications != null)
            me.put("notifications",notifications);
        Application.getInstance().addConsumer(this);
        return me;
    }

    public JSONArray socialJSON(){
        JSONArray friends = new JSONArray();

        for (Consumer friend: social) {
            if (friend.resume.information.getEmail().compareTo(JsonRW.friends_written.last()) > 0){
                JSONObject friendship = new JSONObject();
                friendship.put("friend",friend.resume.information.getEmail());
                friendship.put("friend_of",resume.information.getEmail());
                friends.put(friendship);
            }
        }
        return friends;
    }

    void setPassword(){
        Application.getInstance().registerPassword(this.resume.information.getEmail(),
                this.resume.information.getEmail());
    }

    public void sendNotification(String s){
        notifications.add(s);
    };

    void removeNotifications(String s){
        notifications.remove(s);
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public int compareTo(Consumer consumer) {
        return resume.information.getEmail().compareTo(consumer.resume.information.getEmail());
    }
}
