package com.example.handyproject.utils;

public class Constants {

    // Firestore collections
    public static final String COLLECTION_USERS         = "users";
    public static final String COLLECTION_BOOKINGS      = "bookings";
    public static final String COLLECTION_MESSAGES      = "messages";
    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_CONVERSATIONS = "conversations";

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
    public static final String FIELD_REVIEW_COUNT        = "reviewCount";
    public static final String FIELD_TOTAL_JOBS          = "totalJobs";
    public static final String FIELD_PROFILE_VIEWS       = "profileViews";
    public static final String FIELD_AVAILABLE_FOR_HIRE  = "availableForHire";
    public static final String FIELD_PORTFOLIO_PHOTOS    = "portfolioPhotos";
    public static final String FIELD_SERVICES_OFFERED    = "servicesOffered";
    public static final String FIELD_BIO                 = "bio";
    public static final String FIELD_RESPONSE_TIME       = "responseTime";

    public static final String[] RESPONSE_TIME_OPTIONS = {
            "Within 1 hour", "Within a few hours", "Same day", "1-2 days"
    };
    public static final String DEFAULT_RESPONSE_TIME = "Within a few hours";
    public static final int MAX_SERVICES = 5;
    public static final int MAX_PORTFOLIO_PHOTOS = 4;

    // Firestore field names — notifications
    public static final String FIELD_USER_ID   = "userId";
    public static final String FIELD_TYPE      = "type";
    public static final String FIELD_TITLE     = "title";
    public static final String FIELD_BODY      = "body";
    public static final String FIELD_READ      = "read";
    public static final String FIELD_TIMESTAMP = "timestamp";

    // Notification types
    public static final String TYPE_BOOKING = "booking";
    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_SYSTEM  = "system";

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "app_channel";

    // Firestore field names — conversations
    public static final String FIELD_PARTICIPANT_IDS         = "participantIds";
    public static final String FIELD_PARTICIPANT_NAMES       = "participantNames";
    public static final String FIELD_LAST_MESSAGE            = "lastMessage";
    public static final String FIELD_LAST_MESSAGE_TIMESTAMP  = "lastMessageTimestamp";

    // Firestore field names — messages subcollection
    public static final String FIELD_SENDER_ID = "senderId";
    public static final String FIELD_SENT_AT   = "sentAt";

    // Firestore field names — booking-type messages
    public static final String FIELD_BOOKING_SCHEDULED_AT  = "bookingScheduledAt";
    public static final String FIELD_BOOKING_ADDRESS       = "bookingAddress";
    public static final String FIELD_BOOKING_NOTES         = "bookingNotes";
    public static final String FIELD_BOOKING_STATUS        = "bookingStatus";
    public static final String FIELD_BOOKING_QUOTE_AMOUNT  = "bookingQuoteAmount";

    // Booking status values
    public static final String BOOKING_STATUS_PENDING    = "pending";
    public static final String BOOKING_STATUS_CONFIRMED  = "confirmed";
    public static final String BOOKING_STATUS_DENIED     = "denied";

    // Firestore collections — reviews
    public static final String COLLECTION_REVIEWS    = "reviews";

    // Firestore field names — reviews
    public static final String FIELD_HANDYMAN_ID     = "handymanId";
    public static final String FIELD_CUSTOMER_ID     = "customerId";
    public static final String FIELD_CUSTOMER_NAME   = "customerName";
    public static final String FIELD_REVIEW_TEXT     = "text";
    // FIELD_RATING = "rating" and FIELD_CREATED_AT = "createdAt" already defined above

    // Message body types (distinct from the notification TYPE_* constants above)
    public static final String TYPE_TEXT = "text";

    // Query limits
    public static final int HANDYMAN_LIST_LIMIT = 10;

    // SharedPreferences
    public static final String PREFS_NAME    = "handy_prefs";
    public static final String PREF_KEY_ROLE = "user_role";
}
