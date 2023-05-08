package uk.ac.stir.cs.practical5

import android.os.Build
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameCalculator {

    /**
     * Find logo of a team
     * @param s, the team name
     * @return Int, the image location
     * */
    fun findImage(s:String): Int{
        /** Check whether the string is equal to a team name */
    when (s.lowercase()) {
        //Italian Teams
        "black lions" -> return R.drawable.blacklions
        "bolzano" -> return R.drawable.bolzano
        "milano molotov" -> return R.drawable.milano
        "sesto" -> return R.drawable.sesto
        "spartak milano" -> return R.drawable.spartak
        "sterzing gargazon" -> return R.drawable.sterzing
        "viking roma fc" -> return R.drawable.viking
        "wild boars varese" -> return R.drawable.varese
        //Scottish teams
        "aberdeen oilers" -> return R.drawable.aberdeenoil
        "aberdeen university" -> return R.drawable.aberdeenuni
        "edinburgh unicorns" -> return R.drawable.edinburgh
        "fife lightning" -> return R.drawable.fife
        "hawick hawks" -> return R.drawable.hawks
    }
        return R.drawable.viking
    }
    /**
     * Find team name abbreviation
     * @param s, the name of the team
     * @return String, the abbreviation of the team name
     * */
    fun findAbbrev(s: String) : String{
        when (s.lowercase()) {
            //Italian teams
            "black lions" -> return "BLK"
            "bolzano" -> return "BZN"
            "milano molotov" -> return "MOL"
            "sesto" -> return "SST"
            "spartak milano" -> return "SPA"
            "sterzing gargazon" -> return "STZ"
            "viking roma fc" ->return  "VIK"
            "wild boars varese" -> return "VAR"
            //Scottish teams
            "aberdeen oilers" -> return "OIL"
            "aberdeen university" -> return "UNI"
            "edinburgh unicorns" -> return "EDI"
            "fife lightning" -> return "FIF"
            "hawick hawks" -> return "HWK"
        }
        return "null"
    }
    /**
     * @param g, the game
     * @return a summary of the game
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    public fun getSummary(game: Game): String{
        var s: StringBuilder = StringBuilder()
        var the_date: String = getStringFromDate(game.date)
        /** Add date*/
        s.append("On " + the_date + ", ")
        s.append(getInitialStatement(game))

        /** Add string saying how many points each team won, this depends on the game score */
        if (game.home_score > game.away_score){ //If home team won
            s.append("The final score shows that " + game.home_team + " scored " + (game.home_score - game.away_score) + " more goals than " + game.away_team +
                    ": " + game.home_team + " obtained 3 points")
        }
        else if (game.home_score < game.away_score){ //If away team won
            s.append("The final score shows that " + game.home_team + " conceded " + (game.away_score - game.home_score) + " more goals than " + game.away_team + ":" +
                    "3 points go to " + game.away_team)
        }
        else{ //If it was a draw
            s.append("Both teams scored " + game.home_score + " goals. " + game.home_team + " and " + game.away_team + " will be awarded 1 point")
        }
        return s.toString()
    }

    /**
     * get string from date
     * @param l, the date
     * @return the formatted date to string
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStringFromDate(l: LocalDate) : String{
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM uuuu")
        return l.format(formatter)
    }

    /**
     * Create initial sentence for summary
     * @param game, the game
     * @return String, description of winning team
     * */
    private fun getInitialStatement(game: Game) : String{
        var s: StringBuilder = StringBuilder()
        if (game.home_score > game.away_score)
            s.append("the home team got the better of it, rewarding their fans with another day to celebrate." )
        else if (game.home_score <  game.away_score)
            s.append("the visiting team did not suffer the pressure of the home team's supporters and manage to add a victory to their season.")
        else
            s.append("both teams struggled to outscore the opponents, leading to a draw that did not fully satisfy neither the home team nor the visiting team.")
        return s.toString()
    }
}