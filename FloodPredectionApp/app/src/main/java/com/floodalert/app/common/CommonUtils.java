package com.floodalert.app.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static String mSeparatorMiddleDot = "\u2022";
    public static String mCenterKey = "<@>";
    public static String mLastKey = "<&>";

    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable(Context context)
    {
        boolean isConnected = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        isConnected = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isConnected = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        isConnected = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                        isConnected = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        isConnected = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                        isConnected = true;
                    }
                }
            }
            else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork!=null)
                {
                    isConnected = activeNetwork.isConnectedOrConnecting();
                }
            }
        }
        catch (NullPointerException e){}
        catch (SecurityException e){}
        catch (Exception e){}


        return isConnected;
    }

    public static String formatHTML(String text) {
        return String.valueOf(formatHTML(text, Html.FROM_HTML_MODE_COMPACT));
    }

    public static Spanned formatHTML(String text, int flags) {
        if(text==null) text = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, flags);
        }
        else
        {
            return Html.fromHtml(text);
        }
    }

    public static String getRandomString(final int sizeOfRandomString)
    {
        final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static boolean isEmptyStr(String str) {
        return str == null || "".equals(str) || "null".equalsIgnoreCase(str);
    }

    public static String normalizeText(String str) {
        return str!=null && false=="".equals(str) ? str : "";
    }

    public final static boolean isValidEmail(String target) {
        target = target!=null ? target.trim() : target;
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public final static boolean isValidPassword(String password) {

      /*(?=.*[0-9])       # a digit must occur at least once
        (?=.*[a-z])       # a lower case letter must occur at least once
        (?=.*[A-Z])       # an upper case letter must occur at least once
        (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
        (?=\\S+$)         # no whitespace allowed in the entire string
        .{4,}             # anything, at least six places though*/

        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static View.OnTouchListener getTouchUpListener(final View.OnTouchListener mOnTouchListener) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent!=null && motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    return mOnTouchListener.onTouch(view, motionEvent);
                }
                return false;
            }
        };
    }

    public static View.OnClickListener getDummyOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }

    public static boolean validEmail(String email)
    {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}
