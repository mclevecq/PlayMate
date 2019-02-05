//Michael Levecque
//Senior Project supporting class file
//Originally Created: Fall 2017

import java.awt.*;
import java.awt.Color;

//**********************************************************************************************************************
//Offensive Player Parent Class
//Defines a custom player object that is on the offensive side of the line of scrimmage.
//Using the OPlayer as the parent class, there a several offense skill position classes that extend the OPlayer class.
//***CONSIDER CONCOLIDATING OPLAYER AND DPLAYER CLASSES INTO ONE PLAYER CLASS***
        //***Could use an identifying characteristic such as:
        //***boolean isOffense (true if offensive player, defensive player otherwise), OR
        //***int offenseOrDefenseIndex (ODI) = 1 (offense) or 2 (defensive)

public class OPlayer {

    //Position of player object on display panel pixel space.
    //Clearly this has to be a universal variable for all visual player objects (both O and D)
    public double centerX;
    public double centerY;

    /***CONSIDER THE INCLUSION OF HEIGHT AND WEIGHT ATTRIBUTES
     * VERY IMPORTANT ATTRIBUTES BUT NOT SURE YET HOW OR IF IT CAN FIT INTO THE PROBABILITY ALGORITHM FOR THE
     * SIMULATION ENGINE.*/
    public int height;  //Total in inches
    public int weight;  //Total in pounds


    //Denotes speed of player.
    //Varying speed of different player types should, in theory, enhance realism of simulation/animation engine.
    public double speed;

    //Denotes strength of player.
    //Same theorized contribution as the speed variable.
    public double strength;

    //------------------------------------------------------------------------------------------------------------------
    //Color designation for player type
    //*Currently all player objects are visually represented by a colored circle on the display panel.
    //**Consider different visual reps of players, maybe even icons or other graphics...
    public Color playerCol;
    //------------------------------------------------------------------------------------------------------------------

    //Boolean to denote whether offensive player is an eligible receiver.
    //Supports integration of the official rules of Football.
    public boolean isEligibleReceiver;

    //Boolean to designate if offensive player has ball
    //*Originally believed this is what sets offense and defense apart, but when running simulation and determining
    //outcomes such as fumbles and interceptions, this could plead the case for ONE PARENT PLAYER CLASS
    public boolean hasBall;

    //***ADDED TO SATISFY PREVIOUS TECHNIQUE OF PREDETERMINING A PATH FOR OFFENSIVE PLAYERS***
    public double[][] path;
    //****************************************************************************************

    //Generic Constructor
    public OPlayer(){
        //***Consider writing a more useful generic constructor
    }

    //Constructor
    public OPlayer(double cX, double cY, int h, int w, double sp, double st, Color c, boolean eR, boolean b, double[][] p){
        centerX = cX;
        centerY = cY;
        height = h;
        weight = w;
        speed = sp;
        strength = st;
        playerCol = c;
        isEligibleReceiver = eR;
        hasBall = b;
        //***ADDED TO SATISFY PREVIOUS TECHNIQUE OF PREDETERMINING A PATH FOR OFFENSIVE PLAYERS***
        path = p;
        //****************************************************************************************
    }

    //Getter Methods - Generic
    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public double getSpeed() {
        return speed;
    }

    public double getStrength() {
        return strength;
    }

    public Color getPlayerCol() {
        return playerCol;
    }

    public boolean isEligibleReceiver() {
        return isEligibleReceiver;
    }

    public boolean playerHasBall() {
        return hasBall;
    }

    //***ADDED TO SATISFY PREVIOUS TECHNIQUE OF PREDETERMINING A PATH FOR OFFENSIVE PLAYERS***
    public double[][] getPath() {
        return path;
    }
    //****************************************************************************************

    //Setter Methods - Generic
    public void setCenterX(double newX) {
        this.centerX = newX;
    }

    public void setCenterY(double newY) {
        this.centerY = newY;
    }

    public void setHeight(int newH) {
        this.height = newH;
    }

    public void setWeight(int newW) {
        this.weight = newW;
    }

    public void setSpeed(double newSp) {
        this.speed = newSp;
    }

    public void setStrength(double newSt) {
        this.strength = newSt;
    }

    public void setPlayerColor(Color newCol) {
        this.playerCol = newCol;
    }

