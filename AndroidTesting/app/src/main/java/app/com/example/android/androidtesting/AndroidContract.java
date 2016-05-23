package app.com.example.android.androidtesting;

import android.net.Uri;
        import android.provider.BaseColumns;

/**
 * Created by sari on 21/04/2016.
 */
public class AndroidContract {
    public static class ContractDb implements BaseColumns{
        public static final String TABLE_NAME="FiveFacts";
        public static final String COLUMN_TEXT="text";
        public static final String COLUMN_IMG="img";
        public static final String COLUMN_TITLE="title";
        public static final String AUTHORITY = "content://app.com.example.android.androidtesting.FactsProvider/DBFiveFacts";
        public static final Uri URI = Uri.parse(AUTHORITY);

    }
}
