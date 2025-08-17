package gg.projecteden.nexus.features.events.y2020.halloween20;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.ComboLockNumber;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.QuestNPC;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.SoundButton;
import gg.projecteden.nexus.features.events.y2020.halloween20.quest.Gate;
import gg.projecteden.nexus.features.events.y2020.halloween20.quest.menus.CombinationLockProvider;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.halloween20.Halloween20Service;
import gg.projecteden.nexus.models.halloween20.Halloween20User;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
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
import org.bukkit.potion.PotionEffectType;

public class Halloween20 implements Listener {
	@Getter
	public static final String region = "halloween20";
	@Getter
	public static final String PREFIX = StringUtils.getPrefix("Halloween 2020");

	public Halloween20() {
		new LostPumpkins();
		new ShootingRange();
		Nexus.registerListener(this);
	}

	public static World getWorld() {
		return Bukkit.getWorld("safepvp");
	}

	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static void start(Player player) {
		player.teleportAsync(new Location(Bukkit.getWorld("safepvp"), 297.50, 161.00, -2034.50, .00F, .00F));
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(3)).build());
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SLOW_FALLING).duration(TickTime.SECOND.x(14)).build());
		player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1f, 1f);
	}

	public static void sendInstructions(Player player) {
		PlayerUtils.send(player, "&7&oTo unlock the gate, you will need to find numbers that you can use to unlock it. " +
				"You can find these numbers laying around, or inside of special chests (ender chests) located anywhere from this level or above. " +
				"You will also find clues hidden around the map that will help you figure out the combination. " +
				"Try asking people in the city for help.");
	}

	// Talking NPCs Handler
	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		QuestNPC npc = QuestNPC.getByID(event.getNPC().getId());
		if (npc == null) return;
		if (CooldownService.isOnCooldown(event.getClicker(), "Halloween20_NPC", TickTime.SECOND.x(2)))
			return;
		npc.sendScript(event.getClicker());
	}

	// ComboLock Handler
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!worldguard().getPlayersInRegion(region).contains(event.getPlayer())) return;
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
		if (worldguard().isInRegion(event.getClickedBlock().getLocation(), region + "_combolock"))
			new CombinationLockProvider().open(event.getPlayer());
	}

	@EventHandler
	public void onEnterGateRegion(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(region + "_gate_open")) return;
		Halloween20User user = new Halloween20Service().get(event.getPlayer());
		if (user.getCombinationStage() != QuestStage.Combination.NOT_STARTED) return;
		Gate gate = new Gate(event.getPlayer());
		gate.open();
		Tasks.wait(TickTime.SECOND.x(4), gate::teleportIn);
		Tasks.wait(TickTime.SECOND.x(5), gate::close);
		Tasks.wait(TickTime.SECOND.x(8), () -> PlayerUtils.send(event.getPlayer(), "&7&oYou enter the land of the dead, and the large gate shuts behind you. You try to open it, but it appears to be locked."));
		Tasks.wait(TickTime.SECOND.x(14), () -> QuestNPC.DIEGO.sendScript(event.getPlayer()));
		Tasks.wait(TickTime.SECOND.x(15) + 300, () -> sendInstructions(event.getPlayer()));
	}

	@EventHandler
	public void onButtonClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		SoundButton button = SoundButton.getByLocation(event.getClickedBlock().getLocation());
		if (button == null) return;

		new SoundBuilder(button.getSound()).receiver(event.getPlayer()).play();

		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getFoundButtons().contains(button)) {
			if (CooldownService.isNotOnCooldown(event.getPlayer(), "halloween20-button-alreadyfound", TickTime.SECOND.x(10)))
				user.sendMessage(PREFIX + "You've already found this button!");
			return;
		}

		user.getFoundButtons().add(button);
		service.save(user);

		user.sendMessage(PREFIX + "You have found a spooky button! &e(" + user.getFoundButtons().size() + "/" + SoundButton.values().length + ")");

		if (user.getFoundButtons().size() != SoundButton.values().length)
			return;

		PermissionChange.set().player(event.getPlayer()).permissions("powder.powder.spookyscaryskeletons").runAsync();
		user.sendMessage(PREFIX + "You have unlocked the Spooky Scary Skeletons song! &c/songs");
	}

}
