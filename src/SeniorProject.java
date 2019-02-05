/*
    FILL IN HEADER LATER


 */

import javafx.scene.control.Spinner;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.*;
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;


//----------------------------------------------------------------------------------------------------------------------
//Main Class - PlayMate refers to name of the application, which is an interactive coaching aid to layout, create, and
//run simulations of football plays.

public class SeniorProject {

    //Final int values to pass in for TOTAL IMAGE FRAME size for the GUI
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 790;

    //Implementing worker thread to initialize the GUI-based program in "safe" way on the EDT Thread
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI(){
        JFrame frame = new ImageFrame( WIDTH, HEIGHT );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

//Custom Panel class to serve as the panel that will display all elements of the football play simulator:
//Background Image, player graphics, and so on
class playSimulationPanel extends JPanel{
    private BufferedImage playImage;
    private Graphics2D playG2D;

    public playSimulationPanel(BufferedImage im) {
        this.playImage = im;
        playG2D = playImage.createGraphics();

    }

    public void setImage(BufferedImage image){
        playG2D.setPaintMode();
        playG2D.drawImage(image, 0, 0, this);
        repaint();
    }

    public void paintComponent(Graphics g2d){
        super.paintComponent(g2d);
        g2d.drawImage(playImage, 0 , 0, null);
    }
}

//Second Custom Panel class to serve as the UI panel for user interaction.
class interfacePanel extends JPanel {
    private BufferedImage interImage;
    private Graphics2D interG2D;

    public interfacePanel(BufferedImage im){
        this.interImage = im;
        interG2D = interImage.createGraphics();
    }

    public void setImage(BufferedImage image){
        interG2D.setPaintMode();
        interG2D.drawImage(image, 0, 0, null);
        repaint();
    }

    public void paintComponent(Graphics g2d){
        super.paintComponent(g2d);
        g2d.drawImage(interImage, 0 , 0, null);
    }
}

class ImageFrame extends JFrame {

    //******************************************************************************************************************
    //OFFENSIVE STATS

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

    //MIN AND MAX OFFENSIVE PLAYER HEIGHTS
    private int[][] OHeights = {
            {71, 79},   //0 - QB
            {66, 72},   //1 - RB
            {70, 75},   //2 - FB
            {65, 73},   //3 - WR
            {71, 80},   //4 - TE
            {73, 78},   //5 - C
            {74, 80},   //7 - OG (R and L)
            {74, 80},   //8 - OT (R and L)
            {74, 80}    //6 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER WEIGHTS
    private int[][] OWeights = {
            {197, 253},   //0 - QB
            {174, 242},   //1 - RB
            {222, 271},   //2 - FB
            {156, 229},   //3 - WR
            {225, 270},   //4 - TE
            {280, 320},   //5 - C
            {293, 355},   //7 - OG (R and L)
            {296, 358},   //8 - OT (R and L)
            {280, 358}    //6 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER STRENGTHS
    private double[][] OStrengths = {
            {0.0, 0.0},   //0 - QB
            {0.0, 0.675},   //1 - RB
            {0.225, 0.775},   //2 - FB
            {0.050, 0.525},   //3 - WR
            {0.175, 0.750},   //4 - TE
            {0.200, 0.900},   //5 - C
            {0.100, 1.000},   //7 - OG (R and L)
            {0.200, 0.825},   //8 - OT (R and L)
            {0.100, 1.000}    //6 - Generic OL
    };

    //MIN AND MAX OFFENSIVE PLAYER SPEEDS
    private double[][] OSpeeds = {
            {0.433, 0.537},   //0 - QB
            {0.431, 0.462},   //1 - RB
            {0.453, 0.502},   //2 - FB
            {0.422, 0.450},   //3 - WR
            {0.440, 0.504},   //4 - TE
            {0.489, 0.558},   //5 - C
            {0.490, 0.561},   //7 - OG (R and L)
            {0.471, 0.546},   //8 - OT (R and L)
            {0.471, 0.561}    //6 - Generic OL
    };

    //DEFENSIVE STATS

    //MIN AND MAX DEFENSIVE PLAYER HEIGHTS
    private int[][] DHeights = {
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
    private int[][] DWeights = {
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
    private double[][] DStrengths = {
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
    private double[][] DSpeeds = {
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

    private int qbH = (OHeights[0][0] + OHeights[0][1]) / 2;
    private int rbH = (OHeights[1][0] + OHeights[1][1]) / 2;
    private int fbH = (OHeights[2][0] + OHeights[2][1]) / 2;
    private int wrH = (OHeights[3][0] + OHeights[3][1]) / 2;
    private int teH = (OHeights[4][0] + OHeights[4][1]) / 2;
    private int cH = (OHeights[5][0] + OHeights[5][1]) / 2;
    private int ogH = (OHeights[6][0] + OHeights[6][1]) / 2;
    private int otH = (OHeights[7][0] + OHeights[7][1]) / 2;
    private int olH = (OHeights[8][0] + OHeights[8][1]) / 2;

    private int qbW = (OWeights[0][0] + OWeights[0][1]) / 2;
    private int rbW = (OWeights[1][0] + OWeights[1][1]) / 2;
    private int fbW = (OWeights[2][0] + OWeights[2][1]) / 2;
    private int wrW = (OWeights[3][0] + OWeights[3][1]) / 2;
    private int teW = (OWeights[4][0] + OWeights[4][1]) / 2;
    private int cW = (OWeights[5][0] + OWeights[5][1]) / 2;
    private int ogW = (OWeights[6][0] + OWeights[6][1]) / 2;
    private int otW = (OWeights[7][0] + OWeights[7][1]) / 2;
    private int olW = (OWeights[8][0] + OWeights[8][1]) / 2;

    private double qbSt = (OStrengths[0][0] + OStrengths[0][1]) / 2.0;
    private double rbSt = (OStrengths[1][0] + OStrengths[1][1]) / 2.0;
    private double fbSt = (OStrengths[2][0] + OStrengths[2][1]) / 2.0;
    private double wrSt = (OStrengths[3][0] + OStrengths[3][1]) / 2.0;
    private double teSt = (OStrengths[4][0] + OStrengths[4][1]) / 2.0;
    private double cSt = (OStrengths[5][0] + OStrengths[5][1]) / 2.0;
    private double ogSt = (OStrengths[6][0] + OStrengths[6][1]) / 2.0;
    private double otSt = (OStrengths[7][0] + OStrengths[7][1]) / 2.0;
    private double olSt = (OStrengths[8][0] + OStrengths[8][1]) / 2.0;

    private double qbS = (OSpeeds[0][0] + OSpeeds[0][1]) / 2.0;
    private double rbS = (OSpeeds[1][0] + OSpeeds[1][1]) / 2.0;
    private double fbS = (OSpeeds[2][0] + OSpeeds[2][1]) / 2.0;
    private double wrS = (OSpeeds[3][0] + OSpeeds[3][1]) / 2.0;
    private double teS = (OSpeeds[4][0] + OSpeeds[4][1]) / 2.0;
    private double cS = (OSpeeds[5][0] + OSpeeds[5][1]) / 2.0;
    private double ogS = (OSpeeds[6][0] + OSpeeds[6][1]) / 2.0;
    private double otS = (OSpeeds[7][0] + OSpeeds[7][1]) / 2.0;
    private double olS = (OSpeeds[8][0] + OSpeeds[8][1]) / 2.0;

    private int dlH = (DHeights[0][0] + DHeights[0][1]) / 2;
    private int ntdtH = (DHeights[1][0] + DHeights[1][1]) / 2;
    private int deH = (DHeights[2][0] + DHeights[2][1]) / 2;
    private int lbH = (DHeights[3][0] + DHeights[3][1]) / 2;
    private int imlbH = (DHeights[4][0] + DHeights[4][1]) / 2;
    private int olbH = (DHeights[5][0] + DHeights[5][1]) / 2;
    private int dbH = (DHeights[6][0] + DHeights[6][1]) / 2;
    private int cbH = (DHeights[7][0] + DHeights[7][1]) / 2;
    private int sH = (DHeights[8][0] + DHeights[8][1]) / 2;
    private int fsH = (DHeights[9][0] + DHeights[9][1]) / 2;
    private int ssH = (DHeights[10][0] + DHeights[10][1]) / 2;

    private int dlW = (DWeights[0][0] + DWeights[0][1]) / 2;
    private int ntdtW = (DWeights[1][0] + DWeights[1][1]) / 2;
    private int deW = (DWeights[2][0] + DWeights[2][1]) / 2;
    private int lbW = (DWeights[3][0] + DWeights[3][1]) / 2;
    private int imlbW = (DWeights[4][0] + DWeights[4][1]) / 2;
    private int olbW = (DWeights[5][0] + DWeights[5][1]) / 2;
    private int dbW = (DWeights[6][0] + DWeights[6][1]) / 2;
    private int cbW = (DWeights[7][0] + DWeights[7][1]) / 2;
    private int sW = (DWeights[8][0] + DWeights[8][1]) / 2;
    private int fsW = (DWeights[9][0] + DWeights[9][1]) / 2;
    private int ssW = (DWeights[10][0] + DWeights[10][1]) / 2;

    private double dlSt = (DStrengths[0][0] + DStrengths[0][1]) / 2.0;
    private double ntdtSt = (DStrengths[1][0] + DStrengths[1][1]) / 2.0;
    private double deSt = (DStrengths[2][0] + DStrengths[2][1]) / 2.0;
    private double lbSt = (DStrengths[3][0] + DStrengths[3][1]) / 2.0;
    private double imlbSt = (DStrengths[4][0] + DStrengths[4][1]) / 2.0;
    private double olbSt = (DStrengths[5][0] + DStrengths[5][1]) / 2.0;
    private double dbSt = (DStrengths[6][0] + DStrengths[6][1]) / 2.0;
    private double cbSt = (DStrengths[7][0] + DStrengths[7][1]) / 2.0;
    private double sSt = (DStrengths[8][0] + DStrengths[8][1]) / 2.0;
    private double fsSt = (DStrengths[9][0] + DStrengths[9][1]) / 2.0;
    private double ssSt = (DStrengths[10][0] + DStrengths[10][1]) / 2.0;

    private double dlS = (DSpeeds[0][0] + DSpeeds[0][1]) / 2.0;
    private double ntdtS = (DSpeeds[1][0] + DSpeeds[1][1]) / 2.0;
    private double deS = (DSpeeds[2][0] + DSpeeds[2][1]) / 2.0;
    private double lbS = (DSpeeds[3][0] + DSpeeds[3][1]) / 2.0;
    private double imlbS = (DSpeeds[4][0] + DSpeeds[4][1]) / 2.0;
    private double olbS = (DSpeeds[5][0] + DSpeeds[5][1]) / 2.0;
    private double dbS = (DSpeeds[6][0] + DSpeeds[6][1]) / 2.0;
    private double cbS = (DSpeeds[7][0] + DSpeeds[7][1]) / 2.0;
    private double sS = (DSpeeds[8][0] + DSpeeds[8][1]) / 2.0;
    private double fsS = (DSpeeds[9][0] + DSpeeds[9][1]) / 2.0;
    private double ssS = (DSpeeds[10][0] + DSpeeds[10][1]) / 2.0;

    //******************************************************************************************************************

    //Final variables specific to ImageFrame class
    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;

    File fieldSource;
    BufferedImage baseImage;
    File backSource;
    BufferedImage backImage;
    private final JFileChooser chooser;

    //BufferedImages and Graphics2Ds for play image plane and UI image plane
    private BufferedImage playImage = null;
    private Graphics2D playG2D;
    private BufferedImage interImage = null;
    private Graphics2D interG2D;
    private BufferedImage controlImage = null;
    private Graphics2D controlG2D;

    private BufferedImage saveImage = null;

    private int simWidth, simHeight, panelWidth, panelHeight, controlWidth, controlHeight;

    private OPlayer oMenOnField[] = new OPlayer[15];
    private DPlayer dMenOnField[] = new DPlayer[15];

    private BufferedImage fields[] = new BufferedImage[34];
    private BufferedImage backgrounds[] = new BufferedImage[10];


    //Custom panel variables that will be created based off above images/graphics
    private playSimulationPanel mainPanel;  //Main Simulation Panel
    private interfacePanel UIPanel1;         //User interface/interaction Panel
    private interfacePanel UIPanel2;
    private interfacePanel simControlPanel;

    private Color backColor = new Color(255,51,51, 0);

    private Dimension margin = new Dimension(0, 15);
    private Dimension minorSeparation = new Dimension(0,5);
    private Dimension majorSeparation = new Dimension(0,10);
    private int endSpace = 500;

    private String pS = "";

    private int QBCount = 0;
    private int BackCount = 0;
    private int RecDBCount = 0;
    private int LineCount = 0;

    private OPlayer storedOPlayer[];
    private DPlayer storedDPlayer[];

    //******************************************************************************************************************
    //Block for other "global" variables for ImageFrame class to be implemented later

    private boolean oSpread = false;        //Indicates if offensive spread is on the field
    private boolean dSpread = false;        //Indicates if defensive spread is on the field
    private boolean defaultField = true;    //Indicates if it is default field mode (i.e. no other field option has been chosen)
    private boolean passO = false;          //Boolean TRUE FOR PASS OFFENSE
    private boolean passD = false;          //Boolean TRUE FOR PASS DEFENSE

    private boolean runM = false;           //Run up MIDDLE
    private boolean runL = false;           //RUN LEFT
    private boolean runR = false;           //RUN RIGHT

    private boolean mousePressedInsidePlayer = false;    //Indicates if user "clicked on" a player object, dictates mouse actions
    private boolean mouseDragged;                       //Indicates if mouse is currently being dragged

    private boolean routesDisplayed = true; //Indicates if routes are being displayed (block routes, RB routes, etc.)

    //----------------"CLICKED ON" variables ------------------------------------
    private boolean oClicked;               //Indicates if an offensive player has been "clicked" on (TRUE FOR O, FALSE FOR D)
    private int currIndex;                  //Store the index of the detected "clicked" on player to be used for update purposes

    private double playerDiameter = 30.0;               //Determines size of each player object
    private double checkRadius = playerDiameter/2.0;    //Variable linked to diameter, will be used for radial distance check

    private double latSep = 40.0;

    private double zoneCoverageRadius = 60.0;

    //Set step sizes for different route lengths
    private int shortPassSteps = 2000;        //Short pass
    private int midPassSteps = 4000;          //Mid-field pass
    private int deepPassSteps = 6000;         //Deep pass

    private int runDepth = 3000;

    private int QBDropBackHandOff = 2500;     //Set step size for QB dropback for RB handoff on run play

    private int OLBlock = 2000;

    private int defaultCD = 1500;

    private double WRStartTheta;            //Route start angle for WR
    private double RBStartTheta;            //Route start angle for RB
    private double QBStartTheta;

    //Anticipated variables for later timed-animation implementation
    private final int MILLISECONDS_BETWEEN_FRAMES = 50;
    private Timer timer;
    //private int frameCount = 5000;
    private int frameCount = 0;

    //HOW AM I GOING TO INCORPORATE CHANGING THE SPEED?
    private double speedMult = 2.0;

    private boolean isPaused = true;

    private Random rng = new Random();

    //******************************************************************************************************************
    //INITIALIZING ALL GUI CONTROL PANELS AND COMPONENTS

    //INDIVIDUAL PLAYER ATTRIBUTES
    final JLabel header2 = new JLabel("Player Attributes");
    final JPanel controls = new JPanel();
    final JPanel subH1 = new JPanel(new BorderLayout());
    final JLabel pSpec = new JLabel("Skill Position: " + pS);

    final JPanel basicAttributes = new JPanel(new GridLayout(4,3));
    final SpinnerModel HSModel = new SpinnerNumberModel(73, 66, 80, 1);
    final JSpinner heightSpin = new JSpinner(HSModel);
    final SpinnerModel STSModel = new SpinnerNumberModel(50, 0, 100, 1);
    final JSpinner strengthSpin = new JSpinner(STSModel);
    final SpinnerModel WSModel = new SpinnerNumberModel(275,175,375,1);
    final JSpinner weightSpin = new JSpinner(WSModel);
    final SpinnerModel SSModel = new SpinnerNumberModel(4.92, 4.20, 5.65, 0.01);
    final JSpinner speedSpin = new JSpinner(SSModel);

    final JPanel subH2 = new JPanel(new BorderLayout());
    final JPanel protecting = new JPanel(new GridLayout(1,1));
    final JPanel blocking = new JPanel(new GridLayout(1,1));
    final JPanel eligibility = new JPanel(new GridLayout(1,1));
    final JPanel passCatching = new JPanel(new GridLayout(1,1));
    final JPanel passing = new JPanel(new GridLayout(1,1));

    final JRadioButton block = new JRadioButton("Blocking");
    final JRadioButton protection = new JRadioButton("Protecting QB");
    final JRadioButton eligible = new JRadioButton("Eligible Receiver");
    final JRadioButton passCatch = new JRadioButton("Pass Catching");
    final JRadioButton pass = new JRadioButton("Passing");

//    block.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));

    final JPanel coverage = new JPanel(new GridLayout(2,1));
    final JRadioButton z = new JRadioButton("Zone Coverage");
    final JRadioButton m = new JRadioButton("Man Coverage");
    final JPanel zH = new JPanel(new BorderLayout());
    final JLabel zLabel = new JLabel("Coverage Radius:");
    final JSlider zoneR = new JSlider(JSlider.HORIZONTAL, 0, 300, 150);
    final JPanel zonePanel = new JPanel();

    final JPanel tH = new JPanel(new BorderLayout());
    final JLabel tLabel = new JLabel("Target Man:");
    final String[] targetOI = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    final JComboBox<String> target = new JComboBox<>(targetOI);
    final JPanel targetPanel = new JPanel();

    final JPanel btH = new JPanel(new BorderLayout());
    final JLabel btLabel = new JLabel("Block Target:");
    final String[] bTargetI = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    final JComboBox<String> bTarget = new JComboBox<>(bTargetI);
    final JPanel bTargetPanel = new JPanel();

    final JPanel subH3OP = new JPanel(new BorderLayout());
    final JLabel subH3O = new JLabel("Blocking and Eligibility");
    final JPanel subH3DP = new JPanel(new BorderLayout());
    final JLabel subH3D = new JLabel("Coverage");

    final JPanel rAngle = new JPanel(new GridLayout(1,3));
    final SpinnerModel RAModel = new SpinnerNumberModel(90.0, -180.0, 180.0, 1.0);
    final JSpinner routeAngleSpin = new JSpinner(RAModel);
    final JSlider routeDepth = new JSlider(JSlider.HORIZONTAL, 0, 6000, 3000);
    final JSlider crossDepth = new JSlider(JSlider.HORIZONTAL, 0, 6000, 3000);
    final String[] routeNames = { "Streak", "Post/Corner", "In/Out", "Slant", "Curl/Comeback", "Fade", "Wheel"};
    final JComboBox<String> routes = new JComboBox<>(routeNames);
    final JPanel routeType = new JPanel();
    final JPanel inOut = new JPanel(new GridLayout(1,2));
    final JRadioButton inButton = new JRadioButton("In");
    final JRadioButton outButton = new JRadioButton("Out");
    final JPanel rightLeft = new JPanel(new GridLayout(1,2));
    final JRadioButton rightButton = new JRadioButton("Ball Right");
    final JRadioButton leftButton = new JRadioButton("Ball Left");
    final JPanel rT = new JPanel(new GridLayout(1,1));
    final JPanel subH41 = new JPanel(new BorderLayout());
    final JLabel sub41 = new JLabel("Route Depth:");
    final JPanel subH42 = new JPanel(new BorderLayout());
    final JLabel sub42 = new JLabel("Cross-Step Depth:");
    final JPanel subH43 = new JPanel(new BorderLayout());
    final JLabel sub43 = new JLabel("Route Type:");
    final JPanel subH4 = new JPanel(new BorderLayout());

    final JPanel possession = new JPanel();
    final JButton ballButton = new JButton("Player Has Ball");
    final JPanel BLPanel = new JPanel(new GridLayout(1,1));
    final JPanel BBPanel = new JPanel(new GridLayout(1,1));

    //OVERALL SPREAD ATTRIBUTES
    final JLabel header1 = new JLabel("Spread Attributes");
    final JPanel spread = new JPanel();
    final JPanel configureOP = new JPanel(new GridLayout(1,1));
    final JButton configureO = new JButton("Configure Offense");
    final JPanel configureDP = new JPanel(new GridLayout(1,1));
    final JButton configureD = new JButton("Configure Defense");

    final JPanel sH1 = new JPanel(new BorderLayout());
    final JPanel ODIndicator = new JPanel(new GridLayout(1,2));
    final JRadioButton ORB = new JRadioButton("Offense");
    final JRadioButton DRB = new JRadioButton("Defense");

    final JPanel sH2 = new JPanel(new BorderLayout());
    final String[] OPlayTypes = { "Pass", "Run Left", "Run Middle", "Run Right", "Play Action", "RPO", "Other"};
    final JComboBox<String> OPT = new JComboBox<>(OPlayTypes);
    final String[] DPlayTypes = { "Pass", "Run", "Combination", "Other"};
    final JComboBox<String> DPT = new JComboBox<>(DPlayTypes);

    final JPanel sH3 = new JPanel(new BorderLayout());
    final String[] OPlayForms = { "Basic", "I Formation", "Power I", "Shotgun", "Pistol", "Wishbone", "Single Set Back", "Empty Set", "Wildcat", "Other"};
    final JComboBox<String> OPF = new JComboBox<>(OPlayForms);
    final String[] DPlayForms = { "Basic", "Nickel", "Dime", "4-3", "3-4", "6-1", "2-5", "4-4", "5-3", "6-2", "Other"};
    final JComboBox<String> DPF = new JComboBox<>(DPlayForms);

    final JPanel sH4 = new JPanel(new BorderLayout());
    final JPanel OPersonnelBreakdown = new JPanel(new GridLayout(4, 2));
    final JLabel O1 = new JLabel("" + QBCount);
    final JLabel O2 = new JLabel("" + BackCount);
    final JLabel O3 = new JLabel("" + RecDBCount);
    final JLabel O4 = new JLabel("" + LineCount);
    final JPanel DPersonnelBreakdown = new JPanel(new GridLayout(3, 2));
    final JLabel D1 = new JLabel("" + LineCount);
    final JLabel D2 = new JLabel("" + BackCount);
    final JLabel D3 = new JLabel("" + RecDBCount);

    final JPanel sH5 = new JPanel(new BorderLayout());

    final String[] OPlayClass = { "Quarterback", "Offensive Backfield", "Offensive Line", "Receiver"};
    final JComboBox<String> OPC = new JComboBox<>(OPlayClass);

    final String[] QBPositions = { "Quarterback (QB)"};
    final String[] OBackPositions = { "Running Back (RB)", "FullBack (FB)"};
    final String[] RecPositions = { "Wide Receiver (WR)", "Tight End (TE)"};
    final String[] OLinePositions = { "Center (C)", "Offensive Guard (OG)", "Offensive Tackle (OT)", "Offensive Lineman (OL)"};
    final JComboBox<String> QBP = new JComboBox<>(QBPositions);
    final JComboBox<String> OBP = new JComboBox<>(OBackPositions);
    final JComboBox<String> RP = new JComboBox<>(RecPositions);
    final JComboBox<String> OLP = new JComboBox<>(OLinePositions);

    final String[] DPlayClass = { "Linebacker", "Defensive Linemen", "Secondary"};
    final JComboBox<String> DPC = new JComboBox<>(DPlayClass);

    final String[] DBackPositions = { "Defensive Back (DB)", "Corner Back (CB)", "Safety (S)", "Free Safety (FS)", "Strong Safety (SS)"};
    final String[] LineBackPositions = { "Line Backer (LB)", "Middle Line Backer (MLB)", "Outside Line Backer (OLB)"};
    final String[] DLinePositions = { "Defensive Lineman (DL)", "Nose Tackle (NT)", "Defensive Tackle (DT)", "Defensive End (DE)"};
    final JComboBox<String> DBP = new JComboBox<>(DBackPositions);
    final JComboBox<String> LBP = new JComboBox<>(LineBackPositions);
    final JComboBox<String> DLP = new JComboBox<>(DLinePositions);
    final JPanel OCPPanel = new JPanel();
    final JPanel DCPPanel = new JPanel();

    final JPanel HPanel = new JPanel(new GridLayout(1,1));
    final JButton disRoutes = new JButton("Hide Routes");
    final JPanel DRPanel = new JPanel();

    //Simulation Controls
    final JPanel frameLine = new JPanel();
    final JSlider f = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
    final JPanel animationPanel = new JPanel();
    final JPanel resetPanel = new JPanel();
    final JPanel speedPanel = new JPanel();
    final JSlider s = new JSlider(JSlider.HORIZONTAL, -2, 2, 0);
    final JButton resetPlaySpreadButton = new JButton("Reset Play");


    //******************************************************************************************************************

    //Constructor
    public ImageFrame(int width, int height){

        //IOException block required to pull in BufferedImages from file object
        try{
            //Initializes the base background image as the generic field
            fieldSource = new File("FieldImages/General.png");
            backSource = new File("GUIImages/ChromeBack.jpg");
            baseImage = ImageIO.read(fieldSource);
            backImage = ImageIO.read(backSource);

            //Populate the field image array with all of the edited field images for every NFL team, the generic field,
            //and the field for the Florida Gators (GO GATORS)
            fields[0] = ImageIO.read(new File("FieldImages/Cardinals.png"));
            fields[1] = ImageIO.read(new File("FieldImages/Falcons.png"));
            fields[2] = ImageIO.read(new File("FieldImages/Ravens.png"));
            fields[3] = ImageIO.read(new File("FieldImages/Bills.png"));
            fields[4] = ImageIO.read(new File("FieldImages/Panthers.png"));
            fields[5] = ImageIO.read(new File("FieldImages/Bears.png"));
            fields[6] = ImageIO.read(new File("FieldImages/Bengals.png"));
            fields[7] = ImageIO.read(new File("FieldImages/Browns.png"));
            fields[8] = ImageIO.read(new File("FieldImages/Cowboys.png"));
            fields[9] = ImageIO.read(new File("FieldImages/Broncos.png"));
            fields[10] = ImageIO.read(new File("FieldImages/Lions.png"));
            fields[11] = ImageIO.read(new File("FieldImages/Packers.png"));
            fields[12] = ImageIO.read(new File("FieldImages/Texans.png"));
            fields[13] = ImageIO.read(new File("FieldImages/Colts.png"));
            fields[14] = ImageIO.read(new File("FieldImages/Jaguars.png"));
            fields[15] = ImageIO.read(new File("FieldImages/Chiefs.png"));
            fields[16] = ImageIO.read(new File("FieldImages/Chargers.png"));
            fields[17] = ImageIO.read(new File("FieldImages/Rams.png"));
            fields[18] = ImageIO.read(new File("FieldImages/Dolphins.png"));
            fields[19] = ImageIO.read(new File("FieldImages/Vikings.png"));
            fields[20] = ImageIO.read(new File("FieldImages/Patriots.png"));
            fields[21] = ImageIO.read(new File("FieldImages/Saints.png"));
            fields[22] = ImageIO.read(new File("FieldImages/Giants.png"));
            fields[23] = ImageIO.read(new File("FieldImages/Jets.png"));
            fields[24] = ImageIO.read(new File("FieldImages/Raiders.png"));
            fields[25] = ImageIO.read(new File("FieldImages/Eagles.png"));
            fields[26] = ImageIO.read(new File("FieldImages/Steelers.png"));
            fields[27] = ImageIO.read(new File("FieldImages/49ers.png"));
            fields[28] = ImageIO.read(new File("FieldImages/Seahawks.png"));
            fields[29] = ImageIO.read(new File("FieldImages/Buccaneers.png"));
            fields[30] = ImageIO.read(new File("FieldImages/Titans.png"));
            fields[31] = ImageIO.read(new File("FieldImages/Redskins.png"));

            fields[32] = ImageIO.read(new File("FieldImages/Gators.jpg"));
            fields[33] = ImageIO.read(new File("FieldImages/General.png"));

            backgrounds[0] = ImageIO.read(new File("GUIImages/ChromeBack.jpg"));

        }catch(IOException exception){
            System.out.println("IOEXCEPTION THROWN: Issue loading one of images.");
        }

        //Initialize the width and height final variables for ImageFrame class with the values passed into constructor
        FRAME_WIDTH = width;
        FRAME_HEIGHT = height;

        simWidth = (int)(width*0.50);
        simHeight = height - 120;

        panelWidth = (int)(width*0.25);
        panelHeight = height;

        controlWidth = width;
        controlHeight = height - simHeight;

        //Set title of program frame (May consider a title change or some sort of info tab in Menu)
        this.setTitle("PlayMate - Interactive Football Play Simulator");
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        //Initializing play simulation image and graphics
        playImage = new BufferedImage( simWidth, simHeight, BufferedImage.TYPE_INT_ARGB);
        playImage = baseImage;
        playG2D = (Graphics2D)playImage.createGraphics();

        //Initializing user interaction image and graphics
        interImage = new BufferedImage( panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        interImage = backImage;
        interG2D = (Graphics2D)interImage.createGraphics();

        controlImage = new BufferedImage( controlWidth, controlHeight, BufferedImage.TYPE_INT_ARGB);
        controlImage = backImage;
        controlG2D = (Graphics2D)controlImage.createGraphics();

        //Initialize Custom Panels off their respective play simulation and user interface images
        mainPanel = new playSimulationPanel(playImage);
        mainPanel.setPreferredSize(new Dimension(simWidth,simHeight)); //Set preferred size to maintian in content pane

        UIPanel1 = new interfacePanel(interImage);
        UIPanel1.setPreferredSize(new Dimension(panelWidth, panelHeight));
        UIPanel1.setLayout(new BorderLayout());

        UIPanel2 = new interfacePanel(interImage);
        UIPanel2.setPreferredSize(new Dimension(panelWidth, panelHeight));
        UIPanel2.setLayout(new BorderLayout());

        simControlPanel = new interfacePanel(interImage);
        simControlPanel.setPreferredSize(new Dimension(controlWidth,controlHeight));
        simControlPanel.setLayout(new BorderLayout());

        //Place-holder, commented ActionListener block for timed-animation considerations later
        timer = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                simulation();
            }
        });

        //PLAY SIMULATION BUTTON
        final JButton runSimButton = new JButton("Run Animation");
        runSimButton.setBackground(new Color(255,255,255));

        runSimButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //If there is BOTH an offensive and defensive play spread on the simulation panel, then the button is
                //functional and runs the animation based on conditions
                if(oSpread && dSpread) {

                    resetPlaySpreadButton.setEnabled(true);
                    resetPlaySpreadButton.setBackground(Color.MAGENTA);

                    if(frameCount == 1) {
                        storedOPlayer = oMenOnField;
                        storedDPlayer = dMenOnField;

                        for(int i = 0; oMenOnField[i] != null; i++){
                            if(oMenOnField[i] instanceof QB){
                                oMenOnField[i].setHasBall(true);
                            }
                            else{
                                oMenOnField[i].setHasBall(false);
                            }
                        }
                        //System.out.println(storedOPlayer);
                        //System.out.println(storedDPlayer);
                    }

                    //If the current state of the simulation button is paused when button is clicked, change states
                    //to play the animation
                    if (isPaused) {

                        isPaused = false;
                        //**********************************************************************************************
                        //RUN THE TIMER THAT IS LINKED TO THE CONTINUOUS "DRAW" CALL OF THE simulation() FUNCTION
                        timer.start();
                        //**********************************************************************************************
                        runSimButton.setText("Pause");
                    }

                    //Else, the simulation is currently playing when clicked, switch states to pause the animation
                    else {

                        isPaused = true;
                        //**********************************************************************************************
                        //STOP THE TIMER LINKED TO THE CONTINUOUS CALL OF THE simulation() FUNCTION
                        timer.stop();
                        //**********************************************************************************************
                        runSimButton.setText("Start");
                    }
                }

                //Else if both spreads are not detected, print a single error message to the console as a PRINT CHECK
                else{
                    System.out.println("Error:  Can't run simulation, didn't detect offense and defense.");
                }
            }
        });

        //HEADER LABEL
        //final JLabel header2 = new JLabel("Player Attributes");
        header2.setFont(new Font("Haettenschweiler", Font.PLAIN, 36));
        header2.setForeground(Color.ORANGE);
        header2.setVerticalAlignment(JLabel.CENTER);
        header2.setHorizontalAlignment(JLabel.CENTER);

        controls.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(5,15,15,15));
        controls.add(Box.createHorizontalGlue());
        controls.setBackground(backColor);

        //final JLabel pType = new JLabel("Player Type: " + pT);
        //final JLabel pClass = new JLabel("Classification: " + pC);

        pSpec.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        pSpec.setForeground(Color.GREEN);
        subH1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        subH1.setBackground(Color.DARK_GRAY);
        subH1.add(pSpec);

        final JLabel l1 = new JLabel("Height: ");
        l1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l1.setForeground(Color.GREEN);
        final JLabel l11 = new JLabel(" in.");
        l11.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l11.setForeground(Color.GREEN);
        final JLabel l2 = new JLabel("Strength: ");
        l2.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l2.setForeground(Color.GREEN);
        final JLabel l21 = new JLabel(" Percentile");
        l21.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l21.setForeground(Color.GREEN);
        final JLabel l3 = new JLabel("Weight: ");
        l3.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l3.setForeground(Color.GREEN);
        final JLabel l31 = new JLabel(" lbs.");
        l31.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l31.setForeground(Color.GREEN);
        final JLabel l4 = new JLabel("Speed: ");
        l4.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l4.setForeground(Color.GREEN);
        final JLabel l41 = new JLabel(" sec.");
        l41.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        l41.setForeground(Color.GREEN);

        final JLabel l = new JLabel("Physical Attributes");
        l.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        l.setForeground(Color.GREEN);

        subH3D.setForeground(Color.GREEN);
        subH3O.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        subH3O.setForeground(Color.GREEN);;
        subH3DP.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        subH3DP.setForeground(Color.GREEN);

        final JLabel r1 = new JLabel("Route Angle:");
        final JLabel r2 = new JLabel(" deg.");
        r1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        r2.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        r1.setForeground(Color.GREEN);
        r2.setForeground(Color.GREEN);

        final JLabel ra1 = new JLabel("Route Attributes:");
        ra1.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        ra1.setForeground(Color.GREEN);

        sub41.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        sub42.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        sub43.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        zLabel.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        tLabel.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        btLabel.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        sub41.setForeground(Color.GREEN);
        sub42.setForeground(Color.GREEN);
        sub43.setForeground(Color.GREEN);
        zLabel.setForeground(Color.GREEN);
        tLabel.setForeground(Color.GREEN);
        btLabel.setForeground(Color.GREEN);
        sub41.setBackground(Color.DARK_GRAY);
        sub42.setBackground(Color.DARK_GRAY);
        sub43.setBackground(Color.DARK_GRAY);
        zLabel.setBackground(Color.DARK_GRAY);
        tLabel.setBackground(Color.DARK_GRAY);
        btLabel.setBackground(Color.DARK_GRAY);

        ballButton.setBackground(Color.MAGENTA);
        ballButton.setForeground(Color.DARK_GRAY);
        ballButton.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));

        z.setForeground(Color.GREEN);
        z.setBackground(Color.DARK_GRAY);
        m.setForeground(Color.GREEN);
        m.setBackground(Color.DARK_GRAY);

        final JLabel sho = new JLabel("Short");
        sho.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        sho.setForeground(Color.GREEN);
        final JLabel dee = new JLabel("Short");
        dee.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        dee.setForeground(Color.GREEN);

        basicAttributes.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        basicAttributes.setBackground(Color.DARK_GRAY);
        basicAttributes.add(l1);
        basicAttributes.add(heightSpin);
        basicAttributes.add(l11);

        basicAttributes.add(l2);
        basicAttributes.add(strengthSpin);
        basicAttributes.add(l21);

        basicAttributes.add(l3);
        basicAttributes.add(weightSpin);
        basicAttributes.add(l31);

        basicAttributes.add(l4);
        basicAttributes.add(speedSpin);
        basicAttributes.add(l41);

        controls.add(subH1);
        controls.add(Box.createRigidArea(minorSeparation));

        subH2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        subH2.setBackground(Color.DARK_GRAY);
        subH2.add(l);

        controls.add(subH2);
        controls.add(Box.createRigidArea(minorSeparation));
        controls.add(basicAttributes);
        controls.add(Box.createRigidArea(majorSeparation));

        block.setBackground(Color.DARK_GRAY);
        block.setForeground(Color.GREEN);
        block.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        protection.setBackground(Color.DARK_GRAY);
        protection.setForeground(Color.GREEN);
        protection.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        eligible.setBackground(Color.DARK_GRAY);
        eligible.setForeground(Color.GREEN);
        eligible.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        passCatch.setBackground(Color.DARK_GRAY);
        passCatch.setForeground(Color.GREEN);
        passCatch.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        pass.setBackground(Color.DARK_GRAY);
        pass.setForeground(Color.GREEN);
        pass.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));

        blocking.add(block);
        protecting.add(protection);
        eligibility.add(eligible);
        passCatching.add(passCatch);
        passing.add(pass);

        coverage.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        coverage.setBackground(Color.DARK_GRAY);
        coverage.add(z);
        coverage.add(m);

        subH3OP.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        subH3OP.setBackground(Color.DARK_GRAY);
        subH3DP.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        subH3DP.setBackground(Color.DARK_GRAY);
        subH3OP.add(subH3O);
        subH3DP.add(subH3D);

        subH3OP.setVisible(false);
        subH3DP.setVisible(false);
        coverage.setVisible(false);
        blocking.setVisible(false);
        protecting.setVisible(false);
        eligibility.setVisible(false);
        passCatching.setVisible(false);
        passing.setVisible(false);

        controls.add(subH3OP);
        controls.add(subH3DP);
        controls.add(Box.createRigidArea(minorSeparation));
        controls.add(coverage);
        controls.add(blocking);
        controls.add(protecting);
        controls.add(eligibility);
        controls.add(passCatching);
        controls.add(passing);
        controls.add(Box.createRigidArea(majorSeparation));

        rAngle.setBackground(Color.DARK_GRAY);
        rAngle.add(r1);
        rAngle.add(routeAngleSpin);
        rAngle.add(r2);

        routeDepth.setMajorTickSpacing(1500);
        routeDepth.setMinorTickSpacing(250);
        routeDepth.setPaintTicks(false);
        routeDepth.setPaintLabels(true);
        routeDepth.setBackground(Color.DARK_GRAY);
        routeDepth.setForeground(Color.GREEN);


        Hashtable routeD = new Hashtable();
        routeD.put(0, sho);
        //routeD.put(300, new JLabel("Route Depth"));
        routeD.put(6000, dee);
        routeDepth.setLabelTable(routeD);

        crossDepth.setMajorTickSpacing(1500);
        crossDepth.setMinorTickSpacing(250);
        crossDepth.setPaintTicks(false);
        crossDepth.setPaintLabels(true);
        crossDepth.setBackground(Color.DARK_GRAY);


        Hashtable crossD = new Hashtable();
        crossD.put(0, sho);
        //routeD.put(300, new JLabel("Route Depth"));
        crossD.put(6000, dee);

        crossDepth.setLabelTable(crossD);

        zoneR.setMajorTickSpacing(100);
        zoneR.setMinorTickSpacing(5);
        zoneR.setPaintTicks(true);
        zoneR.setPaintLabels(true);
        zoneR.setBackground(Color.DARK_GRAY);


        final JLabel z1 = new JLabel(" 0.0");
        z1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        z1.setForeground(Color.GREEN);

        final JLabel z2 = new JLabel("100.0");
        z2.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        z2.setForeground(Color.GREEN);

        final JLabel z3 = new JLabel("200.0");
        z3.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        z3.setForeground(Color.GREEN);

        final JLabel z4 = new JLabel("300.0 ");
        z4.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        z4.setForeground(Color.GREEN);

        Hashtable zoneCR = new Hashtable();
        zoneCR.put(0,z1);
        zoneCR.put(100,z2);
        zoneCR.put(200,z3);
        zoneCR.put(300,z4);
        zoneR.setLabelTable(zoneCR);

        inButton.setForeground(Color.GREEN);
        inButton.setBackground(Color.DARK_GRAY);
        outButton.setForeground(Color.GREEN);
        outButton.setBackground(Color.DARK_GRAY);
        leftButton.setForeground(Color.GREEN);
        leftButton.setBackground(Color.DARK_GRAY);
        rightButton.setForeground(Color.GREEN);
        rightButton.setBackground(Color.DARK_GRAY);

        inOut.add(inButton);
        inOut.add(outButton);

        rightLeft.add(leftButton);
        rightLeft.add(rightButton);

        rT.setBackground(Color.DARK_GRAY);
        rT.add(routes);

        sub41.setHorizontalAlignment(JLabel.CENTER);
        sub42.setHorizontalAlignment(JLabel.CENTER);
        sub43.setHorizontalAlignment(JLabel.CENTER);
        zLabel.setHorizontalAlignment(JLabel.CENTER);
        tLabel.setHorizontalAlignment(JLabel.CENTER);

        subH41.setBackground(Color.DARK_GRAY);
        subH42.setBackground(Color.DARK_GRAY);
        subH43.setBackground(Color.DARK_GRAY);
        zH.setBackground(Color.DARK_GRAY);
        tH.setBackground(Color.DARK_GRAY);
        btH.setBackground(Color.DARK_GRAY);

        subH41.add(sub41);
        subH42.add(sub42);
        subH43.add(sub43);
        zH.add(zLabel);
        tH.add(tLabel);
        btH.add(btLabel);

        zonePanel.setBackground(Color.DARK_GRAY);
        zonePanel.setLayout(new BoxLayout(zonePanel, BoxLayout.PAGE_AXIS));
        zonePanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        zonePanel.add(Box.createRigidArea(minorSeparation));
        zonePanel.add(zH);
        zonePanel.add(zoneR);

        routeType.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        routeType.setBackground(Color.DARK_GRAY);
        routeType.setLayout(new BoxLayout(routeType, BoxLayout.PAGE_AXIS));

        routeType.setVisible(false);

        subH4.setVisible(false);
        rAngle.setVisible(false);
        subH41.setVisible(false);
        zonePanel.setVisible(false);
        targetPanel.setVisible(false);
        bTargetPanel.setVisible(false);
        routeDepth.setVisible(false);
        subH42.setVisible(false);
        crossDepth.setVisible(false);
        subH43.setVisible(false);
        rT.setVisible(false);
        inOut.setVisible(false);
        rightLeft.setVisible(false);

        bTargetPanel.setLayout(new GridLayout(1,1));
        bTargetPanel.add(btH);
        bTargetPanel.add(bTarget);

        targetPanel.setLayout(new GridLayout(1,1));
        targetPanel.add(tH);
        targetPanel.add(target);


        routeType.add(rAngle);
        //routeType.add(Box.createRigidArea(minorSeparation));
        routeType.add(subH41);
        //routeType.add(Box.createRigidArea(minorSeparation));
        routeType.add(routeDepth);
        //routeType.add(Box.createRigidArea(minorSeparation));
        routeType.add(subH42);
        //routeType.add(Box.createRigidArea(minorSeparation));
        routeType.add(crossDepth);
        //routeType.add(Box.createRigidArea(majorSeparation));
        routeType.add(subH43);
        //routeType.add(Box.createRigidArea(minorSeparation));
        routeType.add(rT);
        routeType.add(inOut);
        routeType.add(rightLeft);

        subH4.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        subH4.setBackground(Color.DARK_GRAY);
        subH4.add(ra1);

        controls.add(subH4);
        controls.add(zonePanel);
        controls.add(targetPanel);
        controls.add(bTargetPanel);
        controls.add(Box.createRigidArea(new Dimension(minorSeparation)));
        controls.add(routeType);
        controls.add(Box.createRigidArea(minorSeparation));

        possession.setLayout(new BoxLayout(possession, BoxLayout.PAGE_AXIS));
        possession.setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
        possession.setBackground(backColor);
        BLPanel.add(new JLabel("Possession:"));
        BBPanel.add(ballButton);
        //possession.add(BLPanel);
        possession.add(BBPanel);

        possession.setVisible(false);

        controls.add(Box.createRigidArea(new Dimension(0,endSpace)));


        //**************************************************************************************************************
        //SPREAD ATTRIBUTE PANEL

        final JLabel sel = new JLabel("Selected Spread:");
        sel.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        sel.setForeground(Color.GREEN);
        sel.setBackground(Color.DARK_GRAY);

        final JLabel pla = new JLabel("Play Type:");
        pla.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        pla.setBackground(Color.DARK_GRAY);
        pla.setForeground(Color.GREEN);

        final JLabel forma = new JLabel("Formation:");
        forma.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        forma.setBackground(Color.DARK_GRAY);
        forma.setForeground(Color.GREEN);

        final JLabel pers = new JLabel("Personnel:");
        pers.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));
        pers.setBackground(Color.DARK_GRAY);
        pers.setForeground(Color.GREEN);

        header1.setFont(new Font("Haettenschweiler", Font.PLAIN, 36));
        header1.setForeground(Color.ORANGE);
        header1.setVerticalAlignment(JLabel.CENTER);
        header1.setHorizontalAlignment(JLabel.CENTER);

        final JLabel quar = new JLabel("Quarterbacks:");
        quar.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        quar.setBackground(Color.DARK_GRAY);
        quar.setForeground(Color.GREEN);

        final JLabel bac = new JLabel("Backs:");
        bac.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        bac.setBackground(Color.DARK_GRAY);
        bac.setForeground(Color.GREEN);

        final JLabel rece = new JLabel("Receivers:");
        rece.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        rece.setBackground(Color.DARK_GRAY);
        rece.setForeground(Color.GREEN);

        final JLabel lin = new JLabel("Linemen:");
        lin.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        lin.setBackground(Color.DARK_GRAY);
        lin.setForeground(Color.GREEN);

        final JLabel lind = new JLabel("Linemen:");
        lind.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        lind.setBackground(Color.DARK_GRAY);
        lind.setForeground(Color.GREEN);

        final JLabel linb = new JLabel("Linebackers:");
        linb.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        linb.setBackground(Color.DARK_GRAY);
        linb.setForeground(Color.GREEN);

        final JLabel defb = new JLabel("Defensive Backs:");
        defb.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        defb.setBackground(Color.DARK_GRAY);
        defb.setForeground(Color.GREEN);

        final JLabel cha = new JLabel("Change Player:");
        cha.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        cha.setBackground(Color.DARK_GRAY);
        cha.setForeground(Color.GREEN);

        final JLabel cla = new JLabel("Class:");
        cla.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        cla.setBackground(Color.DARK_GRAY);
        cla.setForeground(Color.GREEN);

        final JLabel posi = new JLabel("Position:");
        posi.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        posi.setBackground(Color.DARK_GRAY);
        posi.setForeground(Color.GREEN);

        final JLabel cla1 = new JLabel("Class:");
        cla1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        cla1.setBackground(Color.DARK_GRAY);
        cla1.setForeground(Color.GREEN);

        final JLabel posi1 = new JLabel("Position:");
        posi1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        posi1.setBackground(Color.DARK_GRAY);
        posi1.setForeground(Color.GREEN);

        O1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        O1.setBackground(Color.DARK_GRAY);
        O1.setForeground(Color.GREEN);

        O2.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        O2.setBackground(Color.DARK_GRAY);
        O2.setForeground(Color.GREEN);

        O3.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        O3.setBackground(Color.DARK_GRAY);
        O3.setForeground(Color.GREEN);

        O4.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        O4.setBackground(Color.DARK_GRAY);
        O4.setForeground(Color.GREEN);

        D1.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        D1.setBackground(Color.DARK_GRAY);
        D1.setForeground(Color.GREEN);

        D2.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        D2.setBackground(Color.DARK_GRAY);
        D2.setForeground(Color.GREEN);

        D3.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
        D3.setBackground(Color.DARK_GRAY);
        D3.setForeground(Color.GREEN);

        configureO.setBackground(Color.MAGENTA);
        configureO.setForeground(Color.DARK_GRAY);
        configureO.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));

        configureD.setBackground(Color.MAGENTA);
        configureD.setForeground(Color.DARK_GRAY);
        configureD.setFont(new Font("Haettenschweiler", Font.PLAIN, 18));

        disRoutes.setBackground(Color.MAGENTA);
        disRoutes.setForeground(Color.DARK_GRAY);
        disRoutes.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));


        spread.setLayout(new BoxLayout(spread, BoxLayout.PAGE_AXIS));
        spread.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        spread.setBorder(BorderFactory.createEmptyBorder(5,15,15,15));
        spread.add(Box.createHorizontalGlue());
        spread.setBackground(backColor);

        configureOP.add(configureO);
        configureDP.add(configureD);

        spread.add(configureOP);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(configureDP);
        spread.add(Box.createRigidArea(majorSeparation));

        sH1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        sH1.setBackground(Color.DARK_GRAY);
        sH1.add(sel, BorderLayout.CENTER);

        ORB.setBackground(Color.DARK_GRAY);
        ORB.setForeground(Color.GREEN);
        DRB.setBackground(Color.DARK_GRAY);
        DRB.setForeground(Color.GREEN);

        ODIndicator.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        ODIndicator.setBackground(Color.DARK_GRAY);
        ODIndicator.add(ORB);
        ODIndicator.add(DRB);

        sH1.setVisible(false);
        ODIndicator.setVisible(false);

        spread.add(sH1);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(ODIndicator);
        spread.add(Box.createRigidArea(majorSeparation));

        sH2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        sH2.setBackground(Color.DARK_GRAY);
        sH2.add(pla, BorderLayout.CENTER);

        sH2.setVisible(false);
        OPT.setVisible(false);
        DPT.setVisible(false);

        spread.add(sH2);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(OPT);
        spread.add(DPT);
        spread.add(Box.createRigidArea(minorSeparation));

        sH3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        sH3.setBackground(Color.DARK_GRAY);
        sH3.add(forma, BorderLayout.CENTER);

        sH3.setVisible(false);
        OPF.setVisible(false);
        DPF.setVisible(false);

        spread.add(sH3);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(OPF);
        spread.add(DPF);
        spread.add(Box.createRigidArea(majorSeparation));

        sH4.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        sH4.setBackground(Color.DARK_GRAY);
        sH4.add(pers, BorderLayout.CENTER);

        OPersonnelBreakdown.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        OPersonnelBreakdown.setBackground(Color.DARK_GRAY);
        OPersonnelBreakdown.setForeground(Color.GREEN);

        OPersonnelBreakdown.add(quar);
        OPersonnelBreakdown.add(O1);
        OPersonnelBreakdown.add(bac);
        OPersonnelBreakdown.add(O2);
        OPersonnelBreakdown.add(rece);
        OPersonnelBreakdown.add(O3);
        OPersonnelBreakdown.add(lin);
        OPersonnelBreakdown.add(O4);

        DPersonnelBreakdown.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        DPersonnelBreakdown.setBackground(Color.DARK_GRAY);
        DPersonnelBreakdown.setForeground(Color.GREEN);

        DPersonnelBreakdown.add(lind);
        DPersonnelBreakdown.add(D1);
        DPersonnelBreakdown.add(linb);
        DPersonnelBreakdown.add(D2);
        DPersonnelBreakdown.add(defb);
        DPersonnelBreakdown.add(D3);

        sH4.setVisible(false);
        OPersonnelBreakdown.setVisible(false);
        DPersonnelBreakdown.setVisible(false);

        spread.add(sH4);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(OPersonnelBreakdown);
        spread.add(DPersonnelBreakdown);
        spread.add(Box.createRigidArea(majorSeparation));

        sH5.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        sH5.setBackground(Color.DARK_GRAY);

        sH5.add(cha, BorderLayout.CENTER);

        OCPPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        OCPPanel.setBackground(Color.DARK_GRAY);

        OCPPanel.setLayout(new BoxLayout(OCPPanel, BoxLayout.PAGE_AXIS));
        final JPanel cP = new JPanel(new GridLayout(1,1));
        cP.setBackground(Color.DARK_GRAY);
        cP.add(cla);
        OCPPanel.add(cP);
        OCPPanel.add(OPC);
        OCPPanel.add(Box.createRigidArea(minorSeparation));
        final JPanel pP = new JPanel(new GridLayout(1,1));
        pP.setBackground(Color.DARK_GRAY);
        pP.add(posi);
        OCPPanel.add(pP);
        OCPPanel.add(QBP);
        OCPPanel.add(OBP);
        OCPPanel.add(RP);
        OCPPanel.add(OLP);

        QBP.setVisible(false);
        OBP.setVisible(false);
        RP.setVisible(false);
        OLP.setVisible(false);

        DCPPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        DCPPanel.setBackground(Color.DARK_GRAY);

        DCPPanel.setLayout(new BoxLayout(DCPPanel, BoxLayout.PAGE_AXIS));
        final JPanel cPD = new JPanel(new GridLayout(1,1));
        cPD.setBackground(Color.DARK_GRAY);
        cPD.setForeground(Color.GREEN);
        cPD.add(cla1);
        DCPPanel.add(cPD);
        DCPPanel.add(DPC);
        DCPPanel.add(Box.createRigidArea(minorSeparation));
        final JPanel pPD = new JPanel(new GridLayout(1,1));
        pPD.setBackground(Color.DARK_GRAY);
        pPD.setForeground(Color.GREEN);
        pPD.add(posi1);
        DCPPanel.add(pPD);
        DCPPanel.add(DBP);
        DCPPanel.add(LBP);
        DCPPanel.add(DLP);

        DBP.setVisible(false);
        LBP.setVisible(false);
        DLP.setVisible(false);

        cP.setVisible(true);
        pP.setVisible(true);
        cPD.setVisible(true);
        pPD.setVisible(true);

        sH5.setVisible(false);
        OCPPanel.setVisible(false);
        DCPPanel.setVisible(false);

        spread.add(sH5);
        spread.add(Box.createRigidArea(minorSeparation));
        spread.add(OCPPanel);
        spread.add(DCPPanel);
        spread.add(Box.createRigidArea(majorSeparation));


        DRPanel.setLayout(new BoxLayout(DRPanel, BoxLayout.PAGE_AXIS));
        DRPanel.setBackground(Color.DARK_GRAY);
        DRPanel.setForeground(Color.GREEN);
        DRPanel.setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
        DRPanel.setBackground(backColor);
        HPanel.add(disRoutes);
        DRPanel.add(HPanel);
        DRPanel.setVisible(false);

        spread.add(Box.createRigidArea(new Dimension(0, 500)));

        //**************************************************************************************************************

        final JLabel mult1 = new JLabel("  x0.25");
        mult1.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        mult1.setBackground(Color.DARK_GRAY);
        mult1.setForeground(Color.GREEN);

        final JLabel mult2 = new JLabel("x0.50");
        mult2.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        mult2.setBackground(Color.DARK_GRAY);
        mult2.setForeground(Color.GREEN);

        final JLabel mult3 = new JLabel("x1.00");
        mult3.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        mult3.setBackground(Color.DARK_GRAY);
        mult3.setForeground(Color.GREEN);

        final JLabel mult4 = new JLabel("x1.50");
        mult4.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        mult4.setBackground(Color.DARK_GRAY);
        mult4.setForeground(Color.GREEN);

        final JLabel mult5 = new JLabel("x2.00  ");
        mult5.setFont(new Font("Haettenschweiler", Font.PLAIN, 14));
        mult5.setBackground(Color.DARK_GRAY);
        mult5.setForeground(Color.GREEN);

        resetPlaySpreadButton.setForeground(Color.DARK_GRAY);
        resetPlaySpreadButton.setFont(new Font("Haettenschweiler", Font.PLAIN, 24));

        runSimButton.setBackground(Color.MAGENTA);
        runSimButton.setForeground(Color.DARK_GRAY);
        runSimButton.setFont(new Font("Haettenschweiler", Font.PLAIN, 24));

        frameLine.setLayout(new BoxLayout(frameLine, BoxLayout.PAGE_AXIS));
        frameLine.setBorder(BorderFactory.createEmptyBorder(10,15,5,15));
        frameLine.setBackground(new Color(0,255,255,0));
        //frameLine.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

        f.setMajorTickSpacing(250);
        f.setMinorTickSpacing(1);
        f.setPaintTicks(true);
        f.setPaintLabels(true);
        f.setBackground(Color.DARK_GRAY);
        f.setForeground(Color.GREEN);
        frameLine.add(f);

        animationPanel.setLayout(new BorderLayout());
        animationPanel.setPreferredSize(new Dimension(simWidth, 120));
        final JPanel runButtonPanel1 = new JPanel();
        runButtonPanel1.setLayout(new BoxLayout(runButtonPanel1, BoxLayout.PAGE_AXIS));
        runButtonPanel1.setBorder(BorderFactory.createEmptyBorder(5,8,15,8));
        final JPanel runButtonPanel2 = new JPanel(new GridLayout(1,1));
        runButtonPanel2.add(runSimButton);
        runButtonPanel1.add(runButtonPanel2);
        runButtonPanel1.setBackground(new Color(0,0,0,0));
        animationPanel.setBackground(new Color(0,255,255,0));
        animationPanel.add(runButtonPanel1);

        resetPanel.setLayout(new BorderLayout());
        resetPanel.setPreferredSize(new Dimension(panelWidth, 120));
        final JPanel resButtonPanel1 = new JPanel();
        resButtonPanel1.setLayout(new BoxLayout(resButtonPanel1, BoxLayout.PAGE_AXIS));
        resButtonPanel1.setBorder(BorderFactory.createEmptyBorder(5,15,15,7));
        final JPanel resButtonPanel2 = new JPanel(new GridLayout(1,1));
        resButtonPanel2.add(resetPlaySpreadButton);
        resButtonPanel1.add(resButtonPanel2);
        resButtonPanel1.setBackground(new Color(0,0,0,0));
        resetPanel.setBackground(new Color(0,255,255,0));
        resetPanel.add(resButtonPanel1);

        resetPlaySpreadButton.setEnabled(false);

        s.setMajorTickSpacing(1);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        s.setPaintTrack(true);
        Hashtable speedLT = new Hashtable();
        speedLT.put(-2, mult1);
        speedLT.put(-1, mult2);
        speedLT.put(0, mult3);
        speedLT.put(1, mult4);
        speedLT.put(2, mult5);
        s.setLabelTable(speedLT);
        s.setBackground(Color.DARK_GRAY);

        speedPanel.setLayout(new BorderLayout());
        speedPanel.setPreferredSize(new Dimension(panelWidth, 120));

        final JPanel sPanel1 = new JPanel();
        sPanel1.setLayout(new BoxLayout(sPanel1, BoxLayout.PAGE_AXIS));
        sPanel1.setBorder(BorderFactory.createEmptyBorder(5,7,15,15));
        final JPanel sPanel2 = new JPanel(new GridLayout(1,1));

        sPanel2.add(s);
        sPanel1.add(sPanel2);
        sPanel1.setBackground(new Color(0,0,0,0));
        speedPanel.setBackground(new Color(0,255,255,0));
        speedPanel.add(sPanel1);




        //**************************************************************************************************************

        configureO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currIndex = 0;
                oSpread = true;
                oClicked = true;
                mousePressedInsidePlayer = true;

                basicOPass();
                OPT.setSelectedIndex(0);

                sH1.setVisible(true);
                ODIndicator.setVisible(true);
                ORB.setVisible(true);
                ORB.setSelected(true);
                ORB.setEnabled(false);

                if (!dSpread) {
                    DRB.setVisible(false);
                }
                DRB.setSelected(false);
                DRB.setEnabled(true);

                sH2.setVisible(true);
                OPT.setVisible(true);
                DPT.setVisible(false);

                sH3.setVisible(true);
                OPF.setVisible(true);
                DPF.setVisible(false);

                sH4.setVisible(true);
                OPersonnelBreakdown.setVisible(true);
                DPersonnelBreakdown.setVisible(false);

                sH5.setVisible(true);
                OCPPanel.setVisible(true);
                DCPPanel.setVisible(false);

                configureO.setEnabled(false);
                configureO.setBackground(new Color(0, 255, 0));
                configureO.setText("Offense Displayed");

                updateAttributeFields();
            }
        });

        configureD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currIndex = 0;
                dSpread = true;
                oClicked = false;
                mousePressedInsidePlayer = true;

                basicDPass();
                DPT.setSelectedIndex(0);

                sH1.setVisible(true);
                ODIndicator.setVisible(true);

                if (oSpread) {
                    ORB.setVisible(true);
                }
                else{
                    ORB.setVisible(false);
                }

                ORB.setSelected(false);
                ORB.setEnabled(true);
                DRB.setVisible(true);
                DRB.setSelected(true);
                DRB.setEnabled(false);

                sH2.setVisible(true);
                OPT.setVisible(false);
                DPT.setVisible(true);

                sH3.setVisible(true);
                OPF.setVisible(false);
                DPF.setVisible(true);

                sH4.setVisible(true);
                OPersonnelBreakdown.setVisible(false);
                DPersonnelBreakdown.setVisible(true);

                sH5.setVisible(true);
                OCPPanel.setVisible(false);
                DCPPanel.setVisible(true);

                configureD.setEnabled(false);
                configureD.setBackground(new Color(0, 255, 0));
                configureD.setText("Defense Displayed");

                updateAttributeFields();
            }
        });

        ORB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currIndex = 0;
                oSpread = true;
                oClicked = true;
                mousePressedInsidePlayer = true;

                ODIndicator.setVisible(true);
                ORB.setVisible(true);
                ORB.setSelected(true);
                ORB.setEnabled(false);
                if(!dSpread){
                    DRB.setVisible(false);
                }
                DRB.setSelected(false);
                DRB.setEnabled(true);

                OPT.setVisible(true);
                DPT.setVisible(false);

                OPF.setVisible(true);
                DPF.setVisible(false);

                OPersonnelBreakdown.setVisible(true);
                DPersonnelBreakdown.setVisible(false);

                OCPPanel.setVisible(true);
                DCPPanel.setVisible(false);

                updateAttributeFields();
            }
        });

        DRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currIndex = 0;
                dSpread = true;
                oClicked = false;
                mousePressedInsidePlayer = true;

                ODIndicator.setVisible(true);
                if(!oSpread){
                    ORB.setVisible(false);
                }
                ORB.setSelected(false);
                ORB.setEnabled(true);
                DRB.setVisible(true);
                DRB.setSelected(true);
                DRB.setEnabled(false);

                OPT.setVisible(false);
                DPT.setVisible(true);

                OPF.setVisible(false);
                DPF.setVisible(true);

                OPersonnelBreakdown.setVisible(false);
                DPersonnelBreakdown.setVisible(true);

                OCPPanel.setVisible(false);
                DCPPanel.setVisible(true);

                updateAttributeFields();
            }
        });

        //**************************************************************************************************************
        //**************************************************************************************************************
        //OVERALL PANEL SETUPS

        UIPanel1.add(header1, BorderLayout.NORTH);
        UIPanel1.add(spread, BorderLayout.CENTER);
        UIPanel1.add(DRPanel, BorderLayout.SOUTH);

        UIPanel2.add(header2, BorderLayout.NORTH);
        UIPanel2.add(controls, BorderLayout.CENTER);
        UIPanel2.add(possession, BorderLayout.SOUTH);

        simControlPanel.add(frameLine, BorderLayout.NORTH);
        simControlPanel.add(animationPanel, BorderLayout.CENTER);
        simControlPanel.add(resetPanel, BorderLayout.WEST);
        simControlPanel.add(speedPanel, BorderLayout.EAST);

        //**************************************************************************************************************

        //Create a JPanel grouping of the play simulation buttons to add to the content pane

        disRoutes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(routesDisplayed){
                    routesDisplayed = false;
                    disRoutes.setText("Display Routes");
                }
                else{
                    routesDisplayed = true;
                    disRoutes.setText("Hide Routes");
                }
                displaySpread(playerDiameter);
            }
        });

        heightSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                if(oClicked){
                    System.out.println("Before change, height is: " + oMenOnField[currIndex].getHeight());
                    oMenOnField[currIndex].setHeight((int)(heightSpin.getValue()));
                    System.out.println("After change, height is: " + oMenOnField[currIndex].getHeight());
                }
                else{
                    System.out.println("Before change, height is: " + dMenOnField[currIndex].getHeight());
                    dMenOnField[currIndex].setHeight((int)(heightSpin.getValue()));
                    System.out.println("After change, height is: " + dMenOnField[currIndex].getHeight());
                }

            }
        });

        weightSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                if(oClicked){
                    System.out.println("Before change, weight is: " + oMenOnField[currIndex].getWeight());
                    oMenOnField[currIndex].setWeight((int)weightSpin.getValue());
                    System.out.println("After change, weight is: " + oMenOnField[currIndex].getWeight());
                }
                else{
                    System.out.println("Before change, weight is: " + dMenOnField[currIndex].getWeight());
                    dMenOnField[currIndex].setWeight((int)weightSpin.getValue());
                    System.out.println("After change, weight is: " + dMenOnField[currIndex].getWeight());
                }

            }
        });

        strengthSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                if(oClicked){
                    System.out.println("Before change, strength is: " + oMenOnField[currIndex].getStrength());
                    oMenOnField[currIndex].setStrength(((int)strengthSpin.getValue())/100.0);
                    System.out.println("After change, strength is: " + oMenOnField[currIndex].getStrength());
                }
                else{
                    System.out.println("Before change, strength is: " + dMenOnField[currIndex].getStrength());
                    dMenOnField[currIndex].setStrength(((int)strengthSpin.getValue())/100.0);
                    System.out.println("After change, strength is: " + dMenOnField[currIndex].getStrength());
                }

            }
        });

        speedSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                if(oClicked){
                    System.out.println("Before change, speed is: " + oMenOnField[currIndex].getSpeed());
                    oMenOnField[currIndex].setSpeed(((double)speedSpin.getValue())/10.0);
                    System.out.println("After change, speed is: " + oMenOnField[currIndex].getSpeed());
                }
                else{
                    System.out.println("Before change, speed is: " + dMenOnField[currIndex].getSpeed());
                    dMenOnField[currIndex].setSpeed(((double)speedSpin.getValue())/10.0);
                    System.out.println("After change, speed is: " + dMenOnField[currIndex].getSpeed());
                }

            }
        });

        block.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(oMenOnField[currIndex] instanceof OBack){
                    System.out.println("Block setting before change: " + ((OBack)oMenOnField[currIndex]).isBlocking());
                    ((OBack)oMenOnField[currIndex]).setBlocking(block.isSelected());
                    System.out.println("Block setting after change: " + ((OBack)oMenOnField[currIndex]).isBlocking());
                }
                else{
                    System.out.println("Block setting before change: " + ((Receiver)oMenOnField[currIndex]).isBlocking());
                    ((Receiver)oMenOnField[currIndex]).setBlocking(block.isSelected());
                    System.out.println("Block setting after change: " + ((Receiver)oMenOnField[currIndex]).isBlocking());
                }

                updateAttributeFields();

            }
        });

        protection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                ((OLine)oMenOnField[currIndex]).setProtecting(protection.isSelected());

                if(((OLine)oMenOnField[currIndex]).isProtecting()){
                    displaySpread(playerDiameter);
                }
                else{
                    updateRoutes(oMenOnField, currIndex);
                    displaySpread(playerDiameter);
                }

                updateAttributeFields();
            }
        });

        eligible.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Eligible setting before change: " + oMenOnField[currIndex].isEligibleReceiver());
                oMenOnField[currIndex].setEligibility(eligible.isSelected());
                System.out.println("Eligible setting after change: " + oMenOnField[currIndex].isEligibleReceiver());

            }
        });

        passCatch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Pass-Catching setting before change: " + ((OBack)oMenOnField[currIndex]).isPassCatching());
                ((OBack)oMenOnField[currIndex]).setPassCatching(passCatch.isSelected());
                System.out.println("Pass-Catching setting after change: " + ((OBack)oMenOnField[currIndex]).isPassCatching());

            }
        });

        pass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Passing setting before change: " + ((QB)oMenOnField[currIndex]).isPassing());
                ((QB)oMenOnField[currIndex]).setPassing(pass.isSelected());
                System.out.println("Passing setting after change: " + ((QB)oMenOnField[currIndex]).isPassing());

                updateRoutes(oMenOnField, currIndex);
                updateAttributeFields();
                displaySpread(playerDiameter);
            }
        });

        protection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Protection setting before change: " + ((OLine)oMenOnField[currIndex]).isProtecting());
                ((OLine)oMenOnField[currIndex]).setProtecting(protection.isSelected());
                System.out.println("Protection setting before change: " + ((OLine)oMenOnField[currIndex]).isProtecting());

            }
        });

        routeAngleSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                if(oMenOnField[currIndex] instanceof QB){
                    ((QB)oMenOnField[currIndex]).setDropBackAngle((double)routeAngleSpin.getValue());
                }

                else if(oMenOnField[currIndex] instanceof OLine){
                    ((OLine)oMenOnField[currIndex]).setBAngle((double)routeAngleSpin.getValue());
                }

                else if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setRA((double)routeAngleSpin.getValue());
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setRAngle((double)routeAngleSpin.getValue());
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);
            }
        });

        routeDepth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                int tempD = routeDepth.getValue();

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setRD(tempD);
                }
                else if (oMenOnField[currIndex] instanceof OLine){
                    ((OLine)oMenOnField[currIndex]).setBDepth(tempD);
                }
                else if (oMenOnField[currIndex] instanceof QB){
                    ((QB)oMenOnField[currIndex]).setDropBackDepth(tempD);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setRDepth(tempD);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);
            }
        });

        crossDepth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                int tempCD = crossDepth.getValue();

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setCD(tempCD);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setCDepth(tempCD);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        z.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(m.isSelected()){
                    m.setEnabled(true);
                    m.setSelected(false);
                }

                z.setEnabled(false);
                zonePanel.setVisible(true);
                targetPanel.setVisible(false);

                if(dMenOnField[currIndex] instanceof LineBacks){
                    ((LineBacks)dMenOnField[currIndex]).setZone(true);
                }
                else{
                    ((DBacks)dMenOnField[currIndex]).setZone(true);
                }

                displaySpread(playerDiameter);

            }
        });

        m.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(z.isSelected()){
                    z.setEnabled(true);
                    z.setSelected(false);
                }

                m.setEnabled(false);
                zonePanel.setVisible(false);
                targetPanel.setVisible(true);

                if(dMenOnField[currIndex] instanceof LineBacks){
                    ((LineBacks)dMenOnField[currIndex]).setZone(false);
                    ((LineBacks)dMenOnField[currIndex]).setTargetMan(oMenOnField[0]);
                }
                else{
                    ((DBacks)dMenOnField[currIndex]).setZone(false);
                }

                displaySpread(playerDiameter);

            }
        });

        zoneR.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                int tempD = zoneR.getValue();

                if(dMenOnField[currIndex] instanceof LineBacks){
                    ((LineBacks)dMenOnField[currIndex]).setZoneCR(tempD);
                }
                else {
                    ((DBacks)dMenOnField[currIndex]).setZoneCR(tempD);
                }

                displaySpread(playerDiameter);
            }
        });

        target.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = target.getSelectedIndex();

                if(dMenOnField[currIndex] instanceof DLine){
                    ((DLine)dMenOnField[currIndex]).setTargetMan(oMenOnField[tempInd]);
                }

                else if(dMenOnField[currIndex] instanceof LineBacks){
                    ((LineBacks)dMenOnField[currIndex]).setTargetMan(oMenOnField[tempInd]);
                }

                else{
                    ((DBacks)dMenOnField[currIndex]).setTargetMan(oMenOnField[tempInd]);
                }

                displaySpread(playerDiameter);
            }
        });

        bTarget.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = bTarget.getSelectedIndex();

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setBlockTarget(dMenOnField[tempInd]);
                }

                else if(oMenOnField[currIndex] instanceof Receiver){
                    ((Receiver)oMenOnField[currIndex]).setBlockTarget(dMenOnField[tempInd]);
                }

                displaySpread(playerDiameter);
            }
        });

        routes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = routes.getSelectedIndex();

                switch(tempInd){
                    case 0:
                        //Streak
                        inOut.setVisible(false);
                        rightLeft.setVisible(false);
                        crossDepth.setVisible(false);
                        subH42.setVisible(false);
                        break;
                    case 1:
                        //Post/Corner
                        inOut.setVisible(true);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(true);
                        subH42.setVisible(true);
                        break;
                    case 2:
                        //In/Out
                        inOut.setVisible(true);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(true);
                        subH42.setVisible(true);
                        break;
                    case 3:
                        //Slant
                        inOut.setVisible(false);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(true);
                        subH42.setVisible(true);
                        break;
                    case 4:
                        //Curl/Comeback
                        inOut.setVisible(true);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(true);
                        subH42.setVisible(true);
                        break;
                    case 5:
                        //Fade
                        inOut.setVisible(false);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(false);
                        subH42.setVisible(false);
                        break;
                    case 6:
                        //Wheel
                        inOut.setVisible(true);
                        rightLeft.setVisible(true);
                        crossDepth.setVisible(true);
                        subH42.setVisible(true);
                        break;
                }

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setRouteInd(tempInd);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setRouteInd(tempInd);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);
            }
        });

        inButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(outButton.isSelected()){
                    outButton.setEnabled(true);
                    outButton.setSelected(false);
                }

                inButton.setEnabled(false);

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setRouteIOO(true);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setRouteIOO(true);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        outButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(inButton.isSelected()){
                    inButton.setEnabled(true);
                    inButton.setSelected(false);
                }

                outButton.setEnabled(false);

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setRouteIOO(false);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setRouteIOO(false);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(leftButton.isSelected()){
                    leftButton.setEnabled(true);
                    leftButton.setSelected(false);
                }

                rightButton.setEnabled(false);

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setBallROL(true);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setBallROL(true);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(rightButton.isSelected()){
                    rightButton.setEnabled(true);
                    rightButton.setSelected(false);
                }

                leftButton.setEnabled(false);

                if(oMenOnField[currIndex] instanceof OBack){
                    ((OBack)oMenOnField[currIndex]).setBallROL(false);
                }
                else{
                    ((Receiver)oMenOnField[currIndex]).setBallROL(false);
                }

                updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        ballButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int i = 0;
                while(oMenOnField[i] != null) {
                    if(i == currIndex) {
                        oMenOnField[i].setHasBall(true);
                        if(resetPlaySpreadButton.isEnabled()) {
                            if (!(oMenOnField[i] instanceof QB)) {
                                for (int j = 0; dMenOnField[j] != null; j++) {
                                    if (dMenOnField[j] instanceof DLine) {
                                        ((DLine) dMenOnField[j]).setTargetMan(oMenOnField[i]);
                                    } else if (dMenOnField[j] instanceof DBacks) {
                                        ((DBacks) dMenOnField[j]).setTargetMan(oMenOnField[i]);
                                    } else {
                                        ((LineBacks) dMenOnField[j]).setTargetMan(oMenOnField[i]);
                                    }
                                }
                            }
                        }
                    }
                    else{
                        oMenOnField[i].setHasBall(false);
                    }
                    i++;
                }

                ballButton.setEnabled(false);
                ballButton.setBackground(Color.ORANGE);

                //updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);

            }
        });

        s.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                int tempD = s.getValue();

                switch (tempD){
                    //x0.25
                    case -2:
                        speedMult = 1.0;
                        f.setMaximum(4000);
                        break;
                    //x0.50
                    case -1:
                        speedMult = 2.0;
                        f.setMaximum(2000);
                        break;
                    //x1.00
                    case 0:
                        speedMult = 4.00;
                        f.setMaximum(1000);
                        break;
                    //x1.50
                    case 1:
                        speedMult = 6.00;
                        f.setMaximum(750);
                        break;
                    //x2.00
                    case 2:
                        speedMult = 8.00;
                        f.setMaximum(500);
                        break;
                }

                //updateRoutes(oMenOnField, currIndex);
                displaySpread(playerDiameter);
            }
        });

        //**************************************************************************************************************

        OPT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = OPT.getSelectedIndex();

                switch (tempInd){
                    case 0:
                        basicOPass();
                        break;
                    case 1:
                        runM = false;
                        runL = true;
                        runR = false;
                        basicORun();
                        break;
                    case 2:
                        runM = true;
                        runL = false;
                        runR = false;
                        basicORun();
                        break;
                    case 3:
                        runM = false;
                        runL = false;
                        runR = true;
                        basicORun();
                        break;

                }
                //displaySpread(playerDiameter);
            }
        });

        DPT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = DPT.getSelectedIndex();

                switch (tempInd){
                    case 0:
                        basicDPass();
                        break;
                    case 1:
                        runM = false;
                        runL = true;
                        runR = false;
                        basicDRun();
                        break;

                }
                //displaySpread(playerDiameter);
            }
        });

        OPF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = OPF.getSelectedIndex();

            }
        });

        DPF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempInd = DPF.getSelectedIndex();

            }
        });

        //**************************************************************************************************************
        //OFFENSIVE PLAYER OBJECT CHANGE COMBO BOXES ACTION LISTIONERS

        OPC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempI;
                if(oMenOnField[currIndex] instanceof QB){
                    tempI = 0;
                }
                else if(oMenOnField[currIndex] instanceof OBack){
                    tempI = 1;
                }
                else if(oMenOnField[currIndex] instanceof OLine){
                    tempI = 2;
                }
                else{
                    tempI = 3;
                }

                int cbInd = OPC.getSelectedIndex();
                double tX = oMenOnField[currIndex].getCenterX();
                double tY = oMenOnField[currIndex].getCenterY();

                if(tempI != cbInd) {

                    switch (cbInd) {
                        //QB
                        case 0:
                            QBP.setVisible(true);
                            OBP.setVisible(false);
                            RP.setVisible(false);
                            OLP.setVisible(false);

//                            QBP.setSelectedIndex(0);

                            boolean tempP;
                            if(passO){
                                tempP = true;
                            }
                            else{
                                tempP = false;
                            }
                            double qbDrop[][] = new double[QBDropBackHandOff][2];
                            oMenOnField[currIndex] = new QB(tX, tY, qbH, qbW, qbS, qbSt, Color.GRAY, false, false, qbDrop, QBStartTheta, QBDropBackHandOff, 0.62, 50.0, 2.5, false);
                            QBP.setSelectedIndex(0);
                            break;

                        //OBack
                        case 1:
                            QBP.setVisible(false);
                            OBP.setVisible(true);
                            RP.setVisible(false);
                            OLP.setVisible(false);

                            //OBP.setSelectedIndex(0);

                            double[][] newRB = new double[runDepth][2];
                            double tempT;
                            boolean tempPassC;
                            if (passO) {
                                tempT = WRStartTheta;
                                tempPassC = true;

                            } else {
                                tempT = QBStartTheta;
                                tempPassC = false;
                            }

                            oMenOnField[currIndex] = new OBack(tX, tY, rbH, rbW, rbS, rbSt, Color.LIGHT_GRAY, true, false, newRB, 1, tempT, runDepth, defaultCD,tempPassC, 0, true, true, false, null);
                            OBP.setSelectedIndex(0);
                            break;

                        //OLine
                        case 2:
                            QBP.setVisible(false);
                            OBP.setVisible(false);
                            RP.setVisible(false);
                            OLP.setVisible(true);

                            //OLP.setSelectedIndex(3);

                            boolean tempProtection = false;
                            if (passO) {
                                tempProtection = true;
                            } else {
                                tempProtection = false;
                            }
                            double[][] newP = streakRoute(tX, tY, olS, OLBlock, 90.0);

                            oMenOnField[currIndex] = new OLine(tX, tY, olH, olW, olS, olSt, Color.BLUE, false, false, newP, 4, 90.0, OLBlock, tempProtection);
                            OLP.setSelectedIndex(3);
                            break;

                        //Receiver
                        case 3:
                            QBP.setVisible(false);
                            OBP.setVisible(false);
                            RP.setVisible(true);
                            OLP.setVisible(false);

                            //RP.setSelectedIndex(0);

                            double[][] newR = new double[midPassSteps][2];

                            oMenOnField[currIndex] = new Receiver(tX, tY, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, newR, 1, 0, 90.0, midPassSteps, defaultCD, false, false, false, null);
                            RP.setSelectedIndex(0);
                            break;
                    }
                }
                else{
                    System.out.println("The 'changed' OPC index equals the current selected player instanceof, no change action performed");
                }

                updateAttributeFields();
                countPersonnel();
                initializeDAssignments();
                displaySpread(playerDiameter);
            }
        });

        QBP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //CURRENTLY ONLY ONE TYPE OF QUARTERBACK SKILL POSITION AVAILABLE SO QB IS THE ONLY OPTION IN
                //THIS SECONDARY COMBO BOX
                //COULD POTENTIALLY ADD LATER:
                //- WILDCAT QB
                //- "SECOND" QB
                //ETC...
            }
        });

        OBP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int obInd = OBP.getSelectedIndex();
                int tempI = ((OBack)oMenOnField[currIndex]).getBI();

                if(obInd != (tempI - 1)) {
                    switch (obInd) {
                        //RB
                        case 0:
                            ((OBack)oMenOnField[currIndex]).setBI(obInd+1);
                            oMenOnField[currIndex].setHeight(rbH);
                            oMenOnField[currIndex].setWeight(rbW);
                            oMenOnField[currIndex].setStrength(rbSt);
                            oMenOnField[currIndex].setSpeed(rbS);
                            break;

                        //FB
                        case 1:
                            ((OBack)oMenOnField[currIndex]).setBI(obInd+1);
                            oMenOnField[currIndex].setHeight(fbH);
                            oMenOnField[currIndex].setWeight(fbW);
                            oMenOnField[currIndex].setStrength(fbSt);
                            oMenOnField[currIndex].setSpeed(fbS);
                            break;

                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        OLP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int olInd = OLP.getSelectedIndex();
                int tempI = ((OLine)oMenOnField[currIndex]).getLinemenInd();

                if(olInd != (tempI - 1)) {
                    switch (olInd) {
                        //Center
                        case 0:
                            ((OLine)oMenOnField[currIndex]).setLinemenInd(olInd+1);
                            oMenOnField[currIndex].setHeight(cH);
                            oMenOnField[currIndex].setWeight(cW);
                            oMenOnField[currIndex].setStrength(cSt);
                            oMenOnField[currIndex].setSpeed(cS);
                            break;

                        //OG
                        case 1:
                            ((OLine)oMenOnField[currIndex]).setLinemenInd(olInd+1);
                            oMenOnField[currIndex].setHeight(ogH);
                            oMenOnField[currIndex].setWeight(ogW);
                            oMenOnField[currIndex].setStrength(ogSt);
                            oMenOnField[currIndex].setSpeed(ogS);
                            break;

                        //OT
                        case 2:
                            ((OLine)oMenOnField[currIndex]).setLinemenInd(olInd+1);
                            oMenOnField[currIndex].setHeight(otH);
                            oMenOnField[currIndex].setWeight(otW);
                            oMenOnField[currIndex].setStrength(otSt);
                            oMenOnField[currIndex].setSpeed(otS);
                            break;

                        //OL
                        case 3:
                            ((OLine)oMenOnField[currIndex]).setLinemenInd(olInd+1);
                            oMenOnField[currIndex].setHeight(olH);
                            oMenOnField[currIndex].setWeight(olW);
                            oMenOnField[currIndex].setStrength(olSt);
                            oMenOnField[currIndex].setSpeed(olS);
                            break;
                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        RP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int rInd = RP.getSelectedIndex();
                int tempI = ((Receiver)oMenOnField[currIndex]).getReceiverInd();

                if(rInd != (tempI - 1)) {
                    switch (rInd) {
                        //WR
                        case 0:
                            ((Receiver)oMenOnField[currIndex]).setReceiverInd(rInd+1);
                            oMenOnField[currIndex].setHeight(wrH);
                            oMenOnField[currIndex].setWeight(wrW);
                            oMenOnField[currIndex].setStrength(wrSt);
                            oMenOnField[currIndex].setSpeed(wrS);
                            break;

                        //TE
                        case 1:
                            ((Receiver)oMenOnField[currIndex]).setReceiverInd(rInd+1);
                            oMenOnField[currIndex].setHeight(teH);
                            oMenOnField[currIndex].setWeight(teW);
                            oMenOnField[currIndex].setStrength(teSt);
                            oMenOnField[currIndex].setSpeed(teS);
                            break;
                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        //**************************************************************************************************************
        //DEFENSIVE PLAYER OBJECT CHANGE COMBO BOXES ACTION LISTENERS

        DPC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int tempI;
                if(dMenOnField[currIndex] instanceof LineBacks){
                    tempI = 0;
                }
                else if(dMenOnField[currIndex] instanceof DLine){
                    tempI = 1;
                }
                else {
                    tempI = 2;
                }

                int dpInd = DPC.getSelectedIndex();
                double tX = dMenOnField[currIndex].getCenterX();
                double tY = dMenOnField[currIndex].getCenterY();
                OPlayer newTarget;
                int tempOP = 0;
                int tempOR = 0;

                for(int i = 0; oMenOnField[i] != null; i++){
                    if(oMenOnField[i] instanceof QB){
                        tempOP = i;
                    }
                    else if(oMenOnField[i] instanceof OBack){
                        tempOR = i;
                    }
                }
                if(passD){
                    newTarget = oMenOnField[tempOP];
                }
                else{
                    newTarget = oMenOnField[tempOR];
                }

                if(tempI != dpInd) {

                    switch (dpInd) {
                        //LB
                        case 0:
                            LBP.setVisible(true);
                            DBP.setVisible(false);
                            DLP.setVisible(false);

//                            LBP.setSelectedIndex(0);

                            dMenOnField[currIndex] = new LineBacks(tX, tY, lbH, lbW, lbS, lbSt, false, Color.ORANGE, 1, newTarget, false, true, zoneCoverageRadius);
                            LBP.setSelectedIndex(0);
                            break;

                        //DL
                        case 1:
                            LBP.setVisible(false);
                            DBP.setVisible(false);
                            DLP.setVisible(true);

//                            DLP.setSelectedIndex(0);

                            dMenOnField[currIndex] = new DLine(tX, tY, dlH, dlW, dlS, dlSt, false, Color.RED, 1, newTarget);
                            DLP.setSelectedIndex(0);
                            break;

                        //DB
                        case 2:
                            LBP.setVisible(false);
                            DBP.setVisible(true);
                            DLP.setVisible(false);

//                            DBP.setSelectedIndex(0);

                            dMenOnField[currIndex] = new DBacks(tX, tY, dbH, dbW, dbS, dbSt, false, Color.YELLOW, 1, newTarget, false, true, zoneCoverageRadius);
                            DBP.setSelectedIndex(0);
                            break;
                    }
                }
                else{
                    System.out.println("The 'changed' OPC index equals the current selected player instanceof, no change action performed");
                }

                updateAttributeFields();
                countPersonnel();
                displaySpread(playerDiameter);
            }
        });

        LBP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int lbInd = LBP.getSelectedIndex();
                int tempI = ((LineBacks)dMenOnField[currIndex]).getLineBackInd();

                if(lbInd != (tempI - 1)) {
                    switch (lbInd) {
                        //LB
                        case 0:
                            ((LineBacks)dMenOnField[currIndex]).setLineBackInd(lbInd+1);
                            dMenOnField[currIndex].setHeight(lbH);
                            dMenOnField[currIndex].setWeight(lbW);
                            dMenOnField[currIndex].setStrength(lbSt);
                            dMenOnField[currIndex].setSpeed(lbS);
                            break;

                        //MLB
                        case 1:
                            ((LineBacks)dMenOnField[currIndex]).setLineBackInd(lbInd+1);
                            dMenOnField[currIndex].setHeight(imlbH);
                            dMenOnField[currIndex].setWeight(imlbW);
                            dMenOnField[currIndex].setStrength(imlbSt);
                            dMenOnField[currIndex].setSpeed(imlbS);
                            break;

                        //OLB
                        case 2:
                            ((LineBacks)dMenOnField[currIndex]).setLineBackInd(lbInd+1);
                            dMenOnField[currIndex].setHeight(olbH);
                            dMenOnField[currIndex].setWeight(olbW);
                            dMenOnField[currIndex].setStrength(olbSt);
                            dMenOnField[currIndex].setSpeed(olbS);
                            break;
                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        DLP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int dlInd = DLP.getSelectedIndex();
                int tempI = ((DLine)dMenOnField[currIndex]).getdDLineInd();

                if(dlInd != (tempI - 1)) {
                    switch (dlInd) {
                        //DL
                        case 0:
                            ((DLine)dMenOnField[currIndex]).setDLineInd(dlInd+1);
                            dMenOnField[currIndex].setHeight(dlH);
                            dMenOnField[currIndex].setWeight(dlW);
                            dMenOnField[currIndex].setStrength(dlSt);
                            dMenOnField[currIndex].setSpeed(dlS);
                            break;

                        //NT
                        case 1:
                            ((DLine)dMenOnField[currIndex]).setDLineInd(dlInd+1);
                            dMenOnField[currIndex].setHeight(ntdtH);
                            dMenOnField[currIndex].setWeight(ntdtW);
                            dMenOnField[currIndex].setStrength(ntdtSt);
                            dMenOnField[currIndex].setSpeed(ntdtS);
                            break;

                        //DT
                        case 2:
                            ((DLine)dMenOnField[currIndex]).setDLineInd(dlInd+1);
                            dMenOnField[currIndex].setHeight(ntdtH);
                            dMenOnField[currIndex].setWeight(ntdtW);
                            dMenOnField[currIndex].setStrength(ntdtSt);
                            dMenOnField[currIndex].setSpeed(ntdtS);
                            break;

                        //DE
                        case 3:
                            ((DLine)dMenOnField[currIndex]).setDLineInd(dlInd+1);
                            dMenOnField[currIndex].setHeight(deH);
                            dMenOnField[currIndex].setWeight(deW);
                            dMenOnField[currIndex].setStrength(deSt);
                            dMenOnField[currIndex].setSpeed(deS);
                            break;
                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        DBP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int dbInd = DBP.getSelectedIndex();
                int tempI = ((DBacks)dMenOnField[currIndex]).getDBackInd();

                if(dbInd != (tempI - 1)) {
                    switch (dbInd) {
                        //DB
                        case 0:
                            ((DBacks)dMenOnField[currIndex]).setDBackInd(dbInd+1);
                            dMenOnField[currIndex].setHeight(dbH);
                            dMenOnField[currIndex].setWeight(dbW);
                            dMenOnField[currIndex].setStrength(dbSt);
                            dMenOnField[currIndex].setSpeed(dbS);
                            break;

                        //CB
                        case 1:
                            ((DBacks)dMenOnField[currIndex]).setDBackInd(dbInd+1);
                            dMenOnField[currIndex].setHeight(cbH);
                            dMenOnField[currIndex].setWeight(cbW);
                            dMenOnField[currIndex].setStrength(cbSt);
                            dMenOnField[currIndex].setSpeed(cbS);
                            break;

                        //S
                        case 2:
                            ((DBacks)dMenOnField[currIndex]).setDBackInd(dbInd+1);
                            dMenOnField[currIndex].setHeight(sH);
                            dMenOnField[currIndex].setWeight(sW);
                            dMenOnField[currIndex].setStrength(sSt);
                            dMenOnField[currIndex].setSpeed(sS);
                            break;

                        //FS
                        case 3:
                            ((DBacks)dMenOnField[currIndex]).setDBackInd(dbInd+1);
                            dMenOnField[currIndex].setHeight(fsH);
                            dMenOnField[currIndex].setWeight(fsW);
                            dMenOnField[currIndex].setStrength(fsSt);
                            dMenOnField[currIndex].setSpeed(fsS);
                            break;

                        //SS
                        case 4:
                            ((DBacks)dMenOnField[currIndex]).setDBackInd(dbInd+1);
                            dMenOnField[currIndex].setHeight(ssH);
                            dMenOnField[currIndex].setWeight(ssW);
                            dMenOnField[currIndex].setStrength(ssSt);
                            dMenOnField[currIndex].setSpeed(ssS);
                            break;
                    }
                }

                else {
                    System.out.println("Current back index matches initial switch index - no change");
                }

                updateAttributeFields();
            }
        });

        //**************************************************************************************************************

        //**************************************************************************************************************

//        toggleRoute.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("called to change the route");
//                if(mousePressedInsidePlayer){
//                    if(oClicked && oMenOnField[currIndex] instanceof Receiver){
//                        int rInd = ((Receiver)oMenOnField[currIndex]).getRouteInd();
//                        if(rInd == 6){
//                            rInd = 0;
//                        }
//                        else{
//                            rInd++;
//                        }
//                        ((Receiver)oMenOnField[currIndex]).setRouteInd(rInd);
//
//                        updateRoutes(oMenOnField, currIndex);
//                        displaySpread(playerDiameter);
//                    }
//                }
//            }
//        });
//
//        //Toggle the route IN or OUT Action Listener
//        toggleInOut.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (oClicked && oMenOnField[currIndex] instanceof Receiver) {
//                        boolean inOut = ((Receiver)oMenOnField[currIndex]).isRouteIOO();
//
//                        //If route in towards field (true) --> make it toggle out to sidelines (false)
//                        if(inOut){
//                            inOut = false;
//                        }
//                        //Else if route is out (false) --> make it in (true)
//                        else{
//                            inOut = true;
//                        }
//                        ((Receiver)oMenOnField[currIndex]).setRouteIOO(inOut);
//
//                        updateRoutes(oMenOnField, currIndex);
//                        displaySpread(playerDiameter);
//                    }
//                }
//            }
//        });
//
//        //Toggle which side of the ball the receiver is on (Right or Left) Action Listener
//        toggleBallSide.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (oClicked && (oMenOnField[currIndex] instanceof Receiver || oMenOnField[currIndex] instanceof OBack)) {
//
//                        boolean rL;
//
//                        if(oMenOnField[currIndex] instanceof Receiver) {
//                            rL = ((Receiver) oMenOnField[currIndex]).isOnBallROL();
//                        }
//                        else{
//                            rL = ((OBack) oMenOnField[currIndex]).isOnBallROL();
//                        }
//
//                        //If the receiver object is on the right side (true) --> toggle boolean to left side (false)
//                        if(rL){
//                            rL = false;
//                        }
//                        //Else, if receiver is on left of ball (false) --> toggle to right side (true)
//                        else{
//                            rL = true;
//                        }
//
//                        if(oMenOnField[currIndex] instanceof Receiver) {
//                            ((Receiver) oMenOnField[currIndex]).setBallROL(rL);
//                            updateRoutes(oMenOnField, currIndex);
//                        }
//                        else{
//                            ((OBack) oMenOnField[currIndex]).setBallROL(rL);
//                            updateRBRoutePass(oMenOnField, currIndex);
//                        }
//
//                        //updateRoutes(oMenOnField, currIndex);
//                    }
//
////                    else if(oClicked && oMenOnField[currIndex] instanceof OBack) {
////                        boolean rL = ((OBack) oMenOnField[currIndex]).isOnBallROL();
////                        if (rL) {
////                            rL = false;
////                        } else {
////                            rL = true;
////                        }
////                        ((OBack) oMenOnField[currIndex]).setROL(rL);
////                        updateRBRoutePass(oMenOnField, currIndex);
////                    }
//
//                    displaySpread(playerDiameter);
//                }
//            }
//        });
//
//        togglePassDepth.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (oClicked && oMenOnField[currIndex] instanceof Receiver){
//                        int pD = ((Receiver)oMenOnField[currIndex]).getRD();
//                        if(pD > deepPassSteps){
//                            pD = shortPassSteps;
//                        }
//                        else{
//                            pD += 250;
//                        }
//                        ((Receiver)oMenOnField[currIndex]).setRDepth(pD);
//
//                        updateRoutes(oMenOnField, currIndex);
//                    }
//
//                    else if(oClicked && oMenOnField[currIndex] instanceof OBack) {
//                        int pD = ((OBack) oMenOnField[currIndex]).getRD();
//                        if (pD > deepPassSteps) {
//                            pD = shortPassSteps;
//                        } else {
//                            pD += 250;
//                        }
//                        ((OBack) oMenOnField[currIndex]).setRD(pD);
//                        updateRBRoutePass(oMenOnField, currIndex);
//                    }
//
//                    displaySpread(playerDiameter);
//                }
//            }
//        });

//        toggleZone.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (!oClicked && dMenOnField[currIndex] instanceof LineBacks){
//                        boolean z = ((LineBacks)dMenOnField[currIndex]).isZone();
//                        if(z){
//                            z = false;
//                        }
//                        else{
//                            z = true;
//                        }
//                        ((LineBacks)dMenOnField[currIndex]).setZone(z);
//                    }
//
//                    else if (!oClicked && dMenOnField[currIndex] instanceof DBacks){
//                        boolean z = ((DBacks)dMenOnField[currIndex]).isZone();
//                        if(z){
//                            z = false;
//                        }
//                        else{
//                            z = true;
//                        }
//                        ((DBacks)dMenOnField[currIndex]).setZone(z);
//                    }
//
//                    displaySpread(playerDiameter);
//                }
//            }
//        });
//
//        toggleCoveredMan.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (!oClicked && dMenOnField[currIndex] instanceof LineBacks){
//                        OPlayer myMan = ((LineBacks)dMenOnField[currIndex]).getTargetMan();
//                        int i = 0;
//                        while(oMenOnField[i] != null){
//                            if(oMenOnField[i] == myMan){
//                                break;
//                            }
//                            i++;
//                        }
//                        int coveredIndex = i;
//                        if(oMenOnField[coveredIndex + 1] == null){
//                            coveredIndex = 0;
//                        }
//                        else{
//                            coveredIndex++;
//                        }
//                        ((LineBacks)dMenOnField[currIndex]).setTargetMan(oMenOnField[coveredIndex]);
//                    }
//
//                    else if (!oClicked && dMenOnField[currIndex] instanceof DBacks){
//                        OPlayer myMan = ((DBacks)dMenOnField[currIndex]).getTargetMan();
//                        int i = 0;
//                        while(oMenOnField[i] != null){
//                            if(oMenOnField[i] == myMan){
//                                break;
//                            }
//                            i++;
//                        }
//                        int coveredIndex = i;
//                        if(oMenOnField[coveredIndex + 1] == null){
//                            coveredIndex = 0;
//                        }
//                        else{
//                            coveredIndex++;
//                        }
//                        ((DBacks)dMenOnField[currIndex]).setTargetMan(oMenOnField[coveredIndex]);
//                    }
//
//                    else if (!oClicked && dMenOnField[currIndex] instanceof DLine){
//                        OPlayer myMan = ((DLine)dMenOnField[currIndex]).getTargetMan();
//                        int i = 0;
//                        while(oMenOnField[i] != null){
//                            if(oMenOnField[i] == myMan){
//                                break;
//                            }
//                            i++;
//                        }
//                        int coveredIndex = i;
//                        if(oMenOnField[coveredIndex + 1] == null){
//                            coveredIndex = 0;
//                        }
//                        else{
//                            coveredIndex++;
//                        }
//                        ((DLine)dMenOnField[currIndex]).setTargetMan(oMenOnField[coveredIndex]);
//                    }
//                    displaySpread(playerDiameter);
//                }
//            }
//        });

//        changeToOL.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (oClicked) {
//                        double x = oMenOnField[currIndex].getCenterX();
//                        double y = oMenOnField[currIndex].getCenterY();
//                        double s = 1.5;
//                        Color oC = Color.BLUE;
//                        boolean prot = false;
//                        if(passO){
//                            prot = true;
//                        }
//                        else{
//                            prot = false;
//                        }
//                        double[][] newP = new double[OLBlock][2];
//                        double theta = 0.0;
//                        if(!passO) {
//                            if (runM) {
//                                theta = 90.0;
//                            } else if (runL) {
//                                theta = 60.0;
//                            } else if (runR) {
//                                theta = 120.0;
//                            }
//                        }
//                        newP = streakRoute(x, y ,s, OLBlock, theta);
//                        //oMenOnField[currIndex] = new OLine(x, y, 70.0, 200.0, olS, 0.75, oC, false, false, newP, 1, theta, prot, false);
//                    }
//                }
//                displaySpread(playerDiameter);
//            }
//        });
//
//        changeToWR.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if(mousePressedInsidePlayer) {
//                    if (oClicked) {
//                        double x = oMenOnField[currIndex].getCenterX();
//                        double y = oMenOnField[currIndex].getCenterY();
//                        Color wrC = Color.DARK_GRAY;
//                        double s = 4.0;
//                        double[][] newR = new double[100][2];
//
//                        WRStartTheta = 90.0;
//                        //oMenOnField[currIndex] = new Receiver(x, y, 70.0, 200.0, s, 0.65, wrC, true, false, newR, 1, 0, 90.0, 200, false, false, false, null);
//                        updateRoutes(oMenOnField, currIndex);
//                    }
//                    displaySpread(playerDiameter);
//                }
//            }
//        });
//
//        changeToRB.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (mousePressedInsidePlayer) {
//                    if(oClicked){
//
//                        double x = oMenOnField[currIndex].getCenterX();
//                        double y = oMenOnField[currIndex].getCenterY();
//                        Color wrC = Color.LIGHT_GRAY;
//                        double s = 5.0;
//                        double[][] newR = new double[100][2];
//
//                        if(passO){
//                            WRStartTheta = 0.0;
//                            //oMenOnField[currIndex] = new OBack(x, y, 70.0, 200.0, s, 0.65, wrC, true, false, newR, 1, RBStartTheta, midPassSteps, true, 6, true, true, false,  null);
//                            updateRoutes(oMenOnField, currIndex);
//                        }
//
//                        else if(!passO){
//                            RBStartTheta = 0.0;
//                            //oMenOnField[currIndex] = new OBack(x, y, 70.0, 200.0, s, 0.70, wrC, true, false, newR, 1, RBStartTheta, shortPassSteps, false, 1, true, true, false, null);
//                            //updateRBRoutePass(oMenOnField, currIndex);
//                            updateRoutes(oMenOnField, currIndex);
//                        }
//                        displaySpread(playerDiameter);
//                    }
//                }
//            }
//        });
//
//        changeToDL.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (mousePressedInsidePlayer) {
//                    if (!oClicked) {
//                        double x = dMenOnField[currIndex].getCenterX();
//                        double y = dMenOnField[currIndex].getCenterY();
//                        Color dlC = Color.RED;
//                        double s = 2.0;
//                        OPlayer initCover = oMenOnField[0];
//                        dMenOnField[currIndex] = new DLine(x, y, 70, 200, s, 0.80, false, dlC, 1, initCover);
//                    }
//                    displaySpread(playerDiameter);
//                }
//            }
//        });
//
//        changeToLB.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (mousePressedInsidePlayer) {
//                    if (!oClicked) {
//                        double x = dMenOnField[currIndex].getCenterX();
//                        double y = dMenOnField[currIndex].getCenterY();
//                        Color lbC = Color.ORANGE;
//                        double s = 3.0;
//                        OPlayer initCover = oMenOnField[0];
//                        dMenOnField[currIndex] = new LineBacks(x, y, 70, 200, s, 0.70, false, lbC, 1,  initCover, false, false);
//                    }
//                    displaySpread(playerDiameter);
//                }
//            }
//        });
//
//        changeToDB.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (mousePressedInsidePlayer) {
//                    if (!oClicked) {
//                        double x = dMenOnField[currIndex].getCenterX();
//                        double y = dMenOnField[currIndex].getCenterY();
//                        Color dbC = Color.YELLOW;
//                        double s = 3.9;
//                        OPlayer initCover = oMenOnField[0];
//                        dMenOnField[currIndex] = new DBacks(x, y, 70, 200, s, 0.55, false, dbC, 1, initCover, false, false);
//                    }
//                    displaySpread(playerDiameter);
//                }
//            }
//        });

        resetPlaySpreadButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
//
//                oMenOnField = storedOPlayer;
//                dMenOnField = storedDPlayer;
//
//                System.out.println(oMenOnField);
//                System.out.println(dMenOnField);

                //displaySpread(playerDiameter);

                frameCount = 0;
                f.setValue(frameCount);
                if(passO){
                    basicOPass();
                }
                else{
                    basicORun();
                }
                if(passD){
                    basicDPass();
                }
                else{
                    basicDRun();
                }


            }
        });

        //Mouse Adapter to handle Mouse Events
        addMouseListener(new MouseAdapter() {

            //Mouse CLICKED Handler
            public void mouseClicked(MouseEvent e){

                //If their is an offensive or defensive play spread on the field
                if(oSpread || dSpread) {

                    boolean match = false;      //Boolean to keep track of if match has been found

                    double tempX, tempY;         //Temp variables to take in comparison x and y coordinate values

                    //**************************************************************************************************
                    //ADVISORY: WILL HAVE TO ADJUST THIS IF THE POSITIONING OF THE MAINPANEL IS MANIPULATED IN THE
                    //OVERALL SWING APPLICATION
                    //Get the MouseClicked coordinates and adjust to the positioning of the objects in the panel
                    double mX = e.getX() - 10.0 - panelWidth;
                    double mY = e.getY() - 56.0;
                    //**************************************************************************************************

                    System.out.println("Mouse x CLICKED = " + mX + " , Mouse y CLICKED = " + mY); //PRINT CHECK

                    //While there is a OPlayer in the offensive players on field array at the current index i
                    int i = 0;
                    while (oMenOnField[i] != null) {

                        //Store the OPlayer at the current index's x and y coordinate values (center of the player circle)
                        //Adjust the coordinates by half the diameter to center of Player Object
                        tempX = oMenOnField[i].getCenterX() + checkRadius;
                        tempY = oMenOnField[i].getCenterY() + checkRadius;

                        //If the distance from the mouse clicked point and the OPlayer center point is less than
                        //or equal to the player radius
                        if (distance(tempX, tempY, mX, mY) <= checkRadius) {

                            //A "match" has been found that the mouse click event has registered
                            match = true;
                            //Mouse clicked inside a player is TRUE
                            mousePressedInsidePlayer = true;
                            //Offensive player being clicked is true
                            oClicked = true;
                            //Set the global current index to the iterator index i
                            currIndex = i;
                            //Call the displaySpread method to repaint panel and now isplay selected circle
                            displaySpread(playerDiameter);
                            //Break the WHILE

                            updateAttributeFields();

                            System.out.println("Clicked Player: " + oMenOnField[i]);    //PRINT CHECK
                            System.out.println("Stored center X: " + oMenOnField[i].getCenterX() + ", Stored center Y: " + oMenOnField[i].getCenterY());    //PRINT CHECK
                            System.out.println("ADJUSTED center X = " + tempX + ", ADJUSTED center Y = " + tempY); //PRINT CHECK

                            break;
                        }

                        i++;
                    }

                    //If there still isn't a clicked match found after cycling through all offensive player objects,
                    //check for registered matches against defensive player objects, if they are present on field
                    i = 0;
                    if(!match && dSpread) {

                        while (dMenOnField[i] != null) {

                            tempX = dMenOnField[i].getCenterX() + checkRadius;
                            tempY = dMenOnField[i].getCenterY() + checkRadius;

                            if (distance(tempX, tempY, mX, mY) <= checkRadius) {

                                mousePressedInsidePlayer = true;
                                oClicked = false;
                                currIndex = i;
                                displaySpread(playerDiameter);
                                updateAttributeFields();

                                System.out.println("Clicked Player: " + dMenOnField[i]);    //PRINT CHECK
                                System.out.println("Stored center X: " + dMenOnField[i].getCenterX() + ", Stored center Y: " + dMenOnField[i].getCenterY());    //PRINT CHECK
                                System.out.println("ADJUSTED center X = " + tempX + ", ADJUSTED center Y = " + tempY); //PRINT CHECK

                                break;
                            }
                            i++;
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {

                if (oSpread || dSpread) {

                    boolean match = false;
                    double tempX, tempY;
                    int i;

                    double mX = e.getX() - 10.0 - panelWidth;
                    double mY = e.getY() - 56.0;

                    System.out.println("Mouse PRESSED x = " + mX + " , Mouse PRESSED y = " + mY); //PRINT CHECK

                    i = 0;
                    while (oMenOnField[i] != null) {

                        tempX = oMenOnField[i].getCenterX() + checkRadius;
                        tempY = oMenOnField[i].getCenterY() + checkRadius;

                        if (distance(tempX, tempY, mX, mY) <= checkRadius) {
                            match = true;
                            currIndex = i;
                            mousePressedInsidePlayer = true;
                            oClicked = true;

                            System.out.println(oMenOnField[i]); //PRINT CHECK

                            break;
                        }
                        i++;
                    }

                    i = 0;
                    if (!match && dSpread) {

                        while (dMenOnField[i] != null) {

                            tempX = dMenOnField[i].getCenterX();
                            tempY = dMenOnField[i].getCenterY();

                            if (distance(tempX, tempY, mX, mY) < playerDiameter) {
                                currIndex = i;
                                mousePressedInsidePlayer = true;
                                oClicked = false;

                                System.out.println(dMenOnField[i]);     //PRINT CHECK

                                break;
                            }
                            i++;
                        }
                    }
                }
                mouseDragged = false;
            }

            public void mouseReleased(MouseEvent e) {

                System.out.println("mouseReleased responding");
                if (mouseDragged) {

                    if (oSpread || dSpread) {

                        boolean match = false;
                        double tempX, tempY;
                        int i;

                        double mX = e.getX() - 10.0 - panelWidth;
                        double mY = e.getY() - 56.0;

                        i = 0;
                        if (oSpread) {

                            while (oMenOnField[i] != null) {

                                tempX = oMenOnField[i].getCenterX() + checkRadius;
                                tempY = oMenOnField[i].getCenterY() + checkRadius;

                                if (distance(tempX, tempY, mX, mY) <= checkRadius) {
                                    match = true;
                                    currIndex = i;
                                    mousePressedInsidePlayer = true;
                                    updateOPlayer(i, e);
                                    break;
                                }
                                i++;
                            }

                        }

                        i = 0;
                        if (dSpread && !match) {

                            while (dMenOnField[i] != null) {

                                tempX = dMenOnField[i].getCenterX() + checkRadius;
                                tempY = dMenOnField[i].getCenterY() + checkRadius;

                                if (distance(tempX, tempY, mX, mY) < checkRadius) {
                                    currIndex = i;
                                    mousePressedInsidePlayer = true;
                                    updateDPlayer(i, e);
                                    break;
                                }
                                i++;
                            }
                        }

                        else {
                            mousePressedInsidePlayer = false;
                        }
                    }
                }
            }
        });

        //Mouse Motion Listener to handle mouse movement events
        addMouseMotionListener(new MouseMotionListener() {

            public void mouseMoved(MouseEvent e) {}

            public void mouseDragged(MouseEvent e){

                System.out.println("mouseDragged responding");  //PRINT CHECK

                if( mousePressedInsidePlayer ){

                    //System.out.println("entered");  //PRINT CHECK

                    if(oClicked){
                        updateOPlayer(currIndex, e);
                        System.out.println("UPDATE called");
                        mouseDragged = true;
                    }

                    else{
                        updateDPlayer(currIndex, e);
                        mouseDragged = true;
                    }
                }
            }
        });

        //Call to add entire menu bar to the GUI
        addMenuBar();

        //Layout and add panel and button to content pane
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(UIPanel1, BorderLayout.WEST);
        this.getContentPane().add(UIPanel2, BorderLayout.EAST);
        this.getContentPane().add(simControlPanel, BorderLayout.SOUTH);
        this.setResizable(false);

        //MOST LIKELY OBSOLETE NOW THAT THE MENU HAS BEEN MODIFIED TO HAVE MENU ITEMS WHERE THE USER CHOOSES THE TEAM FIELD
        //BASED ON MENU ITEM NAME
        chooser = new JFileChooser();
        chooser.setCurrentDirectory( new File("." ) );
    }

    //------------------------------------------------------------------------------------------------------------------

    private void updateAttributeFields(){

        if(oSpread || dSpread) {
            if (mousePressedInsidePlayer) {
                if (oClicked) {

                    countPersonnel();
                    possession.setVisible(true);
                    DRPanel.setVisible(true);

                    subH3O.setVisible(true);
                    subH3DP.setVisible(false);
                    coverage.setVisible(false);
                    zonePanel.setVisible(false);
                    targetPanel.setVisible(false);
                    bTargetPanel.setVisible(false);

                    BLPanel.setVisible(true);
                    BBPanel.setVisible(true);

                    if (oMenOnField[currIndex] instanceof QB) {
                        pS = "Quarterback (QB)";

                        OPC.setSelectedIndex(0);
                        QBP.setSelectedIndex(0);

                        QBP.setVisible(true);
                        OBP.setVisible(false);
                        RP.setVisible(false);
                        OLP.setVisible(false);

                        subH3OP.setVisible(true);

                        blocking.setVisible(false);
                        protecting.setVisible(false);
                        eligibility.setVisible(false);
                        passCatching.setVisible(false);
                        passing.setVisible(true);

                        subH42.setVisible(false);
                        crossDepth.setVisible(false);
                        subH43.setVisible(false);
                        rT.setVisible(false);
                        inOut.setVisible(false);
                        rightLeft.setVisible(false);

                        if(((QB)oMenOnField[currIndex]).isPassing()){
                            pass.setSelected(true);

                            subH4.setVisible(false);
                            rAngle.setVisible(false);
                            subH41.setVisible(false);
                            routeDepth.setVisible(false);
                        }
                        else{
                            pass.setSelected(false);

                            routeType.setVisible(true);
                            subH4.setVisible(true);
                            rAngle.setVisible(true);
                            routeAngleSpin.setValue(((QB)oMenOnField[currIndex]).getDropBackAngle());
                            subH41.setVisible(true);
                            routeDepth.setVisible(true);
                            routeDepth.setValue(((QB)oMenOnField[currIndex]).getDropBackDepth());
                        }
                    }

                    else if (oMenOnField[currIndex] instanceof OLine) {
                        int indOL = ((OLine) oMenOnField[currIndex]).getLinemenInd();

                        OPC.setSelectedIndex(2);

                        QBP.setVisible(false);
                        OBP.setVisible(false);
                        RP.setVisible(false);
                        OLP.setVisible(true);

                        switch (indOL) {
                            case 1:
                                pS = "Center (C)";
                                OLP.setSelectedIndex(0);
                                break;
                            case 2:
                                if (oMenOnField[currIndex].getCenterX() > simWidth / 2) {
                                    pS = "Right Guard (RG)";
                                } else {
                                    pS = "Left Guard (LG)";
                                }
                                OLP.setSelectedIndex(1);
                                break;
                            case 3:
                                if (oMenOnField[currIndex].getCenterX() > simWidth / 2) {
                                    pS = "Right Tackle (RT)";
                                } else {
                                    pS = "Left Tackle (LT)";
                                }
                                OLP.setSelectedIndex(2);
                                break;
                            case 4:
                                pS = "Offensive Lineman (OL)";
                                OLP.setSelectedIndex(3);
                                break;
                        }
                        subH3OP.setVisible(true);

                        blocking.setVisible(false);
                        protecting.setVisible(true);
                        eligibility.setVisible(true);
                        passCatching.setVisible(false);
                        passing.setVisible(false);

                        subH42.setVisible(false);
                        crossDepth.setVisible(false);
                        subH43.setVisible(false);
                        rT.setVisible(false);
                        inOut.setVisible(false);
                        rightLeft.setVisible(false);

                        if(((OLine)oMenOnField[currIndex]).isProtecting()){
                            protection.setSelected(true);

                            subH4.setVisible(false);
                            rAngle.setVisible(false);
                            subH41.setVisible(false);
                            routeDepth.setVisible(false);
                        }
                        else{
                            protection.setSelected(false);

                            subH4.setVisible(true);
                            rAngle.setVisible(true);
                            routeAngleSpin.setValue(((OLine)oMenOnField[currIndex]).getBAngle());
                            subH41.setVisible(true);
                            routeDepth.setVisible(true);
                            routeDepth.setValue(OLBlock);
                        }
                        if((oMenOnField[currIndex]).isEligibleReceiver()){
                            eligible.setSelected(true);
                        }
                        else{
                            eligible.setSelected(false);
                        }
                    }

                    else if (oMenOnField[currIndex] instanceof OBack) {
                        int indOB = ((OBack) oMenOnField[currIndex]).getBI();

                        OPC.setSelectedIndex(1);

                        QBP.setVisible(false);
                        OBP.setVisible(true);
                        RP.setVisible(false);
                        OLP.setVisible(false);

                        switch (indOB) {
                            case 1:
                                pS = "Running Back (RB)";
                                OBP.setSelectedIndex(0);
                                break;
                            case 2:
                                pS = "Full Back (FB)";
                                OBP.setSelectedIndex(1);
                                break;
                        }
                        subH3OP.setVisible(true);

                        blocking.setVisible(true);
                        protecting.setVisible(false);
                        eligibility.setVisible(true);
                        passCatching.setVisible(true);
                        passing.setVisible(false);

                        if(((OBack)oMenOnField[currIndex]).isBlocking()){
                            block.setSelected(true);

                            int temp = 0;
                            for(int i = 0; dMenOnField[i] != null; i++){
                                if(dMenOnField[i] == ((OBack)oMenOnField[currIndex]).getBT()){
                                    temp = i;
                                }
                            }
                            bTarget.setSelectedIndex(temp);
                            bTargetPanel.setVisible(true);

                            subH4.setVisible(false);
                            routeType.setVisible(false);
//                            subH42.setVisible(false);
//                            crossDepth.setVisible(false);
//                            subH43.setVisible(false);
//                            rT.setVisible(false);
//                            inOut.setVisible(false);
//                            rightLeft.setVisible(false);
                        }
                        else{
                            block.setSelected(false);

                            bTargetPanel.setVisible(false);

                            subH4.setVisible(true);
                            routeType.setVisible(true);
                            rAngle.setVisible(true);
                            routeAngleSpin.setValue(((OBack)oMenOnField[currIndex]).getRA());
                            subH41.setVisible(true);
                            routeDepth.setVisible(true);
                            routeDepth.setValue(((OBack)oMenOnField[currIndex]).getRD());

                        }
                        if((oMenOnField[currIndex]).isEligibleReceiver()){
                            eligible.setSelected(true);
                        }
                        else{
                            eligible.setSelected(false);
                        }
                        if(((OBack)oMenOnField[currIndex]).isPassCatching()){
                            passCatch.setSelected(true);

                            subH42.setVisible(true);
                            crossDepth.setVisible(true);
                            crossDepth.setValue(((OBack)oMenOnField[currIndex]).getCD());
                            subH43.setVisible(true);
                            rT.setVisible(true);

                            int tempRI = ((OBack)oMenOnField[currIndex]).getRouteInd();
                            routes.setSelectedIndex(tempRI);
                            switch (tempRI){
                                case 0:
                                    inOut.setVisible(false);
                                    rightLeft.setVisible(false);
                                    break;
                                case 1:
                                case 2:
                                case 4:
                                //case 6:
                                    inOut.setVisible(true);
                                    if(((OBack)oMenOnField[currIndex]).isRouteIOO()){
                                        inButton.setSelected(true);
                                        inButton.setEnabled(false);
                                        outButton.setEnabled(true);
                                        outButton.setSelected(false);
                                    }
                                    else{
                                        inButton.setEnabled(true);
                                        inButton.setSelected(false);
                                        outButton.setSelected(true);
                                        outButton.setEnabled(false);
                                    }

                                    rightLeft.setVisible(true);
                                    if(((OBack)oMenOnField[currIndex]).isOnBallROL()){

                                        rightButton.setSelected(true);
                                        rightButton.setEnabled(false);
                                        leftButton.setEnabled(true);
                                        leftButton.setSelected(false);
                                    }
                                    else{
                                        rightButton.setEnabled(true);
                                        rightButton.setSelected(false);
                                        leftButton.setSelected(true);
                                        leftButton.setEnabled(false);
                                    }
                                    break;
                                case 3:
                                //case 4:
                                case 5:
                                case 6:
                                    inOut.setVisible(false);
                                    rightLeft.setVisible(true);
                                    if(((OBack)oMenOnField[currIndex]).isOnBallROL()){
                                        leftButton.setEnabled(true);
                                        rightButton.setSelected(true);
                                        rightButton.setEnabled(false);
                                        leftButton.setSelected(false);
                                    }
                                    else{
                                        rightButton.setSelected(false);
                                        leftButton.setSelected(true);
                                        leftButton.setEnabled(false);
                                        rightButton.setEnabled(true);
                                    }
                                    break;
                            }
                        }
                        else{
                            passCatch.setSelected(false);

                            subH42.setVisible(true);
                            crossDepth.setVisible(true);
                            crossDepth.setValue(((OBack)oMenOnField[currIndex]).getCD());
                            subH43.setVisible(false);
                            rT.setVisible(false);
                            inOut.setVisible(false);
                            rightLeft.setVisible(false);
                        }
                    }

                    else if (oMenOnField[currIndex] instanceof Receiver) {
                        int indR = ((Receiver) oMenOnField[currIndex]).getReceiverInd();

                        OPC.setSelectedIndex(3);

                        QBP.setVisible(false);
                        OBP.setVisible(false);
                        RP.setVisible(true);
                        OLP.setVisible(false);

                        switch (indR) {
                            case 1:
                                pS = "Wide Receiver (WR)";
                                RP.setSelectedIndex(0);
                                break;
                            case 2:
                                pS = "Tight End (TE)";
                                RP.setSelectedIndex(1);
                                break;
                        }
                        subH3OP.setVisible(true);

                        blocking.setVisible(true);
                        protecting.setVisible(false);
                        eligibility.setVisible(true);
                        passCatching.setVisible(false);
                        passing.setVisible(false);

                        if(((Receiver)oMenOnField[currIndex]).isBlocking()){
                            block.setSelected(true);

                            int temp = 0;
                            for(int i = 0; dMenOnField[i] != null; i++){
                                if(dMenOnField[i] == ((Receiver)oMenOnField[currIndex]).getBlockTarget()){
                                    temp = i;
                                }
                            }
                            bTarget.setSelectedIndex(temp);
                            bTargetPanel.setVisible(true);

                            subH4.setVisible(false);
                            rAngle.setVisible(false);
                            subH41.setVisible(false);
                            routeDepth.setVisible(false);
                            subH42.setVisible(false);
                            crossDepth.setVisible(false);
                            subH43.setVisible(false);
                            rT.setVisible(false);
                            inOut.setVisible(false);
                            rightLeft.setVisible(false);
                        }
                        else{
                            block.setSelected(false);

                            subH4.setVisible(true);
                            routeType.setVisible(true);
                            rAngle.setVisible(true);
                            routeAngleSpin.setValue(((Receiver)oMenOnField[currIndex]).getRAngle());
                            subH41.setVisible(true);
                            routeDepth.setVisible(true);
                            routeDepth.setValue(((Receiver)oMenOnField[currIndex]).getRD());
                            subH42.setVisible(true);
                            crossDepth.setVisible(true);
                            crossDepth.setValue(((Receiver)oMenOnField[currIndex]).getCD());
                            subH43.setVisible(true);
                            rT.setVisible(true);

                            int tempRI = ((Receiver)oMenOnField[currIndex]).getRouteInd();
                            routes.setSelectedIndex(tempRI);
                            switch (tempRI){
                                case 0:
                                    inOut.setVisible(false);
                                    rightLeft.setVisible(false);
                                    break;
                                case 1:
                                case 2:
                                case 4:
                                //case 6:
                                    inOut.setVisible(true);
                                    if(((Receiver)oMenOnField[currIndex]).isRouteIOO()){
                                        inButton.setSelected(true);
                                        inButton.setEnabled(false);
                                        outButton.setSelected(false);
                                        outButton.setEnabled(true);
                                    }
                                    else{
                                        inButton.setSelected(false);
                                        inButton.setEnabled(true);
                                        outButton.setSelected(true);
                                        outButton.setEnabled(false);
                                    }

                                    rightLeft.setVisible(true);
                                    if(((Receiver)oMenOnField[currIndex]).isOnBallROL()){
                                        rightButton.setSelected(true);
                                        rightButton.setEnabled(false);
                                        leftButton.setSelected(false);
                                        leftButton.setEnabled(true);
                                    }
                                    else{
                                        rightButton.setSelected(false);
                                        rightButton.setEnabled(true);
                                        leftButton.setSelected(true);
                                        leftButton.setEnabled(false);
                                    }
                                    break;
                                case 3:
                                //case 4:
                                case 5:
                                case 6:
                                    inOut.setVisible(false);
                                    rightLeft.setVisible(true);
                                    if(((Receiver)oMenOnField[currIndex]).isOnBallROL()){
                                        rightButton.setSelected(true);
                                        rightButton.setEnabled(false);
                                        leftButton.setEnabled(true);
                                        leftButton.setSelected(false);
                                    }
                                    else{
                                        rightButton.setEnabled(true);
                                        rightButton.setSelected(false);
                                        leftButton.setSelected(true);
                                        leftButton.setEnabled(false);
                                    }
                                    break;
                            }
                        }
                        if((oMenOnField[currIndex]).isEligibleReceiver()){
                            eligible.setSelected(true);
                        }
                        else{
                            eligible.setSelected(false);
                        }
                    }
                    pSpec.setText("Skill Position: " + pS);
                    heightSpin.setValue(oMenOnField[currIndex].getHeight());
                    weightSpin.setValue(oMenOnField[currIndex].getWeight());
                    strengthSpin.setValue((int) (oMenOnField[currIndex].getStrength() * 100));
                    speedSpin.setValue(Math.round(oMenOnField[currIndex].getSpeed() * 1000.0) / 100.0);

                    if(oMenOnField[currIndex].playerHasBall()){
                        ballButton.setEnabled(false);
                        ballButton.setBackground(Color.ORANGE);
                    }

                    else{
                        ballButton.setEnabled(true);
                        ballButton.setBackground(Color.MAGENTA);
                    }

                    ORB.setSelected(true);
                    ORB.setEnabled(false);
                    DRB.setSelected(false);
                    DRB.setEnabled(true);
                    OPT.setVisible(true);
                    DPT.setVisible(false);
                    OPF.setVisible(true);
                    DPF.setVisible(false);
                    O1.setText("" + QBCount);
                    O2.setText("" + BackCount);
                    O3.setText("" + RecDBCount);
                    O4.setText("" + LineCount);
                    OPersonnelBreakdown.setVisible(true);
                    DPersonnelBreakdown.setVisible(false);
                    OCPPanel.setVisible(true);
                    DCPPanel.setVisible(false);
                }

                else{

                    countPersonnel();

                    int tempI = 0;

                    subH3O.setVisible(false);
                    blocking.setVisible(false);
                    protecting.setVisible(false);
                    eligibility.setVisible(false);
                    passCatching.setVisible(false);
                    passing.setVisible(false);
                    bTargetPanel.setVisible(false);

                    subH4.setVisible(false);
                    routeType.setVisible(false);

                    DRPanel.setVisible(true);

                    BLPanel.setVisible(false);
                    BBPanel.setVisible(false);

                    if(dMenOnField[currIndex] instanceof DLine){
                        int indDL = ((DLine)dMenOnField[currIndex]).getdDLineInd();

                        DPC.setSelectedIndex(1);

                        subH3OP.setVisible(false);
                        subH3DP.setVisible(false);
                        coverage.setVisible(false);
                        zonePanel.setVisible(false);

                        DBP.setVisible(false);
                        LBP.setVisible(false);
                        DLP.setVisible(true);

                        switch (indDL){
                            case 1:
                                pS = "Defensive Lineman (DL)";
                                DLP.setSelectedIndex(0);
                                break;
                            case 2:
                                pS = "Nose Tackle (NT)";
                                DLP.setSelectedIndex(1);
                                break;
                            case 3:
                                pS = "Defensive Tackle (DT)";
                                DLP.setSelectedIndex(2);
                                break;
                            case 4:
                                pS = "Defensive End (DE)";
                                DLP.setSelectedIndex(3);
                                break;
                        }

                        for(int i = 0; oMenOnField[i] != null; i++){
                            if(oMenOnField[i] == ((DLine)dMenOnField[currIndex]).getTargetMan()){
                                tempI = i;
                            }
                        }
                        target.setSelectedIndex(tempI);

                        targetPanel.setVisible(true);
                    }

                    else if (dMenOnField[currIndex] instanceof LineBacks){
                        int indLB = ((LineBacks)dMenOnField[currIndex]).getLineBackInd();

                        DPC.setSelectedIndex(0);

                        DBP.setVisible(false);
                        LBP.setVisible(true);
                        DLP.setVisible(false);

                        switch(indLB){
                            case 1:
                                pS = "Line Backer (LB)";
                                LBP.setSelectedIndex(0);
                                break;
                            case 2:
                                pS = "Middle Line Backer (MLB)";
                                LBP.setSelectedIndex(1);
                                break;
                            case 3:
                                pS = "Outside Line Backer (OLB)";
                                LBP.setSelectedIndex(2);
                                break;
                        }

                        subH3DP.setVisible(true);
                        coverage.setVisible(true);

                        for(int i = 0; oMenOnField[i] != null; i++){
                            if(oMenOnField[i] == ((LineBacks)dMenOnField[currIndex]).getTargetMan()){
                                tempI = i;
                            }
                        }
                        target.setSelectedIndex(tempI);

                        if(((LineBacks)dMenOnField[currIndex]).isZone()){
                            z.setSelected(true);
                            z.setEnabled(false);
                            m.setSelected(false);
                            m.setEnabled(true);
                            zoneR.setValue((int)((LineBacks)dMenOnField[currIndex]).getZoneCR());
                            zonePanel.setVisible(true);
                            targetPanel.setVisible(false);
                        }

                        else{
                            z.setSelected(false);
                            z.setEnabled(true);
                            m.setSelected(true);
                            m.setEnabled(false);
                            zonePanel.setVisible(false);
                            targetPanel.setVisible(true);
                        }
                    }

                    else if (dMenOnField[currIndex] instanceof DBacks){
                        int indDB = ((DBacks)dMenOnField[currIndex]).getDBackInd();

                        subH3DP.setVisible(true);

                        coverage.setVisible(true);

                        DPC.setSelectedIndex(2);

                        DBP.setVisible(true);
                        LBP.setVisible(false);
                        DLP.setVisible(false);

                        switch (indDB){
                            case 1:
                                pS = "Defensive Back (DB)";
                                DBP.setSelectedIndex(0);
                                break;
                            case 2:
                                pS = "Corner Back (CB)";
                                DBP.setSelectedIndex(1);
                                break;
                            case 3:
                                pS = "Safety (S)";
                                DBP.setSelectedIndex(2);
                                break;
                            case 4:
                                pS = "Free Safety (FS)";
                                DBP.setSelectedIndex(3);
                                break;
                            case 5:
                                pS = "Strong Safety (SS)";
                                DBP.setSelectedIndex(4);
                                break;
                        }

                        for(int i = 0; oMenOnField[i] != null; i++){
                            if(oMenOnField[i] == ((DBacks)dMenOnField[currIndex]).getTargetMan()){
                                tempI = i;
                            }
                        }
                        target.setSelectedIndex(tempI);

                        if(((DBacks)dMenOnField[currIndex]).isZone()){
                            z.setSelected(true);
                            z.setEnabled(false);
                            m.setSelected(false);
                            m.setEnabled(true);
                            zoneR.setValue((int)((DBacks)dMenOnField[currIndex]).getZoneCR());
                            zonePanel.setVisible(true);
                            targetPanel.setVisible(false);
                        }

                        else{
                            z.setSelected(false);
                            z.setEnabled(true);
                            m.setSelected(true);
                            m.setEnabled(false);
                            zonePanel.setVisible(false);
                            targetPanel.setVisible(true);
                        }
                    }

                    pSpec.setText("Skill Position: " + pS);
                    heightSpin.setValue(dMenOnField[currIndex].getHeight());
                    weightSpin.setValue(dMenOnField[currIndex].getWeight());
                    strengthSpin.setValue((int)(dMenOnField[currIndex].getStrength()*100));
                    speedSpin.setValue(Math.round(dMenOnField[currIndex].getSpeed()*1000.0)/100.0);

                    ORB.setSelected(false);
                    ORB.setEnabled(true);
                    DRB.setSelected(true);
                    DRB.setEnabled(false);
                    OPT.setVisible(false);
                    DPT.setVisible(true);
                    OPF.setVisible(false);
                    DPF.setVisible(true);
                    D1.setText("" + LineCount);
                    D2.setText("" + BackCount);
                    D3.setText("" + RecDBCount);
                    OPersonnelBreakdown.setVisible(false);
                    DPersonnelBreakdown.setVisible(true);
                    OCPPanel.setVisible(false);
                    DCPPanel.setVisible(true);
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void countPersonnel(){

        QBCount = 0;
        LineCount = 0;
        RecDBCount = 0;
        BackCount = 0;

        int i = 0;
        if(oClicked){
            while(oMenOnField[i] != null){
                if(oMenOnField[i] instanceof QB){
                    QBCount++;
                }
                else if(oMenOnField[i] instanceof OLine){
                    LineCount++;
                }
                else if(oMenOnField[i] instanceof OBack){
                    BackCount++;
                }
                else{
                    RecDBCount++;
                }
                i++;
            }
        }

        else{
            while(dMenOnField[i] != null){
                if(dMenOnField[i] instanceof DLine){
                    LineCount++;
                }
                else if(dMenOnField[i] instanceof LineBacks){
                    BackCount++;
                }
                else{
                    RecDBCount++;
                }
                i++;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void addMenuBar(){

        //Menu Bar setup.

        //File menu for overall program actions
        JMenu fileMenu = new JMenu("File");

        JMenu chooseFieldMenu = new JMenu("Choose Home Field");

        //NFL internal menu
        JMenu nfl = new JMenu("NFL");

        //--------------------------------------------------------------------------------------------------------------
        //AFC North Menu Items
        JMenu anorth = new JMenu("AFC North");
        JMenuItem balt = new JMenuItem("Baltimore Ravens");
        balt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[2]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem cinc = new JMenuItem("Cincinnati Bengals");
        cinc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[6]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem clev = new JMenuItem("Cleveland Browns");
        clev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[7]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem pitt = new JMenuItem("Pittsburgh Steelers");
        pitt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[26]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        anorth.add(balt);
        anorth.add(cinc);
        anorth.add(clev);
        anorth.add(pitt);

        //--------------------------------------------------------------------------------------------------------------
        //AFC South Menu Items
        JMenu asouth = new JMenu("AFC South");
        JMenuItem hous = new JMenuItem("Houston Texans");
        hous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[12]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem indi = new JMenuItem("Indianapolis Colts");
        indi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[13]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem jack = new JMenuItem("Jacksonville Jaguars");
        jack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[14]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem tenn = new JMenuItem("Tennessee Titans");
        tenn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[30]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        asouth.add(hous);
        asouth.add(indi);
        asouth.add(jack);
        asouth.add(tenn);

        //--------------------------------------------------------------------------------------------------------------
        //AFC East Menu Items
        JMenu aeast = new JMenu("AFC East");
        JMenuItem buff = new JMenuItem("Buffalo Bills");
        buff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[3]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem miam = new JMenuItem("Miami Dolphins");
        miam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[18]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem newe = new JMenuItem("New England Patriots");
        newe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[20]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem newj = new JMenuItem("New York Jets");
        newj.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[23]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        aeast.add(buff);
        aeast.add(miam);
        aeast.add(newe);
        aeast.add(newj);

        //--------------------------------------------------------------------------------------------------------------
        //AFC West Menu Item
        JMenu awest = new JMenu("AFC West");

        JMenuItem denv = new JMenuItem("Denver Broncos");
        denv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[9]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem kans = new JMenuItem("Kansas City Chiefs");
        kans.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[15]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem lach = new JMenuItem("Los Angeles Chargers");
        lach.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[16]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem oakl = new JMenuItem("Oakland Raiders");
        oakl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[24]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        awest.add(denv);
        awest.add(kans);
        awest.add(lach);
        awest.add(oakl);

        //--------------------------------------------------------------------------------------------------------------
        //NFC North Menu Items
        JMenu nnorth = new JMenu("NFC North");

        JMenuItem chic = new JMenuItem("Chicago Bears");
        chic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[5]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem detr = new JMenuItem("Detroit Lions");
        detr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[10]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem greb = new JMenuItem("Green Bay Packers");
        greb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[11]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem minn = new JMenuItem("Minnesota Vikings");
        minn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[19]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        nnorth.add(chic);
        nnorth.add(detr);
        nnorth.add(greb);
        nnorth.add(minn);

        //--------------------------------------------------------------------------------------------------------------
        //NFC South Menu Items
        JMenu nsouth = new JMenu("NFC South");

        JMenuItem atla = new JMenuItem("Atlanta Falcons");
        atla.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[1]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem caro = new JMenuItem("Carolina Panthers");
        caro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[4]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem newo = new JMenuItem("New Orleans Saints");
        newo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[21]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem tamp = new JMenuItem("Tampa Bay Buccaneers");
        tamp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[29]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        nsouth.add(atla);
        nsouth.add(caro);
        nsouth.add(newo);
        nsouth.add(tamp);

        //--------------------------------------------------------------------------------------------------------------
        //NFC East Menu Items
        JMenu neast = new JMenu("NFC East");

        JMenuItem dall = new JMenuItem("Dallas Cowboys");
        dall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[8]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem newg = new JMenuItem("New York Giants");
        newg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[22]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem phil = new JMenuItem("Philadelphia Eagles");
        phil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[25]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem wash = new JMenuItem("Washington Redskins");
        wash.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[31]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        neast.add(dall);
        neast.add(newg);
        neast.add(phil);
        neast.add(wash);

        //--------------------------------------------------------------------------------------------------------------
        //NFC West Menu Items
        JMenu nwest = new JMenu("NFC West");

        JMenuItem ariz = new JMenuItem("Arizona Cardinals");
        ariz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[0]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem lara = new JMenuItem("Los Angeles Rams");
        lara.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[17]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem sanf = new JMenuItem("San Francisco 49ers");
        sanf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[27]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        JMenuItem seat = new JMenuItem("Seattle Seahawks");
        seat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[28]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        nwest.add(ariz);
        nwest.add(lara);
        nwest.add(sanf);
        nwest.add(seat);

        //--------------------------------------------------------------------------------------------------------------
        //Block to handle adding the Gator football field menu item (NCAA field, not NFL)
        JMenu ncaa = new JMenu("NCAA");
        JMenu secE = new JMenu("SEC East");

        JMenuItem flor = new JMenuItem("Florida Gators");
        flor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                new Thread(new Runnable(){
                    public void run() {
                        defaultField = false;
                        wipeField(fields[32]);
                        displaySpread(playerDiameter);
                    }
                }).start();
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //Add all internal conference menus to the NFL menu
        nfl.add(anorth);
        nfl.add(asouth);
        nfl.add(aeast);
        nfl.add(awest);
        nfl.add(nnorth);
        nfl.add(nsouth);
        nfl.add(neast);
        nfl.add(nwest);

        //Add Florida Gator menu item to SEC East conference internal menu, then add to NCAA menu
        secE.add(flor);
        ncaa.add(secE);

        //Add NFL and NCAA internal menus to the Choose Home Field menu
        chooseFieldMenu.add(nfl);
        chooseFieldMenu.add(ncaa);

        //**************************************************************************************************************
        //OLD MENU ITEM THAT HANDLED CHOOSING THE FILE OF THE DESIRED FIELD IMAGE
        //Menu item allows user to choose the home football field to run simulation on
//        JMenuItem chooseField = new JMenuItem("Choose Home Field");
//        chooseField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent event) {
//                //Calls the method to run a "long task" appropriately.
//                new Thread(new Runnable(){
//                    public void run() {
//                        defaultField = false;
//                        loadField();
//                    }
//                }).start();
//            }
//        });
        //**************************************************************************************************************

        //Menu item to allow the user to save the image of the current play spread or play progression
        //ONLY WHEN PAUSED OR SIMULATION HASN'T RAN YET
        JMenuItem saveItem = new JMenuItem("Save Play Spread");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.

                //*******************************************
                //FUNCTIONALITY STILL NEEDS TO BE IMPLEMENTED
                //*******************************************

                new Thread(new Runnable(){
                    public void run() {
                        //playG2D.drawImage(saveImage, null, 0, 0);
                        savePlaySpread(saveImage);
                    }
                }).start();
            }
        });

        //Menu item to exit and terminate the program
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Exit and close the program.
                System.exit(0);
            }
        });

        //Adding main menu items to the File Menu
        fileMenu.add(chooseFieldMenu);
        //fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        //New JMenu to contain all menus and respective menu items regarding configuring the current play
        JMenu configPlayMenu = new JMenu("Configure Play Spread");

        //New JMenu to be contained within the configure play JMenu and contain offensive configurations
        JMenu configO = new JMenu("Configure Offense");

        //REMOVED BASIC SPREAD, WAS UNINTUITIVE AND WOULD'VE BEEN CONFUSING TO HANDLE WHEN THERE IS ALREADY A BASIC RUN AND PASS

        JMenu runO = new JMenu("Basic Run Offense");

        //Configure offense to defualt generic run play
        JMenuItem runOM = new JMenuItem("Up the Middle");
        runOM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                runM = true;
                runL = false;
                runR = false;
                basicORun();
            }
        });

        JMenuItem runOL = new JMenuItem("To the Left");
        runOL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                runM = false;
                runL = true;
                runR = false;
                basicORun();
            }
        });

        JMenuItem runOR = new JMenuItem("To the Right");
        runOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                runM = false;
                runL = false;
                runR = true;
                basicORun();
            }
        });

        //Adding Run Options to offensive run menu
        runO.add(runOM);    //Up the middle run menu item
        runO.add(runOL);    //To the left run menu item
        runO.add(runOR);    //To the right run

        //Configure offense to default generic pass play
        JMenuItem passO = new JMenuItem("Basic Pass Offense");
        passO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Exit and close the program.
                basicOPass();
            }
        });

        //Adding all OFFENSIVE menu items to the configure offense JMenu
        //configO.add(basicO);        //Basic O spread menu item
        configO.add(runO);          //Run O menu
        configO.add(passO);         //Pass O menu item


        //New JMenu to be contained within the configure play JMenu and contain defensive configurations
        JMenu configD = new JMenu("Configure Defense");

        //REMOVED BASIC SPREAD, WAS UNINTUITIVE AND WOULD'VE BEEN CONFUSING TO HANDLE WHEN THERE IS ALREADY A BASIC RUN AND PASS

        //Configure defense to default generic run defense play
        JMenuItem runD = new JMenuItem("Basic Run Defense");
        runD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Calls the method to run a "long task" appropriately.
                basicDRun();

            }
        });

        //Configure defense to default generic pass defense play
        JMenuItem passD = new JMenuItem("Basic Pass Defense");
        passD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                basicDPass();
            }
        });

        //Add above menu items to the configure defense JMenu
        //configD.add(basicD);        //Basic D Spread menu item
        configD.add(runD);          //Run D menu item
        configD.add(passD);         //Pass D menu item

        //Add internal menus to configure play menu
        configPlayMenu.add(configO);    //Internal O configure menu
        configPlayMenu.add(configD);    //Internal D configure menu

        //Attach main bar menus to overall menu bar
        JMenuBar menuBar = new JMenuBar();      //Initialize new menu bar
        menuBar.add(fileMenu);                  //Add File menu
        menuBar.add(configPlayMenu);            //Add Configure Play menu
        this.setJMenuBar(menuBar);              //Set Menu Bar of image frame to menu bar
    }

    //------------------------------------------------------------------------------------------------------------------

    private void simulation(){

        //Initialize all temp variables for each iteration of simulation
        double oX, oY, dX, dY, odX, odY, ooX, ooY, doX, doY, ddX, ddY, nextX, nextY, oS, dS;
        nextX = nextY = 0.0;
        OPlayer tempO, oCO, dCO;
        DPlayer tempD, oCD, dCD;

        //System.out.println("Frame: " + frameCount); //PRINT CHECK

        //Every time the timer calls simulation, update the frame slider to reflect current frame count
        f.setValue(frameCount);

        //BOTH oSpread AND dSpread MUST BE PRESENT ON SIMULATION PANEL TO RUN SIMULATION
        if(oSpread && dSpread) {

            //For iterator i is less than number of players allowed on offense and defense (11 Players)
            for (int i = 0; i < 11; i++) {

                //System.out.println("i: " + i);  //PRINT CHECK

                tempO = oMenOnField[i];                     //Store OPlayer at current i index in temp variable
                oX = tempO.getCenterX() + checkRadius;      //Get, calculate, store CENTERED x position of current OPlayer
                oY = tempO.getCenterY() + checkRadius;      //Get, calculate, store CENTERED y position of current OPlayer

                oCD = dMenOnField[OCheckCloseD(tempO)];     //Check the closest DPlayer to the current stored temp OPlayer, store based on returned index
                odX = oCD.getCenterX() + checkRadius;       //Get, calculate, store CENTERED x position of closest DPlayer
                odY = oCD.getCenterY() + checkRadius;       //Get, calculate, store CENTERED y position of closest DPlayer

                oCO = oMenOnField[OCheckCloseO(tempO,i)];   //Check the closest OPlayer to the current stored temp OPlayer, store based on returned index
                ooX = oCO.getCenterX() + checkRadius;       //Get, calculate, store CENTERED x position of closest OPlayer
                ooY = oCO.getCenterY() + checkRadius;       //Get, calculate, store CENTERED x position of closest OPlayer

                oS = (1.0 - tempO.getSpeed()) * speedMult;

                tempD = dMenOnField[i];                     //Store DPlayer at the same current i index in separate temp variable
                dX = tempD.getCenterX() + checkRadius;      //Get, calculate, store CENTERED x position of current DPlayer
                dY = tempD.getCenterY() + checkRadius;      //Get, calculate, store CENTERED y position of current DPlayer

                dCO = oMenOnField[DCheckCloseO(tempD)];     //Check the closest OPlayer to the current stored temp DPlayer, store based on returned index
                doX = dCO.getCenterX() + checkRadius;       //Get, calculate, store CENTERED x position of closest OPlayer
                doY = dCO.getCenterY() + checkRadius;       //Get, calculate, store CENTERED y position of closest OPlayer

                dCD = dMenOnField[DCheckCloseD(tempD, i)];  //Check the closest DPlayer to the current stored temp DPlayer, store based on returned index
                ddX = dCD.getCenterX() + checkRadius;       //Get, calculate, store CENTERED x position of closest DPlayer
                ddY = dCD.getCenterY() + checkRadius;       //Get, calculate, store CENTERED y position of closest DPlayer

                dS = (1.0 - tempD.getSpeed()) * speedMult;

                double thetaD = 0.0;

                //PRINT CHECKS FOR ALL STORED CURRENT/TEMP OPlayer AND DPlayer AND THEIR CLOSEST OBJECTS ON EITHER SIDE OF BALL
                //*************************************************************************************************************
                //System.out.println("Current O: " + tempO + ", oCD: " + oCD + " , oCO: " + oCO);
                //System.out.println("Current D: " + tempD + ", dCO: " + dCO + " , dCD: " + dCD);
                //*************************************************************************************************************

                //TEST IMPLEMENTATION OF NON-CONSTANT FOLLOW ALGORITHM APPROACH ("EASING")
                //double delOX, delOY;
                //delOX = odX - oX;
                //delOY = odY - oY;

                //If the current stored temp OPlayer is an OLine player object
                if (tempO instanceof OLine) {

                    //Confirmed OLine player, now if it is a pass play --> pocket protection and blocking of closest DPlayer
                    if(passO){

                        //EASING
                        //nextX = oX + delOX * tempO.getSpeed() - checkRadius;
                        //nextY = oY + delOY * tempO.getSpeed() - checkRadius;

                        //Calculate next step towards that closest DPlayer
                        thetaD = Math.atan2((odY - oY), (odX - oX));
                        nextX = oX + oS * Math.cos(thetaD);
                        nextY = oY + oS * Math.sin(thetaD);

                    }

                    //Confirmed OLine player, but else if it is not a pass play --> running block assignments
                    else{

                        //Next coordinate positions determined by temp OPlayer speed and block angle
                        nextX = oX + oS * Math.cos(((OLine) tempO).getBAngle());
                        nextY = oY + oS * Math.sin(((OLine) tempO).getBAngle());
                    }

                    if (distance(nextX, nextY, odX, odY) <= playerDiameter) {

                        //***COLLISION OF OPlayer WITH DPlayer
                        int result = collisionMatchup(tempO, oCD);

                        //If result equals 1, that means OLine player won collision matchup
                        if(result == 1){
                            //Proceed to projected step
                            tempO.setCenterX(nextX - checkRadius);
                            tempO.setCenterY(nextY - checkRadius);
                            oMenOnField[i] = tempO;
                        }

                        //Else, the colliding DPlayer won
                        else{
                            //If pass-protection, losing OLine player gets bounced opposite original bearing
                            if(passO){
                                thetaD += 180.0;
                                nextX = oX + oS * Math.cos(thetaD);
                                nextY = oY + oS * Math.sin(thetaD);
                                tempO.setCenterX(nextX - checkRadius);
                                tempO.setCenterY(nextY - checkRadius);
                                oMenOnField[i] = tempO;
                            }

                            //Else on blocking path, calculate collision angle and bounce the OLin player back
                            else{
                                thetaD = Math.atan2((odY - oY), (odX - oX)) + 180.0;
                                nextX = oX + oS * Math.cos(thetaD);
                                nextY = oY + oS * Math.sin(thetaD);
                                tempO.setCenterX(nextX - checkRadius);
                                tempO.setCenterY(nextY - checkRadius);
                                oMenOnField[i] = tempO;
                            }
                        }

                        System.out.println(result); //PRINT CHECK
                    }

                    //THIS ELSE IF MIGHT BE ISSUE LATER*****************************************************************
                    else if (distance(nextX, nextY, ooX, ooY) <= playerDiameter){

                        //Collision between two OPlayer --> Bounce at a 90 degree based on relative x-position
                        thetaD = Math.atan2((ooY - oY), (ooX - oX));
                        if(oX < ooX){
                            thetaD += 90.0;
                        }
                        else{
                            thetaD -= 90.0;
                        }

                        nextX = oX + oS * Math.cos(thetaD);
                        nextY = oY + oS * Math.sin(thetaD);
                        tempO.setCenterX(nextX - checkRadius);
                        tempO.setCenterY(nextY - checkRadius);
                        oMenOnField[i] = tempO;
                    }

                    else{
                        tempO.setCenterX(nextX - checkRadius);
                        tempO.setCenterY(nextY - checkRadius);
                        oMenOnField[i] = tempO;
                    }
                }

                //ELSE IF the temp OPlayer is a RB or Receiver
                else if (tempO instanceof OBack) {

                    //If the OBack is NOT BLOCKING --> that means is pass-catching or running
                    if (!((OBack) tempO).isBlocking()){
                        //Always follow a designated path
                        if (frameCount < tempO.getPath().length) {

                            nextX = tempO.path[frameCount][0] - checkRadius;
                            nextY = tempO.path[frameCount][1] - checkRadius;

                        }

                        else{
                            nextX = oX - checkRadius;
                            nextY = oY - checkRadius;
                        }
                    }

                    //Else the OBack IS BLOCKING
                    else{
                        thetaD = Math.atan2(((((OBack)tempO).getBT().getCenterY()) - oY), ((((OBack)tempO).getBT().getCenterX()) - oX));
                        nextX = oX + oS * Math.cos(thetaD);
                        nextY = oY + oS * Math.sin(thetaD);
                    }


                    //If collision, send through matchup
                    if(distance(nextX, nextY, odX, odY) <= playerDiameter) {

                        int tempR = collisionMatchup(tempO, oCD);

                        //Most cases, the RB will have precedent to run route
                        if(tempR == 1){
                            tempO.setCenterX(nextX);
                            tempO.setCenterY(nextY);
                            oMenOnField[i] = tempO;
                        }

                        //Only else if the RB is a runner, does not have the ball, and collides, just have them stop?
                        else{
                            System.out.println("RB lost");
                        }
                    }

                    //Else, free to proceed
                    else{
                        tempO.setCenterX(nextX);
                        tempO.setCenterY(nextY);
                        oMenOnField[i] = tempO;
                    }

                    //ADVANTAGE: WILL NOT EVER VARY FROM A PREDETERMINED ROUTE
                }

                else if(tempO instanceof Receiver){
                    //If the OBack is NOT BLOCKING --> that means is pass-catching or running
                    if (!((Receiver)tempO).isBlocking()){
                        //Always follow a designated path
                        if (frameCount < tempO.getPath().length) {
                            nextX = tempO.path[frameCount][0] - checkRadius;
                            nextY = tempO.path[frameCount][1] - checkRadius;

                        }
                        else {
                            nextX = oX - checkRadius;
                            nextY = oY - checkRadius;
                        }
                    }

                    //Else the OBack IS BLOCKING, calculate the bearing and next step in direction of target block DPlayer
                    else{
                        thetaD = Math.atan2(((((Receiver)tempO).getBlockTarget().getCenterY()) - oY), ((((Receiver)tempO).getBlockTarget().getCenterX()) - oX));
                        nextX = oX + oS * Math.cos(thetaD);
                        nextY = oY + oS * Math.sin(thetaD);
                    }

                    //If collision, send through matchup
                    if(distance(nextX, nextY, odX, odY) <= playerDiameter) {

                        int tempR = collisionMatchup(tempO, oCD);

                        //Most cases, the RB will have precedent to run route
                        if(tempR == 1){
                            tempO.setCenterX(nextX);
                            tempO.setCenterY(nextY);
                            oMenOnField[i] = tempO;
                        }

                        //Else if blocking WR loses, bounce backwards
                        else{
                            thetaD = Math.atan2((odX - oX), (odY - oY)) + 180.0;
                            nextX = oX + oS * Math.cos(thetaD);
                            nextY = oY + oS * Math.sin(thetaD);
                            tempO.setCenterX(nextX);
                            tempO.setCenterY(nextY);
                            oMenOnField[i] = tempO;
                        }
                    }

                    //Else, free to proceed
                    else{
                        tempO.setCenterX(nextX);
                        tempO.setCenterY(nextY);
                        oMenOnField[i] = tempO;
                    }
                    //ADVANTAGE: WILL NOT EVER VARY FROM A PREDETERMINED ROUTE
                }

                //ELSE IF the current temp OPlayer is a Quarterback
                else if (tempO instanceof QB) {

                    //IF it is a running play and the frame count doesn't exceed the drop back path length
                    if (!passO && frameCount < tempO.getPath().length) {

                        //--> QB will follow a drop back path to hand the ball off
                        nextX = tempO.path[frameCount][0] - checkRadius;
                        nextY = tempO.path[frameCount][1] - checkRadius;
                    }

                    //ELSE IF it is a pass play
                    else if (passO) {

                        //--> Quarterback will remain in the pocket and attempt to avoid incoming pass rushers with enough time to make a pass


                            //Calculate angle from closest incoming pass rusher
                            //***MAY WANT TO USE EASING ALGORITHM FOR QB***
                            thetaD = Math.atan2((odY - oY), (odX - oX));

                            nextX = oX - oS * Math.cos(thetaD);
                            nextY = oY - oS * Math.sin(thetaD);

                    }

                    if (distance(nextX, nextY, odX, odY) <= playerDiameter) {

                        //***COLLISION OF OPlayer WITH DPlayer
                        int result = collisionMatchup(tempO, oCD);

                        if(result == 1){
                            tempO.setCenterX(nextX - checkRadius);
                            tempO.setCenterY(nextY - checkRadius);
                            oMenOnField[i] = tempO;
                        }

                        else{
                            if(oMenOnField[i].playerHasBall()) {
                                timer.stop();
                                System.out.println("");
                            }
                        }
                    }

                    else if (distance(nextX, nextY, ooX, ooY) <= playerDiameter){

                        if(((QB)tempO).isPassing()){
                            thetaD += 180.0;
                            nextX = oX - oS * Math.cos(thetaD);
                            nextY = oY - oS * Math.sin(thetaD);
                        }

                        tempO.setCenterX(nextX - checkRadius);
                        tempO.setCenterY(nextY - checkRadius);
                        oMenOnField[i] = tempO;
                    }

                    else{

                        //Make sure QB doesn't just run down and out of screen
                        if(nextY < simHeight - 250) {
                            tempO.setCenterX(nextX - checkRadius);
                            tempO.setCenterY(nextY - checkRadius);
                            oMenOnField[i] = tempO;
                        }
                        else{
                            tempO.setCenterX(nextX - checkRadius);
                            tempD.setCenterY(oY - checkRadius);
                            oMenOnField[i] = tempO;
                        }
                    }
                }

                //System.out.println("Current OPlayer speed: " + tempO.getSpeed());

                double tX, tY, distCO, distCD, thetaT;

                //double delX, delY;

                //If the tempD DPlayer is a DLine
                if (tempD instanceof DLine) {

                    //Store coordinates for DLine target
                    tX = ((DLine) tempD).getTargetMan().getCenterX() + checkRadius;
                    tY = ((DLine) tempD).getTargetMan().getCenterY() + checkRadius;

                    //Calculate bearing and projected next coordinates
                    thetaT = Math.atan2((tY - dY), (tX - dX));
                    nextX = dX + dS * Math.cos(thetaT);
                    nextY = dY + dS * Math.sin(thetaT);

                    //EASING
                    //delX = tX - dX;
                    //delY = tY - dY;
                    //nextX = dX + delX * tempD.getSpeed();
                    //nextY = dY + delY * tempD.getSpeed();

                    //Pre-calculate distances between current tempD and closest O and D players
                    distCO = distance(nextX, nextY, doX, doY);
                    distCD = distance(nextX, nextY, ddX, ddY);


                    if (distCO <= playerDiameter) {

                        //***COLLISION OF DPlayer with closest OPlayer but not necessarily target man***
                        int result = collisionMatchup(dCO, tempD);

                        //If DPlayer won, proceed to projected next coordinates
                        if(result == -1){
                            tempD.setCenterX(nextX - checkRadius);
                            tempD.setCenterY(nextY - checkRadius);
                            dMenOnField[i] = tempD;
                        }
                        //If DPlayer loses, can do a coin flip for a "special move" (instead of bouncing backwards, the
                        //defensive player side-steps or spins laterally)
                        else{

                            //UNIQUE ONLY TO DLine
                            if(rng.nextDouble() < 0.50){
                                if(rng.nextDouble() < 0.50){
                                    thetaT += 90.0;
                                }
                                else{
                                    thetaT -= 90.0;
                                }
                            }

                            else{
                                thetaT += 180.0;
                            }

                            nextX = dX + dS * Math.cos(thetaT);
                            nextY = dY + dS * Math.sin(thetaT);
                            tempD.setCenterX(nextX - checkRadius);
                            tempD.setCenterY(nextY - checkRadius);
                            dMenOnField[i] = tempD;
                        }
                        //System.out.println(result);
                    }

                    else if(distCD <= playerDiameter){
                        //***Collision with another defender***
                        thetaT = Math.atan2((ddY - dY), (ddX - dX));
                        if(dX < ddX){
                            thetaT -= 90.0;
                        }
                        else{
                            thetaT += 90.0;
                        }

                        nextX = dX + dS * Math.cos(thetaT);
                        nextY = dY + dS * Math.sin(thetaT);
                        tempD.setCenterX(nextX - checkRadius);
                        tempD.setCenterY(nextY - checkRadius);
                        dMenOnField[i] = tempD;
                    }

                    else {
                        tempD.setCenterX(nextX - checkRadius);
                        tempD.setCenterY(nextY - checkRadius);
                        dMenOnField[i] = tempD;
                    }
                }

                else if (tempD instanceof LineBacks || tempD instanceof DBacks) {

                    //***Had to put this because I got a "May Not Have Been Initialized" error...***
                    tX = 0.0;
                    tY = 0.0;
                    thetaT = 0.0;

                    boolean LBorDB;         //Boolean that will check whether the passed in OPlayer is on or the other
                    boolean tempZ;          //Temp variable to store whether the current OPlayer is in Zone coverage
                    OPlayer tempTarget;     //Temp variable to extract and store the assigned target of the LB or DB

                    //Internally check the instance
                    if(tempD instanceof LineBacks){
                        LBorDB = true;
                        tempZ = ((LineBacks) tempD).isZone();
                        tempTarget = ((LineBacks) tempD).getTargetMan();
                    }
                    else {
                        LBorDB = false;
                        tempZ = ((DBacks) tempD).isZone();
                        tempTarget = ((DBacks) tempD).getTargetMan();
                    }

                    //Calculate the distance between the linebacker and the closest:
                    distCO = distance(dX, dY, doX, doY);    //OPlayer to the LB/DB
                    distCD = distance(dX, dY, ddX, ddY);    //DPlayer to the LB/DB

                    //If the LB or DB is in Zone Coverage
                    if(tempZ){

                        if(!(dCO instanceof OLine)) {

                            //Check if the distance between the Linebacker and the closest OPlayer is within the zone radius
                            if (distCO <= zoneCoverageRadius) {


                                //Update the target man to be the closest OPlayer
                                if (LBorDB) {
                                    ((LineBacks) tempD).setTargetMan(dCO);
                                } else {
                                    ((DBacks) tempD).setTargetMan(dCO);
                                }
                                tempTarget = dCO;
                                tX = tempTarget.getCenterX() + checkRadius;                  //Update targetX coordinate
                                tY = tempTarget.getCenterY() + checkRadius;                  //Update targetY coordinate
                                thetaT = Math.atan2((tY - dY), (tX - dX));
                                nextX = dX + dS * Math.cos(thetaT);
                                nextY = dY + dS * Math.sin(thetaT);

                            }
                            else{
                                nextX = dX;
                                nextY = dY;
                            }
                        }
//                        //Else the Linebacker in Zone coverage should stay put and wait for someone to enter zone
                        else{
                            //Keep the current and target position coordinates the same
                            nextX = dX;
                            nextY = dY;
                        }
                    }

                    //Else if the Linebacker is in man-coverage/designated target man
                    else {

                        //Update the target position coordinates
                        tX = tempTarget.getCenterX() + checkRadius;
                        tY = tempTarget.getCenterY() + checkRadius;
                        thetaT = Math.atan2((tY - dY), (tX - dX));
                        nextX = dX + dS * Math.cos(thetaT);
                        nextY = dY + dS * Math.sin(thetaT);
                    }

                    //delX = tX - dX;
                    //delY = tY - dY;
                    //nextX = dX + delX * tempD.getSpeed();
                    //nextY = dY + delY * tempD.getSpeed();

                    if(distCO <= playerDiameter){
                        //***Collision with other OPlayer that is not Target Man***
                        int result = collisionMatchup(dCO, tempD);

                        //If DPlayer won, proceed to projected next coordinates
                        if(result == -1){
                            tempD.setCenterX(nextX - checkRadius);
                            tempD.setCenterY(nextY - checkRadius);
                            dMenOnField[i] = tempD;
                        }

                        //No special move for a DBack or LineBack
                        else{

                            thetaT += 180.0;

                            nextX = dX + dS * Math.cos(thetaT);
                            nextY = dY + dS * Math.sin(thetaT);
                            tempD.setCenterX(nextX - checkRadius);
                            tempD.setCenterY(nextY - checkRadius);
                            dMenOnField[i] = tempD;
                        }
                        //System.out.println(result);
                    }

                    else if(distCD <= playerDiameter){
                        //***Colliding with another DPlayer***
                        thetaT = Math.atan2((ddY - dY), (ddX - dX));
                        if(dX < ddX){
                            thetaT -= 90.0;
                        }
                        else{
                            thetaT += 90.0;
                        }

                        nextX = dX + dS * Math.cos(thetaT);
                        nextY = dY + dS * Math.sin(thetaT);
                        tempD.setCenterX(nextX - checkRadius);
                        tempD.setCenterY(nextY - checkRadius);
                        dMenOnField[i] = tempD;
                    }

                    else{
                        tempD.setCenterX(nextX - checkRadius);
                        tempD.setCenterY(nextY - checkRadius);
                        dMenOnField[i] = tempD;
                    }

                }

                //System.out.println("Current DPlayer speed: " + tempD.getSpeed());

                frameCount++;
            }
        }
        //System.out.println(frameCount);   //PRINT CHECK
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to set and display basic RUN play for the OFFENSE
    private void basicORun(){

        OPF.setSelectedIndex(0);

        defaultFieldCheck();

        WRStartTheta = 90.0;
        RBStartTheta = 90.0;
        QBStartTheta = -90.0;

        passO = false;
        oSpread = true;

        double px = simWidth/2.0 - 10.0;
        double py = simHeight/2.0 - 18.0;

        double block[][] = new double[OLBlock][2];
        double qbDrop[][] = new double[QBDropBackHandOff][2];
        double runR[][] = new double[100][2];
        double wrPath[][] = new double[midPassSteps][2];
        double blockTheta = 0.0;
        int routeDecision = 0;

        OLine center = new OLine(px, py - 3.0, cH, cW, cS, cSt, Color.BLUE, false, true, block, 1, blockTheta, OLBlock,false);
        OLine rG = new OLine(px + latSep, py, ogH, ogW, ogS, ogSt, Color.BLUE, false, false, block, 2, blockTheta, OLBlock,false);
        OLine rT1 = new OLine(px + 2*latSep, py , otH, otW, otS, otSt, Color.BLUE, false, false, block, 3, blockTheta, OLBlock, false);
        OLine rT2 = new OLine( px + 3*latSep, py + 15.0, olH, olW, olS, olSt, Color.BLUE, false, false, block, 4, blockTheta, OLBlock,false);
        OLine lG = new OLine(px - latSep, py, ogH, ogW, ogS, ogSt, Color.BLUE, false, false, block, 2, blockTheta, OLBlock,false);
        OLine lT1 = new OLine(px - 2*latSep, py, otH, otW, otS, otSt, Color.BLUE, false, false, block, 3, blockTheta, OLBlock,false);
        OLine lT2 = new OLine( px - 3*latSep, py + 15.0, olH, olW, olS, olSt, Color.BLUE, false, false, block, 4, blockTheta, OLBlock,false);

        QB qb = new QB(px, py + 20.0, qbH, qbW, qbS, qbSt, Color.GRAY, true, false, qbDrop, QBStartTheta, QBDropBackHandOff, 0.62, 50.0, 2.5, false);

        OBack rb = new OBack(px, py + 75.0, rbH, rbW, rbS, rbSt, Color.LIGHT_GRAY, true, false, runR, 1, RBStartTheta, runDepth/10, defaultCD,false, 1, true, false, false,dMenOnField[0]);

        Receiver rWR = new Receiver(px + 5*latSep, py + 2.0, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, wrPath,1, routeDecision, 90.0, midPassSteps, defaultCD,true, false, false, dMenOnField[0]);
        Receiver lWR = new Receiver( px - 5*latSep, py + 2.0, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, wrPath, 1, routeDecision, 90.0, midPassSteps, defaultCD,false, false, false, dMenOnField[0]);

        oMenOnField[0] = center;
        oMenOnField[1] = rG;
        oMenOnField[2] = rT1;
        oMenOnField[3] = rT2;
        oMenOnField[4] = lG;
        oMenOnField[5] = lT1;
        oMenOnField[6] = lT2;
        oMenOnField[7] = qb;
        oMenOnField[8] = rb;
        oMenOnField[9] = rWR;
        oMenOnField[10] = lWR;

        int i = 0;
        while(oMenOnField[i] != null){

            if(oMenOnField[i] instanceof QB){
                qbDrop = streakRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), QBDropBackHandOff, ((QB)oMenOnField[i]).getDropBackAngle());
                (oMenOnField[i]).setPath(qbDrop);
            }

            else if(oMenOnField[i] instanceof Receiver){

                int tempDepth;

                tempDepth = ((Receiver)oMenOnField[i]).getRD();

                wrPath = streakRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].speed, tempDepth, WRStartTheta);
                (oMenOnField[i]).setPath(wrPath);
            }
            i++;
        }

        if(runM){

            OPT.setSelectedIndex(2);

            (oMenOnField[8]).setPath(rbRoute(oMenOnField[8].getCenterX(), oMenOnField[8].getCenterY(), oMenOnField[8].speed, runDepth/10, ((OBack)oMenOnField[8]).getCD()/10));

            ((OLine)oMenOnField[0]).setBAngle(90.0);
            ((OLine)oMenOnField[1]).setBAngle(65.0);
            ((OLine)oMenOnField[2]).setBAngle(55.0);
            ((OLine)oMenOnField[3]).setBAngle(45.0);
            ((OLine)oMenOnField[4]).setBAngle(115.0);
            ((OLine)oMenOnField[5]).setBAngle(125.0);
            ((OLine)oMenOnField[6]).setBAngle(135.0);

            (oMenOnField[0]).setPath(streakRoute(oMenOnField[0].getCenterX(), oMenOnField[0].getCenterY(), oMenOnField[0].speed, OLBlock, ((OLine)oMenOnField[0]).getBAngle()));
            (oMenOnField[1]).setPath(streakRoute(oMenOnField[1].getCenterX(), oMenOnField[1].getCenterY(), oMenOnField[1].speed, OLBlock, ((OLine)oMenOnField[1]).getBAngle()));
            (oMenOnField[2]).setPath(streakRoute(oMenOnField[2].getCenterX(), oMenOnField[2].getCenterY(), oMenOnField[2].speed, OLBlock, ((OLine)oMenOnField[2]).getBAngle()));
            (oMenOnField[3]).setPath(streakRoute(oMenOnField[3].getCenterX(), oMenOnField[3].getCenterY(), oMenOnField[3].speed, OLBlock, ((OLine)oMenOnField[3]).getBAngle()));
            (oMenOnField[4]).setPath(streakRoute(oMenOnField[4].getCenterX(), oMenOnField[4].getCenterY(), oMenOnField[4].speed, OLBlock, ((OLine)oMenOnField[4]).getBAngle()));
            (oMenOnField[5]).setPath(streakRoute(oMenOnField[5].getCenterX(), oMenOnField[5].getCenterY(), oMenOnField[5].speed, OLBlock, ((OLine)oMenOnField[5]).getBAngle()));
            (oMenOnField[6]).setPath(streakRoute(oMenOnField[6].getCenterX(), oMenOnField[6].getCenterY(), oMenOnField[6].speed, OLBlock, ((OLine)oMenOnField[6]).getBAngle()));
        }

        else if(runL){

            OPT.setSelectedIndex(1);

            (oMenOnField[8]).setPath(rbRoute(oMenOnField[8].getCenterX(), oMenOnField[8].getCenterY(), oMenOnField[8].speed, runDepth/10, ((OBack)oMenOnField[8]).getCD()/10));

            ((OLine)oMenOnField[0]).setBAngle(60.0);
            ((OLine)oMenOnField[1]).setBAngle(55.0);
            ((OLine)oMenOnField[2]).setBAngle(55.0);
            ((OLine)oMenOnField[3]).setBAngle(45.0);
            ((OLine)oMenOnField[4]).setBAngle(70.0);
            ((OLine)oMenOnField[5]).setBAngle(85.0);
            ((OLine)oMenOnField[6]).setBAngle(135.0);

            (oMenOnField[0]).setPath(streakRoute(oMenOnField[0].getCenterX(), oMenOnField[0].getCenterY(), oMenOnField[0].speed, OLBlock, ((OLine)oMenOnField[0]).getBAngle()));
            (oMenOnField[1]).setPath(streakRoute(oMenOnField[1].getCenterX(), oMenOnField[1].getCenterY(), oMenOnField[1].speed, OLBlock, ((OLine)oMenOnField[1]).getBAngle()));
            (oMenOnField[2]).setPath(streakRoute(oMenOnField[2].getCenterX(), oMenOnField[2].getCenterY(), oMenOnField[2].speed, OLBlock, ((OLine)oMenOnField[2]).getBAngle()));
            (oMenOnField[3]).setPath(streakRoute(oMenOnField[3].getCenterX(), oMenOnField[3].getCenterY(), oMenOnField[3].speed, OLBlock, ((OLine)oMenOnField[3]).getBAngle()));
            (oMenOnField[4]).setPath(streakRoute(oMenOnField[4].getCenterX(), oMenOnField[4].getCenterY(), oMenOnField[4].speed, OLBlock, ((OLine)oMenOnField[4]).getBAngle()));
            (oMenOnField[5]).setPath(streakRoute(oMenOnField[5].getCenterX(), oMenOnField[5].getCenterY(), oMenOnField[5].speed, OLBlock, ((OLine)oMenOnField[5]).getBAngle()));
            (oMenOnField[6]).setPath(streakRoute(oMenOnField[6].getCenterX(), oMenOnField[6].getCenterY(), oMenOnField[6].speed, OLBlock, ((OLine)oMenOnField[6]).getBAngle()));
        }


        else if(this.runR){

            OPT.setSelectedIndex(3);

            (oMenOnField[8]).setPath(rbRoute(oMenOnField[8].getCenterX(), oMenOnField[8].getCenterY(), oMenOnField[8].speed, runDepth/10, ((OBack)oMenOnField[8]).getCD()/10));

            ((OLine)oMenOnField[0]).setBAngle(120.0);
            ((OLine)oMenOnField[1]).setBAngle(110.0);
            ((OLine)oMenOnField[2]).setBAngle(95.0);
            ((OLine)oMenOnField[3]).setBAngle(45.0);
            ((OLine)oMenOnField[4]).setBAngle(125.0);
            ((OLine)oMenOnField[5]).setBAngle(125.0);
            ((OLine)oMenOnField[6]).setBAngle(135.0);

            (oMenOnField[0]).setPath(streakRoute(oMenOnField[0].getCenterX(), oMenOnField[0].getCenterY(), oMenOnField[0].speed, OLBlock, ((OLine)oMenOnField[0]).getBAngle()));
            (oMenOnField[1]).setPath(streakRoute(oMenOnField[1].getCenterX(), oMenOnField[1].getCenterY(), oMenOnField[1].speed, OLBlock, ((OLine)oMenOnField[1]).getBAngle()));
            (oMenOnField[2]).setPath(streakRoute(oMenOnField[2].getCenterX(), oMenOnField[2].getCenterY(), oMenOnField[2].speed, OLBlock, ((OLine)oMenOnField[2]).getBAngle()));
            (oMenOnField[3]).setPath(streakRoute(oMenOnField[3].getCenterX(), oMenOnField[3].getCenterY(), oMenOnField[3].speed, OLBlock, ((OLine)oMenOnField[3]).getBAngle()));
            (oMenOnField[4]).setPath(streakRoute(oMenOnField[4].getCenterX(), oMenOnField[4].getCenterY(), oMenOnField[4].speed, OLBlock, ((OLine)oMenOnField[4]).getBAngle()));
            (oMenOnField[5]).setPath(streakRoute(oMenOnField[5].getCenterX(), oMenOnField[5].getCenterY(), oMenOnField[5].speed, OLBlock, ((OLine)oMenOnField[5]).getBAngle()));
            (oMenOnField[6]).setPath(streakRoute(oMenOnField[6].getCenterX(), oMenOnField[6].getCenterY(), oMenOnField[6].speed, OLBlock, ((OLine)oMenOnField[6]).getBAngle()));
        }

        updateAttributeFields();
        initializeDAssignments();
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to set and display basic PASS play for the OFFENSE
    private void basicOPass(){

        OPT.setSelectedIndex(0);
        OPF.setSelectedIndex(0);

        defaultFieldCheck();

        passO = true;
        oSpread = true;

        WRStartTheta = 90.0;
        RBStartTheta = 0.0;
        QBStartTheta = -90.0;

        double px = simWidth/2.0 - 10.0;
        double py = simHeight/2.0 - 18.0;

        double block[][] = new double[OLBlock][2];
        double qbDrop[][] = new double[QBDropBackHandOff][2];
        double runR[][] = new double[midPassSteps][2];
        double wrPath[][] = new double[midPassSteps][2];
        double blockTheta = 90.0;

        OLine center = new OLine(px, py + 20.0, cH, cW, cS, cSt, Color.BLUE, false, true, block, 1, blockTheta, OLBlock,true);
        OLine rG = new OLine(px + latSep, py + 25.0, ogH, ogW, ogS, ogSt, Color.BLUE, false, false, block, 2, blockTheta, OLBlock,true);
        OLine rT = new OLine(px + 2*latSep, py + 32.0, otH, otW, otS, otSt, Color.BLUE, false, false, block, 3, blockTheta, OLBlock,true);
        OLine lG = new OLine(px - latSep, py + 25.0, ogH, ogW, ogS, ogSt, Color.BLUE, false, false, block, 2, blockTheta, OLBlock,true);
        OLine lT = new OLine(px - 2*latSep, py + 32.0, otH, otW, otS, otSt, Color.BLUE, false, false, block, 3, blockTheta, OLBlock,true);

        QB qb = new QB(px, py + 90.0, qbH, qbW, qbS, qbSt, Color.GRAY, true, false, qbDrop, QBStartTheta, QBDropBackHandOff, 0.62, 50.0,2.5,true);

        OBack rb = new OBack(px - latSep, py + 110.0, rbH, rbW, rbS, rbSt, Color.LIGHT_GRAY, true, false, runR,1, 180.0, midPassSteps, defaultCD, true, 6,true,false, false, dMenOnField[0]);

        Receiver rTE1 = new Receiver(px + 4*latSep, py + 40.0, teH, teW, teS, teSt, Color.DARK_GRAY, true, false, wrPath, 2, 4, 90.0, midPassSteps, defaultCD,true, false, false, dMenOnField[0]);
        Receiver rWR2 = new Receiver(px + 5*latSep, py + 25.0, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, wrPath, 1, 2, 90.0, midPassSteps, defaultCD,true, true, false, dMenOnField[0]);
        Receiver lTE1 = new Receiver(px - 4*latSep, py + 40.0, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, wrPath, 2, 3, 90.0, shortPassSteps, defaultCD,false, false, false, dMenOnField[0]);
        Receiver lWR2 = new Receiver(px - 5*latSep, py + 25.0, wrH, wrW, wrS, wrSt, Color.DARK_GRAY, true, false, wrPath, 1, 1, 90.0, midPassSteps, defaultCD,false, true, false, dMenOnField[0]);

        oMenOnField[0] = center;
        oMenOnField[1] = rG;
        oMenOnField[2] = rT;
        oMenOnField[3] = lG;
        oMenOnField[4] = lT;
        oMenOnField[5] = qb;
        oMenOnField[6] = rb;
        oMenOnField[7] = rTE1;
        oMenOnField[8] = rWR2;
        oMenOnField[9] = lTE1;
        oMenOnField[10] = lWR2;

        int i = 0;
        while(oMenOnField[i] != null){
            if(oMenOnField[i] instanceof Receiver){
                updateRoutes(oMenOnField, i);
            }

            else if(oMenOnField[i] instanceof OBack){
                updateRoutes(oMenOnField, i);
            }

            i++;
        }

        updateAttributeFields();
        initializeDAssignments();
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to set and display basic RUN play for the DEFENSE
    private void basicDRun(){

        defaultFieldCheck();

        dSpread = true;
        passD = false;

        double px = simWidth/2.0 - 10.0;
        double py = simHeight/2.0 - 48.0;

        OPlayer tempMan = oMenOnField[0];

        DLine ng = new DLine(px, py, ntdtH, ntdtW, ntdtS, ntdtSt, false, Color.RED, 1, tempMan);
        DLine rDT = new DLine(px + latSep, py, ntdtH, ntdtW, ntdtS, ntdtSt, false, Color.RED, 2, tempMan);
        DLine rDE = new DLine(px + 2*latSep, py, deH, deW, deS, deSt, false, Color.RED, 3, tempMan);
        DLine lDT = new DLine(px - latSep, py, ntdtH, ntdtW, ntdtS, ntdtSt, false, Color.RED, 2, tempMan);
        DLine lDE = new DLine(px - 2*latSep, py, deH, deW, deS, deSt, false, Color.RED, 3, tempMan);

        LineBacks mLB = new LineBacks(px, py - 45.0, imlbH, imlbW, imlbS, imlbSt, false, Color.ORANGE, 2, tempMan, false, false, zoneCoverageRadius);
        LineBacks rOLB = new LineBacks(px + 1.5*latSep, py - 40.0, olbH, olbW, olbS, olbSt, false, Color.ORANGE, 3, tempMan, false, false, zoneCoverageRadius);
        LineBacks lOLB = new LineBacks(px - 1.5*latSep, py - 40.0, olbH, olbW, olbS, olbSt, false, Color.ORANGE, 3, tempMan, false, false, zoneCoverageRadius);

        DBacks rCB = new DBacks(px + 5*latSep, py, cbH, cbW, cbS, cbSt, false, Color.YELLOW, 2, tempMan, true, false, zoneCoverageRadius);
        DBacks lCB = new DBacks(px - 5*latSep, py, cbH, cbW, cbS, cbSt, false, Color.YELLOW, 2, tempMan, true, false, zoneCoverageRadius);
        DBacks safety = new DBacks(px, py - 100.0, sH, sW, sS, sSt, false, Color.YELLOW, 5, tempMan, true, false, zoneCoverageRadius);

        dMenOnField[0] = ng;
        dMenOnField[1] = rDT;
        dMenOnField[2] = rDE;
        dMenOnField[3] = lDT;
        dMenOnField[4] = lDE;
        dMenOnField[5] = mLB;
        dMenOnField[6] = rOLB;
        dMenOnField[7] = lOLB;
        dMenOnField[8] = rCB;
        dMenOnField[9] = lCB;
        dMenOnField[10] = safety;

        for(int i = 0; oMenOnField[i] != null; i++){
            if(oMenOnField[i] instanceof Receiver){
                ((Receiver)oMenOnField[i]).setBlockTarget(dMenOnField[i]);
            }
            else if(oMenOnField[i] instanceof OBack){
                ((OBack)oMenOnField[i]).setBlockTarget(dMenOnField[i]);
            }
        }

        updateAttributeFields();
        initializeDAssignments();
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to set and display basic PASS play for the DEFENSE
    private void basicDPass(){

        defaultFieldCheck();

        dSpread = true;
        passD = true;

        double px = simWidth/2.0 - 10.0;
        double py = simHeight/2.0 - 48.0;

        OPlayer tempMan = oMenOnField[0];

        DLine rDT = new DLine(px + 0.5*latSep, py, ntdtH, ntdtW, ntdtS, ntdtSt, false, Color.RED, 3, tempMan);
        DLine rDE = new DLine(px + 1.5*latSep, py, deH, deW, deS, deSt, false, Color.RED, 4, tempMan);
        DLine lDT = new DLine(px - 0.5*latSep, py, ntdtH, ntdtW, ntdtS, ntdtSt, false, Color.RED, 3, tempMan);
        DLine lDE = new DLine(px - 1.5*latSep, py, deH, deW, deS, deSt, false, Color.RED, 4, tempMan);

        LineBacks mLB = new LineBacks(px, py - 45.0, imlbH, imlbW, imlbS, imlbSt, false, Color.ORANGE, 2, tempMan, true, true, zoneCoverageRadius);
        LineBacks rOLB = new LineBacks(px + 2.5*latSep, py - 40.0, olbH, olbW, olbS, olbSt, false, Color.ORANGE, 3, tempMan, true, true, zoneCoverageRadius);
        LineBacks lOLB = new LineBacks(px - 2.5*latSep, py - 40.0, olbH, olbW, olbS, olbSt, false,  Color.ORANGE, 3, tempMan, true, true, zoneCoverageRadius);

        DBacks rCB = new DBacks(px + 5*latSep, py, cbH, cbW, cbS, cbSt, false, Color.YELLOW, 2, tempMan, true, false, zoneCoverageRadius);
        DBacks lCB = new DBacks(px - 5*latSep, py, cbH, cbW, cbS, cbSt, false, Color.YELLOW, 2, tempMan, true, false, zoneCoverageRadius);
        DBacks sS = new DBacks(px + 1.5*latSep, py - 100.0, ssH, ssW, ssS, ssSt, false, Color.YELLOW, 5, tempMan, true, true, zoneCoverageRadius);
        DBacks fS = new DBacks(px - 1.5*latSep, py - 100.0, fsH, fsW, fsS, fsSt, false, Color.YELLOW, 4, tempMan, true, true, zoneCoverageRadius);

        dMenOnField[0] = rDT;
        dMenOnField[1] = rDE;
        dMenOnField[2] = lDT;
        dMenOnField[3] = lDE;
        dMenOnField[4] = mLB;
        dMenOnField[5] = rOLB;
        dMenOnField[6] = lOLB;
        dMenOnField[7] = rCB;
        dMenOnField[8] = lCB;
        dMenOnField[9] = sS;
        dMenOnField[10] = fS;

        for(int i = 0; oMenOnField[i] != null; i++){
            if(oMenOnField[i] instanceof Receiver){
                ((Receiver)oMenOnField[i]).setBlockTarget(dMenOnField[i]);
            }
            else if(oMenOnField[i] instanceof OBack){
                ((OBack)oMenOnField[i]).setBlockTarget(dMenOnField[i]);
            }
        }

        if(oSpread) {
            initializeDAssignments();
        }
        updateAttributeFields();
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void updateRoutes(OPlayer[] oMenOnField, int i){

        double[][] wrPath = null;
        int tempDepth = 0;
        int tempCDepth = 0;
        int tempInd = 0;
        boolean tempROL = false;
        boolean tempIOO = false;
        double tempTheta = 0.0;

        boolean okToProceed = true;

        if(oMenOnField[i] instanceof Receiver) {
            tempDepth = ((Receiver) oMenOnField[i]).getRD();
            tempCDepth = ((Receiver) oMenOnField[i]).getCD();
            tempInd = ((Receiver) oMenOnField[i]).getRouteInd();
            tempROL = ((Receiver) oMenOnField[i]).isOnBallROL();
            tempIOO = ((Receiver) oMenOnField[i]).isRouteIOO();
            tempTheta = ((Receiver)oMenOnField[i]).getRAngle();

            okToProceed = true;
        }
        else if(oMenOnField[i] instanceof OBack){

            tempDepth = ((OBack) oMenOnField[i]).getRD();
            tempCDepth = ((OBack) oMenOnField[i]).getCD();

            if (((OBack)oMenOnField[i]).isPassCatching()) {
                tempInd = ((OBack) oMenOnField[i]).getRouteInd();
                tempROL = ((OBack) oMenOnField[i]).isOnBallROL();
                tempIOO = ((OBack) oMenOnField[i]).isRouteIOO();
                tempTheta = ((OBack) oMenOnField[i]).getRA();

                okToProceed = true;
            }
            else{
                tempDepth = tempDepth/10;
                tempCDepth = tempCDepth/10;
                okToProceed = false;
            }
        }
        else if(oMenOnField[i] instanceof OLine && (!((OLine)oMenOnField[i]).isProtecting())){
            tempDepth = ((OLine) oMenOnField[i]).getBDepth();
            tempInd = 0;
            tempTheta = ((OLine) oMenOnField[i]).getBAngle();

            okToProceed = true;
        }

        else if(oMenOnField[i] instanceof QB && (!((QB)oMenOnField[i]).isPassing())){
            tempDepth = ((QB) oMenOnField[i]).getDropBackDepth();
            tempInd = 0;
            tempTheta = ((QB) oMenOnField[i]).getDropBackAngle();

            okToProceed = true;
        }

        if(okToProceed) {
            switch (tempInd) {
                case 0:
                    wrPath = streakRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempTheta);
                    break;
                case 1:
                    wrPath = cornerPostRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth, tempIOO, tempROL, tempTheta);
                    break;
                case 2:
                    wrPath = inOutRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth, tempIOO, tempROL, tempTheta);
                    break;
                case 3:
                    wrPath = slantRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth, tempROL, tempTheta);
                    break;
                case 4:
                    wrPath = curlComebackRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth, tempROL, tempIOO, tempTheta);
                    break;
                case 5:
                    wrPath = fadeRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempROL, tempTheta);
                    break;
                case 6:
                    wrPath = wheelRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth, tempROL, tempTheta);
                    break;
            }
        }

        else{
            wrPath = rbRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].getSpeed(), tempDepth, tempCDepth);
        }

        (oMenOnField[i]).setPath(wrPath);

    }

    //------------------------------------------------------------------------------------------------------------------

