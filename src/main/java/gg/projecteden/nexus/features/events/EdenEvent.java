package gg.projecteden.nexus.features.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.EventBreakable.EventBreakableBuilder;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.HasLocation;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.utils.Extensions.isStaff;

public abstract class EdenEvent extends Feature implements Listener {
	protected List<EventBreakable> breakables = new ArrayList<>();

	@Override
	public void onStart() {
		super.onStart();
		registerBreakableBlocks();
	}

	public QuestConfig getConfig() {
		return getClass().getAnnotation(QuestConfig.class);
	}

	public List<IQuest> getQuests() {
		return Arrays.stream(getConfig().quests().getEnumConstants())
				.map(value -> (IQuest) EnumUtils.valueOf(getConfig().quests(), value.name()))
				.toList();
	}

	public boolean isEventActive() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isAfter(getStart().atStartOfDay()) && now.isBefore(getEnd().atStartOfDay());
	}

	public boolean isAfterEvent() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isAfter(getEnd().atStartOfDay());
	}

	public boolean isBeforeEvent() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isBefore(getStart().atStartOfDay());
	}

	public boolean isAtEvent(HasLocation hasLocation) {
		final Location location = hasLocation.getLocation();
		if (!location.getWorld().getName().equals(getConfig().world()))
			return false;

		return new WorldGuardUtils(location).isInRegion(location, getConfig().region());
	}

	public boolean shouldHandle(Player player) {
		if (isStaff(player))
			return true;

		return isEventActive() && isAtEvent(player);
	}

	public Location location(double x, double y, double z) {
		return location(x, y, z, 0, 0);
	}

	public Location location(double x, double y, double z, float yaw, float pitch) {
		return new Location(getWorld(), x, y, z, yaw, pitch);
	}

	@NotNull
	public LocalDate getStart() {
		return toLocalDate(getConfig().start());
	}

	@NotNull
	public LocalDate getEnd() {
		return toLocalDate(getConfig().end());
	}

	@NotNull
	private LocalDate toLocalDate(Date config) {
		return LocalDate.of(config.y(), config.m(), config.d());
	}

	@NonNull
	public String getWorldName() {
		return getConfig().world();
	}

	public World getWorld() {
		return Bukkit.getWorld(getWorldName());
	}

	public WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	@NonNull
	public String getRegionName() {
		return getConfig().region();
	}

	public ProtectedRegion getProtectedRegion() {
		return worldguard().getProtectedRegion(getRegionName());
	}

	public boolean hasPlayers() {
		return worldguard().getPlayersInRegion(getProtectedRegion()).size() > 1;
	}

	public <T extends InteractableEntity> T interactableOf(Entity entity) {
		for (Enum<? extends InteractableEntity> _entity : EnumUtils.valuesExcept(getConfig().entities())) {
			final InteractableEntity interactableEntity = (InteractableEntity) _entity;
			if (interactableEntity.getPredicate() != null)
				if (interactableEntity.getPredicate().test(entity))
					return (T) interactableEntity;
		}

		return null;
	}

	public <T extends InteractableNPC> T interactableOf(NPC npc) {
		for (Enum<? extends InteractableNPC> _npc : EnumUtils.valuesExcept(getConfig().npcs())) {
			final InteractableNPC interactableNPC = (InteractableNPC) _npc;
			if (interactableNPC.getPredicate() != null)
				if (interactableNPC.getPredicate().test(npc))
					return (T) interactableNPC;
		}

		return null;
	}

	@EventHandler
	public void _onNPCRightClick(NPCRightClickEvent event) {
		final Player player = event.getClicker();
		if (!shouldHandle(player))
			return;

		final InteractableNPC npc = interactableOf(event.getNPC());
		if (npc == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getClicker(), quester -> quester.interact(npc, event));
	}

	@EventHandler
	public void _onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		final InteractableEntity entity = interactableOf(event.getRightClicked());
		if (entity == null)
			return;

		event.setCancelled(true);
		new QuesterService().edit(event.getPlayer(), quester -> quester.interact(entity, event));
	}

	@EventHandler
	public void _onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		new QuesterService().edit(player, quester -> quester.interact(event));
	}

	@EventHandler
	public void _onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!shouldHandle(player))
			return;

		if (event.isCancelled()) return;
		if (!isAtEvent(block)) return;
		if (canWorldGuardEdit(player)) return;

		event.setCancelled(true);

		if (breakBlock(event)) return;
		if (handleBlockBreak(event)) return;

		if (new CooldownService().check(player, "event_cantbreak", TickTime.MINUTE)) {
			errorMessage(player, EventErrors.CANT_BREAK);
			EventSounds.VILLAGER_NO.play(player);
		}
	}

	public boolean handleBlockBreak(BlockBreakEvent event) {
		return false;
	}

	public void errorMessage(Player player, String message) {
		PlayerUtils.send(player, PREFIX + message);
	}

	public void registerBreakable(EventBreakableBuilder builder) {
		this.breakables.add(builder.build());
	}

	protected void registerBreakableBlocks() {}

	private @Nullable EventBreakable getBreakable(Block block) {
		return breakables.stream().filter(breakable -> {
			if (!breakable.getBlockMaterials().contains(block.getType()))
				return false;

			if (breakable.getBlockPredicate() != null)
				if (!breakable.getBlockPredicate().test(block))
					return false;

			return true;
		})
		.findFirst()
		.orElse(null);
	}

	private static final Set<Material> CROP_SINGLE_BLOCK = new HashSet<>(Arrays.asList(Material.PUMPKIN, Material.MELON));
	private static final Set<Material> CROP_MULTI_BLOCK = new HashSet<>(Arrays.asList(Material.SUGAR_CANE, Material.CACTUS));

	@Data
	@RequiredArgsConstructor
	private static class BreakException extends RuntimeException {
		private final String cooldownId;
		private final String errorMessage;
	}

	public boolean breakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		final var breakable = getBreakable(block);
		if (breakable == null)
			return false;

		BlockData blockData = block.getState().getBlockData();
		Material material = block.getType();

		try {
			ItemStack tool = player.getInventory().getItemInMainHand();
			if (!breakable.isCorrectTool(tool))
				throw new BreakException("event_break_wrong_tool", EventErrors.CANT_BREAK + " with this tool. Needs either: " + breakable.getAvailableTools());

			if (blockData instanceof Ageable ageable) {
				if (!CROP_MULTI_BLOCK.contains(material))
					if (ageable.getAge() != ageable.getMaximumAge())
						throw new BreakException("event_notFullyGrown", EventErrors.NOT_FULLY_GROWN);
			}

			Block below = block.getRelative(0, -1, 0);
			Block above = block.getRelative(0, 1, 0);
			List<Block> regenBlocks = new ArrayList<>();
			regenBlocks.add(block);

			if (CROP_SINGLE_BLOCK.contains(material)) {
				if (below.getType() != Material.COARSE_DIRT)
					throw new BreakException("event_decorOnly", EventErrors.DECOR_ONLY);

			} else if (CROP_MULTI_BLOCK.contains(material)) {
				if (below.getType() != material)
					throw new BreakException("event_bottomBlock", EventErrors.BOTTOM_BLOCK);

				if (above.getType().equals(material)) {
					for (int i = above.getLocation().getBlockY(); i <= block.getLocation().getWorld().getMaxHeight(); i++) {
						if (!above.getType().equals(material))
							break;

						above.setType(Material.AIR, false);
						regenBlocks.add(above);
						breakable.giveDrops(player);
						above = above.getRelative(0, 1, 0);
					}
				}
			}

			new SoundBuilder(breakable.getSound()).location(player.getLocation()).volume(breakable.getVolume()).pitch(breakable.getPitch()).category(SoundCategory.BLOCKS).play();
			breakable.giveDrops(player);
			breakable.regen(regenBlocks);
		} catch (BreakException ex) {
			if (new CooldownService().check(player, ex.getCooldownId(), TickTime.MINUTE)) {
				errorMessage(player, ex.getErrorMessage());
				EventSounds.VILLAGER_NO.play(player);
			}
		}
		return true;
	}

}
