package com.example.matthijs.pcc_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    private static final Pattern IPv4Pattern = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        setListeners();
    }

    private void setListeners() {
        editTextPort.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    tryToConnect();
                    return true;
                }
                return false;
            }
        });

        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToConnect();
            }
        });

        buttonClear.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                editTextAddress.setText("");
                editTextPort.setText("");
                editTextAddress.requestFocus();
            }
        });
    }

    public void tryToConnect() {
        if (validateIPv4(editTextAddress.getText().toString()) &&  //If valid IPv4 address
                editTextPort.getText().toString().matches("^[0-9]{4,5}") && //And port only contains 4 or 5 NUMBERS
                isBetween(Integer.parseInt(editTextPort.getText().toString()), 1023, 65536)){ //And port is in unreserved range
            //Check if connection can be made
            textResponse.setText("Checking if the host is available..");

            AvailabilityChecker checker = new AvailabilityChecker(editTextAddress.getText().toString(), 3000);
            try {
                if(checker.execute().get()){
                    textResponse.setText("");
                    Intent myIntent = new Intent(MainActivity.this, DriveActivity.class);
                    MainActivity.this.startActivity(myIntent);
                }else{
                    textResponse.setText("Host is not available!");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                textResponse.setText("ne pas de available!");
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Not a legit input", Toast.LENGTH_SHORT).show();
    }

    public class AvailabilityChecker extends AsyncTask<Void, Void, Boolean> {
        private String address;
        private int timeout;
        public AvailabilityChecker(String address, int timeout) {
            this.address = address;
            this.timeout = timeout;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean availability;
            try {
                availability = InetAddress.getByName(address).isReachable(timeout);
            } catch (IOException e) {
                e.printStackTrace();
                availability = false;
            }
            return availability;
        }
    }

    //TODO: Handle the data transfer in a separate class, initialized from the DriveActivity.
    public class SocketConnection extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        SocketConnection(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                DOS.writeUTF("HELLO_WORLD");

//                ByteArrayOutputStream byteArrayOutputStream =
//                        new ByteArrayOutputStream(1024);
//                byte[] buffer = new byte[1024];
//
//                int bytesRead;
//                InputStream inputStream = socket.getInputStream();
//
//    /*
//     * notice:
//     * inputStream.read() will block if no data return
//     */
//                while ((bytesRead = inputStream.read(buffer)) != -1){
//                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                    response += byteArrayOutputStream.toString("UTF-8");
//
//                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println(response);
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }

    public static boolean isBetween(int value, int min, int max){
        return((value > min) && (value < max));
    }

    public static boolean validateIPv4(final String ip) {
        return IPv4Pattern.matcher(ip).matches();
    }

}