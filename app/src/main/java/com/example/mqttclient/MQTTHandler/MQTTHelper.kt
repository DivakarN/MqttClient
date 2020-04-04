package com.example.mqttclient.MQTTHandler

import android.content.Context
import android.util.Log
import com.example.mqttclient.MQTTModel.MQTTModel
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.UnsupportedEncodingException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object MQTTHelper {
   //region mqttconnection details method
    fun mqttConnectionDetails(context: Context?): ArrayList<MQTTModel> {
        val clientId = "web_tsi_1234567"
        val client = MqttAndroidClient(
            context, "wss://test.rapidturnaround.flights:8884/mqtt",
            clientId, MemoryPersistence())
        val options = MqttConnectOptions()
        options.userName = "backend"
        options.password = "backend".toCharArray()
        options.isAutomaticReconnect = true
        options.isCleanSession = true
        options.keepAliveInterval = 60
        try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String)
                         {
                        }
                        override fun getAcceptedIssuers(): Array<X509Certificate>? {
                            return null
                        }
                    }
                )
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            options.socketFactory = sslSocketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        val model = MQTTModel(client, options)
        val list: ArrayList<MQTTModel> = ArrayList<MQTTModel>()
        list.add(model)
        return list
    }
    //endregion

    //region disconnectfromqtt
    fun disconnectFromMqtt(client: MqttAndroidClient) {
        try {
            val token = client.disconnect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) { // We are connected
                    Log.d("MQTTDATA", "Disconnected Successfully")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) { // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTTDATA", exception.toString())
                }
            }
        } catch (e: MqttException) {
            Log.d("MQTTDATA", "Issue Occured while disconnecting from mqtt server")
        }
    }
    //endregion

    //region connect to mqtt method
    fun connectToMqtt(client: MqttAndroidClient, options: MqttConnectOptions?) {
        try {
            val token = client.connect(options)
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) { // We are connected
                    val disconnectedBufferOptions =
                        DisconnectedBufferOptions()
                    println("@@connected")
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    client.setBufferOpts(disconnectedBufferOptions)
                    //subscribeToTopic();
                    Log.d("MQTTDATA", "Connected Successfully")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) { // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTTDATA", exception.toString())
                }
            }
        } catch (e: MqttException) {
            Log.d("MQTTDATA", "Issue Occured while connecting from mqtt server")
        }
    }
    //endregion

    //region publish message method
    fun publishMessage(client: MqttAndroidClient, publishMessage: String) {
        val topic = "foo/bar"
        //String mess = publishMessage;
        var encodedPayload = ByteArray(0)
        try {
            encodedPayload = publishMessage.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            message.isRetained = true
            client.publish(topic, message)
        } catch (e: UnsupportedEncodingException) {
            Log.d("MQTTDATA", "Issue Occured while publishing from mqtt server")
        } catch (e: MqttException) {
            Log.d("MQTTDATA", "Issue Occured while publishing from mqtt server")
        }
    }
    //endregion

    //region subscribe message method
    fun subscribeMessage(client: MqttAndroidClient) {
        val topic = "foo/bar"
        val qos = 1
        try {
            val subToken = client.subscribe(topic, qos, null, object :
                IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w("MqttData", "Subscribed!")
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w("MqttData", "Not Subscribed!")
                }
            })
        } catch (e: MqttException) {
            Log.d("MQTTDATA", "Issue Occured while subscribing from mqtt server")
        }
    }
    //endregion
}


