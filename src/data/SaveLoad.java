package data;

import main.GamePanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveLoad {

    GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));
            DataStorage ds = new DataStorage();

            ds.currentMap = gp.currentMap;
            ds.playerWorldX = gp.player.worldX;
            ds.playerWorldY = gp.player.worldY;
            ds.storyStage = gp.story.getStage();
            ds.growth = gp.story.growth;
            ds.calm = gp.story.calm;
            ds.empathy = gp.story.empathy;
            ds.confidence = gp.story.confidence;
            ds.hasLantern = gp.hasLantern;

            oos.writeObject(ds);
            oos.close();
        }
        catch (Exception e) {
            System.out.println("Save Exception!");
        }
    }

    public boolean load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));
            DataStorage ds = (DataStorage) ois.readObject();
            ois.close();

            gp.currentMap = ds.currentMap;
            gp.player.worldX = ds.playerWorldX;
            gp.player.worldY = ds.playerWorldY;
            gp.player.direction = "down";
            gp.hasLantern = ds.hasLantern;
            gp.story.loadState(ds.storyStage, ds.growth, ds.calm, ds.empathy, ds.confidence);
            gp.aSetter.setObject();
            gp.aSetter.setNPC();
            return true;
        }
        catch (Exception e) {
            System.out.println("Load Exception!");
            return false;
        }
    }
}
