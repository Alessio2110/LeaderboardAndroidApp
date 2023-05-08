package uk.ac.stir.cs.practical5

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Class GameAdapter used as an adapter for listview in fragment 3
 */
class GameAdapter (var mCtx: Context, var resource: Int, var objects: List<Game>) :
    ArrayAdapter<Game>(mCtx, resource, objects) {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View{
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        /** Create view */
        val view: View = layoutInflater.inflate(resource, null)

        /** TextView for home team name (abbreviated) */
        val tv_home: TextView = view.findViewById<TextView>(R.id.home_team_text)
        /** TextView for away team name (abbreviated) */
        val tv_away: TextView = view.findViewById<TextView>(R.id.away_team_text)
        /** TextView for home team score */
        val tv_home_score: TextView = view.findViewById<TextView>(R.id.home_score_tv)
        /** TextView for away team score */
        val tv_away_score: TextView = view.findViewById<TextView>(R.id.away_score_tv)
        /** TextView for home team name (abbreviated) */
        val tv_date: TextView = view.findViewById<TextView>(R.id.editTextDate)
        /** TextView for the summary of the team */
        val tv_summary: TextView = view.findViewById(R.id.TextView_gameSummary)
        /** ImageView for home team logo */
        val iv_home: ImageView = view.findViewById(R.id.home_team_pic)
        /** ImageView for away team logo */
        val iv_away: ImageView = view.findViewById(R.id.away_team_pic)

        /** Get Game object at index 'position' */
        val aGame:Game = objects[position] as Game

        /** Set textviews text, team colours, and team logos */
        setListItemTexts(aGame, tv_home, tv_away, tv_home_score, tv_away_score, tv_date, tv_summary)
        colourTeams(aGame, tv_home, tv_away)
        setListItemImages(aGame, iv_home, iv_away)

        return view
    }
    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * Change values of all the textviews based on the game 'g'
     * @param g, the game
     * @param tv_h, the text view for the home team name abbreviation
     * @param tv_a, the text view for the away team name abbreviation
     * @param tv_hs, the text view for the score of the home team
     * @param tv_as, the text view for the score of the away team
     * @param tv_d, the text view for the date the game took place on
     */
    private fun setListItemTexts(g: Game, tv_h: TextView, tv_a: TextView, tv_hs: TextView, tv_as: TextView, tv_d: TextView, tv_s: TextView){
        /** Instantiate GameCalculator */
        val gc: GameCalculator = GameCalculator()

        /** Set abbreviated team names, and set scores */
        tv_h.setText(gc.findAbbrev(g.home_team))
        tv_a.setText(gc.findAbbrev(g.away_team))
        tv_hs.setText(g.home_score.toString())
        tv_as.setText(g.away_score.toString())

        /** Format date, and apply text to date textview*/
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMM uuuu")
        val date: LocalDate = g.date
        val date_text: String = date.format(formatter)
        tv_d.setText(date_text)

        /** Create game summary and apply to textview*/
        tv_s.setText(gc.getSummary(g))

    }

    /**
     * Set team logos as image for imageviews
     * @param g, the game
     * @parma iv_h, imageview for home team
     * @param iv_a, imagveiew for away team
     */
    private fun setListItemImages(g: Game, iv_h: ImageView, iv_a: ImageView ){
        /** Instantiate game calculator */
        val gc: GameCalculator = GameCalculator()

        /** Find images and apply them to imageviews*/
        val imagePos_H: Int = gc.findImage(g.home_team)
        iv_h.setImageDrawable(mCtx.resources.getDrawable(imagePos_H))
        val imagePos_A: Int = gc.findImage(g.away_team)
        iv_a.setImageDrawable(mCtx.resources.getDrawable(imagePos_A))
    }

    /**
     * Set a colour to team names based on score
     * @param g, the game
     * @param ht, textview for home team
     * @param at, textview for away team
     * */
    private fun colourTeams(g: Game, ht: TextView, at: TextView){
        /** If it was a draw*/
        if (g.home_score == g.away_score){
            ht.setTextColor(Color.parseColor("#FFD700"))
            at.setTextColor(Color.parseColor("#FFD700"))
        }
        /** If the home team won*/
        else if(g.home_score > g.away_score){
            ht.setTextColor(Color.parseColor("#0aad3f"))
            at.setTextColor(Color.parseColor("#FF4500"))
        }
        /** If the away team won*/
        else{
            ht.setTextColor(Color.parseColor("#FF4500"))
            at.setTextColor(Color.parseColor("#0aad3f"))
        }
    }
}