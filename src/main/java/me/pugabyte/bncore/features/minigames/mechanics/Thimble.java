package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public final class Thimble extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Thimble";
	}

	@Override
	public String getDescription() {
		return "Description here.";
	}

	@Override
	public void kill(Minigamer minigamer) {

	}

	@EventHandler
	public void enterRegion(RegionEnteredEvent event){
		if(event.getRegion().getId().equals("thimble_pool")) {
			Player player = event.getPlayer();
			if(Utils.isInWater(player)) {
				if(player.getInventory().getHelmet() == null) return;

				Minigamer minigamer = PlayerManager.get(player);
				if (!minigamer.isPlaying(Thimble.class)) return;

				ItemStack item = player.getInventory().getHelmet();
				short durability = item.getDurability();

				Location location = player.getLocation();
				location.getBlock().setType(Material.CONCRETE);
				location.getBlock().setData(Byte.parseByte(Short.toString(durability)));

				Color color = Utils.getColor(Utils.getColor((int) durability));
				Location fireworkLocation = location.add(0.0,2.0,0.0);

				Firework firework = (Firework) player.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
				FireworkEffect effect = FireworkEffect.builder()
						.with(FireworkEffect.Type.BALL)
						.withColor(color)
						.build();

				FireworkMeta meta = firework.getFireworkMeta();
				meta.setPower(0);

				meta.addEffect(effect);
				firework.setFireworkMeta(meta);
				minigamer.teleport(minigamer.getMatch().getArena().getLobby().getLocation());
				Utils.wait(1, firework::detonate);

			}
		}
	}
}
