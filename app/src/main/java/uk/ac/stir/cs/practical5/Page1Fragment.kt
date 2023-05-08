package uk.ac.stir.cs.practical5
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.LocalDate;
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


/** Create Fragment 1, this will include a date picker to retrieve the date
    and two image buttons to change league **/
class Page1Fragment : Fragment() {
    /** Declare date picker */
    lateinit var datePicker: DatePicker;
    /** The ViewModel used to store values between fragments. */
    private val model: MyViewModel by activityViewModels()
    /** ImageButtons for the italian flag and the scottish flag*/
    private lateinit var btn_italy: ImageButton
    private lateinit var btn_scotland: ImageButton

    /** On create method*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page1_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(ContentValues.TAG, "Fragment 1 view destroyed")
    }

    /**On View Created method, this will be executed each tab the users click on the first tab*/
    @RequiresApi(Build.VERSION_CODES.O) //This line is needed when LocalDate is used
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** Initialise Date Picker */
        datePicker = view.findViewById<DatePicker>(R.id.datePicker1) as DatePicker;
        /** Initialise image buttons: Italy, Scotland */
        btn_italy = view.findViewById<ImageButton>(R.id.ita_Button) as ImageButton
        btn_scotland = view.findViewById<ImageButton>(R.id.scot_Button) as ImageButton

        /** If the value of the date in the view model is not null*/
        if (model.pickedDate.value != null){
            /** Assign model view date value to the date picker*/
            var d: LocalDate = model.pickedDate.value as LocalDate
            datePicker.updateDate(d.year, d.monthValue - 1, d.dayOfMonth)
        }
        else
            model.setDate(LocalDate.now())

        /** If the league has been selected, set one of the two buttons as selected according to the value in the view model */
        if (model.league.value != null) {
            if (model.league.value!!.contains("ITA", ignoreCase = true))
                setIta()
            else if (model.league.value!!.contains("Sco", ignoreCase = true))
                setSco()
        }

        /** Date Picker listener, store date in view model */
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            /** Update model with date */
            model.setDate(date)
        }

        /** When button with Scotland's flag is pressed, hide the Italian one */
        btn_scotland.setOnClickListener{ setSco() }
        /** When button with Italy's flag is pressed, hide the Scottish one  */
        btn_italy.setOnClickListener{ setIta() }
      }

    /**
     * Show the Italian flag, make the Scottish one transparent, set Italy as the chosen league
     * */
    private fun setIta(){
        model.league.value = "ITA" //Update model with league
        btn_italy.alpha = 1F
        btn_scotland.alpha = 0.4F
    }

    /**
     * Show the Scottish flag, make the Italian one transparent, set Scotland as the chosen league
     * */
    private fun setSco(){
        model.league.value = "SCO" //Update model with league
        btn_scotland.alpha = 1F
        btn_italy.alpha = 0.4F
    }
}

