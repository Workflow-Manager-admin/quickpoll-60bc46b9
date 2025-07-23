package com.example.androidfrontend;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for poll backend interactions (REST/WebSocket).
 */
public class PollService {
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Change to real backend address

    public interface PollListCallback {
        void onResult(List<Poll> polls, Exception error);
    }

    public interface PollCallback {
        void onResult(Poll poll, Exception error);
    }

    public interface VoteCallback {
        void onResult(boolean success, Exception error);
    }

    // PUBLIC_INTERFACE
    public static void getPolls(PollListCallback callback) {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "/polls");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    JSONArray array = new JSONArray(sb.toString());
                    List<Poll> result = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String id = obj.getString("id");
                        String title = obj.getString("title");
                        JSONArray opt = obj.getJSONArray("options");
                        List<String> options = new ArrayList<>();
                        for (int j = 0; j < opt.length(); j++) options.add(opt.getString(j));
                        JSONArray resArr = obj.optJSONArray("results");
                        List<Integer> results = new ArrayList<>();
                        if (resArr != null) for (int j = 0; j < resArr.length(); j++) results.add(resArr.getInt(j));
                        result.add(new Poll(id, title, options, results));
                    }
                    return result;
                } catch (Exception e) {
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) callback.onResult(null, (Exception)o);
                else callback.onResult((List<Poll>)o, null);
            }
        }.execute();
    }

    // PUBLIC_INTERFACE
    public static void createPoll(String title, List<String> options, PollCallback callback) {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "/polls");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject payload = new JSONObject();
                    payload.put("title", title);
                    JSONArray arr = new JSONArray();
                    for (String opt : options) arr.put(opt);
                    payload.put("options", arr);

                    OutputStream os = conn.getOutputStream();
                    os.write(payload.toString().getBytes());
                    os.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    JSONObject obj = new JSONObject(sb.toString());
                    String id = obj.getString("id");
                    String rtitle = obj.getString("title");
                    JSONArray ropts = obj.getJSONArray("options");
                    List<String> outOptions = new ArrayList<>();
                    for (int j = 0; j < ropts.length(); j++) outOptions.add(ropts.getString(j));
                    List<Integer> results = new ArrayList<>();
                    JSONArray resArr = obj.optJSONArray("results");
                    if (resArr != null) for (int j = 0; j < resArr.length(); j++) results.add(resArr.getInt(j));
                    Poll p = new Poll(id, rtitle, outOptions, results);
                    return p;
                } catch (Exception e) {
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) callback.onResult(null, (Exception)o);
                else callback.onResult((Poll)o, null);
            }
        }.execute();
    }

    // PUBLIC_INTERFACE
    public static void votePoll(String pollId, int optionIndex, VoteCallback callback) {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    URL url = new URL(BASE_URL + "/polls/" + pollId + "/vote");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    JSONObject payload = new JSONObject();
                    payload.put("optionIndex", optionIndex);

                    OutputStream os = conn.getOutputStream();
                    os.write(payload.toString().getBytes());
                    os.flush();
                    int responseCode = conn.getResponseCode();
                    return responseCode == 200;
                } catch (Exception e) {
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) callback.onResult(false, (Exception)o);
                else callback.onResult((Boolean)o, null);
            }
        }.execute();
    }

    // Note: WebSocket live results would require a small lib like okhttp, but omitted here to keep this file size reasonable.
}
