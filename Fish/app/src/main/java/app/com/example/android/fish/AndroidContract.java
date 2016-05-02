package app.com.example.android.fish;

import android.provider.BaseColumns;

/**
 * Created by sari on 21/04/2016.
 */
public class AndroidContract {
    public static class ContractDb implements BaseColumns{
        public static final String TABLE_NAME="FiveFacts";
        public static final String COLUMN_TEXT="text";
        public static final String COLUMN_IMG="img";


    }
}
