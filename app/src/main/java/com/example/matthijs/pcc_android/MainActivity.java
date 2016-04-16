package com.example.matthijs.pcc_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        editTextAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    onIPEnter(v);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        editTextPort.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    onPortEnter(v);
                    return true;
                }
                else
                {
                    return false;
                }
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

        //buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((editTextPort.getText().length() > 3)&&(editTextAddress.getText().length() > 6)) {
                    MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();
                    //goToDriveActivity();
                }else{
                    textResponse.setText("Not a legit input");
                }
            }
        });

    }


    public void onIPEnter(View view) {
        editTextPort.requestFocus();
    }

    public void onPortEnter(View view) {
        goToDriveActivity();
    }

    public void goToDriveActivity(){
        Intent myIntent = new Intent(MainActivity.this, DriveActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port){
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

}