package gg.projecteden.nexus.features.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.EventBreakableBlock.EventBreakableBlockBuilder;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.HasLocation;
import lombok.NonNull;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.utils.Extensions.isStaff;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

public abstract class EdenEvent extends Feature implements Listener {
	protected List<EventBreakableBlock> breakableBlocks = new ArrayList<>();

	@Override
	public void onStart() {
		super.onStart();
		registerBreakableBlocks();
	}

	public QuestConfig getConfig() {
		return getClass().getAnnotation(QuestConfig.class);
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

	public void registerBreakableBlock(EventBreakableBlockBuilder builder) {
		this.breakableBlocks.add(builder.build());
	}

	protected void registerBreakableBlocks() {}

	private boolean breakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material type = block.getType();

		var maybe = breakableBlocks.stream().filter(breakableBlock -> {
			if (!breakableBlock.getBlockMaterials().contains(type))
				return false;

			if (breakableBlock.getBlockPredicate() != null)
				if (!breakableBlock.getBlockPredicate().test(block))
					return false;

			return true;
		}).findFirst();

		if (maybe.isEmpty())
			return false;

		var match = maybe.get();

		ItemStack tool = player.getInventory().getItemInMainHand();
		if (!match.canBeMinedBy(tool)) {
			if (new CooldownService().check(player, "event_cantbreak_tool", TickTime.SECOND.x(15))) {
				errorMessage(player, EventErrors.CANT_BREAK + " with this tool. Needs either: " + match.getAvailableTools());
				EventSounds.VILLAGER_NO.play(player);
			}
			return true;
		}

		new SoundBuilder(Sound.BLOCK_STONE_BREAK).location(player.getLocation()).category(SoundCategory.BLOCKS).play();
		PlayerUtils.giveItems(player, match.getDrops(tool));

		new BlockRegenJob(block.getLocation(), block.getType()).schedule(randomInt(3 * 60, 5 * 60));
		var replacement = RandomUtils.randomElement(match.getReplacementTypes());
		if (replacement == Material.COBBLESTONE) {
			if (block.getType().name().contains("DEEPSLATE")) {
				block.setType(Material.COBBLED_DEEPSLATE);
			} else {
				block.setType(Material.COBBLESTONE);
			}
		} else {
			block.setType(replacement);
		}
		return true;
	}

}
