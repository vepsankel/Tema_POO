class ResumeIncopleteException extends InvalidDatesException {
    @Override
    public String getMessage() {
        return "Information and at least 1 instance of Education is required";
    }
}
