package com.yordan.finance.utils;
import android.util.Base64;
import android.util.Log;
import com.yordan.finance.model.Expense;
import com.yordan.finance.model.Item;
import com.yordan.finance.model.FinanceAccount;
import com.yordan.finance.sync.FinanceCredentials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class ServerUtils {

    private static final String TAG = "ServerUtils: ";

    public static int login(FinanceAccount user){
        try {
            URL url = new URL("http://10.0.2.2:8080/login");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.setConnectTimeout(10000);

            String basicAuthString = generateAuthorizationString(user);
            httpConnection.setRequestProperty("Authorization", basicAuthString);

            return httpConnection.getResponseCode();
        }
        catch (SocketTimeoutException e){
            e.printStackTrace();
            return 404;
        }catch (IOException e){
            e.printStackTrace();
            return 500;
        }
    }

    public static int register(FinanceAccount account) {
        try {
            try {
                URL url = new URL("http://10.0.2.2:8080/register");
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setDoOutput(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setConnectTimeout(10000);

                OutputStream os = connection.getOutputStream();
                byte[] output = account.toJsonString().getBytes(StandardCharsets.UTF_8);
                os.write(output, 0, output.length);

                return httpConnection.getResponseCode();
            } catch (SocketTimeoutException e) {
                return 404;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String generateAuthorizationString(FinanceCredentials user){
        String userNameAndPassword = user.getName() + ":" + user.getPassword();
        return "Basic " +
                Base64.encodeToString(userNameAndPassword.getBytes(), Base64.DEFAULT);

    }

    public static List<Expense> getExpenses(FinanceCredentials user) throws SocketTimeoutException {
        try {
            URL url = new URL("http://10.0.2.2:8080/expenses");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HTTP_OK) {
                String response = convertStreamToString(connection.getInputStream());


                JSONArray responseArray = new JSONArray(response);
                List<Expense> expenses = new ArrayList<>();

                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject jsonObject = responseArray.getJSONObject(i);
                    expenses.add(Expense.fromJsonString(jsonObject));
                }

                return expenses;
            } else {
                return null;
            }
        }catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private static int deleteExpenses(FinanceCredentials user) {
        try{
            URL url = new URL("http://10.0.2.2:8080/expenses");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));

            return connection.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    private static int uploadExpenses(FinanceCredentials user, List<Expense> expenses){
        try{
            URL url = new URL("http://10.0.2.2:8080/expenses");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));


            String jsonArrayString = ServerUtils.generateJsonStringArrayFromExpenses(expenses);

            byte[] output = jsonArrayString.getBytes();
            OutputStream os = connection.getOutputStream();
            os.write(output, 0, output.length);

            return connection.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static int syncExpenses(FinanceCredentials user, List<Expense> expenses, List<Item> items){
        int responseFromDelete = deleteExpenses(user);
        int responseFromUpload = -1;
        Log.d(TAG, "syncExpenses: Deleting expenses completed.");
        if(responseFromDelete == HTTP_OK){
            responseFromUpload = uploadExpenses(user, expenses);
            Log.d(TAG, "syncExpenses: Adding new Expenses completed.");
            syncItems(user, items);
            Log.d(TAG, "syncExpenses: Syncing items completed.");
        }
        return responseFromUpload;
    }

    public static List<Item> getItems(FinanceCredentials user) throws SocketTimeoutException {
        try {
            URL url = new URL("http://10.0.2.2:8080/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HTTP_OK) {
                String response = convertStreamToString(connection.getInputStream());

                JSONArray responseArray = new JSONArray(response);
                List<Item> items = new ArrayList<>();

                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject jsonObject = responseArray.getJSONObject(i);
                    items.add(Item.fromJsonString(jsonObject));
                }

                return items;
            } else {
                return null;
            }
        }catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private static void uploadItems(FinanceCredentials user, List<Item> items){
        try{
            URL url = new URL("http://10.0.2.2:8080/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonArrayString = generateJsonStringArrayFromItems(items);
            byte[] output = jsonArrayString.getBytes();
            OutputStream os = connection.getOutputStream();
            os.write(output);

            System.out.println(connection.getResponseCode());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int deleteItems(FinanceCredentials user) {
        try{
            URL url = new URL("http://10.0.2.2:8080/items");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", generateAuthorizationString(user));

            return connection.getResponseCode();
        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static void syncItems(FinanceCredentials user, List<Item> items){
        deleteItems(user);
        uploadItems(user, items);
    }


    private static String generateJsonStringArrayFromExpenses(List<Expense> expenses){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < expenses.size() - 1; i++){
            if(i == 0){
                sb.append("[");
            }
            sb.append(expenses.get(i).toJsonString());
            sb.append(",");
        }

        sb.append(expenses.get(expenses.size() - 1).toJsonString());
        sb.append("]");

        return sb.toString();
    }

    private static String generateJsonStringArrayFromItems(List<Item> items){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < items.size() - 1; i++){
            if(i == 0){
                sb.append("[");
            }
            sb.append(items.get(i).toJsonString());
            sb.append(",");
        }

        sb.append(items.get(items.size() - 1).toJsonString());
        sb.append("]");

        return sb.toString();
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}




