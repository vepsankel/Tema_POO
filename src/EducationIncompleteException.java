class EducationIncompleteException extends InvalidDatesException {
    @Override
    public String getMessage() {
        return "Education is incomplete!";
    }
}
