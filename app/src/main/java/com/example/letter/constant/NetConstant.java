package com.example.letter.constant;

public class NetConstant {

    private static String baseURL = "http://192.168.43.5:8081";
    private static String codeURL = baseURL + "/getVfCode";
    private static String registerURL = baseURL + "/register";
    private static String loginURL = baseURL + "/login";
    private static String sendLetterURL = baseURL + "/letter/send";
    private static String getLetterURL = baseURL + "/letter/get";
    private static String getPenPalInfoURL = baseURL + "/penPal/get";
    private static String changeInformationURL = baseURL + "/penPal/changeInformation";
    private static String findPenPalURL = baseURL + "/penPal/find";
    private static String email = MyApp.getInstance().getSharedPreferences().getString("token_email", "");

    public static String getCodeURL() {
        return codeURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getSendLetterURL() {
        return sendLetterURL;
    }

    public static String getChangeInformationURL() {
        return changeInformationURL;
    }

    public static String getLetterURL() {
        return getLetterURL + "?addressee=" + email;
    }

    public static String getPenPalInfoURL() {
        return getPenPalInfoURL + "?email=" + email;
    }

    public static String getFindPenPalURL() {
        return findPenPalURL + "?id=" + email;
    }
}
