package com.zhouyiran.mytasks.data.local;

import android.provider.BaseColumns;

/**
 * Created by zhouyiran on 16/9/11.
 */
public final class TasksPersistenceContract {

    private TasksPersistenceContract() {}


    public static abstract class TaskEntry implements BaseColumns {

        public static final String TABLE_NAME = "task";

        public static final String COLUMN_NAME_ENTRY_ID = "entryid";

        public static final String COLUMN_NAME_TITLE = "title";

        public static final String COLUMN_NAME_DESCRIPTION = "title";

        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
