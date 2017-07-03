package com.example.anne.otp_android_client_v3.model;

import android.provider.BaseColumns;

/**
 * Created by Anne on 6/29/2017.
 */

public final class Contract {

    private Contract() {} // prevent instantiation of contract class

    public static class SearchHistoryTable implements BaseColumns {

        public static final String TABLE_NAME = "search_history";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_FROM_COORDINATES = "from_coords";
        public static final String COLUMN_NAME_TO_COORDINATES = "to_coords";
        public static final String COLUMN_NAME_FROM_NAME = "from_name";
        public static final String COLUMN_NAME_TO_NAME = "to_name";
        public static final String COLUMN_NAME_MODES = "modes";

    }

}
