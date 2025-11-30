package gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.afk.events.NowAFKEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.UUID;

public class Pugmas25BalloonEditor implements Listener {
	private static final Pugmas25 PUGMAS = Pugmas25.get();
	protected static final String REGION_EDIT = Pugmas25BalloonManager.REGION_BASE + "edit";
	protected static final String REGION_SCHEM = Pugmas25BalloonManager.REGION_BASE + "schem";
	protected static final Location SCHEM_PASTE = PUGMAS.location(-620, 162, -3214);
	protected static final int TEMPLATE_SIZE = Pugmas25BalloonManager.getTotalTemplateSchematics();
	protected static final ColorType defaultBrushColor = ColorType.RED;

	public static final String PREFIX = StringUtils.getPrefix("BalloonEditor");

	@Getter
	private static boolean beingUsed = false;
	@Getter
	private static boolean stopping = false;
	protected static int schemId = 1;
	@Getter
	private static Nerd editor;
	protected static boolean flying = false;
	protected static boolean allowedFlight = false;
	protected static boolean lostEditor = false;
	protected static int lostEditorTask = -1;

	@Getter
	@Setter
	private static ColorType brushColor = defaultBrushColor;

	public Pugmas25BalloonEditor() {
		Nexus.registerListener(this);

		PUGMAS.forceLoadRegions(REGION_EDIT);
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

	public static void editBalloon(Nerd nerd) {
		beingUsed = true;
		editor = nerd;

		flying = editor.getPlayer().isFlying();
		allowedFlight = editor.getPlayer().getAllowFlight();

		if (hasSchematic())
			pasteBalloon(getSchematicPath());
		else
			resetBalloon();

		Pugmas25BalloonEditorUtils.giveBrush();
		Pugmas25BalloonEditorUtils.enableFlight();
	}

	public static boolean hasSchematic() {
		return hasSchematic(editor.getUniqueId());
	}

	public static boolean hasSchematic(UUID uuid) {
		File file = Pugmas25BalloonManager.worldedit.getSchematicFile(getSchematicPath(uuid), true);
		return file.exists();
	}

	public static void saveBalloon() {
		Player player = editor.getPlayer();
		if (player == null)
			throw new InvalidInputException(PREFIX + "player cannot be null");

		new Pugmas25UserService().edit(player, user -> user.setBalloonSchemExists(true));
		Pugmas25BalloonEditorUtils.removeBrush();

		String filePath = getSchematicPath();
		Region region = Pugmas25BalloonManager.worldguard.convert(Pugmas25BalloonManager.worldguard.getProtectedRegion(REGION_SCHEM));

		Pugmas25BalloonManager.worldedit.save(filePath, region);
		Tasks.wait(1, Pugmas25BalloonEditor::reset);
	}

	public static void reset() {
		Player player = editor.getPlayer();
		if (player != null && player.isOnline()) {
			Pugmas25BalloonEditorUtils.resetFlight();
			Pugmas25BalloonEditorUtils.removeBrush();
		}

		brushColor = defaultBrushColor;
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
	}

	//

	protected static void pasteBalloon(String filePath) {
		Pugmas25BalloonManager.pasteBalloonAsync(filePath, SCHEM_PASTE);

	}

	protected static String getSchematicPath() {
		return getSchematicPath(editor.getUniqueId());
	}

	protected static String getSchematicPath(UUID uuid) {
		return Pugmas25BalloonManager.SCHEM_USER + uuid;
	}

	//

	@EventHandler
	public void onUseBrush(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25BalloonEditorUtils.isEditing(player))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		Block clickedBlock = event.getClickedBlock();
		if (Nullables.isNullOrAir(clickedBlock))
			return;

		if (!Pugmas25BlockReplaceBrushMenu.isBrushItem(item))
			return;

		event.setCancelled(true);

		Action action = event.getAction();
		if (action.isLeftClick()) {
			new Pugmas25BlockReplaceBrushMenu(item).open(player);
			return;
		}

		if (!action.isRightClick() || !canEditBlock(clickedBlock))
			return;

		clickedBlock.setType(brushColor.getWool());
		new SoundBuilder(Sound.BLOCK_WOOL_PLACE).receiver(player).location(clickedBlock).volume(0.5).play();
		player.swingMainHand();
	}

	private boolean canEditBlock(Block block) {
		if (!Pugmas25BalloonManager.worldguard.isInRegion(block, REGION_SCHEM))
			return false;

		if (block.getType() == brushColor.getWool())
			return false;

		return MaterialTag.WOOL.isTagged(block);
	}

	@EventHandler
	public void whenEditing(PlayerLeavingRegionEvent event) {
		if (!Pugmas25BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(REGION_EDIT))
			return;

		event.setCancelled(true);
		PUGMAS.sendCooldown(editor.getPlayer(), "&cYou're still editing your balloon", "pugmas25_balloon_editor-editing");
	}

	@EventHandler
	public void whenEditing(PlayerTeleportEvent event) {
		if (!Pugmas25BalloonEditorUtils.isEditing(event.getPlayer()))
			return;

		if (Pugmas25BalloonManager.worldguard.isInRegion(event.getTo(), REGION_EDIT))
			return;

		event.setCancelled(true);
		PUGMAS.sendCooldown(editor.getPlayer(), "&cYou're still editing your balloon", "pugmas25_balloon_editor-editing");
	}

	@EventHandler
	public void whenEditing(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25BalloonEditorUtils.isEditing(player))
			return;

		lostEditor = true;
		lostEditorTask = Tasks.wait(TickTime.SECOND.x(30), () -> {
			if (!lostEditor)
				return;

			reset();
		});
	}

	@EventHandler
	public void whenEditing(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25BalloonEditorUtils.isEditing(player)) {
			if (Pugmas25BalloonManager.worldguard.isInRegion(player, REGION_EDIT)) {
				Pugmas25BalloonEditorUtils.removeBrush(player);
				player.setGameMode(GameMode.SURVIVAL);
				Pugmas25BalloonEditorUtils.disableFlight(player);
				PlayerUtils.runCommandAsOp(player, "/deselect");
			}
			return;
		}

		lostEditor = false;
		Tasks.cancel(lostEditorTask);
		lostEditorTask = -1;
	}

	@EventHandler
	public void onAFK(NowAFKEvent event) {
		if (!Pugmas25BalloonEditorUtils.isEditing(event.getUser().getPlayer()))
			return;

		saveBalloon();
	}

}
