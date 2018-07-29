package com.company.cc.floater;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class UpdateERAAsync extends AsyncTask<SQLiteDatabase, Boolean, Boolean> {
    protected Boolean doInBackground(SQLiteDatabase... db) {
        boolean result = updateERA(db[0]);
        return result;
        }

    private Boolean updateERA(SQLiteDatabase db){
        Cursor eras = db.rawQuery("SELECT distinct pitching.er, pitching.ip FROM pitching WHERE NOT EXISTS (SELECT ERA_Stats.er, ERA_Stats.ip FROM ERA_Stats WHERE pitching.er = ERA_Stats.er AND pitching.ip = ERA_Stats.ip)", null);
        eras.moveToFirst();
        if (eras.getCount() < 1) {
            return false;
        }
        do {
            String er = eras.getString(eras.getColumnIndex("pitching.er"));
            String ip = eras.getString(eras.getColumnIndex("pitching.ip"));
            Integer era = Integer.getInteger(er)/Integer.getInteger(ip) * 9;
            db.execSQL(String.format("INSERT INTO ERA_Stats (ER,IP,ERA) VALUES ('%s','%s','%s')",er,ip,era.toString()));
        } while (eras.moveToNext() != false);

        return true;
    }
}
