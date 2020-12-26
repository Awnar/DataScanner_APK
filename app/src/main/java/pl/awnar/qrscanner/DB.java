package pl.awnar.qrscanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DB extends SQLiteOpenHelper {
    static final private Integer DB_VER = 1;
    private static final String DATABASE_NAME = "QR.db";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_QR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_QR);
        onCreate(sqLiteDatabase);
    }

    public static class QRColumns implements BaseColumns {
        static final String TABLE_NAME = "QR";
        static final String COLUMN_NAME_SERVER_ID = "server ID";
        static final String COLUMN_NAME_QR = "QR code";
        static final String COLUMN_NAME_TEXT = "text";
        static final String COLUMN_NAME_DATE = "date";
    }

    private static final String SQL_CREATE_QR =
            "CREATE TABLE " + QRColumns.TABLE_NAME + " (" +
                    QRColumns._ID + " INTEGER PRIMARY KEY," +
                    QRColumns.COLUMN_NAME_SERVER_ID + " INTEGER," +
                    QRColumns.COLUMN_NAME_QR + " BLOB," +
                    QRColumns.COLUMN_NAME_TEXT + " TEXT," +
                    QRColumns.COLUMN_NAME_DATE + " DATE)";

    private static final String SQL_DELETE_QR =
            "DROP TABLE IF EXISTS " + QRColumns.TABLE_NAME;
}