//    private void updateRBRoutePass(OPlayer[] oMenOnField, int i){
//
//        double runR[][];
//        int tempDepth = ((OBack)oMenOnField[i]).getRD() / 10;
//        int tempCDepth = ((OBack)oMenOnField[i]).getCD() / 10;
//
//        ((OBack)oMenOnField[i]).setRA(180.0);
//        double tempRA = ((OBack)oMenOnField[i]).getRA();
//
//        if(((OBack)oMenOnField[i]).isOnBallROL()){
//            RBStartTheta = 180.0;
//        }
//
//        else if(!((OBack)oMenOnField[i]).isOnBallROL()){
//            RBStartTheta = 0;
//        }
//
//        runR = wheelRoute(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), oMenOnField[i].speed, tempDepth, tempCDepth, ((OBack)oMenOnField[i]).isOnBallROL(), tempRA);
//        (oMenOnField[i]).setPath(runR);
//    }

    //------------------------------------------------------------------------------------------------------------------

//*********************************GOT RID OF THIS FOR NOW BECAUSE OF REDUNDANCY****************************************

    //METHOD TO DETERMINE THE DISPLAYED RUN PATH FOR OFFENSIVE RUNNING PLAY SPREADS
    //Condensed: 10/20/2018
    private double[][] rbRoute(double x, double y, double speed, int step, int cross){

        double lastX, lastY;
        double newX = 0.0;
        double newY = 0.0;

        double adjS = ((1.0 - speed)) * speedMult;
        int adjSt = (int)( (step) / speedMult );
        int adjCSt = (int)( (cross) / speedMult );

        int rbSteps = adjSt + adjCSt;

        double[][] returnRoute = new double[rbSteps][2];

        double postX, postY, cornerLX, cornerLY, cornerRX, cornerRY;
        postY = cornerLX = cornerLY = cornerRY = 0.0;
        postX = simWidth/2.0;
        cornerRX = simWidth;

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        double thetaInc;
        double markerTheta = 0.0;
        double theta = 0.0;

        for(int i = 1; i < rbSteps; i++){

            lastX = returnRoute[i - 1][0];
            lastY = returnRoute[i - 1][1];

            if(i < adjCSt){

                //Determine bounding orientation based on which play distinction it is (Up Middle, To Left, To Right).
                //******Should alter this later to make more dynamic and responsive to user manipulating just the
                //run angle of the rb player object******
                if(runM){
                    if(x + playerDiameter/2.0 < simWidth/2.0) {
                        theta = 70.0 * (Math.PI / 180.0);
                    }
                    else {
                        theta = 110.0 * (Math.PI/ 180.0);
                    }
                }
                else if (runL){
                    theta = 140.0 * (Math.PI / 180.0);
                }
                else if (runR){
                    theta = 40.0 * (Math.PI / 180.0);
                }

                newX = lastX + adjS * Math.cos(theta);
                newY = lastY - adjS * Math.sin(theta);
            }

            else{
                if(runM){
                    markerTheta = Math.atan2((postY - newY), (postX - newX));
                }
                else if(runL){
                    markerTheta = Math.atan2((cornerLY - newY), (cornerLX - newX));
                }
                else if(runR){
                    markerTheta = Math.atan2((cornerRY - newY), (cornerRX - newX));
                }

                thetaInc = (markerTheta + theta)/(adjSt);

                theta -= thetaInc;

                newX = lastX + adjS * Math.cos(theta);
                newY = lastY - adjS * Math.sin(theta);
            }

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;

        }
        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL STREAK ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/20/2018
    private double[][] streakRoute(double x, double y, double speed, int step, double startAngle){
        double lastX, lastY, newX, newY, adjS;
        int adjSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );

        //CHANGED PARAMETER TO MAKE SAME AS OL BLOCK/QB HAND OFF METHOD
        double theta = startAngle * (Math.PI/180.0);
        //double theta = Math.PI/2.0;

        double[][] returnRoute = new double[adjSt][2];

        //Set initial path coordinates.
        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        //FOR all subsequent path steps, determine next new coordinate based on imported player object speed and
        //direction angle attributes.
        for(int i = 1; i < returnRoute.length; i++){
            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            newX = lastX + adjS * Math.cos(theta);
            newY = lastY - adjS * Math.sin(theta);

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }

        //Return (most likely to store) determined route.
        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL CORNER/POST ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/19/2018
    private double[][] cornerPostRoute(double x, double y, double speed, int step, int cross, boolean inIOO, boolean inROL, double rA){

        double lastX, lastY, newX, newY, adjS;
        int adjSt, adjCSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );
        adjCSt = (int)( (cross) / speedMult );

        newX = newY = 0.0;


        double startTheta = rA * (Math.PI/180.0);

        int cPSteps = adjSt + adjCSt;
        double[][] returnRoute = new double[cPSteps][2];

        double postX = simWidth/2.0;
        double postY = 0.0;

        double cornerLX = 0.0;
        double cornerLY = 0.0;

        double cornerRX = simWidth;
        double cornerRY = 0.0;


        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;
        for(i = 1; i < returnRoute.length; i++){

            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            if(i < returnRoute.length - adjCSt) {
                newX = lastX + adjS * Math.cos(startTheta);
                newY = lastY - adjS * Math.sin(startTheta);
            }

            else{

                double markerTheta;

                if(inIOO){
                    markerTheta = Math.atan2((postY - newY), (postX - newX));
                }
                else{
                    if(inROL){
                        markerTheta = Math.atan2((cornerRY - newY), (cornerRX - newX));
                    }
                    else{
                        markerTheta = Math.atan2((cornerLY - newY), (cornerLX - newX));
                    }
                }

                newX = lastX + adjS * Math.cos(markerTheta);
                newY = lastY + adjS * Math.sin(markerTheta);
            }

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }

        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL IN/OUT ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/19/2018
    private double[][] inOutRoute(double x, double y, double speed, int step, int cross, boolean inIOO, boolean rOrL, double rA){

        double lastX, lastY, newX, newY, adjS;
        int adjSt, adjCSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );
        adjCSt = (int)( (cross) / speedMult );

        double startTheta = rA * (Math.PI/180.0);

        int iOSteps = adjSt + adjCSt;


        double[][] returnRoute = new double[iOSteps][2];

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;
        for(i = 1; i < returnRoute.length; i++){

            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            if(i < returnRoute.length - adjCSt) {
                newX = lastX + adjS * Math.cos(startTheta);
                newY = lastY - adjS * Math.sin(startTheta);
            }

            else{
                double markerTheta;
                if(inIOO){
                    if(rOrL){
                        markerTheta = 180.0 * (Math.PI/180.0);
                    }
                    else{
                        markerTheta = 0.0;
                    }
                    newX = lastX + adjS * Math.cos(markerTheta);
                    newY = lastY - adjS * Math.sin(markerTheta);
                }
                else{
                    if(rOrL){
                        markerTheta = 0.0;

                    }
                    else{
                        markerTheta = 180.0 * (Math.PI/180.0);
                    }
                    newX = lastX + adjS * Math.cos(markerTheta);
                    newY = lastY + adjS * Math.sin(markerTheta);
                }
            }

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }

        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL SLANT ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/19/2018
    private double[][] slantRoute(double x, double y, double speed, int step, int cross, boolean inROL, double rA){

        double lastX, lastY, newX, newY, adjS;
        int adjSt, adjCSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );
        adjCSt = (int)( (cross) / speedMult );

        double startTheta = rA * (Math.PI/180.0);

        int slantSteps = adjSt + adjCSt;

        double[][] returnRoute = new double[slantSteps][2];

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;
        for(i = 1; i < returnRoute.length; i++){

            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            if(i < returnRoute.length - adjCSt) {
                newX = lastX + adjS * Math.cos(startTheta);
                newY = lastY - adjS * Math.sin(startTheta);
            }

            else{
                double markerTheta;
                if(inROL){
                    markerTheta = 135.0 * (Math.PI/180.0);
                }
                else {
                    markerTheta = 45.0 * (Math.PI/180.0);
                }
                newX = lastX + adjS * Math.cos(markerTheta);
                newY = lastY - adjS * Math.sin(markerTheta);
            }

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }

        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL CURL/COME-BACK ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/19/2018
    private double[][] curlComebackRoute(double x, double y, double speed, int step, int cross, boolean inROL, boolean inIOO, double rA){

        double lastX, lastY, newX, newY, adjS;
        int adjSt, adjCSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );
        adjCSt = (int)( (cross) / speedMult );

        double startTheta = rA * (Math.PI/180.0);

        int ccSteps = adjSt + adjCSt;

        double[][] returnRoute = new double[ccSteps][2];

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;
        for(i = 1; i < returnRoute.length; i++){

            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            if(i < returnRoute.length - adjCSt) {
                newX = lastX + adjS * Math.cos(startTheta);
                newY = lastY - adjS * Math.sin(startTheta);
            }

            else{
                double markerTheta;
                if(inROL){
                    if(inIOO){
                        markerTheta = 120.0 * (Math.PI/180.0);
                    }
                    else {
                        markerTheta = 60.0 * (Math.PI / 180.0);
                    }
                }
                else{
                    if(inIOO) {
                        markerTheta = 60.0 * (Math.PI / 180.0);
                    }
                    else{
                        markerTheta = 120.0 * (Math.PI/180.0);
                    }
                }
                newX = lastX + adjS * Math.cos(markerTheta);
                newY = lastY + adjS * Math.sin(markerTheta);
            }

            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }
        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL FADE ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/20/2018
    private double[][] fadeRoute(double x, double y, double speed, int step, boolean rOrL, double startTheta){

        double lastX, lastY, adjS;
        int adjSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );

        double newX = 0.0;
        double newY = 0.0;

        double sTheta = startTheta * (Math.PI/180.0);
        double theta = sTheta;
        double diffTheta = Math.PI/4.0;
        double thetaInc = diffTheta/1000.0;

        double cornerLX, cornerLY, cornerRX, cornerRY;
        cornerLX = cornerLY = cornerRY = 0.0;
        cornerRX = simWidth;

        int sec1 = (int)(adjSt * 0.125);
        int sec2 = (int)(adjSt * 0.375);
        int sec3 = (int)(adjSt * 0.550);
        int fadeSteps = adjSt + (int)(adjSt * 0.500);

        double[][] returnRoute = new double[fadeSteps][2];

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;

        for(i = 1; i < fadeSteps; i++){

            lastX = returnRoute[i-1][0];
            lastY = returnRoute[i-1][1];

            if(i < sec1) {
                newX = lastX + adjS * Math.cos(sTheta);
                newY = lastY - adjS * Math.sin(sTheta);
            }

            else {
                if (i < sec2) {
                    if (rOrL) {
                        theta -= thetaInc;
                    } else {
                        theta += thetaInc;
                    }
                }

                else if (i < sec3) {
                    if (rOrL) {
                        theta += thetaInc;
                    } else {
                        theta -= thetaInc;
                    }
                }

                else {
                    double markerTheta;
                    if (rOrL) {
                        markerTheta = Math.atan2((cornerRY - newY), (cornerRX - newX));
                        thetaInc = (markerTheta / 4.0) / (fadeSteps - sec3);
                        theta -= thetaInc;
                    } else {
                        markerTheta = Math.atan2((cornerLY - newY), (cornerLX - newX));
                        thetaInc = (markerTheta / 4.0) / (fadeSteps - sec3);
                        theta += thetaInc;
                    }
                }
                newX = lastX + adjS * Math.cos(theta);
                newY = lastY - adjS * Math.sin(theta);
            }
            returnRoute[i][0] = newX;
            returnRoute[i][1] = newY;
        }
        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD TO DETERMINE GENERAL WHEEL ROUTE TO DISPLAY IN OFFENSIVE PASS PLAY SPREAD
    //Updated: 10/19/2018

    //****************Still some issues when a Receiver is designated wheel route, fix later****************************

    private double[][] wheelRoute(double x, double y, double speed, int step, int cross, boolean inROL, double startTheta){

        double lastX, lastY, newX, newY, adjS;
        int adjSt, adjCSt;

        adjS = ((1.0 - speed)/10.0) * speedMult;
        adjSt = (int)( (step) / speedMult );
        adjCSt = (int)( (cross) / speedMult );

        double sTheta = startTheta * (Math.PI/180.0);
        double eTheta;
        double theta = sTheta;

        double diffTheta = Math.PI/2.0;

        double thetaInc = diffTheta/adjSt;

        int wheelSteps = adjSt + adjCSt;


        double[][] returnRoute = new double[wheelSteps][2];

        returnRoute[0][0] = x + playerDiameter/2.0;
        returnRoute[0][1] = y + playerDiameter/2.0;

        int i;

        //if(inROL){

            if(inROL) {
                eTheta = sTheta - diffTheta;
            }
            else{
                eTheta = sTheta + diffTheta;
            }

            for(i = 1; i < returnRoute.length; i++){

                lastX = returnRoute[i-1][0];
                lastY = returnRoute[i-1][1];

                if(i < returnRoute.length - adjCSt) {
                    if (inROL) {
                        theta -= thetaInc;
                    } else {
                        theta += thetaInc;
                    }
                    newX = lastX + adjS * Math.cos(theta);
                    newY = lastY - adjS * Math.sin(theta);
                }
                else {
                    newX = lastX + adjS * Math.cos(eTheta);
                    newY = lastY - adjS * Math.sin(eTheta);
                }

                returnRoute[i][0] = newX;
                returnRoute[i][1] = newY;
            }
        return returnRoute;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to calculate the distance between two coordinates (i.e., distance between two players, distance between
    //mouse click and player objects, etc.)
    //Updated and condensed: 10/19/2018
    private double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt( Math.pow((x2 - x1),2) + Math.pow((y2 - y1),2));
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to update the position of the selected OPlayer when dragged
    private void updateOPlayer(int index, MouseEvent e){
        double mX = e.getX() - 10.0 - checkRadius - panelWidth;
        double mY = e.getY() - 56.0 - checkRadius;

        oMenOnField[index].setCenterX(mX);
        oMenOnField[index].setCenterY(mY);

        double tempX = oMenOnField[index].getCenterX();
        double tempY = oMenOnField[index].getCenterY();

        double tempSpeed = oMenOnField[index].getSpeed();
        int tempDepth;
        int tempCDepth;
        double tempAngle = 0.0;

        double[][] newR = null;

        if(oMenOnField[index] instanceof QB){

            tempDepth = ((QB)oMenOnField[index]).getDropBackDepth();
            tempAngle = ((QB)oMenOnField[index]).getDropBackAngle();

            double[][] newHO = streakRoute(tempX, tempY, tempSpeed,tempDepth,tempAngle);
            oMenOnField[index].setPath(newHO);
        }

        else if(oMenOnField[index] instanceof Receiver){

            tempDepth = ((Receiver)oMenOnField[index]).getRD();
            tempCDepth = ((Receiver)oMenOnField[index]).getCD();
            tempAngle = ((Receiver)oMenOnField[index]).getRAngle();

            switch(((Receiver)oMenOnField[index]).getRouteInd()){
                case 0:
                    newR = streakRoute(tempX, tempY, tempSpeed, tempDepth, tempAngle);
                    break;
                case 1:
                    newR = cornerPostRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((Receiver)oMenOnField[index]).isRouteIOO(), ((Receiver)oMenOnField[index]).isOnBallROL(), tempAngle);
                    break;
                case 2:
                    newR = inOutRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((Receiver)oMenOnField[index]).isRouteIOO(), ((Receiver)oMenOnField[index]).isOnBallROL(), tempAngle);
                    break;
                case 3:
                    newR = slantRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((Receiver)oMenOnField[index]).isOnBallROL(), tempAngle);
                    break;
                case 4:
                    newR = curlComebackRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((Receiver)oMenOnField[index]).isOnBallROL(), ((Receiver)oMenOnField[index]).isRouteIOO(), tempAngle);
                    break;
                case 5:
                    newR = fadeRoute(tempX, tempY, tempSpeed, tempDepth, ((Receiver)oMenOnField[index]).isOnBallROL(), tempAngle);
                    break;
                case 6:
                    newR = wheelRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((Receiver)oMenOnField[index]).isOnBallROL(), tempAngle);
                    break;
            }
            oMenOnField[index].setPath(newR);
        }

        else if(oMenOnField[index] instanceof OBack){
            //if(passO) {

            tempAngle = ((OBack)oMenOnField[index]).getRA();
            tempDepth = ((OBack) oMenOnField[index]).getRD();
            tempCDepth = ((OBack) oMenOnField[index]).getCD();

                if((((OBack)oMenOnField[index]).isPassCatching())) {



                    switch (((OBack) oMenOnField[index]).getRouteInd()) {
                        case 0:
//                            ((OBack) oMenOnField[index]).setRA(WRStartTheta);
//                            tempAngle = ((OBack) oMenOnField[index]).getRA();
                            newR = streakRoute(tempX, tempY, tempSpeed, tempDepth, tempAngle);
                            break;
                        case 1:
                            newR = cornerPostRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((OBack) oMenOnField[index]).isRouteIOO(), ((OBack) oMenOnField[index]).isOnBallROL(), tempAngle);
                            break;
                        case 2:
                            newR = inOutRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((OBack) oMenOnField[index]).isRouteIOO(), ((OBack) oMenOnField[index]).isOnBallROL(), tempAngle);
                            break;
                        case 3:
                            newR = slantRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((OBack) oMenOnField[index]).isOnBallROL(), tempAngle);
                            break;
                        case 4:
                            newR = curlComebackRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((OBack) oMenOnField[index]).isOnBallROL(), ((OBack) oMenOnField[index]).isRouteIOO(), tempAngle);
                            break;
                        case 5:
