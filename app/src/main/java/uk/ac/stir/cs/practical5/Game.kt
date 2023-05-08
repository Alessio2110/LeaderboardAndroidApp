package uk.ac.stir.cs.practical5

import java.time.LocalDate
/**
 * Class for game object
 * @param home_team, the name of the home team
 * @param away_team, the nane of the away team
 * @param home_score, the goals scored by the home team
 * @param away_score, the goals scored by the away team
 * @param date, the LocalDate object: the day (yy--mm-dd) the game was played on
 * @param league, the league the game was played in (SCO or ITA)
 * */
data class Game(val home_team: String, val away_team: String,
                val home_score: Int, val away_score:Int,
                val date: LocalDate, val league: String){}

