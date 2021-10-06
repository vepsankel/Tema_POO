import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.TreeSet;

public class JsonRW {
    private static String JsonFilePathIn = "consumers_out.json";
    private static String JsonFilePathOut = "consumers_out.json";
    private static String JsonString = null;
    private static JSONObject jsonObject = null;
    public static TreeSet<String> friends_written;

    private static void checkIfInitialized(){
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(JsonFilePathIn)));
            JsonString = new String(bis.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonObject = new JSONObject(JsonString);
    }

    public static void main(String[] args) {
        readJSON();
        writeJSON();
    }

    static void readJSON(){
        checkIfInitialized();

        readCompanies();
        System.out.println("Companies read");
        readEmployees();
        System.out.println("Employees read");
        readUsers();
        System.out.println("Users read");
        readRecruiters();
        System.out.println("Recruiters read");
        readManagers();
        System.out.println("Managers read");
        readFriends();
        System.out.println("Social read");

        System.out.println("Requests written");
    }

    private static void readFriends() {
        JSONArray friends = (JSONArray) jsonObject.get("friends");

        for (Object friendship : friends) {
            String friend1 = ((JSONObject)friendship).getString("friend");
            Consumer consumer1 = Application.getInstance().getConsumer(friend1);

            String friend2 = ((JSONObject)friendship).getString("friend_of");
            Consumer consumer2 = Application.getInstance().getConsumer(friend2);

            try{
                consumer1.add(consumer2);
            } catch (NullPointerException e){
                System.out.println(friend1 + "\n");
                System.out.println(friend2);

                System.exit(0);
            }

        }
    }

    private static void readUsers() {
        JSONArray users = (JSONArray) jsonObject.get("users");

        for(Object user : users){
            new User((JSONObject)user);
        }
    }

    private static void readManagers() {
        JSONArray managers = (JSONArray) jsonObject.get("managers");

        for (Object manager : managers) {
            new Manager((JSONObject) manager);
        }
    }

    private static void readRecruiters() {
        JSONArray recruiters = (JSONArray) jsonObject.get("recruiters");

        for (Object recruiter : recruiters) {
            new Recruiter((JSONObject) recruiter);
        }
    }

    private static void readCompanies(){
        JSONArray companies = (JSONArray) jsonObject.get("companies");
        for (Object company: companies) {
            JSONObject jsonCompany = (JSONObject) company;
            new Company(jsonCompany);

            Application.getInstance();
        }
    }

    private static void readEmployees(){
        JSONArray employees = (JSONArray) jsonObject.get("employees");

        for (Object employee : employees) {
            new Employee((JSONObject) employee);
        }
    }

    static void writeJSON(){
        FileWriter fileWriter = null;

        JSONObject result = new JSONObject();

        JSONArray companies = new JSONArray();
        for (Company company :Application.getInstance().getCompanies()) {
            companies.put(company.getJSON());
        }
        result.put("companies",companies);
        System.out.println("Companies written");

        JSONArray employees = writeEmployees();
        result.put("employees",employees);
        System.out.println("Employees written");

        JSONArray recruiters = writeRectuiters();
        result.put("recruiters",recruiters);
        System.out.println("Recruiters written");


        JSONArray users = writeUsers();
        result.put("users",users);
        System.out.println("Users written");

        JSONArray managers = writeManagers();
        result.put("managers",managers);
        System.out.println("Managers written");

        JSONArray friends = writeFriends();
        result.put("friends",friends);
        System.out.println("Social written");



        try{
            fileWriter = new FileWriter(new File(JsonFilePathOut));
            fileWriter.write(result.toString(4));
            fileWriter.flush();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }


    }

    private static JSONArray writeManagers(){
        JSONArray managers = new JSONArray();
        for (Company company: Application.getInstance().getCompanies()) {
            managers.put(company.getManager().toJSON());
        }
        return managers;
    }

    private static JSONArray writeFriends() {
        friends_written = new TreeSet<>();
        JSONArray friends = new JSONArray();

        for (Consumer friend1 : Application.getInstance().getConsumers()) {
            if (friends_written.contains(friend1.resume.information.getEmail())) continue;
            friends.putAll(friend1.socialJSON());
            friends_written.add(friend1.resume.information.getEmail());
        }

        return friends;
    }

    private static JSONArray writeUsers() {
        JSONArray result = new JSONArray();
        for (User user: Application.getInstance().listUsers ) {
            result.put(user.toJSON());
        }
        return result;
    }

    private static JSONArray writeRectuiters() {
        JSONArray recruiters = new JSONArray();
        for (Company company : Application.getInstance().getCompanies()){
            recruiters.putAll(company.writeRecruitersJSON());
        }
        return recruiters;
    }

    private static JSONArray writeEmployees() {
        JSONArray employees = new JSONArray();
        for (Company company : Application.getInstance().getCompanies()) {
            employees.putAll(company.writeEmployees());
        }
        return employees;
    }

    static JSONObject writeNullable(JSONObject here, String key, Object value){
        return here.put(key, value==null ? JSONObject.NULL : value);
    }
}
