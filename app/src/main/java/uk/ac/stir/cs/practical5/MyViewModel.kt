package uk.ac.stir.cs.practical5

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class MyViewModel: ViewModel() {
    var pickedDate = MutableLiveData<LocalDate>()
    var league = MutableLiveData<String>()

    /** ArrayList: information from every result. */
    var allResults = ArrayList<Game>()
    /** MutableLiveData: store all results. */
    val allResults_live = MutableLiveData<ArrayList<Game>>()

    /**
     * Set the value of the selected date.
     * @param selectedDate The new value for the initial unit.
     */
    @SuppressLint("NewApi")
    fun setDate(date: LocalDate) {
        pickedDate.value = date
    }

    /**
     * Set the value of the list of games.
     * @param list The new updated list.
     */
    fun setList(list: ArrayList<Game>) {
        allResults = list
        allResults_live.value = allResults
    }

}