package com.rnrstudio.swkround;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    ServerSocket serverSocket;
    Thread Thread1 = null;
    public static String SERVER_IP = "192.168.4.1";
    public static final int SERVER_PORT = 9239;
    String message;
    Button btnConnect;
    int i = 0;

    SeekBar sbVs;
    SeekBar sbVe;
    SeekBar sbAngle;

    Button btnAdd;
    TextView tvVs;
    TextView tvVe;
    TextView tvAngle;

    TextView tvVs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnStop = (Button) findViewById(R.id.btnStop);
        btnConnect = (Button) findViewById(R.id.btnConnect);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread1 = new Thread(new Thread1());
                Thread1.start();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = "START\r";
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = "STOP\r";
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // message = "TASK|[180,10,50]\r";
                message = "LIST|[[180,10,50],[180,50,10]]\r";

                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
    }

    private PrintWriter output;
    private BufferedReader input;

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        btnConnect.setText("Connected");
                    }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                tvMessages.append("server: " + message + "
//                                        ");
                            }
                        });
                    } else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable {
        private String message;

        Thread3(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    tvMessages.append("client: " + message + "
//                            ");
//                            etMessage.setText("");
                }
            });
        }
    }
}