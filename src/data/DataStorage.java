package data;

import entity.Entity;

import java.io.Serializable;
import java.util.ArrayList;

public class DataStorage implements Serializable {

    // PLAYER STATE
    int level;
    int maxLife;
    int life;
    int strength;
    int dexterity;
    int exp;
    int nextLevelExp;
    int coin;

    // PLAYER INVENTORY
    ArrayList<String> itemNames = new ArrayList<>();
    int currentWeaponSlot;
    int currentShieldSlot;

    // OBJECT ON MAP
    String mapObjectNames[][];
    int mapObjectWorldX[][];
    int mapObjectWorldY[][];
}