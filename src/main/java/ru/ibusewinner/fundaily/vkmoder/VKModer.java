package ru.ibusewinner.fundaily.vkmoder;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ibusewinner.fundaily.vkmoder.commands.vk.inter.CommandManager;
import ru.ibusewinner.fundaily.vkmoder.data.VkMySQL;
import ru.ibusewinner.fundaily.vkmoder.data.items.VkUserItem;
import ru.ibusewinner.fundaily.vkmoder.utils.UpdateUserInfo;
import ru.ibusewinner.fundaily.vkmoder.vk.UpdateMessageHistory;
import ru.ibusewinner.plugin.buseapi.BuseAPI;
import ru.ibusewinner.plugin.buseapi.config.ConfigManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class VKModer extends JavaPlugin {

    private static ConfigManager settings;
    private static VKModer instance;
    private static VkMySQL mySQL;
    private static List<VkUserItem> cachedUsers = new ArrayList<>();
    private static ArrayList<String> banwords = new ArrayList<>();
    private static HashMap<VkUserItem, Long> antiFlood = new HashMap<>();
    private static HashMap<VkUserItem, Long> antiCommandFlood = new HashMap<>();
    private static String commandsPrefix;
    private static int maxWarnings;

    private static TransportClient transportClient;
    private static VkApiClient vk;
    private static GroupActor actor;
    private static int maxMsgId = -1;
    private static int ts;
    private static CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;

        settings = new ConfigManager(this, this.getDataFolder(), "config.yml");
        settings.createConfig();

        mySQL = new VkMySQL(settings.getConfig().getString("mysql.host"),
                settings.getConfig().getInt("mysql.port"),
                settings.getConfig().getString("mysql.database"),
                settings.getConfig().getString("mysql.args"),
                settings.getConfig().getString("mysql.user"),
                settings.getConfig().getString("mysql.password"));

        mySQL.createTables();
        mySQL.addBanwords();

        commandsPrefix = settings.getConfig().getString("vk.commands-prefix");
        maxWarnings = settings.getConfig().getInt("vk.max-warnings");

        new UpdateUserInfo().runTaskTimerAsynchronously(this, 20, 20*60*30);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            transportClient = new HttpTransportClient();
            vk = new VkApiClient(transportClient);
            actor = new GroupActor(settings.getConfig().getInt("vk.group-id"), settings.getConfig().getString("vk.token"));

            commandManager = new CommandManager();

            try {
                ts = vk.messages().getLongPollServer(actor).execute().getTs();

                vk.messages().send(actor)
                        .message("Я включился!")
                        .userId(547377866)
                        .randomId(new Random().nextInt())
                        .execute();
            } catch (ApiException | ClientException e) {
                BuseAPI.getBuseLogger().error(e);
            }
        });

        new UpdateMessageHistory().runTaskTimerAsynchronously(this, 10, 5);
    }

    @Override
    public void onDisable() {
        for (VkUserItem user : cachedUsers) {
            mySQL.updateUser(user);
        }
    }

    public static ConfigManager getSettings() {
        return settings;
    }

    public static VkApiClient getVk() {
        return vk;
    }

    public static GroupActor getActor() {
        return actor;
    }

    public static VKModer getInstance() {
        return instance;
    }

    public static VkMySQL getMySQL() {
        return mySQL;
    }

    public static ArrayList<String> getBanwords() {
        return banwords;
    }

    public static List<VkUserItem> getCachedUsers() {
        return cachedUsers;
    }

    public static VkUserItem getUserById(int id) {
        for (VkUserItem user : cachedUsers) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static String getCommandsPrefix() {
        return commandsPrefix;
    }

    public static int getMaxWarnings() {
        return maxWarnings;
    }

    public static int getTs() {
        return ts;
    }

    public static void setTs(int ts) {
        VKModer.ts = ts;
    }

    public static int getMaxMsgId() {
        return maxMsgId;
    }

    public static void setMaxMsgId(int maxMsgId) {
        VKModer.maxMsgId = maxMsgId;
    }

    public static HashMap<VkUserItem, Long> getAntiFlood() {
        return antiFlood;
    }

    public static HashMap<VkUserItem, Long> getAntiCommandFlood() {
        return antiCommandFlood;
    }
}
