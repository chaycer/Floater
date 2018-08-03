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

    /**
     * Creates a row given a cursor and position
     * @param result - cursor to get the row from
     * @param position - position of the cursor
     */
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

    /**
     * Creates a row, excluding the columns listed in the exclude parameter
     * @param result - cursor to get the row from
     * @param position - position of the cursor
     * @param exclude - array of column names to exclude
     */
    public CursorRow(Cursor result, int position, String[] exclude){
        this.size = 0;
        int max = result.getColumnCount();
        this.columnNames = new ArrayList<>();
        this.values = new ArrayList<>();
        result.moveToPosition(position);
        for(int i = 0; i < max; i++){
            boolean remove = false;
            for (int j = 0; j < exclude.length; j++){
                if (result.getColumnName(i).equals(exclude[j])){
                    remove = true;
                }
            }
            if (!remove) {
                this.size++;
                this.columnNames.add(result.getColumnName(i));
                this.values.add(result.getString(i));
            }
        }
    }

    /**
     * Creates a row with the column name meant to be used as a search filter
     * @param result - cursor to get the row from
     * @param position - position of the cursor
     * @param filter - is this a filter?
     */
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

    /**
     * Returns the value of the row at the specified column
     * @param columnName - name of the column
     * @return Value at the given column, "" if column isn't found
     */
    public String getValueByColumnName(String columnName){
        int index = this.columnNames.indexOf(columnName);
        if (index < 0){
            return "";
        }
        return values.get(index);
    }

    /**
     * Gets the value of the row at a specified index
     * @param index - index where the value is found
     * @return Value at the given column
     */
    public String getValueByIndex(int index){
        return this.values.get(index);
    }

    /**
     * Gets the column name of the row at a specified index
     * @param index - index where the column is found
     * @return Name at the given column
     */
    public String getColumnNameByIndex(int index){
        return this.columnNames.get(index);
    }
}
