package com.example.oderfoodapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.oderfoodapp.Model.User;

public class Common {
    public static User currentUser;
    public static String DELETE = "Delete";
    public static String USER_KEY = "User";
    public static String PASSWORD_KEY = "Password";
    public static final String INTENT_FOOD_ID = "FoodId";

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo [] info = connectivityManager.getAllNetworkInfo();
            if (info !=null)
            {
                for (int i=0 ;i<info.length;i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
