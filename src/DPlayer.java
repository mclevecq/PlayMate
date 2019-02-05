//Michael Levecque
//Senior Project supporting class file
//Originally Created: Fall 2017

import java.awt.*;
import java.awt.Color;

//**********************************************************************************************************************
//Defensive Player Parent Class
public class DPlayer {

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

    //This variable would be used primarily for the simulation engine to determine if the play resulted in a defensive
    //turnover (interception, fumble, etc.).
    public boolean hasBall;

    //Generic Constructor
    public DPlayer(){
        //***Consider writing a more useful generic constructor
    }

    //Constructor
    public DPlayer(double cX, double cY, int h, int w, double sp, double st, boolean hB, Color c){
        centerX = cX;
        centerY = cY;
        height = h;
        weight = w;
        speed = sp;
        strength = st;
        hasBall = hB;
        playerCol = c;
    }

    //Main Getters
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

    public boolean doesHaveBall(){
        return hasBall;
    }

    //Main Setters
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

    public void setSpeed(double newS) {
        this.speed = newS;
    }

    public void setStrength(double newSt){
        this.strength = newSt;
    }

    public void setPlayerCol(Color newC) {
        this.playerCol = newC;
    }

    public void setHasBall(boolean hasBall) {
        this.hasBall = hasBall;
    }
}

//**********************************************************************************************************************
//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players on the DEFENSIVE LINE
//INCLUDES GENERIC LINEMEN (DL), NOSE TACKLES (NT), DEFENSIVE TACKLES (DT), AND DEFENSIVE ENDS (DE)
class DLine extends DPlayer {

    public int dLineInd;    //Set which specific skill position on the defensive line the player object is:
                            //1 - Defensive Lineman (DL)
                            //2 - Nose Tackle (NT)
                            //3 - Defensive Tackle (DT)
                            //4 - Defensive End (DE)

    public OPlayer oMan;    //**VERY IMPORTANT**
                            // Assign the defensive lineman player to an offensive player that they are trying to tackle,
                            //in most cases defensive lineman are trying to tackle the quarterback or receiver in the
                            //backfield with the football on a pass play, or the running back on a run play.

    //Constructor
    public DLine(double cx, double cy, int h, int w, double s, double st, boolean hB, Color c, int DLI, OPlayer man){
        super(cx, cy, h, w, s, st, hB, c);
        dLineInd = DLI;
        oMan = man;
    }

    //Getters
    public int getdDLineInd() {
        return dLineInd;
    }

    public OPlayer getTargetMan() {
        return oMan;
    }

    //Setters
    public void setDLineInd(int newDLI) {
        this.dLineInd = newDLI;
    }

    public void setTargetMan(OPlayer newMan){
        this.oMan = newMan;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players in the set of DEFENSIVE LINE-BACKERS
//INCLUDES GENERIC LINEBACKERS (LB), MIDDLE LINEBACKERS (MLB), AND OUTSIDE LINEBACKERS (OLB)
class LineBacks extends DPlayer {

    public int lineBackInd;     //Designate which specific line backer skill position the linebacker is:
                                //1 - Line-Backer (LB)
                                //2 - Middle Line-Backer (MLB)
                                //3 - Outside Line-Backer (OLB)

    public OPlayer targetMan;   //Set the target offensive player object for the current linebacker player object

    public boolean inCoverage;  //There is a difference between pursuing a target offensive player to tackle and
                                //being in coverage for that target player.  This variable is designed to manage that
                                //but my prove to be unnecessary later.

    public boolean zone;        //Designate whether or not the linebacker is assigned a zone coverage scheme

    public double zoneCR;

    //Constructor
    public LineBacks(double cx, double cy, int h, int w, double s, double st, boolean hB, Color c, int LBI, OPlayer man, boolean iC, boolean z, double zCR){
        super(cx, cy, h, w, s, st, hB, c);
        lineBackInd = LBI;
        targetMan = man;
        inCoverage = iC;
        zone = z;
        zoneCR = zCR;
    }

    //Getters
    public int getLineBackInd() {
        return lineBackInd;
    }

    public OPlayer getTargetMan() {
        return targetMan;
    }

    public boolean isInCoverage() {
        return inCoverage;
    }

    public boolean isZone() {
        return zone;
    }

    public double getZoneCR(){
        return zoneCR;
    }

    //Setters
    public void setLineBackInd(int newLBI) {
        this.lineBackInd = newLBI;
    }

    public void setTargetMan(OPlayer newMan){
        this.targetMan = newMan;
    }

    public void setZone(boolean newZ) {
        this.zone = newZ;
    }

    public void setInCoverage(boolean newIC) {
        this.inCoverage = newIC;
    }

    public void setZoneCR(double newZCR){
        this.zoneCR = newZCR;
    }
}

//----------------------------------------------------------------------------------------------------------------------
//Skill position subclass that defines all players in the DEFENSIVE BACKFIELD/SECONDARY
//INCLUDES GENERIC DEFENSIVE BACKS (DB), CORNER BACKS (CB), SAFETIES (S), FREE SAFETIES (FS), STRONG SAFETIES (SS)
class DBacks extends DPlayer {

    public int dBackInd;        //Designate which specific line backer skill position the linebacker is:
                                //1 - Defensive Back (DB)
                                //2 - Corner Back (CB)
                                //3 - Safety (S)
                                //4 - Free Safety (FS)
                                //5 - Strong Safety (SS)

    public OPlayer targetMan;   //Set the target offensive player object for the current linebacker player object

    public boolean inCoverage;  //There is a difference between pursuing a target offensive player to tackle and
                                //being in coverage for that target player.  This variable is designed to manage that
                                //but my prove to be unnecessary later.

    public boolean zone;        //Designate whether or not the linebacker is assigned a zone coverage scheme

    public double zoneCR;

    //Constructor
    public DBacks(double cx, double cy, int h, int w, double s, double st, boolean hB, Color c, int DBI, OPlayer man, boolean iC, boolean z, double zCR){
        super(cx, cy, h, w, s, st, hB, c);
        dBackInd = DBI;
        targetMan = man;
        inCoverage = iC;
        zone = z;
        zoneCR = zCR;
    }

    //Getters
    public int getDBackInd() {
        return dBackInd;
    }

    public OPlayer getTargetMan() {
        return targetMan;
    }

    public boolean isInCoverage() {
        return inCoverage;
    }

    public boolean isZone() {
        return zone;
    }

    public double getZoneCR(){
        return zoneCR;
    }

    //Setters
    public void setDBackInd(int newDBI) {
        this.dBackInd = newDBI;
    }

    public void setTargetMan(OPlayer newMan){
        this.targetMan = newMan;
    }

    public void setInCoverage(boolean newIC) {
        this.inCoverage = newIC;
    }

    public void setZone(boolean z) {
        this.zone = z;
    }

    public void setZoneCR(double newZCR){
        this.zoneCR = newZCR;
    }
}
