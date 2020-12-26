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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_DATA);
        onCreate(sqLiteDatabase);
    }

    public static class QRColumns implements BaseColumns {
        static final String TABLE_NAME = "Data";
        static final String COLUMN_NAME_SERVER_ID = "server ID";
        static final String COLUMN_NAME_MODULE = "Module name";
        static final String COLUMN_NAME_IN_DATA = "InputData";
        static final String COLUMN_NAME_IN_TYPE = "InputType";
        static final String COLUMN_NAME_OUT_DATA = "OutputData";
        static final String COLUMN_NAME_OUT_TYPE = "OutputType";
        static final String COLUMN_NAME_CREATE = "Create data";
        static final String COLUMN_NAME_Update = "Update data";
    }

    private static final String SQL_CREATE_DATA =
            "CREATE TABLE " + QRColumns.TABLE_NAME + " (" +
                    QRColumns._ID + " INTEGER PRIMARY KEY," +
                    QRColumns.COLUMN_NAME_SERVER_ID + " INTEGER," +
                    QRColumns.COLUMN_NAME_MODULE + " TEXT," +
                    QRColumns.COLUMN_NAME_IN_DATA + " BLOB," +
                    QRColumns.COLUMN_NAME_IN_TYPE + " TEXT," +
                    QRColumns.COLUMN_NAME_OUT_DATA + " BLOB," +
                    QRColumns.COLUMN_NAME_OUT_TYPE + " TEXT," +
                    QRColumns.COLUMN_NAME_CREATE + " DATE," +
                    QRColumns.COLUMN_NAME_Update + " DATE)";

    private static final String SQL_DELETE_DATA =
            "DROP TABLE IF EXISTS " + QRColumns.TABLE_NAME;
}