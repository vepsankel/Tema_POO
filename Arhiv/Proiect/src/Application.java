import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Application {
    private static String logInfo = "log_info.json";
    BufferedInputStream bis;
    private ArrayList<Company> listCompanies = new ArrayList<>();
    TreeSet<User> listUsers = new TreeSet<>();
    private static Application currentApplication = null;
    private TreeSet<Consumer> consumerUsers = new TreeSet<>();

    void addConsumer(Consumer consumer) {
        consumerUsers.add(consumer);
    }

    void removeConsumer(Consumer consumer){
        consumerUsers.remove(consumer);
        listUsers.remove(consumer);
    }

    TreeSet<Consumer> getConsumers(){
        return consumerUsers;
    }

    ArrayList<Company> getCompanies(){
        return listCompanies;
    }

    Company getCompany(String name){
        for (Company company: listCompanies) {
            if (company.name.equals(name)) return company;
        }
        return null;
    }

    public User getUser(Information information){
        for (User user: listUsers ) {
            if (user.resume.information.getEmail().equals(information.getEmail())) return user;
        };
        return null;
    }

    public TreeSet<User> getListUsers() {
        return listUsers;
    }

    Consumer getConsumer(String email){

        for (Consumer consumer : consumerUsers){
            try{
                if (consumer.resume.information.getEmail().equals(email)){
                    return consumer;
                }
            } catch (NullPointerException ignored){
            }
        }
        return null;
    }

    User getUser(String email){
        for (User user : listUsers){
            if (user.resume.information.getEmail().equals(email)) return user;
        }
        return null;
    }

    public void add(Company company){
        listCompanies.add(company);
    }

    public void add(User user){
        addConsumer((Consumer)user);
        listUsers.add(user);
    }

    public boolean remove(Company company){
        return listCompanies.remove(company);
    }

    boolean remove(User user){
        boolean rezult = listUsers.remove(user);
        return rezult;
    }

    private Application(){}

    static Application getInstance() {
        if (currentApplication == null){
            currentApplication = new Application();
        }
        return currentApplication;
    }

    ArrayList<Job> getJobs(List<String> companies){
        ArrayList<Job> retval = new ArrayList<>();
        for (String company: companies) {
            Company currentCompany = getCompany(company);
            if (currentCompany != null){
                retval.addAll(currentCompany.getJobs());
            }
        }
        return retval;
    }

    Consumer getConsumer(Information shortInfoFriend1) {
        return getConsumer(shortInfoFriend1.getEmail());

//        for (Consumer consumer: listUsers) {
//            if (consumer.resume.information.equals(shortInfoFriend1)){
//                return consumer;
//            }
//        }
//
//        for (Company company : listCompanies) {
//            Consumer retVal = company.getEmployee(shortInfoFriend1);
//            if (company.getManager().resume.information.equals(shortInfoFriend1)) return company.getManager();
//            if (retVal != null) return retVal;
//        }
//
//        return null;
    }

    boolean registerEmployee(String companyName,String department, String position, String startDate, String email,
                             String passwd, String name, String phone, String genre, String dob){
        Employee newEmployee = new Employee();
        return registerEmployee(newEmployee, companyName, department, position, startDate, email,  passwd,
                 name,  phone,  genre,  dob);
    }

    boolean registerEmployee(Employee newEmployee, String companyName,String department, String position, String startDate,
                             String email, String passwd, String name, String phone, String genre, String dob){

        if (!registerUser(newEmployee,email,passwd,name,phone,genre,dob)) return false;
        newEmployee.companyName = companyName;
        if (department != null)  //in case of Manager
            getCompany(companyName).add(newEmployee, department);

        newEmployee.add(new Experience(startDate,companyName,department,position));

        return true;
    }

    boolean registerUser(Consumer newUser, String email, String passwd, String name, String phone, String genre, String dob){ ;
        if (!registerPassword(email,passwd)) return false;
        Information information = new Information(name, email, phone, dob, genre);
        newUser.set(information);
        addConsumer(newUser);
        return true;
    }

    boolean registerUser(String email, String passwd, String name, String phone, String genre, String dob){
        User newUser = new User();
        return registerUser(newUser,  email,  passwd,  name,  phone,  genre,  dob);
    }


    boolean registerPassword(String email, String passwd){
        String hashedPasswd = BCrypt.hashpw(passwd, BCrypt.gensalt());

        String logContent = null;
        boolean alreadyExists = false;



        try {
            new File(logInfo).createNewFile();
            if (bis == null) bis = new BufferedInputStream(new FileInputStream(new File(logInfo)));
            logContent = new String(Files.readAllBytes(Path.of(logInfo)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        JSONArray logArray;
        try{
            logArray = new JSONArray(logContent);
        } catch (JSONException e){
            logArray = new JSONArray();
        }

        for (Object userLogInfo: logArray) {
            if (!((JSONObject)userLogInfo).isNull("email")){
                if (((JSONObject)userLogInfo).getString("email").equals(email)){
                    return false;
                }
            }
        }

        if (!alreadyExists){
            try{
                FileWriter fileWriter = new FileWriter(new File(logInfo));

                JSONObject newLog = new JSONObject();

                newLog.put("email",email);
                newLog.put("passwd",hashedPasswd);

                logArray.put(newLog);
                fileWriter.write(logArray.toString(4));
                fileWriter.flush();
            } catch (IOException e){
                System.out.println(e.getMessage());
                return false;
            }
        }

        return true;
    }

    public boolean isRegistered(String email, String passwd) {
        String logContent = null;

        try {
            if (bis == null) bis = new BufferedInputStream(new FileInputStream(new File(logInfo)));
            logContent = new String(Files.readAllBytes(Path.of(logInfo)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray logArray;
        try{
            assert logContent != null;
            logArray = new JSONArray(logContent);
        } catch (JSONException e){
            logArray = new JSONArray();
        }


        for (Object userLogInfo: logArray) {
            if (!((JSONObject)userLogInfo).isNull("email") && !((JSONObject)userLogInfo).isNull("passwd")){

                if (((JSONObject)userLogInfo).getString("email").equals(email)){
                    if (BCrypt.checkpw(passwd,((JSONObject)userLogInfo).getString("passwd"))){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    Consumer logIn(String email, String passwd){
        if (isRegistered(email,passwd)) return getConsumer(email);
        return null;
    }

    public boolean registerManager(String cmpanyName, String startManaging, String email, String passwd, String fullName, String phone, String genre, String dateOfBirth) {
        Company newCompany = new Company(cmpanyName);
        Manager manager = new Manager();
        if (!registerEmployee(manager,cmpanyName,null,"Manager",startManaging,email,passwd,fullName,phone,genre,dateOfBirth)) return false;
        newCompany.setManager(manager);

        return true;
    }
}
