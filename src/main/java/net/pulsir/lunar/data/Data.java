package net.pulsir.lunar.data;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class Data {

    private final Set<UUID> staffChat = new HashSet<>();
    private final Set<UUID> adminChat = new HashSet<>();
    private final Set<UUID> ownerChat = new HashSet<>();

    private final Set<UUID> staffMembers = new HashSet<>();
    private final Set<UUID> adminMembers = new HashSet<>();
    private final Set<UUID> ownerMembers = new HashSet<>();

    private final Set<UUID> staffMode = new HashSet<>();
    private final Set<UUID> vanish = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();

    private final Map<UUID, ItemStack[]> inventories = new HashMap<>();

    public void clearChat(UUID uuid) {
        staffChat.remove(uuid);
        adminChat.remove(uuid);
        ownerChat.remove(uuid);
    }
}