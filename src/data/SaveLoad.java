package data;

import main.GamePanel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveLoad {

    private final GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("save.dat"))) {
            DataStorage ds = new DataStorage();

            ds.currentMap = gp.currentMap;
            ds.playerWorldX = gp.player.worldX;
            ds.playerWorldY = gp.player.worldY;
            ds.storyStage = gp.story.getStage();
            ds.growth = gp.story.growth;
            ds.calm = gp.story.calm;
            ds.empathy = gp.story.empathy;
            ds.confidence = gp.story.confidence;
            ds.responsibility = gp.story.responsibility;
            ds.avoidance = gp.story.avoidance;
            ds.selfWorth = gp.story.selfWorth;
            ds.hasLantern = gp.hasLantern;
            ds.bedroomLampOn = gp.bedroomLampOn;
            ds.tvOn = gp.tvOn;
            ds.phoneEventDone = gp.story.phoneEventDone;
            ds.photoEventDone = gp.story.photoEventDone;
            ds.mirrorEventDone = gp.story.mirrorEventDone;
            ds.lostLanternEventDone = gp.story.lostLanternEventDone;
            ds.woundedBirdEventDone = gp.story.woundedBirdEventDone;
            ds.oldLetterEventDone = gp.story.oldLetterEventDone;
            ds.helpRequestEventDone = gp.story.helpRequestEventDone;
            ds.forkEventDone = gp.story.forkEventDone;
            ds.travelerEventDone = gp.story.travelerEventDone;

            oos.writeObject(ds);
        }
        catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    public boolean load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("save.dat"))) {
            DataStorage ds = (DataStorage) ois.readObject();

            gp.currentMap = ds.currentMap;
            gp.player.worldX = ds.playerWorldX;
            gp.player.worldY = ds.playerWorldY;
            gp.player.direction = "down";
            gp.hasLantern = ds.hasLantern;
            gp.bedroomLampOn = ds.bedroomLampOn;
            gp.tvOn = ds.tvOn;
            gp.story.loadState(ds.storyStage, ds.growth, ds.calm, ds.empathy, ds.confidence,
                    ds.responsibility, ds.avoidance, ds.selfWorth);
            gp.story.phoneEventDone = ds.phoneEventDone;
            gp.story.photoEventDone = ds.photoEventDone;
            gp.story.mirrorEventDone = ds.mirrorEventDone;
            gp.story.lostLanternEventDone = ds.lostLanternEventDone;
            gp.story.woundedBirdEventDone = ds.woundedBirdEventDone;
            gp.story.oldLetterEventDone = ds.oldLetterEventDone;
            gp.story.helpRequestEventDone = ds.helpRequestEventDone;
            gp.story.forkEventDone = ds.forkEventDone;
            gp.story.travelerEventDone = ds.travelerEventDone;
            gp.aSetter.setObject();
            gp.aSetter.setNPC();
            return true;
        }
        catch (Exception e) {
            System.err.println("Load failed: " + e.getMessage());
            return false;
        }
    }
}
