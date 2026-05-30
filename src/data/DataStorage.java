package data;

import java.io.Serializable;

public class DataStorage implements Serializable {

    private static final long serialVersionUID = 2L;

    int currentMap;
    int playerWorldX;
    int playerWorldY;
    int storyStage;
    int growth;
    int calm;
    int empathy;
    int confidence;
    int responsibility;
    int avoidance;
    int selfWorth;
    boolean hasLantern;
    boolean bedroomLampOn;
    boolean tvOn;
    boolean phoneEventDone;
    boolean photoEventDone;
    boolean mirrorEventDone;
    boolean lostLanternEventDone;
    boolean woundedBirdEventDone;
    boolean oldLetterEventDone;
    boolean helpRequestEventDone;
    boolean forkEventDone;
    boolean travelerEventDone;
}
