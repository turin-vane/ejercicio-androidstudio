package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.app.ActivityCompat
//import java.util.jar.Manifest
import androidx.core.content.ContextCompat
import android.Manifest
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

import android.view.View
import android.widget.LinearLayout
import android.widget.ImageView
import android.graphics.Color

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var contador = 0;
    private lateinit var sensorManager: SensorManager;
    private var sensorPasos: Sensor?= null;

    private lateinit var tvContador : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvContador = findViewById<TextView>(R.id.tvContador);
        val btnSumar = findViewById<Button>(R.id.btnSumar);
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar);
        val btnDetalle = findViewById<Button>(R.id.btnDetalle);


        val vistaTarea1 = findViewById<View>(R.id.tarea1);
        val vistaTarea2 = findViewById<View>(R.id.tarea2);
        val vistaTarea3 = findViewById<View>(R.id.tarea3);

        configurarTarjetaInteractiva(vistaTarea1, "Rutina de Fuerza");
        configurarTarjetaInteractiva(vistaTarea2, "Natación");
        configurarTarjetaInteractiva(vistaTarea3, "Desayuno");


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 100) //maybe esta mal
        }else{
            configurarSensor();
        }

        btnSumar.setOnClickListener {
            contador++;

            tvContador.text="Numero de pasos: $contador";
        }

        btnReiniciar.setOnClickListener {
            contador=0;
            tvContador.text="Numero de pasos: $contador";

        }

        btnDetalle.setOnClickListener {
            //navegar a otra pantalla
            val intent = Intent(this,Detail::class.java);

            intent.putExtra("PASOS",contador);

            startActivity(intent);
        }
    }


    private fun configurarSensor(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        sensorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(sensorPasos == null){
            tvContador.text="Error al configurar el sensor de pasos";
        }
    }
    override fun onResume(){
        super.onResume()
        sensorPasos?.let{
            if (::sensorManager.isInitialized) {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    override fun onPause(){
        super.onPause()
        // gemini me recomendo esto ya que mi app se cerraba
        if (::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this);
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
            val pasosTotales = event.values[0].toInt();
            tvContador.text = "Pasos de hoy: $pasosTotales";
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                configurarSensor();
            }else{
                tvContador.text = "Permiso de sensor no concedido";
            }
        }
    }


    private fun configurarTarjetaInteractiva(tarjetaView: View, titulo: String) {
        val llTarjeta = tarjetaView as LinearLayout

        val tvTitulo = tarjetaView.findViewById<TextView>(R.id.tvTitulo)
        val tvSubtitulo = tarjetaView.findViewById<TextView>(R.id.tvSubtitulo)
        val ivEstado = tarjetaView.findViewById<ImageView>(R.id.ivEstado)

        tvTitulo.text = titulo
        var estaCompletada = false

        actualizarAspectoTarjeta(estaCompletada, llTarjeta, tvSubtitulo, ivEstado)

        llTarjeta.setOnClickListener {
            estaCompletada = !estaCompletada
            actualizarAspectoTarjeta(estaCompletada, llTarjeta, tvSubtitulo, ivEstado)
        }
    }

    private fun actualizarAspectoTarjeta(
        completada: Boolean,
        llTarjeta: LinearLayout,
        tvSubtitulo: TextView,
        ivEstado: ImageView
    ) {
        if (completada) {
            llTarjeta.setBackgroundColor(Color.parseColor("#A5D6A7"));
            tvSubtitulo.text = "¡Completado!";
            tvSubtitulo.setTextColor(Color.parseColor("#2E7D32"));
            ivEstado.setImageResource(R.drawable.ic_check_green);
        } else {
            llTarjeta.setBackgroundColor(Color.parseColor("#EEEEEE"));
            tvSubtitulo.text = "Pendiente de completar";
            tvSubtitulo.setTextColor(Color.parseColor("#757575"));
            ivEstado.setImageResource(android.R.color.transparent);
        }
    }

}