package net.pulsir.lunar.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.pulsir.lunar.Lunar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "lunar";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Pulsir";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (identifier.equals("vanish")) {
            return Lunar.getInstance().getData().getVanish().contains(player.getUniqueId()) ? "Enabled" : "Disabled";
        }

        if (identifier.equals("staff")) {
            return Lunar.getInstance().getData().getStaffMode().contains(player.getUniqueId()) ? "Enabled" : "Disabled";
        }

        if (identifier.equals("staff_chat")) {
            return Lunar.getInstance().getData().getStaffChat().contains(player.getUniqueId()) ? "Enabled" : "Disabled";
        }

        if (identifier.equals("admin_chat")) {
            return Lunar.getInstance().getData().getAdminChat().contains(player.getUniqueId()) ? "Enabled" : "Disabled";
        }

        if (identifier.equals("owner_chat")) {
            return Lunar.getInstance().getData().getOwnerChat().contains(player.getUniqueId()) ? "Enabled" : "Disabled";
        }

        if (identifier.equals("frozen")) {
            return Lunar.getInstance().getData().getFrozenPlayers().contains(player.getUniqueId()) ? "Yes" : "No";
        }

        return null;
    }
}