package com.example.smarttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartcar.sdk.SmartcarAuth;
import com.smartcar.sdk.SmartcarCallback;
import com.smartcar.sdk.SmartcarResponse;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String CLIENT_ID;
    private static String REDIRECT_URI;
    private static String[] SCOPE;
    private Context appContext;
    private SmartcarAuth smartcarAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout lView = new LinearLayout(appContext);

        appContext = getApplicationContext();
        CLIENT_ID = "fccf6eef-faad-4a8e-8ec2-a65f6cc882a1";
        REDIRECT_URI = "sc" + "fccf6eef-faad-4a8e-8ec2-a65f6cc882a1" + "://127.0.0.1:8000/exchange";
        SCOPE = new String[]{"required:read_vehicle_info', 'required:read_fuel', 'required:read_odometer"};

        smartcarAuth = new SmartcarAuth(
                CLIENT_ID,
                REDIRECT_URI,
                SCOPE,
                true,
                new SmartcarCallback() {
                    @Override
                    public void handleResponse(final SmartcarResponse smartcarResponse) {

                        final OkHttpClient client = new OkHttpClient();

                        // Request can not run on the Main Thread
                        // Main Thread is used for UI and therefore can not be blocked
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                // send request to exchange the auth code for the access token
                                Request exchangeRequest = new Request.Builder()
                                        // Android emulator runs in a VM, therefore localhost will be the
                                        // emulator's own loopback address
                                        .url("http://127.0.0.1:8000/exchange?code=" + smartcarResponse.getCode())
                                        .build();

                                try {
                                    client.newCall(exchangeRequest).execute();
                                    System.out.println(0);
                                } catch (IOException e) {
                                    e.printStackTrace();     
                                }

                                // send request to retrieve the vehicle info
                                Request vehicleInfoRequest = new Request.Builder()
                                        .url("http://127.0.0.1:8000/vehicle")
                                        .build();

                                try {
                                    Response response = client.newCall(vehicleInfoRequest).execute();

                                    String jsonBody = response.body().string();
                                    JSONObject JObject = new JSONObject(jsonBody);
                                    String make = JObject.getString("make");
                                    String model = JObject.getString("model");
                                    String year = JObject.getString("year");
                                    String fuelTankLevel = JObject.getString("");
                                    String distanceLeft = JObject.getString("distance [km]");
                                    String costToRefuel = JObject.getString("fuel cost [$]");

                                    String vehicle_info = "Make: " + make +
                                            "Model: " + model +
                                            "Year: " + year +
                                            "Fuel Tank Level: " + fuelTankLevel +
                                            "Distance Left Until Refuel: " + distanceLeft +
                                            "Cost to Refuel" + costToRefuel;

                                    // TextView myText = new TextView(appContext);
                                    // myText.setText(vehicle_info);
                                    // lView.addView(myText);
                                    // setContentView(lView);
                                    System.out.println(vehicle_info);
                                    System.out.println(1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
        );
    }
}



















