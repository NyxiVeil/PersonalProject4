package cs2.javafx.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveManager {

    private static final String SAVE_DIR = "saves";

    private static void ensureSaveDirExists() {
        try {
            Path path = Paths.get(SAVE_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSaveFilePath(int slot) {
        return SAVE_DIR + "/save_slot_" + slot + ".dat";
    }

    public static void saveGame(int slot, SaveData data) {
        ensureSaveDirExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getSaveFilePath(slot)))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData loadGame(int slot) {
        if (!isSlotOccupied(slot)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getSaveFilePath(slot)))) {
            return (SaveData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteGame(int slot) {
        try {
            Files.deleteIfExists(Paths.get(getSaveFilePath(slot)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSlotOccupied(int slot) {
        return Files.exists(Paths.get(getSaveFilePath(slot)));
    }
}
