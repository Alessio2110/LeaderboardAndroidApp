package uk.ac.stir.cs.practical5
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import java.time.LocalDate
import kotlin.math.abs
import android.content.DialogInterface
import android.util.Log
import android.widget.*
import androidx.core.view.contains
import android.widget.TextView


class Page3Fragment : Fragment() {
    /** ArrayList of items: Game. Store all games that the user wants to display*/
    private var gameList = ArrayList<Game>()
    /** ListView: Display games */
    private lateinit var listView: ListView
    /** List of filtered and sorted games*/
    lateinit var allGames: ArrayList<Game>
    /** Initialise game selected position */
    private var positionSelected: Int = -1
    /** The ViewModel used to store values between fragments. */
    private val model: MyViewModel by activityViewModels()

    /** Spinner for sorting */
    private lateinit var spnr_sorting: Spinner
    /** Spinner for filtering by league */
    private lateinit var spnr_filter_league: Spinner
    /** Spinner for filtering by team */
    private lateinit var spnr_filter_team: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page3_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "Fragment 3 view destroyed")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onViewCreated(view, savedInstanceState)
        /** Initialise ListView */
        listView = view.findViewById(R.id.historyList) as ListView
        /** Add GameAdapter as listView adapter */
        listView.adapter = context?.let { it -> GameAdapter(it, R.layout.my_item, gameList) }

        /** Initialise spinners for sorting, filtering by league, and filtering by team */
        spnr_sorting = view.findViewById<Spinner>(R.id.spinner_sort) as Spinner
        spnr_filter_league = view.findViewById<Spinner>(R.id.spinner_filter_league) as Spinner
        spnr_filter_team = view.findViewById<Spinner>(R.id.spinner_filter_team) as Spinner

        /** Instantiate SQLite manager to retrieve games*/
        var sqliteManager: SQLiteManager = SQLiteManager(context)
        /** Populate listview when fragment is created*/
        updateList()

        /** listView click listener */
        listView.setOnItemClickListener { parent, view, position, id ->
            positionSelected = position
            listView.clearFocus()
            (listView.adapter as ArrayAdapter<String>?)?.notifyDataSetChanged()
        }

        //Create Button, when this is clicked open DialogInterface to confirm that the user wants to delete all entries
        /** Instantiate button to delete all entries */
        val btn_remove = view.findViewById<Button>(R.id.remove_button) as Button
        btn_remove.setOnClickListener(){
            if (positionSelected >= 0){
                var aGame: Game = listView.getItemAtPosition(positionSelected) as Game
                val success:Boolean = sqliteManager.deleteOne(aGame.home_team, aGame.away_team, aGame.date)
                System.out.println("Success:" + success)
                updateList()
            }
            else
                Toast.makeText(getActivity(), "Select the game you want to remove before clicking the button", Toast.LENGTH_LONG).show()
        }
        /** team filtering spinner listener */
        spnr_filter_team.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { updateList() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        /** sorting spinner listener */
        spnr_sorting.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { updateList() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        /** league filtering spinner listener*/
        spnr_filter_league.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var initial_position: Int = 0
            /** When an item in the spinner is selected */
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, newPosition: Int, id: Long) {
                var league_of_game: String = spnr_filter_league.selectedItem as String
                var spnr_adapter: ArrayAdapter<CharSequence>

                if (league_of_game.contains("Ita", ignoreCase = true)) { //If "ITA" (Italy) is selected, use italian teams
                    spnr_adapter = context?.let { ArrayAdapter.createFromResource(it, R.array.italian_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                    spnr_adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnr_filter_team.adapter = spnr_adapter
                    spnr_filter_team.isEnabled = true
                }
                else if(league_of_game.contains("Sco", ignoreCase = true)){
                    spnr_adapter = context?.let { ArrayAdapter.createFromResource(it, R.array.scottish_teams, android.R.layout.simple_spinner_item) } as ArrayAdapter<CharSequence>
                    spnr_adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnr_filter_team.adapter = spnr_adapter
                    spnr_filter_team.isEnabled = true
                }
                else
                    spnr_filter_team.isEnabled = false

                updateList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        /** This textview only shows up when the listview is empty*/
        var emptyTextView: TextView = view.findViewById<TextView>(R.id.emptyList) as TextView
        listView.setEmptyView(emptyTextView)

        /** Instantiate button to delete all entries */
        val btn_delete_all = view.findViewById<Button>(R.id.deleteAll) as Button
        /** delete entries button listener*/
        btn_delete_all.setOnClickListener{
            val dialogClickListener =
                /** Ask the user whether he is sure he wants to delete all entries */
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> { //If "YES", delete all entries
                            deleteEntries()
                            updateList()
                            Toast.makeText(getActivity(), "All games have been deleted from the database!", Toast.LENGTH_SHORT).show()

                        }
                        DialogInterface.BUTTON_NEGATIVE -> { //else return to fragment
                            Toast.makeText(getActivity(), "Wise choice!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            /** Question, positive, and negative answer */
            builder.setMessage("Are you sure you want to delete all entries? You'll not be able to restore them! If you are a professor, you might want to test this at the very end, so you do not have to insert a lot of games to test the app!")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }

        /** When the array list has been modified, update gameList*/
        model.allResults_live.observe(viewLifecycleOwner, object: Observer<Any>
        {
            override fun onChanged(t: Any?) {
                updateList()
            }
        })
    }

    /**
     * return list sorted by date descending
     * @param theList, the unsorted list
     * @return the list sorted
     * */
    private fun sortDateDesc(theList: ArrayList<Game>): ArrayList<Game>{
        theList.sortWith(compareByDescending { it.date })
        return theList
    }

    /**
     * return list sorted by date descending
     * @param theList, the list sorted by date descending
     * @return the list sorted
     * */
    private fun sortDateAsc(theList: ArrayList<Game>): ArrayList<Game>{
        var sortedList = sortDateDesc(theList)
        sortedList.reverse()
        return sortedList
    }

    /**
     * update list for listview based on sorting and filters
     * */
    private fun updateList() {
        /** Instantiate SQLiteManager database handler*/
        var sqliteManager: SQLiteManager = SQLiteManager(context)
        /** Clear game list from previous entries*/

        gameList.clear()
        allGames = sqliteManager.populateGameArrList()

        /** Filtering*/
        if ((spnr_filter_team.isEnabled) && !(spnr_filter_team.selectedItem as String).contains("All teams")) {
            val team: String = (spnr_filter_team.selectedItem as String)
            allGames = filter_by_team(team)
        } else if (!(spnr_filter_league.selectedItem as String).contains("All leagues")) {
            val league: String = spnr_filter_league.selectedItem as String
            allGames = filter_by_league(league)
        }

        /** Sorting by*/
        if ((spnr_sorting.selectedItem as String).contains("most recent", ignoreCase = true)){
            allGames = sortDateDesc(allGames)
        }
        else if ((spnr_sorting.selectedItem as String).contains("least recent", ignoreCase = true)){
            allGames = sortDateAsc(allGames)
        }
        else if ((spnr_sorting.selectedItem as String).contains("highest score", ignoreCase = true)){
            allGames.sortWith(compareByDescending { abs(it.home_score - it.away_score) })
        }
        else if ((spnr_sorting.selectedItem as String).contains("lowest score", ignoreCase = true)){
            allGames.sortWith(compareByDescending { abs(it.home_score - it.away_score) })
            allGames.reverse()
        }

        /** Add each game in the game list to be displayed*/
        allGames.forEach {
            gameList.add(it)
        }
        /** Since listView listener for item selected cannot be removed ( only changed), we need to
         * set a new adapter each time. Likely inefficient, but it is the only solution found */
        listView.adapter = context?.let { it -> GameAdapter(it, R.layout.my_item, gameList) }
        /** Notify adapter of the change */
        (listView.adapter as GameAdapter?)?.notifyDataSetChanged()
        positionSelected = -1

    }

    /**
     * Get a filtered list by league
     * @param league, the league of the game (ITA or SCO)
     * @return ArrayList<Game>, return filtered array
     * */
    private fun filter_by_league(league: String): ArrayList<Game> {
        var league_abbrev: String
        if (league.contains("Ita"))
            league_abbrev = "ITA"
        else
            league_abbrev = "SCO"

        var sqliteManager: SQLiteManager = SQLiteManager(context)
        var theList: ArrayList<Game> = sqliteManager.league_filter_arr(league_abbrev)
        return theList
    }

    /**
     * Get a filtered list by team
     * @param team, show only games where this team was either the home team or away team
     * @return ArrayList<Game>, return filtered array
     * */
    private fun filter_by_team(team: String): ArrayList<Game> {
        var sqliteManager: SQLiteManager = SQLiteManager(context)
        var theList: ArrayList<Game> = sqliteManager.filteredArr(team)
        return theList
    }

    /**
     * Delete all entries from the database
     * */
    private fun deleteEntries(){
    val sqliteManager: SQLiteManager = SQLiteManager(context)
    sqliteManager.deleteAll() //delete entries from database
    }
}

