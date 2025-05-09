package edu.wpi.cs.cs4518.doodledetective

import android.content.Context
import android.graphics.Bitmap
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random


class DrawViewModel : ViewModel() {

	/**
	 * -----------------------------------------
	 * 			LIVE VARIABLES AND FLAGS
	 * -----------------------------------------
	 */
	private val predictor = Predict()

	private  val _predictions = MutableLiveData<List<Prediction>>()
	val predictions: LiveData<List<Prediction>> get() = _predictions

	private  val _remainingTime = MutableLiveData<Int>()
	val remainingTime: LiveData<Int> get() = _remainingTime

	private var timer: CountDownTimer? = null

	private  val _score = MutableLiveData<Int>(0)
	val score: LiveData<Int> get() = _score

	private  val _prompt = MutableLiveData<String>()
	val prompt: LiveData<String> get() = _prompt

	private val _winEvent = MutableLiveData<Boolean>(false)
	val winEvent: LiveData<Boolean> get() = _winEvent

	private val _currentBitmap = MutableLiveData<Bitmap?>()
	val currentBitmap: LiveData<Bitmap?> get() = _currentBitmap

	private val _gameOverEvent = MutableLiveData<Boolean>(false)
	val gameOverEvent: LiveData<Boolean> get() = _gameOverEvent

	var hasStarted: Boolean = false

	/**
	 * -----------------------------------------
	 * 			FUNCTIONS
	 * -----------------------------------------
	 */

	fun classifyDrawing(bitmap: Bitmap){
		viewModelScope.launch {
			try {
				val result = predictor.classify(bitmap)
				_predictions.value = result.predictions
				Log.d(TAG, "New predictions: ${result.predictions}")

				val promptValue = _prompt.value
				val winValue = _winEvent.value
				Log.d(TAG, "Prompt: ${promptValue}")

				if(promptValue!=null && containsPrediction(promptValue) && winValue!=null && !winValue){
					_score.postValue(_score.value?.plus(1))
					_winEvent.value = true
				}
			}catch (e: Exception){
				//Do nothing
			}
		}
	}

	fun startTimer() {
		timer?.cancel()
		_remainingTime.value = 15

		// Set up a task to run after 15 seconds
		timer = object : CountDownTimer(15 * 1000L, 1000) {
			override fun onTick(millisUntilFinished: Long) {
				_remainingTime.postValue((millisUntilFinished / 1000).toInt())
			}

			override fun onFinish() {
				_remainingTime.postValue(0)
				_prompt.value = "Time's up!"
				_gameOverEvent.value = true
			}
		}.start()
	}

	fun getRandomPrompt(context: Context) {
		// Open the prompt.txt from assets
		val inputStream = context.assets.open("prompts.txt")
		val reader = BufferedReader(InputStreamReader(inputStream))

		// Read all lines into a list
		val lines = reader.readLines()

		// Pick a random line from the list
		if (lines.isNotEmpty()) {
			val randomLine = lines[Random.nextInt(lines.size)]
			_prompt.value = randomLine.trim().lowercase()
		}

		reader.close()
	}

	fun containsPrediction(searchString: String): Boolean{
		val normalizedPrompt = searchString.trim().lowercase()
		return _predictions.value?.any {
			it.predicted_label.trim().lowercase() == normalizedPrompt
		} == true
	}

	fun resetWinEvent(){
		_winEvent.value = false
	}

	fun saveBitmap(bitmap: Bitmap){
		_currentBitmap.value = bitmap
	}

	companion object {
		private const val TAG = "DrawViewModel"
	}
}