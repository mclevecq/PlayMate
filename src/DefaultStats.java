//Michael Levecque
//Senior Project supporting class file
//Originally Created: Fall 2017

//All of the following data was used from the Pro Football Reference database, and is based on all historical data
//present from individual player records from the NFL Combine from the years 2000-2018.  Height is recorded in (int)
//inches, and weight is recorded in (int) pounds to better serve for the intended use in the play simulation algorithm.
//Stats for Speeds were adapted from results recorded from individual players in the 40-Yard Dash Event (e.g. if a
//player recorded a 4.40 second 40-yard dash, the associated stat is the double 0.440).  Stats for Strengths were
//adapted from the Bench Press Event (number of repetitions recorded lifting a consistent 225 lbs) into usable
//percentiles in double form.  For example, 25 repetitions on the Bench Press Event at the Combine for an offensive
//player corresponds to a double strength value of 0.500, or the 50th percentile among all players.  This is because
//the minimum and maximum repetitions recorded historically for all offensive players are 5 repetitions and 45
//repetitions, respectively.  25 repetitions is 20 more than the minimum 5 repetitions in a total range of 40 repetitions
//(45 repetitions minus 5 repetitions equals a range of 40 repetitions).  So, the adapted statistic is 20/40 = 0.500.

import java.awt.*;
import java.awt.Color;

public class DefaultStats {

    public int skillPos;

    //******************************************************************************************************************
    //OFFENSIVE STATS

    //MIN AND MAX OFFENSIVE PLAYER HEIGHTS
    public int[][] OHeights = {
            {71, 79},   //0 - QB
            {66, 72},   //1 - RB
            {70, 75},   //2 - FB
            {65, 73},   //3 - WR
            {71, 80},   //4 - TE
            {73, 78},   //5 - C
            {74, 80},   //6 - OG (R and L)
            {74, 80},   //7 - OT (R and L)
            {74, 80}    //8 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER WEIGHTS
    public int[][] OWeights = {
            {197, 253},   //0 - QB
            {174, 242},   //1 - RB
            {222, 271},   //2 - FB
            {156, 229},   //3 - WR
            {225, 270},   //4 - TE
            {280, 320},   //5 - C
            {293, 355},   //6 - OG (R and L)
            {296, 358},   //7 - OT (R and L)
            {280, 358}    //8 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER STRENGTHS
    public double[][] OStrengths = {
            {0.0, 0.0},   //0 - QB
            {0.0, 0.675},   //1 - RB
            {0.225, 0.775},   //2 - FB
            {0.050, 0.525},   //3 - WR
            {0.175, 0.750},   //4 - TE
            {0.200, 0.900},   //5 - C
            {0.100, 1.000},   //6 - OG (R and L)
            {0.200, 0.825},   //7 - OT (R and L)
            {0.100, 1.000}    //8 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER SPEEDS
    public double[][] OSpeeds = {
            {0.433, 0.537},   //0 - QB
            {0.431, 0.462},   //1 - RB
            {0.453, 0.502},   //2 - FB
            {0.422, 0.450},   //3 - WR
            {0.440, 0.504},   //4 - TE
            {0.489, 0.558},   //5 - C
            {0.490, 0.561},   //6 - OG (R and L)
            {0.471, 0.546},   //7 - OT (R and L)
            {0.471, 0.561}    //8 - Generic OL
    };

    //******************************************************************************************************************
    //******************************************************************************************************************
    //DEFENSIVE STATS

    //MIN AND MAX DEFENSIVE PLAYER HEIGHTS
    public int[][] DHeights = {
            {72, 78},   //0 - Generic DL
            {72, 77},   //1 - NT OR DT
            {73, 78},   //2 - DE
            {70, 76},   //3 - Generic LB
            {71, 76},   //4 - ILB/MLB
            {70, 76},   //5 - OLB
            {67, 76},   //6 - Generic DB
            {67, 72},   //7 - CB
            {71, 75},   //8 - S
            {69, 76},   //9 - FS
            {69, 76},   //10 - SS
    };

    //MIN AND MAX DEFENSIVE PLAYER WEIGHTS
    public int[][] DWeights = {
            {235, 354},   //0 - Generic DL
            {277, 354},   //1 - NT OR DT
            {235, 310},   //2 - DE
            {218, 270},   //3 - Generic LB
            {228, 265},   //4 - ILB/MLB
            {218, 270},   //5 - OLB
            {175, 231},   //6 - Generic DB
            {175, 216},   //7 - CB
            {195, 215},   //8 - S
            {190, 231},   //9 - FS
            {184, 228},   //10 - SS
    };

    //MIN AND MAX DEFENSIVE PLAYER STRENGTHS
    public double[][] DStrengths = {
            {0.213, 1.000},   //0 - Generic DL
            {0.319, 1.000},   //1 - NT OR DT
            {0.212, 0.723},   //2 - DE
            {0.191, 0.723},   //3 - Generic LB
            {0.191, 0.702},   //4 - ILB/MLB
            {0.213, 0.723},   //5 - OLB
            {0.0, 0.553},   //6 - Generic DB
            {0.0, 0.511},   //7 - CB
            {0.234, 0.489},   //8 - S
            {0.128, 0.532},   //9 - FS
            {0.149, 0.553},   //10 - SS
    };

    //MIN AND MAX DEFENSIVE PLAYER SPEEDS
    public double[][] DSpeeds = {
            {0.453, 0.531},   //0 - Generic DL
            {0.468, 0.531},   //1 - NT OR DT
            {0.453, 0.498},   //2 - DE
            {0.438, 0.505},   //3 - Generic LB
            {0.442, 0.505},   //4 - ILB/MLB
            {0.438, 0.490},   //5 - OLB
            {0.428, 0.475},   //6 - Generic DB
            {0.428, 0.452},   //7 - CB
            {0.434, 0.458},   //8 - S
            {0.431, 0.473},   //9 - FS
            {0.438, 0.475},   //10 - SS
    };

    public DefaultStats(){

    }

}
