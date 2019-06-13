package com.archivos.work_scra.mqtt_client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivityMQTT extends AppCompatActivity {


    static String USERNAME= "qqyffaic";
    static String PASSWORD= "pksjtUUO2EN4";
    String topicStr= "cp/switch";
    public int   cont= 0;
    MqttAndroidClient client;

    ImageView MQTTStatus;
    TextView PublishStatus;
    Button Publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_mqtt );

        MQTTStatus = findViewById( R.id.MQTTStatus );
        PublishStatus = findViewById( R.id.publishStatus );
        Publish = findViewById( R.id.onPublishBtn );

        connect();

        Log.e("MAC:","getMacAddr -> " +getMacAddr());

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    private void connect(){


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://postman.cloudmqtt.com:16323", clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());



        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    PublishStatus.setText( "turned off!" );
                    MQTTStatus.setImageResource( R.drawable.ic_action_name2 );
                    Toast.makeText( MainActivityMQTT.this, "Connected!!", Toast.LENGTH_SHORT ).show();
                    Log.d("infoSuccess:", "The connection was successful");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText( MainActivityMQTT.this, "Connection failed", Toast.LENGTH_SHORT ).show();
                    Log.d("infoFailure:", "Te connection has falied");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void pub(View v){
        cont += 1;
        if (cont==1){
            String topic = topicStr;
            String message = "on";
            PublishStatus.setText( "turned on!" );
            MQTTStatus.setImageResource( R.drawable.ic_action_name );
            try {
                client.publish(topic, message.getBytes(),0,false);
            } catch ( MqttException e) {
                e.printStackTrace();
            }
        }

        if (cont==2){
            cont =0;
            String topic = "cp/switch";
            String message = "off";
            PublishStatus.setText( "turned off!" );
            MQTTStatus.setImageResource( R.drawable.ic_action_name2 );


            try {
                client.publish(topic, message.getBytes(),0,false);
            } catch ( MqttException e) {
                e.printStackTrace();
            }
        }

    }

}
