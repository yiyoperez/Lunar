package net.pulsir.lunar.command.player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pulsir.lunar.Lunar;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class RequestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return false;

        if (Lunar.getInstance().getData().getRequestCooldown().get(player.getUniqueId()) != null && Lunar.getInstance().getData().getRequestCooldown().get(player.getUniqueId()) > 1) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                            .getConfiguration().getString("REQUEST.COOLDOWN"))
                    .replace("{time}", String.valueOf(Lunar.getInstance().getData().getRequestCooldown().get(player.getUniqueId()))))));
            return false;
        }

        if (args.length == 0) {
            for (final String lines : Lunar.getInstance().getLanguage().getConfiguration().getStringList("REQUEST.USAGE")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(lines));
            }
        } else {
            String message = "";
            for (String arg : args) {
                if (message.isEmpty()) {
                    message = arg;
                } else {
                    message = message + " " + arg;
                }
            }

            Lunar.getInstance().getData().getRequestCooldown().put(player.getUniqueId(),
                    Lunar.getInstance().getConfiguration().getConfiguration().getInt("request-cooldown"));
            for (UUID uuid : Lunar.getInstance().getData().getStaffMembers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Objects.requireNonNull(Lunar.getInstance()
                                .getLanguage().getConfiguration().getString("REQUEST.FORMAT"))
                        .replace("{player}", player.getName())
                        .replace("{message}", message))));
            }
            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                    .getConfiguration().getString("REQUEST.SUCCESS"))));
        }

        return true;
    }
}
