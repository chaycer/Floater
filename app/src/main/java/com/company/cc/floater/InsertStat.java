package com.company.cc.floater;

public class InsertStat {
    private String table;
    private String column;
    private String value;

    public InsertStat(String table, String column, String value) {
        this.table = table;
        this.column = column;
        this.value = value;
    }
    public String getTable() {return this.table; }

    public String getColumn() { return this.column; }

    public String getValue() {return this.value;}
}
