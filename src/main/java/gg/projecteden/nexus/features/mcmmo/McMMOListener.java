package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.events.skills.unarmed.McMMOPlayerDisarmEvent;
import com.gmail.nossr50.util.player.UserManager;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.afk.AFKCommand;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.mcmmo.reset.McMMOResetProvider.ResetSkillType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class McMMOListener implements Listener {

	public McMMOListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onAfkGain(McMMOPlayerXpGainEvent event) {
		if (AFK.get(event.getPlayer()).isAfk()) {
			PlayerUtils.sendWithCooldown("afk_exp-gain", TickTime.MINUTE, event.getPlayer(),
				StringUtils.getPrefix(AFKCommand.class) + "Exp gain is disabled while AFK");

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRepair(McMMOPlayerRepairCheckEvent event) {
		if (AFK.get(event.getPlayer()).isAfk()) {
			PlayerUtils.sendWithCooldown("afk_cancel-repair", TickTime.MINUTE, event.getPlayer(),
				StringUtils.getPrefix(AFKCommand.class) + "Prevented waste of materials on repair while AFK");

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSalvage(McMMOPlayerSalvageCheckEvent event) {
		if (AFK.get(event.getPlayer()).isAfk()) {
			PlayerUtils.sendWithCooldown("afk_cancel-salvage", TickTime.MINUTE, event.getPlayer(),
				StringUtils.getPrefix(AFKCommand.class) + "Prevented waste of materials on salvage while AFK");

			event.setCancelled(true);
		}
	}

	private static final List<PrimarySkillType> MELEE_SKILLS = List.of(PrimarySkillType.AXES, PrimarySkillType.SWORDS, PrimarySkillType.UNARMED);

	@EventHandler
	public void onEndExpGain(McMMOPlayerXpGainEvent event) {
		final Player player = event.getPlayer();
		if (player.getWorld().getEnvironment() != Environment.THE_END)
			return;

		final WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (!worldguard.getRegionNamesAt(player.getLocation()).contains("endermanfarm-deny"))
			return;

		if (!MELEE_SKILLS.contains(event.getSkill()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlacePotionLauncherHopper(BlockPlaceEvent event) {
		if (!event.getBlockPlaced().getType().equals(Material.HOPPER))
			return;

		NBTItem itemNBT = new NBTItem(event.getItemInHand());
		if (!itemNBT.hasNBTData())
			return;

		if (itemNBT.asNBTString().contains("&8Potion Launcher"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onMcMMOPlayerDisarm(McMMOPlayerDisarmEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		int skillLevel = event.getSkillLevel();
		if (skillLevel > 0 && skillLevel % McMMO.TIER_ONE == 0)
			Koda.say(Nickname.of(event.getPlayer()) + " reached level " + skillLevel + " in " + StringUtils.camelCase(event.getSkill().name()) + "! Congratulations!");

		final McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(event.getPlayer());

		final List<ResetSkillType> tierOne = new ArrayList<>();
		final List<ResetSkillType> tierTwo = new ArrayList<>();
		for (ResetSkillType skillType : ResetSkillType.values()) {
			final int level = mcMMOPlayer.getSkillLevel(skillType.asPrimarySkill());
			if (level >= McMMO.TIER_ONE)
				tierOne.add(skillType);

			if (level >= McMMO.TIER_TWO)
				tierTwo.add(skillType);
		}

		if (tierTwo.size() == ResetSkillType.values().length)
			if (mcMMOPlayer.getSkillLevel(event.getSkill()) == McMMO.TIER_TWO)
				Koda.say(Nickname.of(event.getPlayer()) + " has exceptionally mastered all their skills! Congratulations!");
		else if (tierOne.size() == ResetSkillType.values().length)
			if (mcMMOPlayer.getSkillLevel(event.getSkill()) == McMMO.TIER_ONE)
				Koda.say(Nickname.of(event.getPlayer()) + " has mastered all their skills! Congratulations!");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (WorldGroup.of(event.getEntity()) != WorldGroup.SURVIVAL)
			return;

		var totem = PlayerUtils.searchInventory(event.getPlayer(), ItemModelType.KEEP_INVENTORY_TOTEM);
		if (Nullables.isNullOrAir(totem))
			return;

		event.setKeepInventory(true);
		event.getDrops().clear();
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		totem.subtract();
	}

}
