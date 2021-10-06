import org.json.JSONArray;
import org.json.JSONObject;

import java.util.SortedSet;
import java.util.TreeSet;

class Resume{

    Resume(Information information, SortedSet<Education> education, SortedSet<Experience> experience){
        this.education = education;
        this.experience = experience;
        this.information = information;
    }

    private Resume(){
        information = new Information();
        education = new TreeSet<>();
        experience = new TreeSet<>();
    }

    Resume(JSONObject resume){
        this();
        information = new Information(resume);

        JSONArray educations = resume.getJSONArray("education");
        for (Object education : educations) {
            Education newEducation = new Education((JSONObject) education);
            if (newEducation.institutionName == null || newEducation.educationLevel==null || newEducation.start == null)
                throw new EducationIncompleteException();
            this.education.add(newEducation);
        }

        JSONArray experiences = resume.getJSONArray("experience");
        for (Object experience : experiences) {

            Experience newExperience = new Experience((JSONObject) experience);
            if (newExperience.position == null || newExperience.start == null)
                throw new ExperienceIncopleteException();

            this.experience.add(newExperience);
        }
    }

    Information information;
    SortedSet<Education> education;
    SortedSet<Experience> experience;

    private Resume(ResumeBuilder resumeBuilder) throws ResumeIncopleteException {
        if (resumeBuilder == null) throw new ResumeIncopleteException();
        if (resumeBuilder.education == null) throw new ResumeIncopleteException();
        this.information = resumeBuilder.information;
        this.experience = resumeBuilder.experience;
        this.education = resumeBuilder.education;
    }

    @Override
    public String toString() {
        return "INFO: \n"+information+"\nEDU: \n"+education+"\nEXP: \n"+experience;
    }

    JSONObject writeResume(){
        JSONObject resume = information.writeInformation();

        JSONArray educations = new JSONArray();
        for (Education education: education) {
            educations.put(education.toJSON());
        }
        resume.put("education",educations);

        JSONArray experiences = new JSONArray();
        for (Experience experience: experience){
            experiences.put(experience.toJSON());
        }
        resume.put("experience",experiences);

        return resume;
    }

    public static class ResumeBuilder{
        Information information;
        SortedSet<Education> education;
        SortedSet<Experience> experience;

        private ResumeBuilder(){
            information = null;
            education = new TreeSet<>();
            experience = new TreeSet<>();
        }

        ResumeBuilder(Information information){
            this();
            this.information = information;
        }

        ResumeBuilder addEducation(Education e){
            if (e.institutionName == null || e.educationLevel==null || e.start == null) throw new EducationIncompleteException();
            education.add(e);
            return this;
        }

        ResumeBuilder addExperience(Experience e){
            if (e.position == null || e.start == null) throw new ExperienceIncopleteException();
            experience.add(e);
            return this;
        }

        ResumeBuilder information(Information i){
            this.information = i;
            return this;
        }

        ResumeBuilder education(TreeSet<Education> t){
            this.education = t;
            return this;
        }

        ResumeBuilder experience(TreeSet<Experience> t){
            this.experience = t;
            return this;
        }

        Resume build() throws ResumeIncopleteException {
            return new Resume(this);
        }
    }
}
