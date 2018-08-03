package com.company.cc.floater;

import java.util.ArrayList;

public class ValidDictionary {
    private ArrayList<String> columns;
    private ArrayList<Integer> types;

    final private int NUMBER = 100;
    final private int WORD = 200;
    final private int DOUBLE = 300;
    final private int DATE = 400;

    public ValidDictionary(){
        this.columns = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    public void add(String column, int type){
        if (columns.indexOf(column) >= 0){
            return;
        }
        this.columns.add(column);
        this.types.add(type);
    }

    public void addNumber(String column){
        add(column, NUMBER);
    }

    public void addWord(String column){
        add(column, WORD);
    }

    public void addDouble(String column){
        add(column, DOUBLE);
    }

    public void addDate(String column){
        add(column, DATE);
    }

    public int getType(String column){
        int index = columns.indexOf(column);
        if (index < 0){
            return index;
        }
        return this.types.get(index);
    }

    public boolean isValid(String column, String value){
        if (value.equals("")){
            return true;
        }
        int type = getType(column);

        switch (type) {
            case NUMBER: return isNumber(value);
            case WORD: return isAlphanumeric(value);
            case DOUBLE: return isDouble(value);
            case DATE: return isDate(value);
            default: return false;
        }
    }

    public boolean isNumber(String value){
        for (int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    public boolean isAlphanumeric(String value){
        for (int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            if (!Character.isLetterOrDigit(c)){
                return false;
            }
        }
        return true;
    }

    public boolean isDouble(String value){
        for (int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            if (!Character.isDigit(c)){
                if (c != '.'){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isDate(String value){
        for (int i = 0; i < value.length(); i++){
            char c = value.charAt(i);
            if (!Character.isDigit(c)){
                if (c != '-' && c!= '/'){
                    return false;
                }
            }
        }
        return true;
    }
}
