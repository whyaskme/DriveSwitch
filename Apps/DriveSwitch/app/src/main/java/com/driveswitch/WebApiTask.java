package com.driveswitch;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import com.loopj.android.http.*;

public class WebApiTask extends AsyncTask<Void, JSONArray, JSONArray>
{
    private WebApiTask myWebApiTask = null;

    HttpURLConnection webApiClient = null;
    URL url = null;

    String serviceResponse = "";

    JSONArray JSONResponse;

    String serviceMethod = Constants.WebMethods.GET;

    String serviceHost = Constants.ServiceHost.Production;
    String serviceParams = "";
    String postData = "";

    WebApiTask(String requestMethod, String inputParams, String inputData)
    {
        if(myWebApiTask != null)
            return;

        serviceMethod = requestMethod;
        serviceParams = inputParams;
        postData = inputData;
    }

    @Override
    protected JSONArray doInBackground(Void... params)
    {
        BufferedReader br = null;
        JSONArray jsonResponseArray = null;

        try
        {
            url = new URL(serviceHost + serviceParams);

            webApiClient = (HttpURLConnection) url.openConnection();

            // Available web methods [OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH]
            webApiClient.setRequestMethod(serviceMethod);

            if(serviceMethod == Constants.WebMethods.POST) // Post method
            {
                try
                {
                    webApiClient.addRequestProperty("Accept", "application/json");
                    //webApiClient.addRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    // This content type works!
                    webApiClient.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    webApiClient.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(webApiClient.getOutputStream());

                    // You must prepend post body with "=" !!!
                    wr.write("=" + postData);
                    wr.flush();

                    //  Read answer from server.
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(webApiClient.getInputStream()));
                    String serviceResponse;
                    while ((serviceResponse = serverAnswer.readLine()) != null)
                    {
                        jsonResponseArray = new JSONArray("[" + serviceResponse + "]");
                    }

                    wr.close();
                    serverAnswer.close();
                }
                catch(Exception ex)
                {
                    String errMsg = ex.toString();
                }
            }
            else // Get method
            {
                Integer responseCode = webApiClient.getResponseCode();
                if (responseCode == webApiClient.HTTP_OK)
                {
                    br = new BufferedReader(new InputStreamReader(webApiClient.getInputStream()));
                    while ((serviceResponse = br.readLine()) != null) {
                        // Do not wrap if it's a renewal period list
                        Boolean isRenewalPeriodList = serviceParams.toLowerCase().contains("?requesttype=renewalperiods");
                        Boolean isTNCList = serviceParams.toLowerCase().contains("tnc");

                        if (isRenewalPeriodList)
                            jsonResponseArray = new JSONArray(serviceResponse);
                        else if (isTNCList)
                            jsonResponseArray = new JSONArray(serviceResponse);
                        else
                            jsonResponseArray = new JSONArray("[" + serviceResponse + "]");
                    }
                } else {
                    jsonResponseArray = new JSONArray("[{'HttpError':'" + responseCode + "'}]");
                }
            }
        }
        catch (Exception ex)
        {
            String errMsg = ex.toString();

            try
            {
                //JSONResponse = new JSONObject("{'HttpError':'" + ex.toString() + "'}");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        finally
        {
            // Cleanup resources
            if(webApiClient != null) {
                webApiClient.disconnect();
                webApiClient = null;
            }
        }

        return jsonResponseArray;
    }

    @Override
    protected void onPostExecute(final JSONArray success)
    {
        webApiClient = null;

        if (success != null)
        {

        }
        else
        {

        }
    }

    @Override
    protected void onCancelled()
    {
        webApiClient = null;
    }
}
