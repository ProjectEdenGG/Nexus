package me.pugabyte.bncore.features.holidays.pugmas20;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.features.holidays.pugmas20.models.QuestNPC;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;

public class Pugmas20 implements Listener {
	@Getter
	public static final String region = "pugmas20";
	@Getter
	public static final World world = Bukkit.getWorld("safepvp");
	@Getter
	public static final String PREFIX = StringUtils.getPrefix("Pugmas20");
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(world);
	// Dates
	public static final LocalDateTime openingDay = LocalDateTime.of(2020, 12, 1, 0, 0, 0, 0);
	public static final LocalDateTime secondChance = LocalDateTime.of(2020, 12, 25, 0, 0, 0, 0);
	public static final LocalDateTime closingDay = LocalDateTime.of(2021, 1, 11, 0, 0, 0, 0);
	// Advent Menu

	public Pugmas20() {
		BNCore.registerListener(this);

		AdventMenu.loadHeads();
		new AdventChests();
		new Train();
		new Ores();
	}

	public static Location pugmasLoc(int x, int y, int z) {
		return new Location(world, x, y, z);
	}

	public static ItemBuilder pugmasItem(Material material) {
		return new ItemBuilder(material).lore("Pugmas20 Item");
	}

	public static boolean isBeforePugmas(LocalDateTime localDateTime) {
		return localDateTime.isBefore(openingDay);
	}

	public static boolean isPastPugmas(LocalDateTime localDateTime) {
		return localDateTime.isAfter(closingDay);
	}

	public static boolean isSecondChance(LocalDateTime localDateTime) {
		return ((localDateTime.isEqual(secondChance) || localDateTime.isAfter(secondChance))
				&& !isPastPugmas(localDateTime));
	}

	public static boolean isAtPugmas(Player player) {
		return WGUtils.isInRegion(player.getLocation(), region);
	}

	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		QuestNPC npc = QuestNPC.getByID(event.getNPC().getId());
		if (npc == null) return;
		if (!new CooldownService().check(event.getClicker(), "Pugmas20_NPC", Time.SECOND.x(2)))
			return;
		npc.sendScript(event.getClicker());
	}
}
