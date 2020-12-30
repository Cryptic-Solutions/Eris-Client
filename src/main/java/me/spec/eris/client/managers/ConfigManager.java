package me.spec.eris.client.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spec.eris.api.config.ClientConfig;
import me.spec.eris.api.manager.Manager;
import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.spec.eris.Eris;
import me.spec.eris.api.config.file.FileManager;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.Value;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.api.notification.Notification;

public class ConfigManager extends Manager<ClientConfig> {

    public ArrayList<ClientConfig> getConfigs() {
        return getManagerArraylist();
    }

    @Override
    public void loadManager() {
        File defaultFile = new File(FileManager.dir, "defaultconfig.eriscnf");
        if (!defaultFile.exists()) {
            try {
                defaultFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String content = FileUtils.readFileToString(defaultFile);
                JsonObject configurationObject = new GsonBuilder().create().fromJson(content, JsonObject.class);

                for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
                    if (entry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) entry.getValue();

                        for (Module module : Eris.getInstance().moduleManager.getModules()) {
                            if (module.getName().equalsIgnoreCase(moduleObject.get("name").getAsString()) && module.getCategory() != null) {
                                if (moduleObject.get("active").getAsBoolean()) {
                                    module.toggle(false);
                                } else {
                                    module.setToggled(false, false);
                                }
                                for (Value value : module.getSettings()) {
                                    if (moduleObject.get(value.getValueName()) != null) {
                                        if (value instanceof NumberValue) {
                                            if (value.getValue() instanceof Double) {
                                                value.setValueObject(moduleObject.get(value.getValueName()).getAsDouble());
                                            }
                                            if (value.getValue() instanceof Integer) {
                                                value.setValueObject(moduleObject.get(value.getValueName()).getAsInt());
                                            }
                                            if (value.getValue() instanceof Float) {
                                                value.setValueObject(moduleObject.get(value.getValueName()).getAsFloat());
                                            }
                                        }
                                        if (value instanceof BooleanValue) {
                                            value.setValueObject(moduleObject.get(value.getValueName()).getAsBoolean());
                                        }
                                        if (value instanceof ModeValue) {
                                            for (int i = 0; i < ((ModeValue) value).getModes().length; i++) {
                                                if (((ModeValue) value).getModes()[i].name().equalsIgnoreCase(moduleObject.get(value.getValueName()).getAsString())) {
                                                    value.setValueObject(((ModeValue) value).getModes()[i]);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveDefaultFile() {
        Eris.getInstance().clickUI.reload(false);
        JsonObject jsonObject = new JsonObject();
        for (Module module : Eris.getInstance().moduleManager.getModules()) {
            if (module.getCategory() != null) {
                JsonObject moduleObject = new JsonObject();
                moduleObject.addProperty("name", module.getName());
                moduleObject.addProperty("active", module.isToggled());

                module.getSettings().forEach(value -> moduleObject.addProperty(value.getValueName(), String.valueOf(value.getValue())));

                jsonObject.add(module.getName(), moduleObject);
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(new File(FileManager.dir, "defaultconfig.eriscnf"));
            fileWriter.write(new GsonBuilder().create().toJson(jsonObject));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(ClientConfig clientConfig) {
        Eris.getInstance().notificationManager.send(new Notification("Saved " + clientConfig.getConfigName(), "Config", 2000));
        Eris.getInstance().clickUI.reload(false);
        JsonObject jsonObject = new JsonObject();
        for (Module module : Eris.getInstance().moduleManager.getModules()) {
            if (module.getCategory() != null) {
                JsonObject moduleObject = new JsonObject();
                moduleObject.addProperty("name", module.getName());
                moduleObject.addProperty("active", module.isToggled());

                module.getSettings().forEach(value -> moduleObject.addProperty(value.getValueName(), String.valueOf(value.getValue())));

                jsonObject.add(module.getName(), moduleObject);
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(clientConfig.getConfigFile());
            fileWriter.write(new GsonBuilder().create().toJson(jsonObject));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addToManagerArraylist(clientConfig);
    }

    public void loadConfig(ClientConfig clientConfig) throws IOException {
        Eris.getInstance().moduleManager.getModules().forEach(module -> module.setToggled(false, false));
        Eris.getInstance().notificationManager.send(new Notification("Loaded " + clientConfig.getConfigName(), "Config", 1500));
        String content = FileUtils.readFileToString(clientConfig.getConfigFile());

        JsonObject configurationObject = new GsonBuilder().create().fromJson(content, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : configurationObject.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
                JsonObject moduleObject = (JsonObject) entry.getValue();

                for (Module module : Eris.getInstance().moduleManager.getModules()) {
                    if (module.getName().equalsIgnoreCase(moduleObject.get("name").getAsString()) && module.getCategory() != null) {
                        if (moduleObject.get("active").getAsBoolean()) {
                            module.toggle(false);
                        } else {
                            module.setToggled(false, false);
                        }
                        for (Value value : module.getSettings()) {
                            if (moduleObject.get(value.getValueName()) != null) {
                                if (value instanceof NumberValue) {
                                    if (value.getValue() instanceof Double) {
                                        value.setValueObject(moduleObject.get(value.getValueName()).getAsDouble());
                                    }
                                    if (value.getValue() instanceof Integer) {
                                        value.setValueObject(moduleObject.get(value.getValueName()).getAsInt());
                                    }
                                    if (value.getValue() instanceof Float) {
                                        value.setValueObject(moduleObject.get(value.getValueName()).getAsFloat());
                                    }
                                }
                                if (value instanceof BooleanValue) {
                                    value.setValueObject(moduleObject.get(value.getValueName()).getAsBoolean());
                                }
                                if (value instanceof ModeValue) {
                                    for (int i = 0; i < ((ModeValue) value).getModes().length; i++) {
                                        if (((ModeValue) value).getModes()[i].name().equalsIgnoreCase(moduleObject.get(value.getValueName()).getAsString())) {
                                            value.setValueObject(((ModeValue) value).getModes()[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadConfigs() {
        List<String> searchResult = new ArrayList<>();
        for (File file : Eris.getInstance().fileManager.configDir.listFiles()) {
            if (file.getName().endsWith(".eriscnf")) {
                searchResult.add(file.getName());
            }
        }
        for (String configName : searchResult) {

            addToManagerArraylist(new ClientConfig(configName.replace(".eriscnf", "")));
        }
    }

    public void deleteConfig(ClientConfig clientConfig) {
        if (clientConfig.getConfigFile() != null) {
            if (clientConfig.getConfigFile().delete()) {
                deleteConfig(clientConfig);
                removeFromManagerArraylist(clientConfig);
                loadConfigs();
            }
        }
    }

    public ClientConfig getConfigByName(String configName) {
        for(ClientConfig clientConfig : getConfigs()) {
            if(clientConfig.getConfigName().equalsIgnoreCase(configName)) {
                return clientConfig;
            }
        }
        return null;
    }
}