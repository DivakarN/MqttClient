package com.example.mqttclient.MQTTModel
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
//region mqtt model class
class MQTTModel(client: MqttAndroidClient, options:MqttConnectOptions) {
    //region using getter and setter
    var clientObj:MqttAndroidClient=client
        get() = field

        set(value) {
            field = value
        }

    var optionsObj:MqttConnectOptions=options
        get() = field
        set(value) {
            field = value
        }
    //endregion

}
//endregion

