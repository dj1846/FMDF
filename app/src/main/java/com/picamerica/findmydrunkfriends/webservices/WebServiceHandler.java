/**
 * WebServiceHandler.java
 * Created Date :: 31/05/2015
 * @Author :: Rakesh
 * Responsible to handle all Request for applicaiton.
 */
package com.picamerica.findmydrunkfriends.webservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.picamerica.findmydrunkfriends.utils.AppConstants;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServiceHandler extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = WebServiceHandler.class.getName();

    private Context context;
    private boolean progress = false;
    private ProgressDialog progressDialog = null;
    private WebServiceCallBackListener callBackListener;
    private RequestType reqType = RequestType.GET;
    private HashMap<String, File> imgs;
    private HashMap<String, String> queryMap;

    public static enum RequestType {
        POST, GET ,MULTIPART
    };


    /**
     *
     * @return WebServiceHandler
     */
    public static WebServiceHandler getInstanceWithProgress(Context context) {
        return new WebServiceHandler(context,true);
    }
    public static WebServiceHandler getInstance() {
        return new WebServiceHandler();
    }

    public static WebServiceHandler getInstance(Context context) {
        return new WebServiceHandler(context,false);
    }

    public static WebServiceHandler getInstance(Context context, boolean isProgress) {
        return new WebServiceHandler(context,isProgress);
    }

    private WebServiceHandler(){}

    private WebServiceHandler(Context context, boolean progress){
        this.context = context;
        this.progress = progress;
    }
    /**
     *
     * @param methodName
     * @param callBackListener
     * @param req
     * @param queryData
     */

    public void init(String methodName,
                     RequestType req, JSONObject queryData,WebServiceCallBackListener callBackListener) {
        try {
            this.callBackListener = callBackListener;
            this.reqType = req;
            String base_url = AppConstants.BASE_URL;
            if(!base_url.contains("?") && methodName != null){
                base_url +=methodName;
            }
            Log.i(LOG_TAG, "SERVICE URL :: " + base_url);
            Log.i(LOG_TAG,"QUERY DATA :: "+queryData);
            this.execute(base_url);
        } catch (Exception e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i(LOG_TAG,"Exception in init WebService :: "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * @param methodName
     * @param callBackListener
     * @param req
     * @param queryData
     *
     */
    public void init(String methodName,
                     RequestType req, String queryData,WebServiceCallBackListener callBackListener) {
        try {
            this.callBackListener =callBackListener;
            this.reqType = req;
            String base_url = AppConstants.BASE_URL;
            if (queryData.contains("?") || "".equals(queryData)) {
                base_url += methodName + URLEncoder.encode(queryData, "utf-8");
            } else {
                base_url += methodName + "?" + URLEncoder.encode(queryData, "utf-8");
            }
            Log.i(LOG_TAG,"SERVIVCE URL :: "+base_url);
            Log.i(LOG_TAG,"QUERY DATA :: "+queryData);
            this.execute(base_url);
        } catch (Exception e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i(LOG_TAG,"Exception in init WebService :: "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * @param methodName
     * @param callBackListener
     * @param req
     * @param queryData
     */
    public void init(String methodName,
                     RequestType req, HashMap<String,String> queryData, WebServiceCallBackListener callBackListener) {
        try {
            this.callBackListener =callBackListener;
            this.reqType = req;
            String base_url = AppConstants.BASE_URL;
            base_url += methodName;
            int counter = 0;
            if(req==RequestType.GET) {
                for (Object key : queryData.keySet()) {
                    if (counter == 0) {
                        base_url += "?" + key.toString() + "=" + URLEncoder.encode(queryData.get(key), "utf-8");
                    } else {
                        base_url += "&" + key.toString() + "=" + URLEncoder.encode(queryData.get(key), "utf-8");
                    }
                    counter++;
                }
            }else{
                queryMap = queryData;
            }
            Log.i(LOG_TAG,"SERVICE URL :: "+base_url);
            Log.i(LOG_TAG,"QUERY DATA :: "+queryData);
            this.execute(base_url);
        } catch (Exception e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i(LOG_TAG,
                    "Exception in init WebService :: "
                            + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * @param methodName
     * @param callBackListener
     * @param req
     * @param queryData
     * @param imgs
     */
    public void init(String methodName, WebServiceCallBackListener callBackListener,
                     RequestType req, HashMap<String,String> queryData,HashMap<String, File> imgs) {
        try {
            this.callBackListener =callBackListener;
            this.reqType = req;
            this.queryMap = queryData;
            String base_url = AppConstants.BASE_URL;
            base_url += methodName;
            this.imgs = imgs;

            Log.i(LOG_TAG,"SERVICE URL :: "+base_url);
            Log.i(LOG_TAG,"QUERY DATA :: "+queryData);
            this.execute(base_url);
        } catch (Exception e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i(LOG_TAG,
                    "Exception in init WebService :: "
                            + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * @param callBackListener
     * @param req
     * @param queryData
     */

    public void init(RequestType req, Map<String,String> queryData,WebServiceCallBackListener callBackListener) {
        try {
            this.callBackListener =callBackListener;
            this.reqType = req;
            int counter = 0;
            String base_url = AppConstants.BASE_URL;
            for (Object key : queryData.keySet()) {
                if (counter == 0) {
                    base_url +=  "?"+key.toString()+"="+URLEncoder.encode(queryData.get(key), "utf-8");
                } else {
                    base_url += "&"+key.toString()+"="+URLEncoder.encode(queryData.get(key), "utf-8");
                }
                counter++;
            }
            this.execute(base_url);
        } catch (Exception e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i(LOG_TAG,"Exception in init WebService :: "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progress && context!=null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Requesting To server...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... arg0) {
        switch (reqType) {
            case GET:
                try {
                    return getHttpConnection(arg0[0]);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            case POST:
                try {
                    return excutePost(arg0[0],getPostDataString(queryMap));
                } catch (Exception e) {
                    if(callBackListener != null){
                        callBackListener.onRequestFailed(e.getMessage());
                    }
                    e.printStackTrace();
                }
                break;


            case MULTIPART:
                try {
                    MultipartUtility multipart = new MultipartUtility(arg0[0], "UTF-8");
                    multipart.addHeaderField("Accept", "application/json");
                    multipart.addHeaderField("Content-type","multipart/form-data");
                    for (Object key : queryMap.keySet()) {
                        multipart.addFormField(key.toString(), queryMap.get(key));
                        try{
                            Log.v(key.toString(), queryMap.get(key));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(imgs!=null)
                        //multipart.addFilePart("fileUpload", file);
                        for(String key:imgs.keySet()){
                            multipart.addFilePart(key, imgs.get(key));
                        }

                    return multipart.finish();
                }

                catch (IOException e) {
                    if(callBackListener != null){
                        callBackListener.onRequestFailed(e.getMessage());
                    }
                    Log.i("info","Excpetion in :: doInBackground :: "+ e.getMessage());
                    e.printStackTrace();
                }
                catch (Exception e) {
                    if(callBackListener != null){
                        callBackListener.onRequestFailed(e.getMessage());
                    }
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    /**
     * @param results
     */
    protected void onPostExecute(String results) {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        try {
            Log.i("info", "response data :: " + results);
            if(this.callBackListener!=null){
                this.callBackListener.onRequestSuccess(results);
            }
        } catch (IllegalArgumentException e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            Log.i("Info", "IllegalArgumentException");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param is
     * @return
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(e.getMessage());
            }
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                if(callBackListener != null){
                    callBackListener.onRequestFailed(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public interface WebServiceCallBackListener{
        public void onRequestSuccess(String response);
        public void onRequestFailed(String errorMsg);
    }

    public static String readTestData(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }

    // Makes HttpURLConnection and returns InputStream
    private String getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        StringBuilder response   = new StringBuilder();
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AppConstants.AUTH_USER, AppConstants.AUTH_PASS.toCharArray());
                }
            });
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
//            httpConnection.setRequestProperty("Authorization", "basic " +
//                    Base64.encode((AppConstants.AUTH_USER + ":" + AppConstants.AUTH_PASS).getBytes(), Base64.NO_WRAP));
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setRequestProperty("charset", "utf-8");
            httpConnection.connect();
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                httpConnection.disconnect();
            }

        } catch (Exception ex) {
            if(callBackListener != null){
                callBackListener.onRequestFailed(ex.getMessage());
            }
            ex.printStackTrace();
        }
        return response.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public static String excutePost(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Authorization", "basic " +
                    Base64.encode((AppConstants.AUTH_USER + ":" + AppConstants.AUTH_PASS).getBytes(), Base64.NO_WRAP));
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");


            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {

            }

            Log.i("status", "status data :: " + status);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }
    }
