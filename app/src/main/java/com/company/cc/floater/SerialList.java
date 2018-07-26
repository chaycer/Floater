package com.company.cc.floater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SerialList implements Serializable {
    private ArrayList<FilterSearch> list;

    public SerialList(ArrayList<FilterSearch> list){
        this.list = list;
    }

    public void add(FilterSearch o){
        list.add(o);
    }

    public void remove(FilterSearch o){
        list.remove(o);
    }

    public Iterator iterator(){
        return list.iterator();
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }

    public ArrayList<FilterSearch> getList() {
        return list;
    }
}
