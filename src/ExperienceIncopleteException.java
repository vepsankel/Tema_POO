class ExperienceIncopleteException extends InvalidDatesException {
    @Override
    public String getMessage() {
        return "Experience is incomplete";
    }
}
