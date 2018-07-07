package ccenterprises.floater;


public class FilterSearch {
    String stat;
    String operator;
    String value;

    //constructor
    public FilterSearch(String stat, String operator, String value){
        this.stat = stat;
        this.operator = operator;
        this.value = value;
    }

    public String getStat(){
        return stat;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
}


