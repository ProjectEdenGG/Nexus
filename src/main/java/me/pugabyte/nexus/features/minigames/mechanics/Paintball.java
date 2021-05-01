package me.pugabyte.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.Regenerating;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Regenerating("regen")
public final class Paintball extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "Paintball";
	}

	@Override
	public @NotNull String getDescription() {
		return "Shoot players";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SNOWBALL);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		event.getAttacker().scored();
		event.getAttacker().getMatch().scored(event.getAttacker().getTeam());

		if (event.getAttacker().getTeam().getScore(event.getMatch()) == event.getMatch().getArena().getCalculatedWinningScore(event.getMatch()) - 1)
			event.getMatch().broadcast(event.getAttacker().getColoredName() + " &3took the final kill from " + event.getMinigamer().getColoredName());

		super.onDeath(event);
	}

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (event.getHitEntity() == null) return;
		if (!(event.getEntity() instanceof Snowball)) return;
		if (!(event.getHitEntity() instanceof Player)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		Minigamer victim = PlayerManager.get((Player) event.getHitEntity());
		Minigamer attacker = PlayerManager.get((Player) event.getEntity().getShooter());
		if (victim.equals(attacker)) return;
		if (victim.isPlaying(this) && attacker.isPlaying(this) && !victim.getTeam().equals(attacker.getTeam()))
			kill(victim, attacker);
	}

	@EventHandler
	public void onSnowBallHitBlock(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		Minigamer minigamer = PlayerManager.get((Player) event.getEntity().getShooter());
		if (!minigamer.isPlaying(this)) return;
		Block hitBlock = event.getHitBlock();
		if (hitBlock == null) return;
		Region region = minigamer.getMatch().getArena().getRegion("regen");
		if (region == null) return;
		if (!region.contains(minigamer.getMatch().getWGUtils().toBlockVector3(hitBlock.getLocation()))) return;
		for (BlockFace face : BlockFace.values()) {
			Block relative = hitBlock.getRelative(face);
			if (!region.contains(minigamer.getMatch().getWGUtils().toBlockVector3(relative.getLocation()))) continue;
			changeBlockColor(minigamer, relative);
		}
	}

	public void changeBlockColor(Minigamer minigamer, Block block) {
		ColorType colorType = ColorType.of(minigamer.getTeam().getChatColor());
		Material type = block.getType();
		if (MaterialTag.COLORABLE.isTagged(type))
			block.setType(colorType.switchColor(type));
		else
			switch (type) {
				case SAND:
				case RED_SAND:
					block.setType(ColorType.switchColor(Material.WHITE_CONCRETE_POWDER, colorType.getSimilarDyeColor()));
					break;
				case QUARTZ_BLOCK:
				case SNOW_BLOCK:
				case SANDSTONE:
					block.setType(ColorType.switchColor(Material.WHITE_CONCRETE, colorType.getSimilarDyeColor()));
					break;
				case TERRACOTTA:
				case PACKED_ICE:
				case ICE:
					block.setType(ColorType.switchColor(Material.WHITE_TERRACOTTA, colorType.getSimilarDyeColor()));
					break;
			}
	}

	// TODO:
	// Guns
	// Different objects to throw (enderpearl/snowball/armour stand with colored blocks)

}
