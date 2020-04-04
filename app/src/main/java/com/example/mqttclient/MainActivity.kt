package com.example.mqttclient

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mqttclient.MQTTHandler.MQTTHelper
import com.example.mqttclient.MQTTModel.MQTTModel
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    //region initialize the MQTT client,options and component inittialization
    lateinit var publishEditText:EditText
    lateinit var subscripeEditText:EditText
    lateinit var publishButton:Button
    lateinit var subscribeButton:Button
    lateinit var connectButton:Button
    var list: ArrayList<MQTTModel> = MQTTHelper.mqttConnectionDetails(this)
    var client = list.get(0).clientObj
    var options = list.get(0).optionsObj
    //endregion

    //region activity life cycle

    //region oncreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //region exception handling
        try {
            //region initialize the UI components methods
            println("@@ enter the main function")
             initialSetup()
            //endregion

            //region call setcallback for mqtt
            client.setCallback(object : MqttCallback {

                override fun connectionLost(cause: Throwable) {
                    try {
                        Log.w("MqttData", cause.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("@@" + e.printStackTrace())
                    }

                }
                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    Log.w("MqttData", message.toString())
                    edtSubscribe.setText(message.toString())
                    message.getPayload()
                    System.out.println("Mqtt" + message.getId())
                    println("Message: " + topic + " : " + String(message.getPayload()))
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.w("MqttData", "Message sent")
                }
            })
            //endregion
        }catch (e:Exception){
            e.printStackTrace()
            println("@@"+e.printStackTrace())
        }
        //endregion

        }
    //endregion

    //region intialSetup() method
    private fun initialSetup() {
        publishEditText = findViewById(R.id.edtPublish) as EditText
        publishButton= findViewById(R.id.btnPublish) as Button
        subscripeEditText= findViewById(R.id.edtSubscribe) as EditText
        subscribeButton=findViewById(R.id.btnSubscribe) as Button
        connectButton = findViewById(R.id.btnConnect) as Button
    }
    //endregion

    //region publish() method
    fun publish(view: View) {
        try {
        var payload: String = publishEditText.text.toString()
        MQTTHelper.publishMessage(client, payload)
    }catch (e:Exception)
    {
        e.printStackTrace()
        println("@@"+e.printStackTrace())
    }
    }
    //endregion

    //region publish() method
    fun subscribe(view: View) {
        try{
            MQTTHelper.subscribeMessage(client)}
        catch (e:Exception)
        {e.printStackTrace()
            println("@@"+e.toString())
        }
    }
    //endregion

    //region connect() method
    fun connect(view: View) {
        try {
        MQTTHelper.connectToMqtt(client, options)
    }
    catch (e:Exception){
        e.printStackTrace()
        println("@@"+e.toString())
    }
    }
    //endregion

    //region onDestory() method
    override fun onDestroy() {
      //  MQTTHelper.disconnectFromMqtt(client);
        client.unregisterResources()
        client.close()
        super.onDestroy()
    } //endregion
    //endregion
}



