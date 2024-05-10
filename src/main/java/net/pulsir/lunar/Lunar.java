package net.pulsir.lunar;

import lombok.Getter;
import net.pulsir.lunar.command.chat.AdminChatCommand;
import net.pulsir.lunar.command.chat.OwnerChatCommand;
import net.pulsir.lunar.command.chat.StaffChatCommand;
import net.pulsir.lunar.command.player.ReportCommand;
import net.pulsir.lunar.command.player.RequestCommand;
import net.pulsir.lunar.command.restore.InventoryRestoreCommand;
import net.pulsir.lunar.command.staff.*;
import net.pulsir.lunar.data.Data;
import net.pulsir.lunar.database.IDatabase;
import net.pulsir.lunar.database.impl.FlatFile;
import net.pulsir.lunar.database.impl.Mongo;
import net.pulsir.lunar.hook.PlaceHolderHook;
import net.pulsir.lunar.inventories.manager.InventoryManager;
import net.pulsir.lunar.listener.*;
import net.pulsir.lunar.task.LunarTask;
import net.pulsir.lunar.task.MessagesTask;
import net.pulsir.lunar.task.ServerTask;
import net.pulsir.lunar.utils.bungee.listener.BungeeListener;
import net.pulsir.lunar.utils.config.Config;
import net.pulsir.lunar.utils.message.Message;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class Lunar extends JavaPlugin {

    @Getter private static Lunar instance;
    private Data data;

    private Config configuration;
    private Config language;
    private Config inventory;
    private Config messages;

    private IDatabase database;
    @Getter private Message message;

    private final InventoryManager inventoryManager = new InventoryManager();

    @Getter private final NamespacedKey namespacedKey = new NamespacedKey(this, "staff");

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfiguration();
        this.message = new Message();

        if (Objects.requireNonNull(getConfiguration().getConfiguration().getString("database")).equalsIgnoreCase("mongo")) {
            database = new Mongo();
            Bukkit.getConsoleSender().sendMessage("[Lunar] Successfully loaded MONGO as database.");
        } else if (Objects.requireNonNull(getConfiguration().getConfiguration().getString("database")).equalsIgnoreCase("flatfile")) {
            database = new FlatFile();
            Bukkit.getConsoleSender().sendMessage("[Lunar] Successfully loaded FLATFILE as database.");
        } else {
            Bukkit.getConsoleSender().sendMessage("[Lunar] Unsupported database. Loading default one [FLATFILE].");
            database = new FlatFile();
        }

        this.registerCommands();
        Bukkit.getConsoleSender().sendMessage("[Lunar] Successfully loaded commands.");
        this.registerListeners(Bukkit.getPluginManager());
        Bukkit.getConsoleSender().sendMessage("[Lunar] Successfully loaded listeners.");

        this.data = new Data();

        if (configuration.getConfiguration().getBoolean("bungee")) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }

        this.registerTasks();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderHook().register();
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.database.saveInventory();

        if (getData().getInventories().isEmpty()) return;
        for (UUID uuid : getData().getInventories().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.getInventory().setContents(getData().getInventories().get(player.getUniqueId()));
                getData().getStaffMode().clear();

                Location location = player.getLocation();

                if (location.getBlock().getType().equals(Material.AIR)) {

                    for (int i = location.getBlockY(); i > 1; i--) {
                        Location newLocation = new Location(location.getWorld(), location.getBlockX(), i, location.getBlockZ());

                        if (!newLocation.getBlock().getType().equals(Material.AIR)) {
                            player.teleport(new Location(location.getWorld(), location.getBlockX(), i + 1, location.getBlockZ()));
                            break;
                        }
                    }
                }
            }
        }

        getData().getInventories().clear();

        instance = null;
    }

    private void loadConfiguration() {
        this.configuration = new Config(this, new File(getDataFolder(), "configuration.yml"),
                new YamlConfiguration(), "configuration.yml");
        this.language = new Config(this, new File(getDataFolder(), "language.yml"),
                new YamlConfiguration(), "language.yml");
        this.inventory = new Config(this, new File(getDataFolder(), "inventories.yml"),
                new YamlConfiguration(), "inventories.yml");
        this.messages = new Config(this, new File(getDataFolder(), "messages.yml"),
                new YamlConfiguration(), "messages.yml");

        this.configuration.create();
        this.language.create();
        this.inventory.create();
        this.messages.create();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("staffchat")).setExecutor(new StaffChatCommand());
        Objects.requireNonNull(getCommand("adminchat")).setExecutor(new AdminChatCommand());
        Objects.requireNonNull(getCommand("ownerchat")).setExecutor(new OwnerChatCommand());

        Objects.requireNonNull(getCommand("staff")).setExecutor(new StaffCommand());
        Objects.requireNonNull(getCommand("vanish")).setExecutor(new VanishCommand());

        Objects.requireNonNull(getCommand("freeze")).setExecutor(new FreezeCommand());
        Objects.requireNonNull(getCommand("inspect")).setExecutor(new InvseeCommand());

        Objects.requireNonNull(getCommand("broadcast")).setExecutor(new BroadcastCommand());

        Objects.requireNonNull(getCommand("report")).setExecutor(new ReportCommand());
        Objects.requireNonNull(getCommand("request")).setExecutor(new RequestCommand());

        Objects.requireNonNull(getCommand("restore")).setExecutor(new InventoryRestoreCommand());

        Objects.requireNonNull(getCommand("spy")).setExecutor(new SpyCommand());
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new StaffModeListener(), this);
        pluginManager.registerEvents(new VanishListener(), this);
        pluginManager.registerEvents(new FreezeListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new SpyListener(), this);
    }

    private void registerTasks() {
        if (getConfiguration().getConfiguration().getBoolean("staff-bar")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new LunarTask(), 0L, 20L);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ServerTask(), 0L, 20L);
        }
        Bukkit.getScheduler().runTaskTimer(this, new MessagesTask(), 0L, 20L);
    }
}
