package edu.wpi.cs.cs4518.doodledetective

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.wpi.cs.cs4518.doodledetective.databinding.ActivityPlayBinding
import java.util.Locale
import kotlin.math.sqrt

class PlayActivity : AppCompatActivity(), SensorEventListener  {

	private lateinit var binding: ActivityPlayBinding
	private val viewModel: DrawViewModel by viewModels()

	private lateinit var sensorManager: SensorManager
	private var lastShakeTime: Long = 0
	private var shakeThreshhold = 0.75f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityPlayBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val drawView = binding.drawView

		// Register sensor for shaking detection
		sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)

		// Bind the draw view with viewmodel
		drawView.setActionUpListener { bitmap ->
			viewModel.classifyDrawing(bitmap)
			viewModel.saveBitmap(drawView.getBitmap()!!)
		}

		binding.clearButton.setOnClickListener {
			drawView.clear()
		}

		viewModel.currentBitmap.observe(this) { bitmap ->
			bitmap?.let { binding.drawView.setBitmap(it) }
		}

		viewModel.predictions.observe(this) { predictions ->
			val label = predictions.joinToString("\n") { it.predicted_label }
			binding.predictionText.text = label
			val score = predictions.joinToString("\n") {
				"${String.format(Locale.US, "%.2f", it.score * 100)}%"
			}
			binding.predictionScore.text = score
		}

		viewModel.remainingTime.observe(this) { remainingTime ->
			binding.timer.text = "Time: ${remainingTime.toString()}"
		}

		viewModel.prompt.observe(this) { prompt ->
			binding.promptText.text = prompt
		}

		viewModel.score.observe(this) { score ->
			binding.score.text = "Score: ${score.toString()}"
		}

		viewModel.winEvent.observe(this) { win ->
			if (win == true) {
				showWinPopUp()
			}
		}

		viewModel.gameOverEvent.observe(this) { win ->
			if (win == true) {
				val intent = Intent(this, LeaderboardActivity::class.java)
				val score = viewModel.score.value ?: 0
				intent.putExtra("score", score)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
				startActivity(intent)
				finish()
			}
		}

		if(!viewModel.hasStarted){
			viewModel.getRandomPrompt(this)
			viewModel.startTimer()
			viewModel.hasStarted = true
		}
	}

	private fun showWinPopUp(){
		Toast.makeText(this, "🎉 Correct!", Toast.LENGTH_SHORT).show()

		Handler(Looper.getMainLooper()).postDelayed({
			binding.drawView.clear()
			viewModel.getRandomPrompt(this)
			viewModel.startTimer()
			viewModel.resetWinEvent()
		}, 2000)

	}

	override fun onSensorChanged(event: SensorEvent?) {
		if(event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
			val x = event.values[0]
			val y = event.values[0]
			val z = event.values[0]

			val magnitude = sqrt(x * x + y * y + z * z)

			if(magnitude > shakeThreshhold){
				val currentTime = System.currentTimeMillis()
				//Prevent the shake from rapidly firing
				if(currentTime - lastShakeTime > 1500){
					lastShakeTime = currentTime
					Log.d(TAG, "Shake detected!")
					binding.drawView.revert()
					viewModel.saveBitmap(binding.drawView.getBitmap()!!)
				}
			}
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		//Do nothing
	}
	companion object {
		private const val TAG = "PlayActivity"
	}

}