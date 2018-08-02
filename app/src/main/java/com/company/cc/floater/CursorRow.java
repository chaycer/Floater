package com.company.cc.floater;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to represent a single row from a cursor. Serializable to allow passing between activities
 */
public class CursorRow implements Serializable {
    private ArrayList<String> columnNames;
    private ArrayList<String> values;
    private int size;

    public CursorRow(){
        this.columnNames = new ArrayList<>();
        this.values = new ArrayList<>();
        this.size = 0;
    }

    public CursorRow(Cursor result, int position){
        this.size = result.getColumnCount();
        this.columnNames = new ArrayList<>();
        this.values = new ArrayList<>();
        result.moveToPosition(position);
        for(int i = 0; i < this.size; i++){
            this.columnNames.add(result.getColumnName(i));
            this.values.add(result.getString(i));
        }
    }

    public CursorRow(Cursor result, int position, boolean filter){
        this.size = result.getColumnCount();
        this.columnNames = new ArrayList<>();
        this.values = new ArrayList<>();
        result.moveToPosition(position);
        for(int i = 0; i < this.size; i++){
            if (filter){
                String name = result.getColumnName(i);
                this.columnNames.add(name.substring(name.indexOf(".") + 1));
                this.values.add(result.getString(i));
            }
            else {
                this.columnNames.add(result.getColumnName(i));
                this.values.add(result.getString(i));
            }
        }
    }

    public ArrayList<String> getColumnNames(){
        return this.columnNames;
    }

    public ArrayList<String> getValues(){
        return this.values;
    }

    public int getSize(){
        return this.size;
    }

    public boolean isEmpty(){
        return size < 1;
    }

    public String getValueByColumnName(String columnName){
        int index = this.columnNames.indexOf(columnName);
        if (index < 0){
            return "";
        }
        return values.get(index);
    }

    public String getValueByIndex(int index){
        return this.values.get(index);
    }

    public String getColumnNameByIndex(int index){
        return this.columnNames.get(index);
    }
}
