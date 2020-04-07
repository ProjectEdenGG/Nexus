package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

@Regenerating("regen")
public final class Paintball extends BalancedTeamMechanic {

	@Override
	public String getName() {
		return "Paintball";
	}

	@Override
	public String getDescription() {
		return "Shoot players";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.SNOWBALL);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		event.getAttacker().scored();
		if (event.getAttacker().getTeam().getScore(event.getMatch()) == event.getMatch().getArena().getCalculatedWinningScore(event.getMatch()) - 1)
			event.getMatch().broadcast(event.getAttacker().getColoredName() + " &3took the final kill from " + event.getMinigamer().getColoredName());
		event.getAttacker().getMatch().scored(event.getAttacker().getTeam());
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
		if (victim.isPlaying(this) && attacker.isPlaying(this))
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
		if (!region.contains(WGUtils.toVector(hitBlock.getLocation()))) return;
		for (BlockFace face : BlockFace.values()) {
			Block relative = hitBlock.getRelative(face);
			if (!region.contains(WGUtils.toVector(relative.getLocation()))) continue;
			changeBlockColor(minigamer, relative);
		}
	}

	public void changeBlockColor(Minigamer minigamer, Block block) {
		ColorType colorType = ColorType.fromChatColor(minigamer.getTeam().getColor());
		if (MaterialTag.COLORABLE.isTagged(block.getType()))
			block.setType(colorType.switchColor(block.getType()));
		else
			switch (block.getType()) {
				case SAND:
					block.setType(colorType.getConcretePowder());
					break;
				case GLASS_PANE:
					block.setType(colorType.getStainedGlassPane());
					break;
				case QUARTZ_BLOCK:
				case SNOW_BLOCK:
				case SANDSTONE:
					block.setType(colorType.getConcrete());
					break;
				case TERRACOTTA:
				case PACKED_ICE:
				case ICE:
					block.setType(colorType.getTerracotta());
					break;
			}
	}

	// TODO:
	// Guns
	// Different objects to throw (enderpearl/snowball/armour stand with colored blocks)

}
