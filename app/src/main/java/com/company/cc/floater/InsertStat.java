package com.company.cc.floater;

public class InsertStat {
    private String column;
    private String value;

    public InsertStat(String column, String value) {
        this.column = column;
        this.value = value;
    }

    public String getColumn() { return this.column; }

    public String getValue() {return this.value;}
}
