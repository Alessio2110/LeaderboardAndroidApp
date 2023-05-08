package uk.ac.stir.cs.practical5
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import java.lang.NumberFormatException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Page2Fragment : Fragment() {
    /** Maximum number of items stored in the database
     * Change this value to allow the user to store
     * a different amount of games in the database*/
    private val MAX_GAMES_STORED: Int = 20

    /** The name of the home team */
    private var home_team_name: String = ""
    /** The name of the away team */
    private var away_team_name: String = ""
    /** The goals scored by the home team */
    private var home_team_score: Int = -1
    /** The goals scored by the away team */
    private var away_team_score: Int = -1
    /** The date the game took place */
    private lateinit var game_date: LocalDate
    /** The league the game was played in (Scotland or Italy) */
    private lateinit var league_of_game: String

    /** EditText for the score of the home team (numerical) */
    private lateinit var home_score_text: EditText
    /** EditText for the score of the away team (numerical) */
    private lateinit var away_score_text: EditText
    /** Spinner with names of the teams from the selected league */
    private lateinit var home_spinner: Spinner
    /** Spinner with names of the teams from the selected league */
    private lateinit var away_spinner: Spinner
    /** Date showing the date selected in fragment 1, this is the day the game was played on */
    private lateinit var dateTextView: TextView
    /** The ViewModel used to store values between fragments. */
    private val model: MyViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return inflater.inflate(R.layout.page2_fragment, container, false)
    }
    /**
     * When changing fragment, hide the keyboard and remove focus from the scores
     * */
    override fun onPause() {
        super.onPause()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        /** This allows the soft keyboard to disappear when changing fragment */
        home_score_text.clearFocus()
        away_score_text.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(ContentValues.TAG, "Fragment 2 view destroyed")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        game_date = LocalDate.now()
        league_of_game = "ITA"

        /** Create text view to display date selected in fragment 1*/
        dateTextView = view.findViewById<TextView>(R.id.textView_date) as TextView
        /** When the user clicks on the date, he will be able to change it again in the first fragment */
        dateTextView.setOnClickListener {// When the user click on the TextView, switch back to the first tab.
            var tabs: TabLayout = requireActivity().findViewById(R.id.tab_layout)
            tabs.getTabAt(0)!!.select()
        }
        /** Create spinner for home team */
        home_spinner = view.findViewById<Spinner>(R.id.spnr_home_team) as Spinner
        /** Create spinner for away team */
        away_spinner = view.findViewById<Spinner>(R.id.spnr_away_team) as Spinner

        /** when the league changes, change the teams displayed in the two spinners*/
        model.league.observe(viewLifecycleOwner,object : Observer<Any>{
            override fun onChanged(t: Any?) {
                /** If it the same league as the one previously selected, do nothing*/
                if (league_of_game == model.league.value)
                    return

                /** Retrieve league name from model view */
                league_of_game = model.league.value as String
                /** Create spinner adapters for home team and away team */
                var spinner_adapter_home: ArrayAdapter<CharSequence>
                var spinner_adapter_away: ArrayAdapter<CharSequence>


                if (league_of_game == "ITA"){ //If "ITA" (Italy) is selected, use italian teams
                    spinner_adapter_home = context?.let { ArrayAdapter.createFromResource(it, R.array.italian_home_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                    spinner_adapter_away = context?.let { ArrayAdapter.createFromResource(it, R.array.italian_away_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                }
                else{ //Else, if "SCO" (Scotland) is selected, use scottish teams
                    spinner_adapter_home = context?.let { ArrayAdapter.createFromResource(it, R.array.scottish_home_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                    spinner_adapter_away = context?.let { ArrayAdapter.createFromResource(it, R.array.scottish_away_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                }
                /** Apply adapters to both spinners*/
                spinner_adapter_home?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_adapter_away?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                home_spinner.adapter = spinner_adapter_home
                away_spinner.adapter = spinner_adapter_away
                clearEntries()
            }
        })

        /** When the selected date changes, change the textview in this fragment */
        model.pickedDate.observe(viewLifecycleOwner, object : Observer<Any> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChanged(t: Any?) {// If changed, updated the class variable and modify the TextView
                /** Create formatters, get date, and format date */
                val formatter = DateTimeFormatter.ofPattern("EEE, d MMM uuuu")
                val date: LocalDate = t!! as LocalDate
                val text: String = date.format(formatter)

                /** Update date of the game */
                game_date = t as LocalDate
                /** Update TextView */
                dateTextView.setText(" " + text)
            }
        })
        /** When the spinner is selected*/
        home_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var initial_position: Int = 0
            /** When an item in the spinner is selected */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, newPosition: Int, id: Long) {
                /** Store current home team name*/
                var currentHomeTeam: String = home_team_name
                /** Get selected team name*/
                home_team_name = parent?.getItemAtPosition(newPosition).toString()

                /** If the selected item is equal to the currently selected item in the other spinner,
                 *  swap the two selections. */
                if (home_team_name == away_team_name && !(away_team_name.contains("Select", ignoreCase = true) )) {
                    away_team_name = currentHomeTeam
                    away_spinner.setSelection(initial_position)
                    Toast.makeText(getActivity(), "Swapped teams!", Toast.LENGTH_SHORT).show()
                }
                initial_position = newPosition //Update position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        away_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var initial_position: Int = 0
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, newPosition: Int, id: Long) {
                /** Store current away team name*/
                var currentAwayTeam: String = away_team_name
                /** Get selected team name*/
                away_team_name = parent?.getItemAtPosition(newPosition).toString()
                /** If the selected item is equal to the currently selected item in the other spinner,
                 *  swap the two selections. */
                if (home_team_name == away_team_name && !(home_team_name.contains("Select", ignoreCase = true) )) {
                    home_team_name = currentAwayTeam
                    home_spinner.setSelection(initial_position)
                    Toast.makeText(getActivity(), "Swapped teams!", Toast.LENGTH_SHORT).show()
                }
                initial_position = newPosition //Update position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        /** Initialise EditText for editing the score for the home team*/
        home_score_text = view.findViewById<Spinner>(R.id.edit_home_score) as EditText
        /** Initialise EditText for editing the score for the away team*/
        away_score_text = view.findViewById<Spinner>(R.id.edit_away_score) as EditText

        /** home score text listener*/
        home_score_text.addTextChangedListener(object : TextWatcher {
            /** When text has been changed */
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                /** Read number from text changed, if it is empty set it equal to -1 */
                try {
                    home_team_score = Integer.parseInt(s.toString()) as Int
                }
                catch (e:  NumberFormatException){
                    home_team_score = -1
                }
            }
            /** Do nothing on after text changed and before text changed*/
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })

        /** away score text listener*/
        away_score_text.addTextChangedListener(object : TextWatcher {
            /** When the text has been changed */
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /** Read number from text changed, if it is empty set it equal to -1 */
                try {
                    away_team_score = Integer.parseInt(s.toString()) as Int
                }
                catch (e: NumberFormatException){
                    away_team_score = -1
                }
            }
            /** Do nothing on after text changed and before text changed*/
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })
        /** initialise button to add a game to the database*/
        val btn_add_game = view.findViewById<Button>(R.id.add_game_button) as Button
        /** Button listener: add game */
        btn_add_game.setOnClickListener{ addGame() } //Try to add game

        /** initialise button to clear entries*/
        val btn_clear = view.findViewById<Button>(R.id.clear_button) as Button
        /** Button listener: clear entries*/
        btn_clear.setOnClickListener{ clearEntries()}
    }




    /**
     * Try to add game to database, or prompt reason why it could not be added
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addGame(){
        /** Get the length, and check whether there are too many games already*/
        var length = model.allResults.size
        if (length > MAX_GAMES_STORED){
            Toast.makeText(getActivity(), "Too many results, eliminate one or more!", Toast.LENGTH_SHORT).show()
            return
        }

        /** Check whether home and away team are valid entries */
        if (away_spinner.selectedItemPosition == 0 || home_spinner.selectedItemPosition == 0){
            Toast.makeText(getActivity(), "Select the home team and the away team", Toast.LENGTH_SHORT).show()
            return
        }

        /** Check whether both scores are valid*/
        if (away_team_score < 0 || home_team_score < 0){
            Toast.makeText(getActivity(), "Select a valid score for both teams", Toast.LENGTH_SHORT).show()
            return
        }

        /** Check whether date is valid*/
        if (model.pickedDate.value != null){
            var d: LocalDate = model.pickedDate.value as LocalDate
            var today: LocalDate = LocalDate.now()
            /** was the game played before tomorrow?*/
            if (!d.isBefore(today.plus(1, ChronoUnit.DAYS))){
            Toast.makeText(getActivity(), "This is not a time machine! Wait for the games to happen", Toast.LENGTH_SHORT).show()
            return
            }
        }

        /** Create game from user input*/
        val game: Game = Game(home_team_name, away_team_name, home_team_score, away_team_score, game_date, league_of_game);
        /** Initialise database */
        val sqLiteManager: SQLiteManager = SQLiteManager(context)

        /** If the teams have not already played against each other in the same day. */
        if (!sqLiteManager.findOne(game.home_team, game.away_team, game.date)) {
            sqLiteManager.addGameToDB(game) //Add game to database
            Toast.makeText(getActivity(), "Game added!", Toast.LENGTH_SHORT).show()
            clearEntries() //clear entries
        }
        else //Let the user know why the game cannot be added
            Toast.makeText(getActivity(), "Two teams cannot play against each other on the same day, let them rest!", Toast.LENGTH_LONG).show()

        /** Retrieve list of all games from database*/
        var myList = sqLiteManager.populateGameArrList()
        /** Update model view list so that it updates the listview in the third fragment*/
        model.setList(myList)
    }

    /**
     * Clear all entries
     */
    private fun clearEntries(){
        /** Clear scores*/
        home_team_score = 0
        away_team_score = 0
        /** Clear scores' text*/
        home_score_text.setText("")
        away_score_text.setText("")
        /** Clear selected teams*/
        home_team_name = ""
        away_team_name = ""
        /** Set spinners to position 0 */
        home_spinner.setSelection(0)
        away_spinner.setSelection(0)
    }
}


