package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KangarooJumping extends TeamlessMechanic {
    @Override
    public String getName() {
        return "Kangaroo Jumping";
    }

    @Override
    public String getDescription() {
        return "Jump higher and higher and be the first to the finish!";
    }

    @Override
    public ItemStack getMenuItem() {
        return new ItemStack(Material.LEATHER_BOOTS);
    }

    @Override
    public void onStart(Match match) {
    }

    @EventHandler
    public void onPlayerPressurePlate(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (event.getClickedBlock().getType() != Material.STONE_PLATE) return;
        Minigamer minigamer = PlayerManager.get(event.getPlayer());
        if (!minigamer.isPlaying(this)) return;
        minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 10));
        minigamer.getPlayer().sendMessage(Utils.getPrefix("Kangaroo Jump") + "Boooiiinnnggg!");
        event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getLocation()).setType(Material.AIR);
        minigamer.getMatch().getTasks().wait(10 * 20, () -> event.getClickedBlock().getWorld().getBlockAt(event.getClickedBlock().getLocation()).setType(Material.STONE_PLATE));
    }

    boolean winnable = true;

    @EventHandler
    public void onEnterWinningArea(RegionEnteredEvent event) {
        Minigamer minigamer = PlayerManager.get(event.getPlayer());
        if (!minigamer.isPlaying(this)) return;
        if (!event.getRegion().getId().equalsIgnoreCase("kangarooJumping_" + minigamer.getMatch().getArena().getName() + "_winningRegion")) return;
        if(!winnable) return;
        minigamer.scored();
        minigamer.getMatch().broadcast("&e" + minigamer.getColoredName() + " has reached the finish area!");
        minigamer.getMatch().getTasks().wait(5 * 20, () -> minigamer.getMatch().end());
    }
}