//                            ((OBack) oMenOnField[index]).setRA(WRStartTheta);
//                            tempAngle = ((OBack) oMenOnField[index]).getRA();
                            newR = fadeRoute(tempX, tempY, tempSpeed, tempDepth, ((OBack) oMenOnField[index]).isOnBallROL(), tempAngle);
                            break;
                        case 6:
                            //((OBack) oMenOnField[index]).setRA(RBStartTheta);
                            //tempAngle = ((OBack) oMenOnField[index]).getRA();
                            newR = wheelRoute(tempX, tempY, tempSpeed, tempDepth, tempCDepth, ((OBack) oMenOnField[index]).isOnBallROL(), tempAngle);
                            break;
                    }
                }
                (oMenOnField[index]).setPath(newR);
            //}

            //else {

                if((!((OBack)oMenOnField[index]).isPassCatching())) {
                    tempDepth = ((OBack) oMenOnField[index]).getRD();
                    newR = rbRoute(oMenOnField[index].getCenterX(), oMenOnField[index].getCenterY(), tempSpeed, tempDepth, ((OBack) oMenOnField[index]).getCD());
                }

                (oMenOnField[index]).setPath(newR);
            //}
        }

        else if(oMenOnField[index] instanceof OLine){
            if(!((OLine)oMenOnField[index]).isProtecting()){

                tempDepth = ((OLine)oMenOnField[index]).getBDepth();
                tempAngle = ((OLine)oMenOnField[index]).getBAngle();

                double[][] newBlock = streakRoute(oMenOnField[index].getCenterX(), oMenOnField[index].getCenterY(), tempSpeed, tempDepth, tempAngle);

                (oMenOnField[index]).setPath(newBlock);
            }
        }

        //PRINT CHECK
        System.out.println("Updated to x = " + oMenOnField[index].getCenterX() + ", y = " + oMenOnField[index].getCenterY());

        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to update the position of the selected DPlayer when dragged
    private void updateDPlayer(int index, MouseEvent e){

        double mX = e.getX() - 10.0 - checkRadius - panelWidth;
        double mY = e.getY() - 56.0 - checkRadius;

        dMenOnField[index].setCenterX(mX);
        dMenOnField[index].setCenterY(mY);

        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //OVERALL METHOD TO DISPLAY THE PLAY SPREAD
    private void displaySpread(double playerRadius){

        String tempS = "";
        int tempInd;
        int hasBallInd = 0;

        //Setting Anti-Aliasing Hints
        playG2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        //Start with a clear, wiped field
        wipeField(baseImage);

        //IF toggled to display route paths and coverages
        if(routesDisplayed) {
            displayPaths();
        }

        //Reset stroke so that the displayPaths stroke setting doesn't interfere with Player icons
        playG2D.setStroke(new BasicStroke(0));

        //If there is an offensive spread
        int i = 0;
        if(oSpread) {
            //While the current index in the OPlayer array is not null (end of the personnel spread)
            while (oMenOnField[i] != null) {

                if(oMenOnField[i] instanceof QB){
                    tempS = "QB " + (i+1);
                }
                else if(oMenOnField[i] instanceof OBack){
                    tempInd = ((OBack)oMenOnField[i]).getBI();
                    switch (tempInd){
                        case 1:
                            tempS = "RB " + (i+1);
                            break;
                        case 2:
                            tempS = "FB " + (i+1);
                            break;
                    }
                }
                else if(oMenOnField[i] instanceof OLine){
                    tempInd = ((OLine)oMenOnField[i]).getLinemenInd();
                    switch (tempInd){
                        case 1:
                            tempS = " C " + (i+1);
                            break;
                        case 2:
                            tempS = "OG " + (i+1);
                            break;
                        case 3:
                            tempS = "OT " + (i+1);
                            break;
                        case 4:
                            tempS = "OL " + (i+1);
                            break;
                    }
                }
                else {
                    tempInd = ((Receiver)oMenOnField[i]).getReceiverInd();
                    switch (tempInd){
                        case 1:
                            tempS = "WR " + (i+1);
                            break;
                        case 2:
                            tempS = "TE " + (i+1);
                            break;
                    }
                }

                if(oMenOnField[i].playerHasBall()){
                    hasBallInd = i;
                }

                double tempL = tempS.length();
                double xInc = (playerDiameter/5.0) - (tempL/2.0);
                //Set the current color to the stored color of the player object
                playG2D.setColor(oMenOnField[i].getPlayerCol());
                //Draw a Double Ellipse with the same height and with "diameters"
                playG2D.draw(new Ellipse2D.Double(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), playerRadius, playerRadius));
                //Fill the player ellipse
                playG2D.fill(new Ellipse2D.Double(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), playerRadius, playerRadius));

                playG2D.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
                playG2D.setColor(Color.WHITE);
                playG2D.drawString(tempS,(float)(oMenOnField[i].getCenterX()+xInc), (float)(oMenOnField[i].getCenterY()+playerRadius*0.67));

                i++;
            }
        }

        //If there is also a defensive spread, check also that the same current index in the DPlayer array is not null and draw the
        //current DPlayer object at the current index as well
        i = 0;
        if(dSpread){
            while(dMenOnField[i] != null){

                if(dMenOnField[i] instanceof LineBacks){
                    tempInd = ((LineBacks)dMenOnField[i]).getLineBackInd();
                    switch (tempInd){
                        case 1:
                            tempS = "LB " + (i+1);
                            break;
                        case 2:
                            tempS = "MLB" + (i+1);
                            break;
                        case 3:
                            tempS = "OLB" + (i+1);
                            break;
                    }
                }
                else if(dMenOnField[i] instanceof DLine){
                    tempInd = ((DLine)dMenOnField[i]).getdDLineInd();
                    switch (tempInd){
                        case 1:
                            tempS = "DL " + (i+1);
                            break;
                        case 2:
                            tempS = "DT " + (i+1);
                            break;
                        case 3:
                            tempS = "DT " + (i+1);
                            break;
                        case 4:
                            tempS = "DE " + (i+1);
                            break;
                    }
                }

                else {
                    tempInd = ((DBacks)dMenOnField[i]).getDBackInd();
                    switch (tempInd){
                        case 1:
                            tempS = "DB " + (i+1);
                            break;
                        case 2:
                            tempS = "CB " + (i+1);
                            break;
                        case 3:
                            tempS = " S " + (i+1);
                            break;
                        case 4:
                            tempS = "FS " + (i+1);
                            break;
                        case 5:
                            tempS = "SS " + (i+1);
                            break;
                    }
                }

                double tempL = tempS.length();
                double xInc = (playerDiameter/5.0) - (tempL/2.0);

                playG2D.setColor(dMenOnField[i].getPlayerCol());
                playG2D.draw(new Ellipse2D.Double(dMenOnField[i].getCenterX(), dMenOnField[i].getCenterY(), playerRadius, playerRadius));
                playG2D.fill(new Ellipse2D.Double(dMenOnField[i].getCenterX(), dMenOnField[i].getCenterY(), playerRadius, playerRadius));

                playG2D.setFont(new Font("Haettenschweiler", Font.PLAIN, 16));
                playG2D.setColor(Color.BLACK);
                playG2D.drawString(tempS,(float)(dMenOnField[i].getCenterX()+xInc), (float)(dMenOnField[i].getCenterY()+playerRadius*0.67));

                i++;
            }
        }

        if(mousePressedInsidePlayer){
            if(oClicked){
                playG2D.setColor(Color.GREEN);
                playG2D.setStroke(new BasicStroke(3));
                playG2D.draw(new Ellipse2D.Double(oMenOnField[currIndex].getCenterX() - 1.5, oMenOnField[currIndex].getCenterY() - 1.5, playerRadius + 3.0, playerRadius + 3.0));
            }

            else if(!oClicked && dSpread){
                playG2D.setColor(Color.GREEN);
                playG2D.setStroke(new BasicStroke(3));
                playG2D.draw(new Ellipse2D.Double(dMenOnField[currIndex].getCenterX() - 1.5, dMenOnField[currIndex].getCenterY() - 1.5, playerRadius + 3.0, playerRadius + 3.0));
            }
        }

        if(oSpread){
            playG2D.setColor(Color.ORANGE);
            playG2D.setStroke(new BasicStroke(3));
            playG2D.draw(new Ellipse2D.Double(oMenOnField[hasBallInd].getCenterX() - 3.0, oMenOnField[hasBallInd].getCenterY() - 3.0, playerRadius + 4.5, playerRadius + 4.5));
        }

        mainPanel.repaint();
        UIPanel1.repaint();
        UIPanel2.repaint();
        simControlPanel.repaint();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void displayPaths(){
        if(oSpread){
            int i = 0;

            playG2D.setStroke(new BasicStroke(2));

            while (oMenOnField[i] != null){
                if(oMenOnField[i] instanceof QB && !((QB)oMenOnField[i]).isPassing()){
                    int j = 0;
                    int k = 1;
                    while(k < ((QB)oMenOnField[i]).getPath().length){
                        playG2D.setColor(Color.WHITE);
                        //playG2D.setStroke(new BasicStroke(2));
                        playG2D.draw(new Line2D.Double(((QB)oMenOnField[i]).path[j][0], ((QB)oMenOnField[i]).path[j][1], ((QB)oMenOnField[i]).path[k][0], ((QB)oMenOnField[i]).path[k][1]));
                        j++;
                        k++;
                    }
                }

                else if(oMenOnField[i] instanceof Receiver){
                    int j = 0;
                    int k = 1;
                    if(dSpread && ((Receiver)oMenOnField[i]).isBlocking()){
                        playG2D.setColor(Color.WHITE);
                        //playG2D.setStroke(new BasicStroke(2));
                        playG2D.draw(new Line2D.Double(oMenOnField[i].getCenterX() + playerDiameter/2.0, oMenOnField[i].getCenterY() + playerDiameter/2.0, ((Receiver)oMenOnField[i]).getBlockTarget().getCenterX() + playerDiameter/2.0, ((Receiver)oMenOnField[i]).getBlockTarget().getCenterY() + playerDiameter/2.0));
                    }
                    else {
                        while (k < ((Receiver) oMenOnField[i]).getPath().length) {
                            playG2D.setColor(Color.WHITE);
                            //playG2D.setStroke(new BasicStroke(2));
                            playG2D.draw(new Line2D.Double(((Receiver) oMenOnField[i]).path[j][0], ((Receiver) oMenOnField[i]).path[j][1], ((Receiver) oMenOnField[i]).path[k][0], ((Receiver) oMenOnField[i]).path[k][1]));
                            j++;
                            k++;
                        }
                    }
                }

                else if(oMenOnField[i] instanceof OBack){
                    int j = 0;
                    int k = 1;
                    if(dSpread && ((OBack)oMenOnField[i]).isBlocking()){
                        playG2D.setColor(Color.WHITE);
                        //playG2D.setStroke(new BasicStroke(2));
                        playG2D.draw(new Line2D.Double(oMenOnField[i].getCenterX() + playerDiameter/2.0, oMenOnField[i].getCenterY() + playerDiameter/2.0, ((OBack)oMenOnField[i]).getBT().getCenterX() + playerDiameter/2.0, ((OBack)oMenOnField[i]).getBT().getCenterY() + playerDiameter/2.0));
                    }
                    else {
                        while (k < ((OBack) oMenOnField[i]).getPath().length) {
                            playG2D.setColor(Color.WHITE);
                            //playG2D.setStroke(new BasicStroke(2));
                            playG2D.draw(new Line2D.Double(((OBack) oMenOnField[i]).path[j][0], ((OBack) oMenOnField[i]).path[j][1], ((OBack) oMenOnField[i]).path[k][0], ((OBack) oMenOnField[i]).path[k][1]));
                            j++;
                            k++;
                        }
                    }

                }

                else if(oMenOnField[i] instanceof OLine && dSpread) {
                    if (((OLine) oMenOnField[i]).isProtecting()) {
                        int closestDIndex = OCheckCloseD(oMenOnField[i]);

                        double radius = distance(oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY(), dMenOnField[closestDIndex].getCenterX(), dMenOnField[closestDIndex].getCenterY());
                        playG2D.setColor(new Color(0f, 1f, 1f, 0.5f));
                        playG2D.draw(new Ellipse2D.Double(oMenOnField[i].getCenterX() - radius + playerDiameter / 2.0, oMenOnField[i].getCenterY() - radius + playerDiameter / 2.0, 2 * radius, 2 * radius));
                        playG2D.fill(new Ellipse2D.Double(oMenOnField[i].getCenterX() - radius + playerDiameter / 2.0, oMenOnField[i].getCenterY() - radius + playerDiameter / 2.0, 2 * radius, 2 * radius));

                        playG2D.setColor(Color.WHITE);
                        //playG2D.setStroke(new BasicStroke(2));
                        playG2D.draw(new Line2D.Double(oMenOnField[i].getCenterX() + playerDiameter / 2.0, oMenOnField[i].getCenterY() + playerDiameter / 2.0, dMenOnField[closestDIndex].getCenterX() + playerDiameter / 2.0, dMenOnField[closestDIndex].getCenterY() + playerDiameter / 2.0));
                    }

                    else if(!((OLine) oMenOnField[i]).isProtecting()){
                        int j = 0;
                        int k = 1;
                        while(k < ((OLine)oMenOnField[i]).getPath().length){
                            playG2D.setColor(Color.WHITE);
                            //playG2D.setStroke(new BasicStroke(2));
                            playG2D.draw(new Line2D.Double(((OLine)oMenOnField[i]).path[j][0], ((OLine)oMenOnField[i]).path[j][1], ((OLine)oMenOnField[i]).path[k][0], ((OLine)oMenOnField[i]).path[k][1]));
                            j++;
                            k++;
                        }
                    }
                }

                else if(oMenOnField[i] instanceof OLine && !((OLine) oMenOnField[i]).isProtecting()){
                    int j = 0;
                    int k = 1;
                    while(k < ((OLine)oMenOnField[i]).getPath().length){
                        playG2D.setColor(Color.WHITE);
                        //playG2D.setStroke(new BasicStroke(2));
                        playG2D.draw(new Line2D.Double(((OLine)oMenOnField[i]).path[j][0], ((OLine)oMenOnField[i]).path[j][1], ((OLine)oMenOnField[i]).path[k][0], ((OLine)oMenOnField[i]).path[k][1]));
                        j++;
                        k++;
                    }
                }
                i++;
            }
        }

        if(dSpread){
            int i = 0;
            while(dMenOnField[i] != null){

                if(dMenOnField[i] instanceof DLine && oSpread){
                    playG2D.setColor(Color.RED);
                    //playG2D.setStroke(new BasicStroke(2));
                    playG2D.draw(new Line2D.Double(dMenOnField[i].getCenterX() + playerDiameter/2.0, dMenOnField[i].getCenterY() + playerDiameter/2.0, ((DLine)dMenOnField[i]).getTargetMan().getCenterX() + playerDiameter/2.0, ((DLine)dMenOnField[i]).getTargetMan().getCenterY() + playerDiameter/2.0));
                }

                else if(dMenOnField[i] instanceof LineBacks && !((LineBacks)dMenOnField[i]).isZone() && oSpread){
                    playG2D.setColor(Color.RED);
                    //playG2D.setStroke(new BasicStroke(2));
                    playG2D.draw(new Line2D.Double(dMenOnField[i].getCenterX() + playerDiameter/2.0, dMenOnField[i].getCenterY() + playerDiameter/2.0, ((LineBacks)dMenOnField[i]).getTargetMan().getCenterX() + playerDiameter/2.0, ((LineBacks)dMenOnField[i]).getTargetMan().getCenterY() + playerDiameter/2.0));
                }

                else if(dMenOnField[i] instanceof DBacks && !((DBacks)dMenOnField[i]).isZone() && oSpread){
                    playG2D.setColor(Color.RED);
                    //playG2D.setStroke(new BasicStroke(2));
                    playG2D.draw(new Line2D.Double(dMenOnField[i].getCenterX() + playerDiameter/2.0, dMenOnField[i].getCenterY() + playerDiameter/2.0, ((DBacks)dMenOnField[i]).getTargetMan().getCenterX() + playerDiameter/2.0, ((DBacks)dMenOnField[i]).getTargetMan().getCenterY() + playerDiameter/2.0));
                }

                /*else if(dMenOnField[i] instanceof S && !((S)dMenOnField[i]).isZone() && oSpread){
                    playG2D.setColor(Color.RED);
                    playG2D.setStroke(new BasicStroke(2));
                    playG2D.draw(new Line2D.Double(dMenOnField[i].getCenterX() + playerRadius/2.0, dMenOnField[i].getCenterY() + playerRadius/2.0, ((S)dMenOnField[i]).getTargetMan().getCenterX() + playerRadius/2.0, ((S)dMenOnField[i]).getTargetMan().getCenterY() + playerRadius/2.0));
                }*/

                else if((dMenOnField[i] instanceof LineBacks && ((LineBacks)dMenOnField[i]).isZone()) || (dMenOnField[i] instanceof DBacks && ((DBacks)dMenOnField[i]).isZone())){
                    double tempZCR;
                    if(dMenOnField[i] instanceof LineBacks){
                        tempZCR = ((LineBacks)dMenOnField[i]).getZoneCR();
                    }
                    else{
                        tempZCR = ((DBacks)dMenOnField[i]).getZoneCR();
                    }

                    playG2D.setColor(new Color(1f,0f,1f,0.25f));
                    playG2D.draw(new Ellipse2D.Double(dMenOnField[i].getCenterX() - tempZCR + playerDiameter/2.0, dMenOnField[i].getCenterY() - tempZCR + playerDiameter/2.0, 2*tempZCR, 2*tempZCR));
                    playG2D.fill(new Ellipse2D.Double(dMenOnField[i].getCenterX() - tempZCR + playerDiameter/2.0, dMenOnField[i].getCenterY() - tempZCR + playerDiameter/2.0, 2*tempZCR, 2*tempZCR));
                }

                i++;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private int OCheckCloseD(OPlayer oP){

        //Temp double variables: 1)stored minimum distance, 2)current distance calculated
        double minD, currD;

        //Get the x and y coordinates from the passed in OPlayer checking around
        double oX = oP.getCenterX() + checkRadius;
        double oY = oP.getCenterY() + checkRadius;

        //Current minimum distance is between the passed in OPlayer and the first checked DPlayer
        minD = distance(oX, oY, dMenOnField[0].getCenterX() + checkRadius, dMenOnField[0].getCenterY() + checkRadius);
        int retI = 0;   //Designate the first index 0 as the current index of the closest DPlayer

        int i = 1;      //Iterator i
        //While the current index of the DPlayer in the D Men On Field array is not null
        while(dMenOnField[i] != null){

            //Calculate and store the current calculated distance between OPlayer and current index
            currD = distance(oX, oY, dMenOnField[i].getCenterX() + checkRadius, dMenOnField[i].getCenterY() + checkRadius);

            if(currD < minD){
                minD = currD;
                retI = i;
            }

            i++;
        }
        return retI;
    }

    //------------------------------------------------------------------------------------------------------------------

    private int OCheckCloseO(OPlayer oP, int skipI){

        double minD, currD;
        int retI;

        double oX = oP.getCenterX();
        double oY = oP.getCenterY();

        if(skipI == 0){
            minD = distance(oX, oY, oMenOnField[1].getCenterX(), oMenOnField[1].getCenterY());
            retI = 1;
        }
        else {
            minD = distance(oX, oY, oMenOnField[0].getCenterX(), oMenOnField[0].getCenterY());
            retI = 0;
        }

        int i = retI + 1;
        while(oMenOnField[i] != null){

            if(i != skipI) {
                currD = distance(oX, oY, oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY());
                if (currD < minD) {
                    minD = currD;
                    retI = i;
                }
            }
            else{}

            i++;
        }
        return retI;
    }

    //------------------------------------------------------------------------------------------------------------------

    private int DCheckCloseO(DPlayer dP){
        double minD, currD;

        double dX = dP.getCenterX();
        double dY = dP.getCenterY();

        minD = distance(dX, dY, oMenOnField[0].getCenterX(), oMenOnField[0].getCenterY());
        int retI = 0;

        int i = 1;
        while(oMenOnField[i] != null){
            currD = distance(dX, dY, oMenOnField[i].getCenterX(), oMenOnField[i].getCenterY());
            if(currD < minD){
                minD = currD;
                retI = i;
            }
            i++;
        }
        return retI;
    }

    //------------------------------------------------------------------------------------------------------------------

    private int DCheckCloseD(DPlayer dP, int skipI){
        double minD, currD;
        int index;

        double dX = dP.getCenterX();
        double dY = dP.getCenterY();

        if(skipI == 0) {
            minD = distance(dX, dY, dMenOnField[1].getCenterX(), dMenOnField[1].getCenterY());
            index = 1;
        }
        else{
            minD = distance(dX, dY, dMenOnField[0].getCenterX(), dMenOnField[0].getCenterY());
            index = 0;
        }

        int i = index + 1;
        while(oMenOnField[i] != null){
            if(i != skipI) {
                currD = distance(dX, dY, dMenOnField[i].getCenterX(), dMenOnField[i].getCenterY());
                if (currD < minD) {
                    minD = currD;
                    index = i;
                }
            }
            else{}
            i++;
        }
        return index;
    }

    //------------------------------------------------------------------------------------------------------------------

    private int collisionMatchup(OPlayer oP, DPlayer dP){

        double oH, oW, oSp, oSt, dH, dW, dSp, dSt;

        oH = oP.getHeight();
        oW = oP.getWeight();
        oSp = oP.getSpeed();
        oSt = oP.getStrength();

        dH = dP.getHeight();
        dW = dP.getWeight();
        dSp = dP.getSpeed();
        dSt = dP.getStrength();

        double oMUW;
        //double dMUW;
        double hWeight, wWeight, spWeight, stWeight;

        //hWeight = wWeight = spWeight = stWeight = 0.25;

        //If the colliding OPlayer is a QB, give chance for QB to escape
        if(oP instanceof QB){
                spWeight = 0.40;
                stWeight = 0.30;
                hWeight = 0.15;
                wWeight = 0.15;
        }

        //If the colliding OPlayer is a Running Back type
        else if(oP instanceof OBack){

            //If they are blocking, then there is a collision matchup
            if(((OBack)oP).isBlocking()){
                spWeight = 0.10;
                stWeight = 0.45;
                hWeight = 0.15;
                wWeight = 0.30;
            }

            //Else not blocking
            else{

                //If, rather, the RB is designated pass-catching and running a route,
                //then the RB has precedent to run his route
                if(((OBack)oP).isPassCatching()){
                    return 1;
                }

                //Else if not pass-catching, is a ball carrier
                else{
                    //If Running and has possession of ball, give chance to evade like QB
                    if(oP.playerHasBall()){
                        spWeight = 0.40;
                        stWeight = 0.30;
                        hWeight = 0.15;
                        wWeight = 0.15;
                    }
                    //Treat collision as blocking
                    else{
                        spWeight = 0.10;
                        stWeight = 0.45;
                        hWeight = 0.15;
                        wWeight = 0.30;
                    }
                }
            }
        }

        else if(oP instanceof Receiver){
            //Receiver is either blocking or running a route
            if(((Receiver)oP).isBlocking()){
                spWeight = 0.10;
                stWeight = 0.40;
                hWeight = 0.20;
                wWeight = 0.30;
            }
            else{
                //Receiver has precedent to run route
                return 1;
            }
        }

        //Any opposing defender block with OLine is a collision matchup
        else{
            spWeight = 0.15;
            stWeight = 0.50;
            hWeight = 0.10;
            wWeight = 0.25;
        }

        double decision;
        int result;
        rng = new Random();

        oMUW = ((oH/(oH+dH)) * hWeight) + ((oW/(oW+dW)) * wWeight) + (((1.0 - oSp)/((1.0 - oSp)+(1.0 - dSp))) * spWeight) + ((oSt/(oSt+dSt)) * stWeight);

        //Might be totally redundant
        //dMUW = ((dH/(dH+oH)) * hWeight) + ((dW/(dW+oW)) * wWeight) + ((dSp/(dSp+oSp)) * spWeight) + ((dSt/(dSt+oSt)) * stWeight);
        //OR...
        //dMUW = 1.0 - oMUW;

        decision = rng.nextDouble();
        if(decision <= oMUW){
            result = 1;
        }
        else{
            result = -1;
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    private void initializeDAssignments(){
        double px = simWidth/2.0 - 10.0;

        if(oSpread && dSpread) {
            if (passD && passO) {
                int i = 0;
                while (oMenOnField[i] != null) {

                    if (oMenOnField[i] instanceof QB) {
                        ((LineBacks) dMenOnField[4]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[0]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[1]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[2]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[3]).setTargetMan(oMenOnField[i]);
                    }

                    else if (oMenOnField[i] instanceof Receiver) {
                        if (oMenOnField[i].getCenterX() > (px + 140.0)) {
                            ((DBacks) dMenOnField[7]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() > px && oMenOnField[i].getCenterX() < (px + 140.0)) {
                            ((LineBacks) dMenOnField[5]).setTargetMan(oMenOnField[i]);
                            ((DBacks)dMenOnField[9]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() < px && oMenOnField[i].getCenterX() > (px - 140.0)) {
                            ((LineBacks) dMenOnField[6]).setTargetMan(oMenOnField[i]);
                            ((DBacks)dMenOnField[10]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() < (px - 140.0)) {
                            ((DBacks) dMenOnField[8]).setTargetMan(oMenOnField[i]);
                        }
                    }
                    i++;
                }
            }

            else if (passD && !passO) {
                int i = 0;
                while (oMenOnField[i] != null) {

                    if (oMenOnField[i] instanceof QB) {
                        ((LineBacks) dMenOnField[5]).setTargetMan(oMenOnField[i]);
                        ((LineBacks) dMenOnField[6]).setTargetMan(oMenOnField[i]);
                        ((DBacks) dMenOnField[9]).setTargetMan(oMenOnField[i]);
                        ((DBacks) dMenOnField[10]).setTargetMan(oMenOnField[i]);
                    }

                    else if (oMenOnField[i] instanceof OBack) {
                        ((LineBacks) dMenOnField[4]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[0]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[1]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[2]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[3]).setTargetMan(oMenOnField[i]);
                    }

                    else if (oMenOnField[i] instanceof Receiver) {
                        if (oMenOnField[i].getCenterX() > px) {
                            ((DBacks) dMenOnField[7]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() < px) {
                            ((DBacks) dMenOnField[8]).setTargetMan(oMenOnField[i]);
                        }
                    }
                    i++;
                }
            }

            else if (!passD && passO){
                int i = 0;
                while (oMenOnField[i] != null) {

                    if (oMenOnField[i] instanceof QB) {
                        ((DLine) dMenOnField[0]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[1]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[2]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[3]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[4]).setTargetMan(oMenOnField[i]);
                    }

                    else if (oMenOnField[i] instanceof Receiver) {
                        if (oMenOnField[i].getCenterX() > px + 140.0) {
                            ((DBacks) dMenOnField[8]).setTargetMan(oMenOnField[i]);
                        }

                        else if(oMenOnField[i].getCenterX() > px && oMenOnField[i].getCenterX() < px + 140.0){
                            ((LineBacks)dMenOnField[6]).setTargetMan(oMenOnField[i]);
                        }

                        else if(oMenOnField[i].getCenterX() < px && oMenOnField[i].getCenterX() > px - 140.0){
                            ((LineBacks)dMenOnField[7]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() < px - 140.0) {
                            ((DBacks) dMenOnField[9]).setTargetMan(oMenOnField[i]);
                        }
                    }

                    else if (oMenOnField[i] instanceof OBack){
                        ((LineBacks)dMenOnField[5]).setTargetMan(oMenOnField[i]);
                        ((DBacks)dMenOnField[10]).setTargetMan(oMenOnField[i]);
                    }
                    i++;
                }
            }

            else if(!passD && !passO){
                int i = 0;
                while (oMenOnField[i] != null) {

                    if (oMenOnField[i] instanceof QB) {
                        ((DLine) dMenOnField[0]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[1]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[2]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[3]).setTargetMan(oMenOnField[i]);
                        ((DLine) dMenOnField[4]).setTargetMan(oMenOnField[i]);
                    }

                    else if(oMenOnField[i] instanceof OBack){
                        ((LineBacks) dMenOnField[5]).setZone(false);
                        ((LineBacks) dMenOnField[6]).setZone(false);
                        ((LineBacks) dMenOnField[7]).setZone(false);
                        ((LineBacks) dMenOnField[5]).setTargetMan(oMenOnField[i]);
                        ((LineBacks) dMenOnField[6]).setTargetMan(oMenOnField[i]);
                        ((LineBacks) dMenOnField[7]).setTargetMan(oMenOnField[i]);
                        ((DBacks)dMenOnField[10]).setTargetMan(oMenOnField[i]);
                    }

                    else if (oMenOnField[i] instanceof Receiver) {
                        if (oMenOnField[i].getCenterX() > px) {
                            ((DBacks) dMenOnField[8]).setTargetMan(oMenOnField[i]);
                        }

                        else if (oMenOnField[i].getCenterX() < px) {
                            ((DBacks) dMenOnField[9]).setTargetMan(oMenOnField[i]);
                        }
                    }
                    i++;
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to "wipe" the field for paint updates
    private void wipeField(BufferedImage im){
        baseImage = im;
        playImage = baseImage;
        mainPanel.setImage(playImage);

    }

    //------------------------------------------------------------------------------------------------------------------
    //Method to check and see if the generic field is the current displayed field
    private void defaultFieldCheck(){
        if(defaultField){
            baseImage = checkFile(fieldSource);
            playImage = baseImage;
            mainPanel.setImage(playImage);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private void loadField(){
        fieldSource = getFile();
        baseImage = checkFile(fieldSource);
        playImage = baseImage;
        mainPanel.setImage(playImage);
        displaySpread(playerDiameter);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Open file selected by the user.
    private File getFile()
    {
        File file = null;

        if (chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
        {
            file = chooser.getSelectedFile();
        }

        return file;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Display specified file in frame.
    private BufferedImage checkFile( File file )
    {
        BufferedImage test = null;
        boolean isIm = false;

        while(isIm == false) {

            try {
                //If selected file is read as an image, store the source image as a created BufferedImage, display it
                //exit the loop, and return the generated source BufferedImage.
                test = ImageIO.read(file);
                isIm = true;
            }

            //IOException catch.
            catch (IOException exception) {
                JOptionPane.showMessageDialog(this, exception);
            }
        }
        return test;
    }

    //------------------------------------------------------------------------------------------------------------------
    //Display BufferedImage Method
    public void displayBufferImage( BufferedImage image )
    {
        this.setContentPane( new JScrollPane( new JLabel( new ImageIcon( image ) ) ) );

        this.validate();
    }

    public void savePlaySpread(BufferedImage im){
        String fileName = nameInput();
        //Create new File outputFile with desired fileName
        File outputFile = new File(fileName + ".png");
        //Try-catch block to write to save the file or to throw an exception
        try {
            javax.imageio.ImageIO.write(im, "png", outputFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ImageFrame.this,
                    "Error saving file",
                    "oops!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nameInput() {

        //Prompt user to input the number of steps per stem in directed random walk plant.
        String name = JOptionPane.showInputDialog("Input the desired name for the saved file: ");

        return name;
    }
}
