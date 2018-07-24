package com.company.cc.floater;


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

    //constructor
    public FilterSearch(String stat, String operator, String value, int type){
        if (type == FloaterApplication.BATTING){
            stat = "batting." + stat;
        }
        else if(type == FloaterApplication.PITCHING){
            stat = "pitching." + stat;
        }
        else if(type == FloaterApplication.FIELDING){
            stat = "fielding." + stat;
        }
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

    public String toString() {
        return stat + operator + value;
    }
}


