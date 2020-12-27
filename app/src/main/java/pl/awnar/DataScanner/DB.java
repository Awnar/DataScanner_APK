package pl.awnar.DataScanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DB extends SQLiteOpenHelper {
    static final private Integer DB_VER = 1;
    private static final String DATABASE_NAME = "Data.db";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_DATA);
        sqLiteDatabase.execSQL(SQL_CREATE_MODULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATA);
        sqLiteDatabase.execSQL(SQL_DELETE_MODULE);
        onCreate(sqLiteDatabase);
    }

    public static class DataColumns implements BaseColumns {
        static final String TABLE_NAME = "Data";
        static final String COLUMN_NAME_SERVER_ID = "server ID";
        static final String COLUMN_NAME_MODULE = "Module ID";
        static final String COLUMN_NAME_IN_DATA = "InputData";
        static final String COLUMN_NAME_IN_TYPE = "InputType";
        static final String COLUMN_NAME_OUT_DATA = "OutputData";
        static final String COLUMN_NAME_OUT_TYPE = "OutputType";
        static final String COLUMN_NAME_CREATE = "Create data";
        static final String COLUMN_NAME_Update = "Update data";
    }

    public static class ModuleColumns implements BaseColumns {
        static final String TABLE_NAME = "Module";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_URL = "url";
    }

    private static final String SQL_CREATE_DATA =
            "CREATE TABLE " + DataColumns.TABLE_NAME + " (" +
                    DataColumns._ID + " INTEGER PRIMARY KEY," +
                    DataColumns.COLUMN_NAME_SERVER_ID + " INTEGER UNIQUE," +
                    DataColumns.COLUMN_NAME_MODULE + " INTEGER REFERENCES "+ModuleColumns.TABLE_NAME+" ("+ModuleColumns._ID+")," +
                    DataColumns.COLUMN_NAME_IN_DATA + " BLOB," +
                    DataColumns.COLUMN_NAME_IN_TYPE + " TEXT," +
                    DataColumns.COLUMN_NAME_OUT_DATA + " BLOB," +
                    DataColumns.COLUMN_NAME_OUT_TYPE + " TEXT," +
                    DataColumns.COLUMN_NAME_CREATE + " DATE," +
                    DataColumns.COLUMN_NAME_Update + " DATE)";

    private static final String SQL_CREATE_MODULE =
            "CREATE TABLE " + ModuleColumns.TABLE_NAME + " (" +
                    ModuleColumns._ID + " INTEGER PRIMARY KEY," +
                    ModuleColumns.COLUMN_NAME_NAME + " TEXT," +
                    ModuleColumns.COLUMN_NAME_URL + " TEXT UNIQUE";

    private static final String SQL_DELETE_DATA =
            "DROP TABLE IF EXISTS " + DataColumns.TABLE_NAME;

    private static final String SQL_DELETE_MODULE =
            "DROP TABLE IF EXISTS " + ModuleColumns.TABLE_NAME;
}