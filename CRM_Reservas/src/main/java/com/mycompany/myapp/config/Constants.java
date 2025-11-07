package com.mycompany.myapp.config;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    private Constants() {}
}
