package com.po_chunchen.connectivitymanagerapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private static ConnectivityManager connectivityManager;

    private NetworkRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();

//        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        request = builder.build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        connectivityManager.registerNetworkCallback(request, callback);
        connectivityManager.requestNetwork(request, callback);
    }


    @Override
    protected void onStop() {
        super.onStop();

        connectivityManager.unregisterNetworkCallback(callback);
    }

    ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.i("MainActivity", "已根据功能和传输类型找到合适的网络");
            if (Build.VERSION.SDK_INT >= 23) {
                connectivityManager.bindProcessToNetwork(network);
            } else {
                // 23后这个方法舍弃了
                ConnectivityManager.setProcessDefaultNetwork(network);
            }
            Task run = new Task();
            run.execute();

        }
    };


    public class Task extends AsyncTask<Void,Void,String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public String doInBackground(Void... arg0) {

            URL url = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder;

            try
            {
                // create the HttpURLConnection
                url = new URL("https://www.google.com.tw/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // 使用甚麼方法做連線
                connection.setRequestMethod("GET");

                // 是否添加參數(ex : json...等)
                //connection.setDoOutput(true);

                // 設定TimeOut時間
                connection.setReadTimeout(15*1000);
                connection.connect();

                // 伺服器回來的參數
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                // close the reader; this can throw an exception too, so
                // wrap it in another try/catch block.
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(String result) {
            if(!"".equals(result) || null != result){
                Log.d("MainActivity","Resopnse: " + result);
            }
        }
    }
}
