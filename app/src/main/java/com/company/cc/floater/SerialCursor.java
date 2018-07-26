package com.company.cc.floater;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SerialCursor implements Serializable {
    private Cursor cursor;

    public SerialCursor(Cursor cursor){
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void close(){
        cursor.close();
    }
}
