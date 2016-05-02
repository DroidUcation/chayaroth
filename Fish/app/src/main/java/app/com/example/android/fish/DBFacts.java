package app.com.example.android.fish;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sari on 18/04/2016.
 */
public class DBFacts extends SQLiteOpenHelper {
   private String sqlCreateSring= "CREATE TABLE "+AndroidContract.ContractDb.TABLE_NAME+
           " _id INT PRIMARY KEY     NOT NULL,"+
           AndroidContract.ContractDb.COLUMN_TEXT+ "TEXT, "+
          AndroidContract.ContractDb.COLUMN_IMG+ "INT"+
           " );";


    public DBFacts(Context context) {
        super(context, "FiveFacts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateSring);
        initDB(db);

    }
    public void initDB(SQLiteDatabase db)
    {

        ContentValues values = new ContentValues();
        values.put(AndroidContract.ContractDb.COLUMN_TEXT, R.string.one);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.ONE);
        db.insert(AndroidContract.ContractDb.TABLE_NAME, null, values);
        values.put(AndroidContract.ContractDb.COLUMN_TEXT, R.string.two);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.TWO);
        db.insert(AndroidContract.ContractDb.TABLE_NAME, null, values);
        values.put(AndroidContract.ContractDb.COLUMN_TEXT, R.string.three);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.THREE);
        db.insert(AndroidContract.ContractDb.TABLE_NAME, null, values);
        values.put(AndroidContract.ContractDb.COLUMN_TEXT, R.string.four);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.FOUR);
        db.insert(AndroidContract.ContractDb.TABLE_NAME, null, values);
        values.put(AndroidContract.ContractDb.COLUMN_TEXT, R.string.one);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.FIVE);
        db.insert(AndroidContract.ContractDb.TABLE_NAME, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