    public void setEligibility(boolean e) {
        this.isEligibleReceiver = e;
    }

    public void setHasBall(boolean b) {
        this.hasBall = b;
    }

    //***ADDED TO SATISFY PREVIOUS TECHNIQUE OF PREDETERMINING A PATH FOR OFFENSIVE PLAYERS***
    public void setPath(double[][] newP) {
        this.path = newP;
    }
    //****************************************************************************************
}

//**********************************************************************************************************************
//----------------------------------------------------------------------------------------------------------------------
//Quarterback skill position child subclass of OPLayer parent class.
class QB extends OPlayer {

    public double dropBackAngle;        //Denotes at what angle the QB drops back once the C snaps the ball to him.
                                        //*CONSIDER DYNAMIC CHANGING IN THE INTERFACE

    public int dropBackDepth;

    public double compRate;             //Key stat that is followed for a QB is there completion percentage, both career
                                        //and current season.  This variable will interact with other pertinent ones in
                                        //the simulation to determine a completion.
                                        //*CONSIDER DYNAMIC CHANGING IN THE INTERFACE

    public double maxAR;                //Denotes the maximum accuracy range; the threshold before accuracy stops to
                                        //drop off.  Can be though of as arm strength.
                                        //*CONSIDER DYNAMIC CHANGING IN THE INTERFACE

    public double releaseTime;          //Denotes the time it will take for the quaterback player icon to "pass" the
                                        //ball during the simulation (can be ranomized from 0.5 - 5.0 seconds, or
                                        //possibly specifically set for play type like quick pass or hail mary where
                                        //time to throw varies respectively.
                                        //*CONSIDER DYNAMIC CHANGING IN THE INTERFACE

    public boolean isPassing;           //Boolean to denote whether the quarterback will be passing or not.


    //Key Constructor for QB skilled position.
    public QB(double cX, double cY, int h, int w, double sp, double st, Color col, boolean el, boolean ball, double[][] pa, double dbA, int dbD, double cR, double mAR, double rT, boolean p){
        super(cX, cY, h, w, sp, st, col, el, ball, pa);
        dropBackAngle = dbA;
        dropBackDepth = dbD;
        compRate = cR;
        maxAR = mAR;
        releaseTime = rT;
        isPassing = p;
    }

    //Getters
    public double getDropBackAngle() {
        return dropBackAngle;
    }

    public int getDropBackDepth(){
        return dropBackDepth;
    }

    public double getCompRate(){
        return compRate;
    }

    public double getMaxAR() {
        return maxAR;
    }

    public double getReleaseTime(){
        return releaseTime;
    }

    public boolean isPassing() {
        return isPassing;
    }

    //Setters
    public void setDropBackAngle(double newDBA) {
        this.dropBackAngle = newDBA;
    }

    public void setDropBackDepth(int newDBD){
        this.dropBackDepth = newDBD;
    }

    public void setCompRate(double newCR){
        this.compRate = newCR;
    }

    public void setMaxAR(double newMAR) {
        this.maxAR = newMAR;
    }

    public void setTimeOfRelease(double newRT){
        this.releaseTime = newRT;
    }

