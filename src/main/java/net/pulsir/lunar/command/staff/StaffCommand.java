package net.pulsir.lunar.command.staff;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pulsir.lunar.Lunar;
import net.pulsir.lunar.utils.staff.Staff;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StaffCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!(player.hasPermission("lunar.staff"))) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                    .getConfiguration().getString("NO-PERMISSIONS"))));
            return false;
        }

        if (Lunar.getInstance().getData().getStaffMode().contains(player.getUniqueId())) {
            Lunar.getInstance().getData().getStaffMode().remove(player.getUniqueId());

            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setContents(Lunar.getInstance().getData().getInventories().get(player.getUniqueId()));
            Lunar.getInstance().getData().getInventories().remove(player.getUniqueId());

            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                    .getConfiguration().getString("STAFF-MODE.DISABLED"))));
        } else {
            Lunar.getInstance().getData().getStaffMode().add(player.getUniqueId());
            if (Lunar.getInstance().getConfiguration().getConfiguration().getBoolean("force-vanish")) {
                Lunar.getInstance().getData().getVanish().add(player.getUniqueId());
            }

            Lunar.getInstance().getData().getInventories().put(player.getUniqueId(), player.getInventory().getContents());
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            Staff.items(player);

            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(Lunar.getInstance().getLanguage()
                    .getConfiguration().getString("STAFF-MODE.ENABLED"))));
        }

        return true;
    }
}