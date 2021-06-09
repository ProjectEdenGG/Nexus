package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.Loadout;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MinigamerLoadoutEvent extends MinigamerEvent {
    private final @NotNull Loadout loadout;
    public MinigamerLoadoutEvent(@NotNull Minigamer minigamer, @NotNull Loadout loadout) {
        super(minigamer);
        this.loadout = loadout;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
