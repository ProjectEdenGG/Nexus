package me.pugabyte.bncore.features.holidays.halloween20;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.halloween20.models.ComboLockNumber;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestNPC;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Halloween20 implements Listener {
	@Getter
	public static String region = "halloween20";
	@Getter
	public static World world = Bukkit.getWorld("safepvp");
	@Getter
	public static String PREFIX = StringUtils.getPrefix("Halloween20");

	public Halloween20() {
		new LostPumpkins();
		new ShootingRange();
		BNCore.registerListener(this);
	}

	// Talking NPCs Handler
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		QuestNPC npc = QuestNPC.getByID(event.getNPC().getId());
		if (npc == null) return;
		if (!new CooldownService().check(event.getClicker(), "Halloween20_NPC", Time.SECOND.x(2)))
			return;
		npc.sendScript(event.getClicker());
	}

	// ComboLock Handler
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!new WorldGuardUtils(world).getPlayersInRegion(region).contains(event.getPlayer())) return;
		ComboLockNumber number = ComboLockNumber.getByLocation(event.getClickedBlock().getLocation());
		if (number == null) return;
		number.onFind(event.getPlayer());
	}


}
