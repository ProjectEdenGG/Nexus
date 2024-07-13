package gg.projecteden.nexus.features.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.models.EventBreakable.EventBreakableBuilder;
import gg.projecteden.nexus.features.events.models.EventErrors;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ChunkLoader;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.HasLocation;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
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
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.utils.Extensions.isStaff;

public abstract class EdenEvent extends Feature implements Listener {
	protected List<EventBreakable> breakables = new ArrayList<>();
	@Getter
	protected List<FishingLoot> fishingLoot = new ArrayList<>();
	@Getter
	protected EventFishingListener fishingListener;

	public static List<EdenEvent> EVENTS = new ArrayList<>();

	public static EdenEvent of(Player player) {
		for (EdenEvent event : EVENTS) {
			if (event.isInEventWorld(player.getLocation())) {
				return event;
			}
		}

		return null;
	}

	@Override
	public void onStart() {
		super.onStart();
		EVENTS.add(this);

		registerFishingLoot();
		registerInteractHandlers();
		registerBreakableBlocks();

		if (!fishingLoot.isEmpty())
			fishingListener = new EventFishingListener(this);

		LuckPermsUtils.registerContext(new EventActiveCalculator());
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

	public boolean isInEventWorld(HasLocation hasLocation) {
		final Location location = hasLocation.getLocation();
		return location.getWorld().getName().equals(getConfig().world());
	}

	public boolean isAtEvent(PlayerInteractEvent event) {
		return isAtEvent(event.getHand(), event.getPlayer());
	}

	public boolean isAtEvent(PlayerInteractEntityEvent event) {
		return isAtEvent(event.getHand(), event.getPlayer());
	}

	private boolean isAtEvent(EquipmentSlot slot, Player player) {
		if (!EquipmentSlot.HAND.equals(slot)) return false;

		return isAtEvent(player);
	}

	public boolean isAtEvent(HasLocation hasLocation) {
		if (!isInEventWorld(hasLocation))
			return false;

		return worldguard().isInRegion(hasLocation.getLocation(), getConfig().region());
	}


	public boolean isInRegion(HasLocation location, String region) {
		return isAtEvent(location) && worldguard().isInRegion(location, region);
	}

	public boolean isInRegionRegex(HasLocation location, String regex) {
		return isAtEvent(location) && !worldguard().getRegionsLikeAt(regex, location).isEmpty();
	}

	public boolean anyActivePlayers() {
		return !getPlayers().isEmpty();
	}

	public Set<Player> getPlayers() {
		return new HashSet<>(worldguard().getPlayersInRegion(getProtectedRegion()));
	}

	public boolean hasPlayers() {
		return !getPlayers().isEmpty();
	}

	public Set<Player> getPlayersIn(ProtectedRegion region) {
		return getPlayersIn(region.getId());
	}

	public Set<Player> getPlayersIn(String region) {
		return new HashSet<>(worldguard().getPlayersInRegion(region));
	}

	public void send(Player player, String message) {
		PlayerUtils.send(player, PREFIX + message);
	}

	public void actionBar(String message, long ticks) {
		getPlayers().forEach(player -> ActionBarUtils.sendActionBar(player, message, ticks));
	}

	public void forceLoadChunks(String regionId) {
		ChunkLoader.loadChunks(getWorld(), regionId);
	}

	public class EventActiveCalculator implements ContextCalculator<Player> {

		@Override
		public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
			contextConsumer.accept("event-" + getName().toLowerCase() + "-active", String.valueOf(isEventActive()));
		}

		@Override
		public ContextSet estimatePotentialContexts() {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			builder.add("event-" + getName().toLowerCase() + "-active", "true");
			builder.add("event-" + getName().toLowerCase() + "-active", "false");
			return builder.build();
		}

	}

	static {
		LuckPermsUtils.registerContext(new CurrentWorldEventActiveCalculator());
	}

	public static class CurrentWorldEventActiveCalculator implements ContextCalculator<Player> {

		@Override
		public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
			var value = false;
			for (EdenEvent event : EVENTS)
				if (event.isInEventWorld(target.getLocation())) {
					value = event.isEventActive();
					break;
				}

			contextConsumer.accept("current-world-event-active", String.valueOf(value));
		}

		@Override
		public ContextSet estimatePotentialContexts() {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			builder.add("current-world-event-active", "true");
			builder.add("current-world-event-active", "false");
			return builder.build();
		}

	}

	public boolean shouldHandle(Player player) {
		if (!isAtEvent(player))
			return false;

		if (isStaff(player))
			return true;

		return isEventActive();
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

	protected final Map<Interactable, BiConsumer<Player, Interactable>> interactHandlers = new HashMap<>();

	public void handleInteract(Interactable npc, BiConsumer<Player, Interactable> handler) {
		interactHandlers.put(npc, handler);
	}

	public void registerInteractHandlers() {}

	@EventHandler
	public void _onNPCRightClick(NPCRightClickEvent event) {
		final Player player = event.getClicker();
		if (!shouldHandle(player))
			return;

		final InteractableNPC npc = interactableOf(event.getNPC());
		if (npc == null)
			return;

		event.setCancelled(true);

		if (interactHandlers.containsKey(npc)) {
			interactHandlers.get(npc).accept(player, npc);
			return;
		}

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

		if (interactHandlers.containsKey(entity)) {
			interactHandlers.get(entity).accept(player, entity);
			return;
		}

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

	@EventHandler
	public void _onBlockDropItemEvent(BlockDropItemEvent event) {
		Player player = event.getPlayer();
		if (!shouldHandle(event.getPlayer()))
			return;

		new QuesterService().edit(player, quester -> quester.handleBlockEvent(event));
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

	protected void registerFishingLoot() {}

	public void registerFishingLoot(EventFishingLootCategory... categories) {
		List<EventFishingLootCategory> categoriesList = Arrays.asList(categories);
		for (EventDefaultFishingLoot defaultValue : EventDefaultFishingLoot.values())
			if (categoriesList.contains(defaultValue.getCategory()))
				this.fishingLoot.add(defaultValue.build());
	}

	public void registerFishingLoot(EventDefaultFishingLoot... defaultFishingLoot) {
		for (var defaultValue : defaultFishingLoot)
			this.fishingLoot.add(defaultValue.build());
	}

	public void registerFishingLoot(FishingLoot... fishingLoot) {
		this.fishingLoot.addAll(Arrays.asList(fishingLoot));
	}

	public FishingLoot getFishingLoot(EventDefaultFishingLoot defaultFishingLoot) {
		return getFishingLoot(defaultFishingLoot.getMaterial(), defaultFishingLoot.getModelId());
	}

	public FishingLoot getFishingLoot(Material material) {
		return this.fishingLoot.stream().filter(fishingLoot -> fishingLoot.getMaterial() == material).findFirst().orElse(null);
	}

	public FishingLoot getFishingLoot(Material material, int modelId) {
		return this.fishingLoot.stream().filter(fishingLoot -> fishingLoot.getMaterial() == material && fishingLoot.getModelId() == modelId).findFirst().orElse(null);
	}

}
