package pl.awnar.DataScanner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.awnar.DataScanner.api.model.Data;
import pl.awnar.DataScanner.api.model.home;

public class DB extends SQLiteOpenHelper {
    static final private Integer DB_VER = 1;
    private static final String DATABASE_NAME = "Data.db";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MODULE);
        sqLiteDatabase.execSQL(SQL_CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATA);
        sqLiteDatabase.execSQL(SQL_DELETE_MODULE);
        onCreate(sqLiteDatabase);
    }

    public void Module(home data) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] columns = {ModuleColumns._ID, ModuleColumns.COLUMN_NAME_NAME, ModuleColumns.COLUMN_NAME_URL};
        @SuppressLint("Recycle") Cursor cursor = DB.query(ModuleColumns.TABLE_NAME, columns, null, null, null, null, null);

        Map<String, Map<String, String>> dbmap = new TreeMap<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex(ModuleColumns._ID)));
                String name = cursor.getString(cursor.getColumnIndex(ModuleColumns.COLUMN_NAME_NAME));
                String url = cursor.getString(cursor.getColumnIndex(ModuleColumns.COLUMN_NAME_URL));
                Map<String, String> tmp = new TreeMap<>();
                tmp.put("name", name);
                tmp.put("url", url);
                dbmap.put(id, tmp);
            } while (cursor.moveToNext());
        } else {
            for (Map.Entry<String, Map<String, String>> entry : data.endpoints.entrySet()) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(ModuleColumns._ID, entry.getKey());
                contentValue.put(ModuleColumns.COLUMN_NAME_NAME, entry.getValue().get("name"));
                contentValue.put(ModuleColumns.COLUMN_NAME_URL, entry.getValue().get("url"));
                contentValue.put(ModuleColumns.COLUMN_NAME_LAST_SYNC, 0);
                DB.insert(ModuleColumns.TABLE_NAME, null, contentValue);
            }
            return;
        }

        if (dbmap.equals(data.endpoints))
            return;

        Set<String> keysData = data.endpoints.keySet();
        Set<String> keysDB = dbmap.keySet();
        keysData.removeAll(dbmap.keySet());
        keysDB.removeAll(data.endpoints.keySet());

        for (String s : keysDB) {
            DB.delete(DataColumns.TABLE_NAME, DataColumns.COLUMN_NAME_MODULE + "=" + s, null);
            DB.delete(ModuleColumns.TABLE_NAME, ModuleColumns._ID + "=" + s, null);
        }

        for (String s : keysData) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(ModuleColumns._ID, s);
            Map<String, String> tmp = data.endpoints.get(s);
            contentValue.put(ModuleColumns.COLUMN_NAME_NAME, tmp.get("name"));
            contentValue.put(ModuleColumns.COLUMN_NAME_URL, tmp.get("url"));
            contentValue.put(ModuleColumns.COLUMN_NAME_LAST_SYNC, 0);
            DB.insert(ModuleColumns.TABLE_NAME, null, contentValue);
        }

        for (Map.Entry<String, Map<String, String>> stringMapEntry : data.endpoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ModuleColumns.COLUMN_NAME_NAME, stringMapEntry.getValue().get("name"));
            contentValues.put(ModuleColumns.COLUMN_NAME_URL, stringMapEntry.getValue().get("url"));
            DB.update(ModuleColumns.TABLE_NAME, contentValues, ModuleColumns._ID + " = " + stringMapEntry.getKey(), null);
        }
    }

    public Map<Integer, String[]> getModules() {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] columns = {ModuleColumns._ID, ModuleColumns.COLUMN_NAME_NAME, ModuleColumns.COLUMN_NAME_URL, ModuleColumns.COLUMN_NAME_LAST_SYNC};
        @SuppressLint("Recycle") Cursor cursor = DB.query(ModuleColumns.TABLE_NAME, columns, null, null, null, null, null);

        Map<Integer, String[]> dbmap = new TreeMap<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(ModuleColumns._ID));
                String name = cursor.getString(cursor.getColumnIndex(ModuleColumns.COLUMN_NAME_NAME));
                String url = cursor.getString(cursor.getColumnIndex(ModuleColumns.COLUMN_NAME_URL));
                String date = Long.toString(cursor.getLong(cursor.getColumnIndex(ModuleColumns.COLUMN_NAME_LAST_SYNC)));
                String[] result = {name, url, date};
                dbmap.put(id, result);
            } while (cursor.moveToNext());
        }
        return dbmap;
    }

    public void Clear() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL(SQL_DELETE_DATA);
        DB.execSQL(SQL_DELETE_MODULE);
        onCreate(DB);
    }

    public void ClearSynchr() {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModuleColumns.COLUMN_NAME_LAST_SYNC, 0);
        DB.update(ModuleColumns.TABLE_NAME, contentValues, null, null);
    }

    public void lastSync(String moduleurl, String time) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModuleColumns.COLUMN_NAME_LAST_SYNC, time);
        DB.update(ModuleColumns.TABLE_NAME, contentValues, ModuleColumns.COLUMN_NAME_URL + " = ?", new String[]{moduleurl});
    }

    public void updateData(String moduleurl, Data data) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] columns = {ModuleColumns._ID, ModuleColumns.COLUMN_NAME_URL};

        Cursor module = DB.query(ModuleColumns.TABLE_NAME, columns, ModuleColumns.COLUMN_NAME_URL + " = ?", new String[]{moduleurl}, null, null, null);
        if (module.getCount() <= 0)
            return;
        module.moveToFirst();
        String moduleId = String.valueOf(module.getInt(module.getColumnIndex(ModuleColumns._ID)));

        columns = new String[]{DataColumns._ID, DataColumns.COLUMN_NAME_SERVER_ID};
        Cursor cursor = DB.query(DataColumns.TABLE_NAME, columns, DataColumns.COLUMN_NAME_MODULE + " = ? AND " + DataColumns.COLUMN_NAME_SERVER_ID + " = ?", new String[]{moduleId, data.server_ID}, null, null, null);
        if (cursor.getCount() <= 0) {
            // instert
            ContentValues contentValue = new ContentValues();
            contentValue.put(DataColumns.COLUMN_NAME_IN_DATA, data.in_blob);
            contentValue.put(DataColumns.COLUMN_NAME_IN_TYPE, data.in_blob_type);
            contentValue.put(DataColumns.COLUMN_NAME_OUT_DATA, data.out_blob);
            contentValue.put(DataColumns.COLUMN_NAME_OUT_TYPE, data.out_blob_type);
            contentValue.put(DataColumns.COLUMN_NAME_CREATE, data.create);
            contentValue.put(DataColumns.COLUMN_NAME_Update, data.update);
            contentValue.put(DataColumns.COLUMN_NAME_SERVER_ID, data.server_ID);
            contentValue.put(DataColumns.COLUMN_NAME_MODULE, moduleId);
            DB.insert(DataColumns.TABLE_NAME, null, contentValue);
        }
        //update
        ContentValues contentValue = new ContentValues();
        contentValue.put(DataColumns.COLUMN_NAME_IN_DATA, data.in_blob);
        contentValue.put(DataColumns.COLUMN_NAME_IN_TYPE, data.in_blob_type);
        contentValue.put(DataColumns.COLUMN_NAME_OUT_DATA, data.out_blob);
        contentValue.put(DataColumns.COLUMN_NAME_OUT_TYPE, data.out_blob_type);
        contentValue.put(DataColumns.COLUMN_NAME_CREATE, data.create);
        contentValue.put(DataColumns.COLUMN_NAME_Update, data.update);
        contentValue.put(DataColumns.COLUMN_NAME_SERVER_ID, data.server_ID);
        DB.update(DataColumns.TABLE_NAME, contentValue, DataColumns.COLUMN_NAME_SERVER_ID + " = ? AND " + DataColumns.COLUMN_NAME_MODULE + " = ?", new String[]{data.server_ID, moduleId});
    }

    public List<Data> getData(String moduleurl) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] columns = {ModuleColumns._ID, ModuleColumns.COLUMN_NAME_URL};

        Cursor module = DB.query(ModuleColumns.TABLE_NAME, columns, ModuleColumns.COLUMN_NAME_URL + " = ?", new String[]{moduleurl}, null, null, null);
        if (module.getCount() <= 0)
            return null;
        module.moveToFirst();
        String moduleId = String.valueOf(module.getInt(module.getColumnIndex(ModuleColumns._ID)));

        columns = new String[]{DataColumns.COLUMN_NAME_SERVER_ID, DataColumns.COLUMN_NAME_CREATE, DataColumns.COLUMN_NAME_IN_TYPE, DataColumns.COLUMN_NAME_IN_DATA, DataColumns.COLUMN_NAME_OUT_TYPE, DataColumns.COLUMN_NAME_OUT_DATA};
        Cursor data = DB.query(DataColumns.TABLE_NAME, columns, DataColumns.COLUMN_NAME_MODULE + " = ?", new String[]{moduleId}, null, null, DataColumns.COLUMN_NAME_Update + " DESC");

        List<Data> dbmap = new LinkedList<>();
        if (data.getCount() > 0) {
            data.moveToFirst();
            do {
                Data darray = new Data();
                darray.server_ID = Integer.toString(data.getColumnIndex(DataColumns.COLUMN_NAME_SERVER_ID));
                darray.create = data.getString(data.getColumnIndex(DataColumns.COLUMN_NAME_CREATE));
                darray.in_blob_type = data.getString(data.getColumnIndex(DataColumns.COLUMN_NAME_IN_TYPE));
                darray.in_blob = data.getString(data.getColumnIndex(DataColumns.COLUMN_NAME_IN_DATA));
                darray.out_blob_type = data.getString(data.getColumnIndex(DataColumns.COLUMN_NAME_OUT_TYPE));
                darray.out_blob = data.getString(data.getColumnIndex(DataColumns.COLUMN_NAME_OUT_DATA));
                dbmap.add(darray);
            } while (data.moveToNext());
        }
        return dbmap;
    }

    private static class DataColumns implements BaseColumns {
        static final String TABLE_NAME = "Data";
        static final String COLUMN_NAME_SERVER_ID = "server_ID";
        static final String COLUMN_NAME_MODULE = "Module_ID";
        static final String COLUMN_NAME_IN_DATA = "InputData";
        static final String COLUMN_NAME_IN_TYPE = "InputType";
        static final String COLUMN_NAME_OUT_DATA = "OutputData";
        static final String COLUMN_NAME_OUT_TYPE = "OutputType";
        static final String COLUMN_NAME_CREATE = "CreateData";
        static final String COLUMN_NAME_Update = "UpdateData";
    }

    private static class ModuleColumns implements BaseColumns {
        static final String TABLE_NAME = "Module";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_URL = "url";
        static final String COLUMN_NAME_LAST_SYNC = "sync";
    }

    private static final String SQL_CREATE_DATA =
            "CREATE TABLE " + DataColumns.TABLE_NAME + " (" +
                    DataColumns._ID + " INTEGER PRIMARY KEY," +
                    DataColumns.COLUMN_NAME_SERVER_ID + " INTEGER UNIQUE," +
                    DataColumns.COLUMN_NAME_MODULE + " INTEGER REFERENCES " + ModuleColumns.TABLE_NAME + " (" + ModuleColumns._ID + ")," +
                    DataColumns.COLUMN_NAME_IN_DATA + " BLOB," +
                    DataColumns.COLUMN_NAME_IN_TYPE + " TEXT," +
                    DataColumns.COLUMN_NAME_OUT_DATA + " BLOB," +
                    DataColumns.COLUMN_NAME_OUT_TYPE + " TEXT," +
                    DataColumns.COLUMN_NAME_CREATE + " DATE," +
                    DataColumns.COLUMN_NAME_Update + " DATE)";

    private static final String SQL_CREATE_MODULE =
            "CREATE TABLE " + ModuleColumns.TABLE_NAME + " (" +
                    ModuleColumns._ID + " INTEGER PRIMARY KEY, " +
                    ModuleColumns.COLUMN_NAME_NAME + " TEXT, " +
                    ModuleColumns.COLUMN_NAME_URL + " TEXT UNIQUE, " +
                    ModuleColumns.COLUMN_NAME_LAST_SYNC + " DATE)";

    private static final String SQL_DELETE_DATA =
            "DROP TABLE IF EXISTS " + DataColumns.TABLE_NAME;

    private static final String SQL_DELETE_MODULE =
            "DROP TABLE IF EXISTS " + ModuleColumns.TABLE_NAME;
}