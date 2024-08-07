package net.pulsir.lunar.listener;

import io.papermc.paper.event.entity.EntityDamageItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.pulsir.lunar.Lunar;
import net.pulsir.lunar.session.SessionPlayer;
import net.pulsir.lunar.utils.bungee.Bungee;
import net.pulsir.lunar.utils.bungee.message.ChannelType;
import net.pulsir.lunar.utils.discord.DiscordWebhook;
import net.pulsir.lunar.utils.inventory.impl.FreezeInventory;
import net.pulsir.lunar.utils.inventory.impl.InspectionInventory;
import net.pulsir.lunar.utils.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class StaffModeListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("lunar.staff")) {
            Lunar.getInstance().getData().getOnlinePlayers().add(event.getPlayer().getUniqueId());
            Lunar.getInstance().getData().getStaffMembers().add(event.getPlayer().getUniqueId());

            Lunar.getInstance().getSessionPlayerManager().getSessionPlayers()
                    .put(event.getPlayer().getUniqueId(), new SessionPlayer(event.getPlayer().getUniqueId(), 0));

            if (Lunar.getInstance().getDiscord().getConfiguration().getBoolean("enabled") && Lunar.getInstance().getDiscord().getConfiguration().getBoolean("staff-join.enabled")) {
                DiscordWebhook discordWebhook = new DiscordWebhook(Lunar.getInstance().getDiscord().getConfiguration().getString("webhook-url"));
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(Objects.requireNonNull(Lunar.getInstance().getDiscord().getConfiguration().getString("staff-join.message"))
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("server-name"))))
                        .setAuthor(event.getPlayer().getName(), "", ""));
                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (event.getPlayer().hasPermission("lunar.admin")) {
            Lunar.getInstance().getData().getOnlinePlayers().add(event.getPlayer().getUniqueId());
            Lunar.getInstance().getData().getAdminMembers().add(event.getPlayer().getUniqueId());
        }
        if (event.getPlayer().hasPermission("lunar.owner")) {
            Lunar.getInstance().getData().getOnlinePlayers().add(event.getPlayer().getUniqueId());
            Lunar.getInstance().getData().getOwnerMembers().add(event.getPlayer().getUniqueId());
        }

        if (event.getPlayer().hasPermission("lunar.staff")) {
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("join-message")) {
                for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                    .getConfiguration().getString("STAFF.JOIN-MESSAGE"))
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration()
                                    .getConfiguration().getString("server-name")))));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("quit-message")) {
            if (event.getPlayer().hasPermission("lunar.staff")) {
                for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                    .getConfiguration().getString("STAFF.QUIT-MESSAGE"))
                            .replace("{player}", event.getPlayer().getName())
                            .replace("{server}", event.getPlayer().getName())));
                }
            }
        }

        if (event.getPlayer().hasPermission("lunar.staff")) {
            if (Lunar.getInstance().getDiscord().getConfiguration().getBoolean("enabled") && Lunar.getInstance().getDiscord().getConfiguration().getBoolean("staff-quit.enabled")) {
                DiscordWebhook discordWebhook = new DiscordWebhook(Lunar.getInstance().getDiscord().getConfiguration().getString("webhook-url"));
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(Objects.requireNonNull(Lunar.getInstance().getDiscord().getConfiguration().getString("staff-quit.message"))
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("server-name"))))
                        .setAuthor(event.getPlayer().getName(), "", ""));
                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Lunar.getInstance().getData().getStaffMembers().contains(event.getPlayer().getUniqueId())) {
            Lunar.getInstance().getSessionPlayerManager().getSessionPlayers()
                    .remove(event.getPlayer().getUniqueId());
        }

        Lunar.getInstance().getData().getStaffMembers().remove(event.getPlayer().getUniqueId());
        Lunar.getInstance().getData().getAdminMembers().remove(event.getPlayer().getUniqueId());
        Lunar.getInstance().getData().getOwnerMembers().remove(event.getPlayer().getUniqueId());
        Lunar.getInstance().getData().getStaffMode().remove(event.getPlayer().getUniqueId());
        Lunar.getInstance().getData().getVanish().remove(event.getPlayer().getUniqueId());

        if (Lunar.getInstance().getData().getInventories().containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().clear();
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().getInventory().setContents(Lunar.getInstance().getData().getInventories().get(event.getPlayer().getUniqueId()));

            Lunar.getInstance().getData().getInventories().remove(event.getPlayer().getUniqueId());
        }

        Location location = event.getPlayer().getLocation();

        if (location.getBlock().getType().equals(Material.AIR)) {

            for (int i = location.getBlockY(); i > 1; i--) {
                Location newLocation = new Location(location.getWorld(), location.getBlockX(), i, location.getBlockZ());

                if (!newLocation.getBlock().getType().equals(Material.AIR)) {
                    event.getPlayer().teleport(new Location(location.getWorld(), location.getBlockX(), i + 1, location.getBlockZ()));
                    break;
                }
            }
        }

        Lunar.getInstance().getData().getFilterChat().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Lunar.getInstance().getData().getStaffMode().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    @Deprecated /*(Reason: Compatibility with Spigot & Paper)*/
    public void onPickup(PlayerPickupItemEvent event) {
        if (Lunar.getInstance().getData().getStaffMode().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Lunar.getInstance().getData().getStaffMode().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (Lunar.getInstance().getData().getStaffMode().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (Lunar.getInstance().getData().getStaffMode().contains(event.getWhoClicked().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRandomTeleport(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("lunar.staff")) return;
        if (!event.hasItem() || event.getItem() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey()))
            return;

        String key = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("randomtp")) return;

        int index = new Random().nextInt(Bukkit.getOnlinePlayers().size());
        Player randomPlayer = (Player) Bukkit.getOnlinePlayers().toArray()[index];

        if (randomPlayer != null) {
            if (!randomPlayer.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                event.getPlayer().teleport(randomPlayer.getLocation());
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                .getConfiguration().getString("STAFF.TELEPORTED"))
                        .replace("{player}", randomPlayer.getName())));
            } else {
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                        .getLanguage().getConfiguration().getString("STAFF.TELEPORT-FAIL")));
            }
        }
    }

    @EventHandler
    public void onRandomMinerTeleport(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("lunar.staff")) return;
        if (!event.hasItem() || event.getItem() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey()))
            return;

        String key = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("randomminertp")) return;

        List<Player> onlineMiningPlayers = new ArrayList<>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getY() < Lunar.getInstance().getConfiguration().getConfiguration().getInt("staff-items.random-miners.y-level")) {
                onlineMiningPlayers.add(player);
            }
        }

        if (onlineMiningPlayers.isEmpty()) {
            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                    .getLanguage().getConfiguration().getString("STAFF.NO-MINERS")));
            return;
        }

        int index = new Random().nextInt(onlineMiningPlayers.size());
        Player randomPlayer = (Player) Bukkit.getOnlinePlayers().toArray()[index];

        if (randomPlayer != null) {
            if (!randomPlayer.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                event.getPlayer().teleport(randomPlayer.getLocation());
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                .getConfiguration().getString("STAFF.TELEPORTED"))
                        .replace("{player}", randomPlayer.getName())));
            } else {
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                        .getLanguage().getConfiguration().getString("STAFF.TELEPORT-FAIL")));
            }
        } else {
            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                    .getLanguage().getConfiguration().getString("STAFF.NO-MINERS")));
        }
    }

    @EventHandler
    public void onRandomFighterTeleport(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("lunar.staff")) return;
        if (!event.hasItem() || event.getItem() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey()))
            return;

        String key = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("randomfightertp")) return;

        List<Player> onlineFightingPlayers = new ArrayList<>();
        for (final UUID uuid : Lunar.getInstance().getData().getFightingPlayers().keySet()) {
            onlineFightingPlayers.add(Bukkit.getPlayer(uuid));
        }

        if (onlineFightingPlayers.isEmpty()) {
            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                    .getLanguage().getConfiguration().getString("STAFF.NO-FIGHTERS")));
            return;
        }

        int index = new Random().nextInt(onlineFightingPlayers.size());
        Player randomPlayer = (Player) Bukkit.getOnlinePlayers().toArray()[index];

        if (randomPlayer != null) {
            if (!randomPlayer.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                event.getPlayer().teleport(randomPlayer.getLocation());
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                                .getConfiguration().getString("STAFF.TELEPORTED"))
                        .replace("{player}", randomPlayer.getName())));
            } else {
                event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                        .getLanguage().getConfiguration().getString("STAFF.TELEPORT-FAIL")));
            }
        } else {
            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance()
                    .getLanguage().getConfiguration().getString("STAFF.NO-FIGHTERS")));
        }
    }

    @EventHandler
    public void onVanish(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("lunar.staff")) return;
        if (!event.hasItem() || event.getItem() == null) return;
        if (event.getClickedBlock() != null && event.useInteractedBlock() == Event.Result.DENY) return;

        if (item.getItemMeta() == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey())) {
            return;
        }

        String key = item.getItemMeta()
                .getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("vanish")) return;
        player.performCommand("vanish");
    }

    @EventHandler
    public void onOnlineStaff(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("lunar.staff")) return;
        if (!event.hasItem() || event.getItem() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer() == null)
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey()))
            return;

        String key = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("online")) return;

        Inventory inventory = Bukkit.createInventory(event.getPlayer(),
                Lunar.getInstance().getConfiguration().getConfiguration().getInt("online-inventory.size"),
                Lunar.getInstance().getMessage().getMessage(Lunar.getInstance().getConfiguration().getConfiguration().getString("online-inventory.title")));

        int index = 0;
        List<Integer> values = Lunar.getInstance().getConfiguration().getConfiguration().getIntegerList("online-inventory.slots");

        for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
            String vanish = Lunar.getInstance().getData().getVanish().contains(uuid) ? "Enabled" : "Disabled";
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration()
                            .getString("online-inventory.item-format.name"))
                    .replace("{player}", Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName())).decoration(TextDecoration.ITALIC, false));
            meta.getPersistentDataContainer().set(Lunar.getInstance().getOnlineStaffKey(), PersistentDataType.STRING, uuid.toString());

            List<Component> lore = new ArrayList<>();
            for (final String lines : Lunar.getInstance().getConfiguration().getConfiguration().getStringList("online-inventory.item-format.lore")) {
                lore.add(Lunar.getInstance().getMessage()
                        .getMessage(lines.replace("{vanished}", vanish)
                                .replace("{session_time}", Time.parse(Lunar.getInstance().getSessionPlayerManager()
                                        .getSessionPlayers().get(event.getPlayer().getUniqueId()).getSessionTime())))
                        .decoration(TextDecoration.ITALIC, false));
            }

            meta.lore(lore);
            itemStack.setItemMeta(meta);

            if (index < inventory.getSize()) {
                inventory.setItem(values.get(index) - 1, itemStack);
            }

            index++;
        }

        if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("online-inventory.overlay")) {
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("online-inventory.slot-overlay")) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i) == null) {
                        inventory.setItem(i, new ItemStack(Material.valueOf(Lunar.getInstance().getConfiguration()
                                .getConfiguration().getString("online-inventory.overlay-item"))));
                    }
                }
            } else {
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i) == null && !values.contains(i)) {
                        inventory.setItem(i, new ItemStack(Material.valueOf(Lunar.getInstance().getConfiguration()
                                .getConfiguration().getString("online-inventory.overlay-item"))));
                    }
                }
            }
        }

        event.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onFreeze(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().hasPermission("lunar.staff")) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null)
            return;
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer() == null)
            return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(Lunar.getInstance().getNamespacedKey()))
            return;
        if (!(event.getRightClicked() instanceof Player target)) return;
        if (!(event.getHand().equals(EquipmentSlot.HAND))) return;

        String key = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                .get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (key == null || !key.equalsIgnoreCase("freeze")) return;

        if (Lunar.getInstance().getData().getStaffMembers().contains(target.getUniqueId())) {
            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Lunar.getInstance().getLanguage()
                    .getConfiguration().getString("FREEZE.STAFF")));
            return;
        }

        if (Lunar.getInstance().getData().getFrozenPlayers().contains(target.getUniqueId())) {
            Lunar.getInstance().getData().getFrozenPlayers().remove(target.getUniqueId());
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("inventory-on-freeze")) {
                new FreezeInventory().close(target);
            }

            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("freeze-chat")) {
                Lunar.getInstance().getData().getFreezeChat().remove(target.getUniqueId());
            }

            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration()
                            .getString("FREEZE.UNFROZEN"))
                    .replace("{player}", target.getName())));
            for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar
                                .getInstance().getLanguage().getConfiguration().getString("FREEZE.STAFF-UNFROZEN"))
                        .replace("{player}", target.getName())
                        .replace("{staff}", event.getPlayer().getName())
                        .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration()
                                .getString("server-name")))));
            }

            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("allow-sync")) {
                if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("bungee")) {
                    Bungee.sendMessage(event.getPlayer(),
                            Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration().getString("FREEZE.STAFF-UNFROZEN"))
                                    .replace("{player}", target.getName())
                                    .replace("{staff}", event.getPlayer().getName())
                                    .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration()
                                            .getString("server-name"))),
                            ChannelType.UNFREEZE);
                } else if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("redis")) {
                    String message = target.getName() + "<splitter>" + event.getPlayer().getName() + "<splitter>" + ".";
                    Lunar.getInstance().getRedisManager().publish("unfreeze-chat", message);
                }
            }

            if (Lunar.getInstance().getDiscord().getConfiguration().getBoolean("enabled") && Lunar.getInstance().getDiscord().getConfiguration().getBoolean("staff-unfreeze.enabled")) {
                DiscordWebhook discordWebhook = new DiscordWebhook(Lunar.getInstance().getDiscord().getConfiguration().getString("webhook-url"));
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(Objects.requireNonNull(Lunar.getInstance().getDiscord().getConfiguration().getString("staff-unfreeze.message"))
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("server-name")))
                                .replace("{target}", target.getName()))
                        .setAuthor(event.getPlayer().getName(), "", ""));
                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            Lunar.getInstance().getData().getFrozenPlayers().add(target.getUniqueId());
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("inventory-on-freeze")) {
                new FreezeInventory().open(target);
            }

            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("freeze-chat")) {
                Lunar.getInstance().getData().getFreezeChat().add(target.getUniqueId());
            }

            event.getPlayer().sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration()
                            .getString("FREEZE.FROZEN"))
                    .replace("{player}", target.getName())));

            for (final String lines : Lunar.getInstance().getLanguage().getConfiguration().getStringList("FREEZE-CHAT.MESSAGE")) {
                target.sendMessage(Lunar.getInstance().getMessage().getMessage(lines));
            }
            for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(Lunar.getInstance().getMessage().getMessage(Objects.requireNonNull(Lunar
                                .getInstance().getLanguage().getConfiguration().getString("FREEZE.STAFF-FROZEN"))
                        .replace("{player}", target.getName())
                        .replace("{staff}", event.getPlayer().getName())
                        .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration()
                                .getString("server-name")))));
            }
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("allow-sync")) {
                if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("bungee")) {
                    Bungee.sendMessage(event.getPlayer(),
                            Objects.requireNonNull(Lunar.getInstance().getLanguage().getConfiguration().getString("FREEZE.STAFF-FROZEN"))
                                    .replace("{player}", target.getName())
                                    .replace("{staff}", event.getPlayer().getName())
                                    .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration()
                                            .getString("server-name"))),
                            ChannelType.FREEZE);
                } else if (Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("sync-system"))
                        .equalsIgnoreCase("redis")) {
                    String message = target.getName() + "<splitter>" + event.getPlayer().getName() + "<splitter>" + ".";
                    Lunar.getInstance().getRedisManager().publish("freeze-chat", message);
                }
            }
            if (Lunar.getInstance().getDiscord().getConfiguration().getBoolean("enabled") && Lunar.getInstance().getDiscord().getConfiguration().getBoolean("staff-freeze.enabled")) {
                DiscordWebhook discordWebhook = new DiscordWebhook(Lunar.getInstance().getDiscord().getConfiguration().getString("webhook-url"));
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setDescription(Objects.requireNonNull(Lunar.getInstance().getDiscord().getConfiguration().getString("staff-freeze.message"))
                                .replace("{player}", event.getPlayer().getName())
                                .replace("{server}", Objects.requireNonNull(Lunar.getInstance().getConfiguration().getConfiguration().getString("server-name")))
                                .replace("{target}", target.getName()))
                        .setAuthor(event.getPlayer().getName(), "", ""));
                try {
                    discordWebhook.execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @EventHandler
    public void onInspect(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!player.hasPermission("lunar.staff")
                || itemInHand.getItemMeta() == null
                || !event.getHand().equals(EquipmentSlot.HAND)
                || !(event.getRightClicked() instanceof Player target)) {
            return;
        }

        PersistentDataContainer dataContainer = itemInHand.getItemMeta().getPersistentDataContainer();
        String key = dataContainer.get(Lunar.getInstance().getNamespacedKey(), PersistentDataType.STRING);

        if (!"inspect".equalsIgnoreCase(key)) return;

        new InspectionInventory(target).open(player);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Set<UUID> staffSet = Lunar.getInstance().getData().getStaffMode();
        if (event.getEntity() instanceof Player player) {
            if (staffSet.contains(player.getUniqueId())) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Set<UUID> staffSet = Lunar.getInstance().getData().getStaffMode();

        if (event.getEntity() instanceof Player player) {
            if (staffSet.contains(player.getUniqueId())) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDamage(EntityDamageItemEvent event) {
        Set<UUID> staffSet = Lunar.getInstance().getData().getStaffMode();

        if (event.getEntity() instanceof Player player) {
            if (staffSet.contains(player.getUniqueId())) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent event) {
        Set<UUID> staffSet = Lunar.getInstance().getData().getStaffMode();

        if (event.getEntity() instanceof Player player) {
            if (staffSet.contains(player.getUniqueId())) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLivingEntityTarget(EntityTargetLivingEntityEvent event) {
        Set<UUID> staffModeSet = Lunar.getInstance().getData().getStaffMode();

        if (event.getTarget() instanceof Player player) {
            if (staffModeSet.contains(player.getUniqueId())) {
                event.setTarget(null);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Set<UUID> staffModeSet = Lunar.getInstance().getData().getStaffMode();

        if (event.getTarget() instanceof Player player) {
            if (staffModeSet.contains(player.getUniqueId())) {
                event.setTarget(null);
                event.setCancelled(true);
            }
        }
    }
}