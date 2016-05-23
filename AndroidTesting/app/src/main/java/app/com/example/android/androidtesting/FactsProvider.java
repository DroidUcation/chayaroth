
package app.com.example.android.androidtesting;
        import android.content.ContentProvider;
        import android.content.ContentUris;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.UriMatcher;
        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.database.sqlite.SQLiteQueryBuilder;
        import android.net.Uri;
        import android.support.annotation.Nullable;


public class FactsProvider extends ContentProvider {

    private SQLiteDatabase db;

    private static class DBFiveFacts extends SQLiteOpenHelper {
        public DBFiveFacts(Context context) {
            super(context, "FiveFacts.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sqlCreateSring= "CREATE TABLE "+AndroidContract.ContractDb.TABLE_NAME+
                    "( _id INTEGER PRIMARY KEY     NOT NULL, "+
                    AndroidContract.ContractDb.COLUMN_TITLE+" TEXT,"+
                    AndroidContract.ContractDb.COLUMN_TEXT+ " TEXT, "+
                    AndroidContract.ContractDb.COLUMN_IMG+ " INT "+
                    " )";
            db.execSQL(sqlCreateSring);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + AndroidContract.ContractDb.TABLE_NAME);
            onCreate(db);
        }
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBFiveFacts dbFiveFacts = new DBFiveFacts(context);
        db = dbFiveFacts.getWritableDatabase();
        return (db != null);

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(AndroidContract.ContractDb.TABLE_NAME);
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        //cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(AndroidContract.ContractDb.TABLE_NAME,null, values);
        if (rowID > 0) {
            return null;
        }
        throw new SQLException("Unable to add a new fact record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}



