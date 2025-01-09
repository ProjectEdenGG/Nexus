package gg.projecteden.nexus.features.legacy;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.commands.staff.admin.CreativeFlagsCommand;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.spawnlimits.SpawnLimits.SpawnLimitType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameRule;
import org.bukkit.World;

import static gg.projecteden.nexus.utils.Utils.registerListeners;
/*
	TODO
		Reset
			playerdata.dat
				EnderItems
				Inventory
				XpLevel
				XpP
				XpSeed
				XpTotal
				bukkit.expToDrop
				bukkit.newExp
				bukkit.newLevel
				bukkit.newTotalExp
				foodExhaustionLevel
				foodLevel
				foodSaturationLevel
				foodTickTimer
				recipeBook.recipes
				recipeBook.toBeDisplayed
				seenCredits
				???
			Inventories
				Delete mvinv files & playerdata.dat:inventory
			Advancements
				Delete world/advancements
			Ctrl+f
				"world"
				"survival"

 */

@Environments({Env.PROD, Env.STAGING})
public class Legacy extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Legacy");

	@Override
	public void onStart() {
		registerListeners(getClass().getPackage().getName() + ".listeners");

		setGameRules();
	}

	private void setGameRules() {
		for (World world : WorldGroup.LEGACY.getWorlds()) {
			for (SpawnLimitType limitType : SpawnLimitType.values())
				limitType.set(world, 0);

			world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
			world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
			world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
			world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
			world.setGameRule(GameRule.DISABLE_RAIDS, true);
			world.setGameRule(GameRule.DO_INSOMNIA, false);
			world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 4);

			final ProtectedRegion globalRegion = new WorldGuardUtils(world).getManager().getRegion("__global__");
			if (globalRegion != null) {
				CreativeFlagsCommand.setFlags(world);
				globalRegion.setFlag(Flags.PASSTHROUGH, State.DENY);
				globalRegion.setFlag(Flags.MOB_SPAWNING, State.DENY);
				globalRegion.setFlag(Flags.INVINCIBILITY, State.ALLOW);
				globalRegion.setFlag(Flags.CHEST_ACCESS, State.ALLOW);
				globalRegion.setFlag(Flags.USE, State.ALLOW);
			}
		}
	}

}
