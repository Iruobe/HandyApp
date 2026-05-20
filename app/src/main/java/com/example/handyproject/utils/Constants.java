package com.example.handyproject.utils;

public class Constants {

    // Firestore collections
    public static final String COLLECTION_USERS    = "users";
    public static final String COLLECTION_BOOKINGS = "bookings";
    public static final String COLLECTION_MESSAGES = "messages";

    // User roles
    public static final String ROLE_HANDYMAN = "handyman";
    public static final String ROLE_CUSTOMER = "customer";

    // Firestore field names — shared
    public static final String FIELD_UID          = "uid";
    public static final String FIELD_ROLE         = "role";
    public static final String FIELD_FULL_NAME    = "fullName";
    public static final String FIELD_EMAIL        = "email";
    public static final String FIELD_PHONE        = "phoneNumber";
    public static final String FIELD_LOCATION     = "location";
    public static final String FIELD_CREATED_AT   = "createdAt";

    // Firestore field names — handyman only
    public static final String FIELD_SERVICE_CATEGORY    = "serviceCategory";
    public static final String FIELD_SERVICE_DESCRIPTION = "serviceDescription";
    public static final String FIELD_YEARS_EXPERIENCE    = "yearsOfExperience";
    public static final String FIELD_HOURLY_RATE         = "hourlyRate";
    public static final String FIELD_RATING              = "rating";
    public static final String FIELD_TOTAL_JOBS          = "totalJobs";
    public static final String FIELD_PROFILE_VIEWS       = "profileViews";
    public static final String FIELD_AVAILABLE_FOR_HIRE  = "availableForHire";
    public static final String FIELD_PORTFOLIO_PHOTOS    = "portfolioPhotos";

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "app_channel";

    // Query limits
    public static final int HANDYMAN_LIST_LIMIT = 10;
}
