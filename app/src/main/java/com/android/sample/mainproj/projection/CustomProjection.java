package com.android.sample.mainproj.projection;

import android.net.Uri;

import com.android.sample.mainproj.BuildConfig;

public class CustomProjection {

    public static final String AUTHORITY = "com.android.sample.Subproj";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final class ContactData
    {
        public static final String PATH = "contact";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        public static final String CONTACT_ID = "ID";

        public static final String CONTACT_NAME = "NAME";

        public static final String CONTACT_NUMBER = "NUMBER";
    }

    public static final class MapData
    {
        public static final String PATH = "map";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        public static final String MAP_ID = "ID";

        public static final String MAP_X_POS = "X_POS";

        public static final String MAP_Y_POS = "Y_POS";
    }
}
