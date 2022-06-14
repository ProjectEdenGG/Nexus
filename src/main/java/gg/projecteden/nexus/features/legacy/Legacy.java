package gg.projecteden.nexus.features.legacy;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.spawnlimits.SpawnLimits.SpawnLimitType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.utils.Env;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
			Update getWorldDisplayName

 */

@Environments(Env.TEST)
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

			PlayerUtils.runCommandAsConsole("creativeflags " + world.getName());
			PlayerUtils.runCommandAsConsole(WorldGuardFlagUtils.command(world, Flags.BUILD, State.DENY));
			PlayerUtils.runCommandAsConsole(WorldGuardFlagUtils.command(world, Flags.MOB_SPAWNING, State.DENY));
			PlayerUtils.runCommandAsConsole(WorldGuardFlagUtils.command(world, Flags.INVINCIBILITY, State.ALLOW));
		}
	}

	public static List<ItemStack> convertItems(List<ItemStack> contents) {
		return contents.stream().map(Legacy::convertItem).toList();
	}

	private static ItemStack convertItem(ItemStack item) {
		/* TODO Convert items
			Crate Keys
			Coupons
			All items that aren’t paper
		 */
		return item;
	}

}