    public void setPassing(boolean p) {
        this.isPassing = p;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players in the offensive backfield.
//INCLUDES HALF-BACKS/RUNNING-BACKS (RBs) and FULL-BACKS (FBs)
class OBack extends OPlayer {

    public int backIndex;           //Denotes whether the player object is specifically a Half-Back (RUNNING BACK): 1,
                                    //OR a Full-Back: 2.

    public double rAngle;           //Designates the angle of the player's run path or ROUTE.

    public int rDepth;              //Designates the depth of the player's run path or ROUTE.
                                    //***IF USING RANDOM WALK ALGORITHM, CONSIDER CHANGING THIS TO INT AND MAKING IT
                                    //***A NUMBER OF STEPS BASE.

    public int cDepth;              //Designates additional number of steps added to route depth that will serve
                                    //as an additional cross section to certain routes.

    public boolean passCatching;    //Boolean to indicate if RB is designated to run a route.
                                    //TRUE - FOCUS ON ROUTES LIKE RECEIVER SUB-CLASS PLAYER OBJECT
                                    //FALSE - RUSHING

    public int routeInd;            //Same designation of routes.

    public boolean ballROL;         //RB on right or left side of ball.
    public boolean routeIOO;        //Route is directed toward IN field or OUT to sideline/out-of-bounds.

    public boolean isBlocker;       //Boolean to denote whether the back is serving as a blocker for the current play.

    public DPlayer blockTarget;     //Allows to set which defensive player the back is assigned to block.

    public OBack(double cX, double cY, int h, int w, double sp, double st, Color col, boolean el, boolean ball, double[][] pa, int bI, double rA, int rD, int cD, boolean pC, int rI, boolean rOl, boolean iOO, boolean iB, DPlayer bT){
        super(cX, cY, h, w, sp, st, col, el, ball, pa);
        backIndex = bI;
        rAngle = rA;
        rDepth = rD;
        cDepth = cD;
        passCatching = pC;
        routeInd = rI;
        ballROL = rOl;
        routeIOO = iOO;
        isBlocker = iB;
        blockTarget = bT;
    }

    //Getters
    public int getBI(){
        return backIndex;
    }

    public double getRA(){
        return rAngle;
    }

    public int getRD(){
        return rDepth;
    }

    public int getCD(){
        return cDepth;
    }

    public boolean isPassCatching() {
        return passCatching;
    }

    public int getRouteInd() {
        return routeInd;
    }

    public boolean isOnBallROL(){
        return ballROL;
    }

    public boolean isRouteIOO() {
        return routeIOO;
    }

    public boolean isBlocking(){
        return isBlocker;
    }

    public DPlayer getBT(){
        return blockTarget;
    }

    //Setters
    public void setBI(int newBI) {
        this.backIndex = newBI;
    }

    public void setRA(double newRA) {
        this.rAngle = newRA;
    }

    public void setRD(int newRD){
        this.rDepth = newRD;
    }

    public void setCD(int newCD) {
        this.cDepth = newCD;
    }

    public void setPassCatching(boolean newPC) {
        this.passCatching = newPC;
    }

    public void setRouteInd(int newRI){
        this.routeInd = newRI;
    }

    public void setBallROL(boolean newROL){
        this.ballROL = newROL;
    }

    public void setRouteIOO(boolean newIOO){
        this.routeIOO = newIOO;
    }

    public void setBlocking(boolean nB){
        this.isBlocker = nB;
    }

    public void setBlockTarget(DPlayer newBlock){
        this.blockTarget = newBlock;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players in the offensive receiver corp.
//INCLUDES WIDE RECEIVERS (WR), TIGHT ENDS (TE), AND OTHER PASS-CATCHING PERSONNEL/BACKS
class Receiver extends OPlayer {

    public int receiverInd;         //Same idea as back index above; 1: Wide Receiver (WR), 2: Tight End (TE)

    public double rAngle;           //Designates the angle of the player's run path or ROUTE.

    public int rDepth;           //Designates route based on number of steps in altered random walk algorithm.
                                    //***IF USING RANDOM WALK ALGORITHM, CONSIDER CHANGING THIS TO INT AND MAKING IT
                                    //***A NUMBER OF STEPS BASE.

    public int cDepth;           //Designates number of extra steps added on to depth steps to account for a cross step.

    public boolean ballROL;     //Boolean to indicate if the reciever player is lined up on the left or right of the
                                //set football at the line of scrimmage.

    public boolean routeIOO;    //Boolean to indicate whether the designated route goes IN (I) to the field OR (O) OUT
                                //(O) to the sideline/out-of-bounds.

    public int routeInd;            //Designates the type of route, either a preset or custom.
                                    // 0 - Streak
                                    // 1 - Post/Corner
                                    // 2 - In/Out
                                    // 3 - Slant
                                    // 4 - Curl/Comeback
                                    // 5 - Fade
                                    // 6 - Wheel
                                    //***... 7 - CUSTOM ROUTE --> SEPARATE METHOD TO DETERMINE CUSTOM ROUTE.

    public boolean isBlocker;       //Boolean to denote whether the back is serving as a blocker for the current play.

    public DPlayer blockTarget;     //Allows to set which defensive player the back is assigned to block.

    //Constructor
    public Receiver(double cX, double cY, int h, int w, double sp, double st, Color col, boolean el, boolean ball, double[][] pa, int rI, int roI, double rA, int rD, int cD, boolean rOl, boolean iOo, boolean iB, DPlayer bT){
        super(cX, cY, h, w, sp, st, col, el, ball, pa);
        receiverInd = rI;
        routeInd = roI;
        rAngle = rA;
        rDepth = rD;
        cDepth = cD;
        ballROL = rOl;
        routeIOO = iOo;
        isBlocker = iB;
        blockTarget = bT;
    }

    //Getters
    public int getReceiverInd() {
        return receiverInd;
    }

    public int getRouteInd(){
        return routeInd;
    }

    public double getRAngle() {
        return rAngle;
    }

    public int getRD() {
        return rDepth;
    }

    public int getCD() {
        return cDepth;
    }

    public boolean isOnBallROL() {
        return ballROL;
    }

    public boolean isRouteIOO() {
        return routeIOO;
    }

    public boolean isBlocking() {
        return isBlocker;
    }

    public DPlayer getBlockTarget(){
        return blockTarget;
    }

    //Setters
    public void setReceiverInd(int newRI){
        this.receiverInd = newRI;
    }

    public void setRouteInd(int newRoI) {
        this.routeInd = newRoI;
    }

    public void setRAngle(double newRA) {
        this.rAngle = newRA;
    }

    public void setRDepth(int newRD) {
        this.rDepth = newRD;
    }

    public void setCDepth(int newCD) {
        this.cDepth = newCD;
    }

    public void setBallROL(boolean newROL) {
        this.ballROL = newROL;
    }

    public void setRouteIOO(boolean newIOO){
        this.routeIOO = newIOO;
    }

    public void setBlocking(boolean newIB) {
        this.isBlocker = newIB;
    }

    public void setBlockTarget(DPlayer newBT) {
        this.blockTarget = newBT;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players in the offensive linemen set.
//INCLUDES CENTERS (C), OFFENSIVE GUARDS (OG, LG, RG), OFFENSIVE TACKLES (OT, LT, RT)
class OLine extends OPlayer {

    public int linemenInd;              //Designates which specific linemen position:
                                        //1 - Center (*WILL START EVERY PLAY WITH hasBall = TRUE*)
                                        //2 - Offensive Guards
                                        //3 - Offensive Tackles
                                        //4 - Generic OL

    public double bAngle;               //Block angle.

    public int bDepth;                  //Depth of block path (running plays)

    public boolean isProtecting;        //This boolean indicates if they are protecting QB in pass situations (i.e.,
                                        //creating a pocket to allow the QB to throw).

    //public boolean isPickingUpBlock;    //This will be important in pass-blocking, as some players will have specific
                                        //blocking assignments, but some personnel such as RBs are responsible for
                                        //picking up blocks as they come in.

    //public DPlayer blockAssign;         //Designates DPlayer object that the linemen is assigned to block.

    //Constructor
    public OLine(double cX, double cY, int h, int w, double sp, double st, Color col, boolean el, boolean ball, double[][] pa, int lI, double th, int bD, boolean iP){
        super(cX, cY, h, w, sp, st, col, el, ball, pa);
        linemenInd = lI;
        bAngle = th;
        bDepth = bD;
        isProtecting = iP;
        //isPickingUpBlock = iPUB;
        //blockAssign = bA;
    }

    //Getters
    public int getLinemenInd() {
        return linemenInd;
    }

    public double getBAngle() {
        return bAngle;
    }

    public int getBDepth() {
        return bDepth;
    }

    public boolean isProtecting() {
        return isProtecting;
    }

//    public boolean isPickingUpBlock() {
//        return isPickingUpBlock;
//    }

//    public DPlayer getBlockAssign(){
//        return blockAssign;
//    }

    //Setters
    public void setLinemenInd(int newLI) {
        this.linemenInd = newLI;
    }

    public void setBAngle(double newBAngle){
        this.bAngle = newBAngle;
    }

    public void setBDepth(int newBDepth){
        this.bDepth = newBDepth;
    }

    public void setProtecting(boolean newIP) {
        this.isProtecting = newIP;
    }

//    public void setPickingUpBlock(boolean newPUB){
//        this.isPickingUpBlock = newPUB;
//    }

//    public void setBlockAssign(DPlayer newBA){
//        this.blockAssign = newBA;
//    }
}