package net.pulsir.lunar.listener;

import net.pulsir.lunar.Lunar;
import net.pulsir.lunar.utils.bungee.Bungee;
import net.pulsir.lunar.utils.bungee.message.ChannelType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    @Deprecated /*(Reason: Compatibility with Spigot & Paper)*/
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (Lunar.getInstance().getData().getStaffChat().contains(event.getPlayer().getUniqueId())) {
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("allow-sync")) {
                if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("bungee")) {
                    for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                        Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                        .getConfiguration().getString("STAFF-CHAT.FORMAT"))
                                .replace("{message}", event.getMessage())
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Bukkit.getServer().getName())));
                    }

                    Bungee.sendMessage(event.getPlayer(),
                            Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration().getString("STAFF-CHAT.FORMAT"))
                                    .replace("{message}", event.getMessage())
                                    .replace("{player}", event.getPlayer().getName())
                                    .replace("{server}", Bukkit.getServer().getName()),
                            ChannelType.STAFF);
                } else if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("redis")) {
                    String message = event.getPlayer().getName() + "<splitter>" + Bukkit.getServer().getName() + "<splitter>" + event.getMessage();
                    Lunar.getInstance().getRedisManager().publish("staff-chat", message);
                }
            } else {
                for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                    .getConfiguration().getString("STAFF-CHAT.FORMAT"))
                            .replace("{message}", event.getMessage())
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{server}", Bukkit.getServer().getName())));
                }
            }
            event.setCancelled(true);
        } else if (Lunar.getInstance().getData().getAdminChat().contains(event.getPlayer().getUniqueId())) {
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("allow-sync")) {
                if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("bungee")) {
                    for (UUID uuid : Lunar.getInstance().getData().getAdminMembers()) {
                        Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                        .getConfiguration().getString("ADMIN-CHAT.FORMAT"))
                                .replace("{message}", event.getMessage())
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Bukkit.getServer().getName())));
                    }

                    Bungee.sendMessage(event.getPlayer(),
                            Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration().getString("ADMIN-CHAT.FORMAT"))
                                    .replace("{message}", event.getMessage())
                                    .replace("{player}", event.getPlayer().getName())
                                    .replace("{server}", Bukkit.getServer().getName()),
                            ChannelType.STAFF);
                } else if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("redis")) {
                    String message = event.getPlayer().getName() + "<splitter>" + Bukkit.getServer().getName() + "<splitter>" + event.getMessage();
                    Lunar.getInstance().getRedisManager().publish("admin-chat", message);
                }
            } else {
                for (UUID uuid : Lunar.getInstance().getData().getAdminMembers()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                    .getConfiguration().getString("ADMIN-CHAT.FORMAT"))
                            .replace("{message}", event.getMessage())
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{server}", Bukkit.getServer().getName())));
                }
            }
            event.setCancelled(true);
        } else if (Lunar.getInstance().getData().getOwnerChat().contains(event.getPlayer().getUniqueId())) {
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("allow-sync")) {
                if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("bungee")) {
                    for (UUID uuid : Lunar.getInstance().getData().getOwnerMembers()) {
                        Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                        .getConfiguration().getString("OWNER-CHAT.FORMAT"))
                                .replace("{message}", event.getMessage())
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Bukkit.getServer().getName())));
                    }

                    Bungee.sendMessage(event.getPlayer(),
                            Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration().getString("OWNER-CHAT.FORMAT"))
                                    .replace("{message}", event.getMessage())
                                    .replace("{player}", event.getPlayer().getName())
                                    .replace("{server}", Bukkit.getServer().getName()),
                            ChannelType.STAFF);
                } else if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("redis")) {
                    String message = event.getPlayer().getName() + "<splitter>" + Bukkit.getServer().getName() + "<splitter>" + event.getMessage();
                    Lunar.getInstance().getRedisManager().publish("owner-chat", message);
                }
            } else {
                for (UUID uuid : Lunar.getInstance().getData().getOwnerMembers()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                    .getConfiguration().getString("OWNER-CHAT.FORMAT"))
                            .replace("{message}", event.getMessage())
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{server}", Bukkit.getServer().getName())));
                }
            }
            event.setCancelled(true);
        } else if (Lunar.getInstance().getData().getFreezeChat().contains(event.getPlayer().getUniqueId())) {
            if (Lunar.getInstance().getData().getStaffMembers().contains(event.getPlayer().getUniqueId())) {
                for (UUID uuid : Lunar.getInstance().getData().getFreezeChat()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance()
                                    .getLanguage().getConfiguration().getString("FREEZE-CHAT.FORMAT"))
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{message}", event.getMessage())));
                }
            } else {
                for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance()
                                    .getLanguage().getConfiguration().getString("FREEZE-CHAT.FORMAT"))
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{message}", event.getMessage())));
                }

                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance()
                                .getLanguage().getConfiguration().getString("FREEZE-CHAT.FORMAT"))
                        .replace("{player}", event.getPlayer().getName())
                        .replace("{message}", event.getMessage())));
            }
            event.setCancelled(true);
        }
    }
}