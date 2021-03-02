package com.dilip.cloudattendance.helper;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;


public class Functions {

    //Main URL
    private static String MAIN_URL = "**** Enter you Server URL Here ******";

    // Login URL
    public static String LOGIN_URL = MAIN_URL + "login.php";

    public static String Start_Attendance_URL = MAIN_URL + "StartAttendance.php";

    public static String VIEW_TODAY_ATTENDANCE_URL = MAIN_URL + "ViewTodayAttendance.php";

    public static String DELETE_ATTENDANCE_URL = MAIN_URL + "DeleteUsersScripts/DeleteAttendance.php";

    public static String UPLOAD_PROFILE_URL = MAIN_URL + "upload_profile.php";

    public static String UPLOAD_FETCH_PROFILE_URL = MAIN_URL + "fetch_profile.php";

    public static String VIEW_STUDNETS_URL = MAIN_URL + "ViewStudents.php";

    public static String VIEW_BLOCK_STUDNETS_URL = MAIN_URL + "ViewBlockedStudents.php";

    public static String VIEW_UNBLOCK_STUDNETS_URL = MAIN_URL + "ViewUnblockedStudents.php";

    public static String DELETE_STUDNET_VIEW_URL = MAIN_URL + "DeleteUsersScripts/ViewStudents.php";

    public static String VIEW_FACULTIES_URL = MAIN_URL + "DeleteUsersScripts/ViewFaculties.php";

    public static String BLOCK_STUDNETS_URL = MAIN_URL + "BlockStudents.php";

    public static String UNBLOCK_STUDNETS_URL = MAIN_URL + "UnblockStudents.php";

    public static String VALIDATE_STUDNETS_URL = MAIN_URL + "ValidateStudents.php";

    public static String Student_Attendance_URL = MAIN_URL + "StudentAttendance.php";

    public static String INSERT_STUDENTS_URL = MAIN_URL + "InsertStudents.php";

    public static String DELETE_USERS_URL = MAIN_URL + "DeleteUsersScripts/DeleteUsers.php";

    public static String ADD_USER_URL = MAIN_URL + "AddUser.php";

    public static String FEEDBACK_URL = MAIN_URL + "feedback.php";

    public static String VIEW_FEEDBACK_URL = MAIN_URL + "ViewFeedback.php";

    public static String VIEW_FEEDBACK_COMMENTS_URL = MAIN_URL + "ViewComments.php";

    public static String CHECK_FEEDBACK_URL = MAIN_URL + "feedback_status.php";

    public static String START_FEEDBACK_URL = MAIN_URL + "start_feedback.php";

    // Forgot Password
    public static String RESET_PASS_URL = MAIN_URL + "reset-password.php";

    public static String Stop_Atten_URL = MAIN_URL + "StopAtten.php";

    public static String UPDATE_QR_URL = MAIN_URL + "UpdateQR.php";


    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
