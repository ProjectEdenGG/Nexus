package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.halloween20.models.ComboLockNumber;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestNPC;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.features.holidays.halloween20.models.SoundButton;
import me.pugabyte.bncore.features.holidays.halloween20.quest.Gate;
import me.pugabyte.bncore.features.holidays.halloween20.quest.menus.Halloween20Menus;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.pugabyte.bncore.utils.Utils.runCommandAsConsole;

public class Halloween20 implements Listener {
	@Getter
	public static final String region = "halloween20";
	@Getter
	public static final World world = Bukkit.getWorld("safepvp");
	@Getter
	public static final String PREFIX = StringUtils.getPrefix("Halloween 2020");
	public WorldGuardUtils utils = new WorldGuardUtils(world);

	public Halloween20() {
		new LostPumpkins();
		new ShootingRange();
		BNCore.registerListener(this);
	}

	public static void start(Player player) {
		player.teleport(new Location(Bukkit.getWorld("safepvp"), 297.50, 161.00, -2034.50, .00F, .00F));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Time.SECOND.x(3), 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Time.SECOND.x(14), 1));
		player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1f, 1f);
	}

	public static void sendInstructions(Player player) {
		Utils.send(player, "&7&oTo unlock the gate, you will need to find numbers that you can use to unlock it. " +
				"You can find these numbers laying around, or inside of special chests (ender chests) located anywhere from this level or above. " +
				"You will also find clues hidden around the map that will help you figure out the combination. " +
				"Try asking people in the city for help.");
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
		if (!utils.getPlayersInRegion(region).contains(event.getPlayer())) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		ComboLockNumber number = ComboLockNumber.getByLocation(event.getClickedBlock().getLocation());
		if (number == null) return;
		event.setCancelled(true);
		Tasks.wait(1, () -> number.onFind(event.getPlayer()));
	}

	// Open Combo Lock
	@EventHandler
	public void onComboLockInteract(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (utils.isInRegion(event.getClickedBlock().getLocation(), region + "_combolock"))
			Halloween20Menus.openComboLock(event.getPlayer());
	}

	@EventHandler
	public void onEnterGateRegion(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region + "_gate_open")) return;
		Halloween20User user = new Halloween20Service().get(event.getPlayer());
		if (user.getCombinationStage() != QuestStage.Combination.NOT_STARTED) return;
		Gate gate = new Gate(event.getPlayer());
		gate.open();
		Tasks.wait(Time.SECOND.x(4), gate::teleportIn);
		Tasks.wait(Time.SECOND.x(5), gate::close);
		Tasks.wait(Time.SECOND.x(8), () -> Utils.send(event.getPlayer(), "&7&oYou enter the land of the dead, and the large gate shuts behind you. You try to open it, but it appears to be locked."));
		Tasks.wait(Time.SECOND.x(14), () -> QuestNPC.DIEGO.sendScript(event.getPlayer()));
		Tasks.wait(Time.SECOND.x(15) + 300, () -> sendInstructions(event.getPlayer()));
	}


	@EventHandler
	public void onButtonClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		SoundButton button = SoundButton.getByLocation(event.getClickedBlock().getLocation());
		if (button == null) return;

		SoundUtils.playSound(event.getPlayer(), button.getSound(), 1, 1);

		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getFoundButtons().contains(button)) {
			if (new CooldownService().check(event.getPlayer(), "halloween20-button-alreadyfound", Time.SECOND.x(10)))
				user.send(PREFIX + "You've already found this button!");
			return;
		}

		user.getFoundButtons().add(button);
		service.save(user);

		user.send(PREFIX + "You have found a spooky button! &e(" + user.getFoundButtons().size() + "/" + SoundButton.values().length + ")");

		if (user.getFoundButtons().size() != SoundButton.values().length)
			return;

		runCommandAsConsole("lp user " + event.getPlayer().getName() + " permission set powder.powder.spookyscaryskeletons true");
		user.send(PREFIX + "You have unlocked the Spooky Scary Skeletons song! &c/songs");
	}

}
