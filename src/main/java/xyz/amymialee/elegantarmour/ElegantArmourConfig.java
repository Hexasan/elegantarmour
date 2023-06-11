package xyz.amymialee.elegantarmour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import xyz.amymialee.elegantarmour.util.ElegantPlayerData;
import xyz.amymialee.elegantarmour.util.ElegantState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElegantArmourConfig {
    private static final File optionsFile = new File(MinecraftClient.getInstance().runDirectory, "config/elegantarmour.json");
    public static ElegantPlayerData defaultSettings = new ElegantPlayerData("Default");
    public static Map<UUID, ElegantPlayerData> playerData = new HashMap<>();
    public static boolean slimArms = true;

    public static ElegantState getState(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> defaultSettings.getHeadState();
            case CHEST -> defaultSettings.getChestState();
            case LEGS -> defaultSettings.getLegsState();
            case FEET -> defaultSettings.getFeetState();
            default -> ElegantState.DEFAULT;
        };
    }

    public static ElegantState getState(int index) {
        return switch (index) {
            case 0 -> defaultSettings.getHeadState();
            case 1 -> defaultSettings.getChestState();
            case 2 -> defaultSettings.getLegsState();
            case 3 -> defaultSettings.getFeetState();
            case 4 -> defaultSettings.getElytraState();
            case 5 -> defaultSettings.getSmallArmourState();
            default -> ElegantState.DEFAULT;
        };
    }

    public static ElegantState getDefaultElytra() {
        return defaultSettings.getElytraState();
    }

    public static ElegantState getDefaultSmallArmour() {
        return defaultSettings.getSmallArmourState();
    }

    public static ElegantPlayerData getOrCreate(UUID uuid, String string) {
        if (playerData.containsKey(uuid)) {
            return playerData.get(uuid);
        } else {
            ElegantPlayerData data = new ElegantPlayerData(string);
            playerData.put(uuid, data);
            return data;
        }
    }

    public static void saveConfig() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject json = new JsonObject();
            json.addProperty("defaultHead", defaultSettings.getHeadState().name());
            json.addProperty("defaultChest", defaultSettings.getChestState().name());
            json.addProperty("defaultLegs", defaultSettings.getLegsState().name());
            json.addProperty("defaultFeet", defaultSettings.getFeetState().name());
            json.addProperty("defaultElytra", defaultSettings.getElytraState().name());
            json.addProperty("defaultSmallArmour", defaultSettings.getSmallArmourState().name());
            JsonArray playerJsonArray = new JsonArray();
            for (Map.Entry<UUID, ElegantPlayerData> entry : playerData.entrySet()) {
                ElegantPlayerData data = entry.getValue();
                JsonObject playerJson = new JsonObject();
                playerJson.addProperty("uuid", entry.getKey().toString());
                playerJson.addProperty("name", data.getPlayerName());
                playerJson.addProperty("headState", data.getHeadState().name());
                playerJson.addProperty("chestState", data.getChestState().name());
                playerJson.addProperty("legsState", data.getLegsState().name());
                playerJson.addProperty("feetState", data.getFeetState().name());
                playerJson.addProperty("elytraState", data.getElytraState().name());
                playerJson.addProperty("smallArmour", data.getSmallArmourState().name());
                playerJsonArray.add(playerJson);
            }
            json.add("playerData", playerJsonArray);
            json.addProperty("slimArms", slimArms);
            FileWriter writer = new FileWriter(optionsFile);
            writer.write(gson.toJson(json));
            writer.close();
        } catch (Exception e) {
            ElegantArmour.LOGGER.info(e.toString());
        }
    }

    public static void loadConfig() {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(optionsFile);
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            defaultSettings.setHeadState(ElegantState.valueOf(data.get("defaultHead").getAsString()));
            defaultSettings.setChestState(ElegantState.valueOf(data.get("defaultChest").getAsString()));
            defaultSettings.setLegsState(ElegantState.valueOf(data.get("defaultLegs").getAsString()));
            defaultSettings.setFeetState(ElegantState.valueOf(data.get("defaultFeet").getAsString()));
            defaultSettings.setElytraState(ElegantState.valueOf(data.get("defaultElytra").getAsString()));
            defaultSettings.setSmallArmourState(ElegantState.valueOf(data.get("defaultSmallArmour").getAsString()));
            JsonArray playerJsonArray = data.get("playerData").getAsJsonArray();
            for (int i = 0; i < playerJsonArray.size(); i++) {
                JsonObject playerJson = playerJsonArray.get(i).getAsJsonObject();
                ElegantPlayerData playerData = new ElegantPlayerData(playerJson.get("name").getAsString());
                playerData.setHeadState(ElegantState.valueOf(playerJson.get("headState").getAsString()));
                playerData.setChestState(ElegantState.valueOf(playerJson.get("chestState").getAsString()));
                playerData.setLegsState(ElegantState.valueOf(playerJson.get("legsState").getAsString()));
                playerData.setFeetState(ElegantState.valueOf(playerJson.get("feetState").getAsString()));
                playerData.setElytraState(ElegantState.valueOf(playerJson.get("elytraState").getAsString()));
                playerData.setSmallArmourState(ElegantState.valueOf(playerJson.get("smallArmour").getAsString()));
                ElegantArmourConfig.playerData.put(UUID.fromString(playerJson.get("uuid").getAsString()), playerData);
            }
            slimArms = data.get("slimArms").getAsBoolean();
        } catch (FileNotFoundException e) {
            ElegantArmour.LOGGER.info("Config data not found.");
        } catch (Exception e) {
            ElegantArmour.LOGGER.info("Error loading config data.");
            ElegantArmour.LOGGER.info(e.toString());
        }
    }
}