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
    boolean hasLantern;
}
