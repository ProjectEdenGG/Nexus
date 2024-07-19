package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class BalloonEditor implements Listener {
	private static final String REGION_EDIT = Pugmas24.get().getRegionName() + "_balloon_edit";  // TODO FINAL: DEFINE REGION
	private static final String REGION_SCHEM = Pugmas24.get().getRegionName() + "_balloon_schem";  // TODO FINAL: DEFINE REGION
	private static final Location SCHEM_PASTE = Pugmas24.get().location(-620, 162, -3214); // TODO FINAL: LOCATION
	private static final String SCHEM_BASE = "pugmas24/balloons/";
	private static final String SCHEM_TEMPLATE = SCHEM_BASE + "template/";
	public static final int TEMPLATE_SIZE = 1;

	private static final WorldEditUtils worldedit = Pugmas24.get().worldedit();
	private static final WorldGuardUtils worldguard = Pugmas24.get().worldguard();
	private static final String PREFIX = StringUtils.getPrefix("BalloonEditor");
	private static final TeleportCause ignoreTeleportCause = TeleportCause.NETHER_PORTAL;

	@Getter
	private static boolean beingUsed = false;
	@Getter
	private static boolean stopping = false;
	private static int schemId = 1;
	@Getter
	private static Nerd editor;
	private static boolean flying = false;
	private static boolean allowedFlight = false;
	private static boolean lostEditor = false;
	private static int lostEditorTask;
	private static boolean savingSchem = false;

	@Getter
	@Setter
	private static ColorType brushColor = ColorType.RED;

	public BalloonEditor() {
		Nexus.registerListener(this);

		Pugmas24.get().forceLoadChunks(REGION_EDIT);

		resetBalloon();
	}

	public static void shutdown() {
		if (!isBeingUsed())
			return;

		saveBalloon();
	}

	public static boolean reload() {
		if (!isBeingUsed())
			return false;

		if (isStopping())
			return true;

		stopping = true;
		saveBalloon();
		return true;
	}

	public static void editBalloon(Nerd editor) {
		beingUsed = true;
		BalloonEditor.editor = editor;

		flying = editor.getPlayer().isFlying();
		allowedFlight = editor.getPlayer().getAllowFlight();


		File file = worldedit.getSchematicFile(getSchematicPath(), true);
		if (file.exists()) {
			pasteBalloon(getSchematicPath());
			send("pasted previous balloon");
		} else {
			resetBalloon();
			send("pasted template");
		}

		PlayerUtils.giveItem(editor, getReplaceBrush());
		editor.getPlayer().setAllowFlight(true);
		editor.getPlayer().setFlying(true);

		send("editing");
	}

	public static void saveBalloon() {
		Player player = editor.getPlayer();
		if (player == null)
			throw new InvalidInputException(PREFIX + "player cannot be null");

		PlayerUtils.removeItem(player, getReplaceBrush());

		String filePath = getSchematicPath();
		Region region = worldguard.convert(worldguard.getProtectedRegion(REGION_SCHEM));

		String selectCommand = "rg select " + REGION_SCHEM;
		String copyCommand = "/copy";
		String saveCommand = "/schem save " + filePath + " -f";
		String multiCommand = "mcmd " + selectCommand + " ;; wait 10 ;; " + copyCommand + " ;; wait 10 ;; " + saveCommand + " --asOp";

		GameMode originalGameMode = player.getGameMode();
		Location originalLocation = player.getLocation().clone();
		Location location = worldedit.toLocation(region.getMinimumPoint());

		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(location, ignoreTeleportCause);

		savingSchem = true;

		PlayerUtils.runCommandAsOp(player, multiCommand);
		Tasks.wait(30, () -> {
			PlayerUtils.runCommandAsOp(player, "/deselect");
			savingSchem = false;

			player.teleportAsync(originalLocation);
			player.setGameMode(originalGameMode);

			Tasks.wait(1, () -> {
				send("Schematic saved");
				reset();
			});
		});
	}

	public static void reset() {
		editor.getPlayer().setAllowFlight(allowedFlight);
		editor.getPlayer().setFlying(flying);

		stopping = false;
		lostEditor = false;
		if (lostEditorTask != -1)
			Tasks.cancel(lostEditorTask);
		lostEditorTask = -1;

		editor = null;
		beingUsed = false;
	}

	public static void resetBalloon() {
		selectTemplate(schemId);
	}

	public static void selectTemplate(int id) {
		schemId = id;
		pasteBalloon(SCHEM_TEMPLATE + schemId);
	}

	//

	private static void sendCooldown(String message, String key) {
		if (!new CooldownService().check(editor.getUuid(), key, TickTime.SECOND))
			return;

		send(message);
	}

	private static void send(String message) {
		editor.sendMessage(PREFIX + message);
	}

	private static void pasteBalloon(String filePath) {
		clearSchemRegion();
		Tasks.wait(1, () -> worldedit.paster().file(filePath).at(SCHEM_PASTE).pasteAsync());

	}

	private static void clearSchemRegion() {
		// templates are different sizes, so clear region before paste
		for (Block block : worldedit.getBlocks(worldguard.getRegion(REGION_SCHEM))) {
			block.setType(Material.AIR);
		}
	}

	private static String getSchematicPath() {
		return SCHEM_BASE + editor.getUniqueId();
	}

	public static boolean isEditing(Player player) {
		if (!Pugmas24.get().isAtEvent(player))
			return false;

		if (!isBeingUsed())
			return false;

		if (editor == null || player == null)
			return false;

		return player.getUniqueId().toString().equalsIgnoreCase(editor.getUniqueId().toString());
	}

	public static String getEditorName() {
		return editor.getNickname();
	}

	public static ItemStack getReplaceBrush() {
		return BlockReplaceBrushMenu.getBrushItem().dyeColor(brushColor).build();
	}

	//

	@EventHandler
	public void onSavingSchem(PlayerMoveEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		if (!savingSchem)
			return;

		event.setCancelled(true);
		sendCooldown("&cPlease wait while the schematic is saving", "pugmas24_balloon_editor-schem");
	}

	@EventHandler
	public void onSavingSchem(PlayerTeleportEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		if (!savingSchem)
			return;

		event.setCancelled(true);
		send("&cPlease wait while the schematic is saving");
	}

	@EventHandler
	public void onUseBrush(PlayerInteractEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock))
			return;

		if (!BlockReplaceBrushMenu.isBrushItem(item))
			return;

		event.setCancelled(true);

		Action action = event.getAction();
		if (action.isLeftClick()) {
			new BlockReplaceBrushMenu(item).open(editor.getPlayer());
			return;
		}

		if (!action.isRightClick() || !canEditBlock(clickedBlock))
			return;

		clickedBlock.setType(brushColor.getWool());
		new SoundBuilder(Sound.BLOCK_WOOL_PLACE).receiver(editor).location(clickedBlock).volume(0.5).play();
		event.getPlayer().swingMainHand();
	}

	private boolean canEditBlock(Block block) {
		if (!worldguard.isInRegion(block, REGION_SCHEM))
			return false;

		if (block.getType() == brushColor.getWool())
			return false;

		return MaterialTag.WOOL.isTagged(block);
	}

	@EventHandler
	public void whenEditing(PlayerLeavingRegionEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(REGION_EDIT) || savingSchem)
			return;

		event.setCancelled(true);
		sendCooldown("&cYou're still editing your balloon (Region)", "pugmas24_balloon_editor-leaving_region");
	}

	@EventHandler
	public void whenEditing(PlayerTeleportEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		if (event.getCause() == ignoreTeleportCause || savingSchem)
			return;

		event.setCancelled(true);
		sendCooldown("&cYou're still editing your balloon (Teleport)", "pugmas24_balloon_editor-leaving_teleport");
	}

	@EventHandler
	public void whenEditing(PlayerQuitEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		PlayerUtils.removeItem(event.getPlayer(), getReplaceBrush());

		lostEditor = true;
		lostEditorTask = Tasks.wait(TickTime.SECOND.x(30), () -> {
			if (!lostEditor)
				return;

			reset();
		});
	}

	@EventHandler
	public void whenEditing(PlayerJoinEvent event) {
		if (!isEditing(event.getPlayer()))
			return;

		lostEditor = false;
		Tasks.cancel(lostEditorTask);
		lostEditorTask = -1;

		PlayerUtils.giveItem(event.getPlayer(), getReplaceBrush());
	}
}
