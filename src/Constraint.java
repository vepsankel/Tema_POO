public class Constraint<T> {
    private Pair<T,T> constraint;

    Constraint(T value1, T value2){
        constraint = new Pair<>(value1, value2);
    }

    boolean satisfies(Comparable<T> value){

        boolean satisfiesLower = (constraint.getKey() == null)
                || (value != null && value.compareTo(constraint.getKey())>=0);
        boolean satisfiesUpper = (constraint.getValue() == null)
                || (value != null && value.compareTo(constraint.getValue())<=0);

        return !satisfiesLower || !satisfiesUpper;
    }

    @Override
    public String toString() {
        return "min "+constraint.getKey()+" max "+constraint.getValue();
    }

    T getLower(){
        if (constraint != null)
            return constraint.getKey();
        return null;
    }

    T getUpper(){
        if (constraint != null)
            return constraint.getValue();
        return null;
    }
}
