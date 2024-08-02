package gg.projecteden.nexus.features.events;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.models.EventBreakable.EventBreakableBuilder;
import gg.projecteden.nexus.features.events.models.EventErrors;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.events.models.EventPlaceable;
import gg.projecteden.nexus.features.events.models.EventPlaceable.EventPlaceableBuilder;
import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.Interactable;
import gg.projecteden.nexus.features.quests.interactable.InteractableEntity;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ChunkLoader;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;
import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;

public abstract class EdenEvent extends Feature implements Listener {
	public static final String PREFIX_EVENTS = StringUtils.getPrefix("Events");
	public static final String PREFIX_STORE = StringUtils.getPrefix("Event Store");

	protected List<EventBreakable> breakables = new ArrayList<>();
	protected List<EventPlaceable> placeables = new ArrayList<>();
	@Getter
	protected List<FishingLoot> fishingLoot = new ArrayList<>();
	@Getter
	protected EventFishingListener fishingListener;
	@Getter
	protected List<String> customGenericGreetings = new ArrayList<>();

	public static List<EdenEvent> EVENTS = new ArrayList<>();

	public static EdenEvent of(Player player) {
		for (EdenEvent event : EVENTS) {
			if (event.isInEventWorld(player.getLocation())) {
				return event;
			}
		}

		return null;
	}

	public String getTabLine() {
		return null;
	}

	public ItemStack getWarpMenuItem() {
		return null;
	}

	public String getMotd() {
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

	public static List<EdenEvent> getActiveEvents() {
		return EVENTS.stream()
			.filter(EdenEvent::isEventActive)
			.toList();
	}

	public static List<EdenEvent> getActiveEvents(HasUniqueId player) {
		return EVENTS.stream()
			.filter(edenEvent -> edenEvent.isEventActive(player))
			.toList();
	}

	// We can assume there's only one truly active event, so sort
	// by date and get the one the starts the earliest
	public static EdenEvent getActiveEvent() {
		return getActiveEvents().stream()
			.min(Comparator.comparing(EdenEvent::getStart))
			.orElse(null);
	}

	public static EdenEvent getActiveEvent(HasUniqueId player) {
		return getActiveEvents(player).stream()
			.min(Comparator.comparing(EdenEvent::getStart))
			.orElse(null);
	}

	public boolean isEventActive(HasUniqueId player) {
		if (isBeforeEvent() && Rank.of(player).isStaff())
			return true;

		return isEventActive();
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

	public void forceLoadChunk(Chunk chunk) {
		ChunkLoader.loadChunk(chunk);
	}

	public void forceLoadChunks(Set<Chunk> chunks) {
		ChunkLoader.loadChunks(chunks);
	}

	public void forceLoadRegions(Set<ProtectedRegion> regions) {
		forceLoadRegions(new ArrayList<>(regions));
	}

	public void forceLoadRegions(List<ProtectedRegion> regions) {
		for (ProtectedRegion region : regions) {
			forceLoadRegions(region.getId());
		}
	}

	public void forceLoadRegion(ProtectedRegion region) {
		forceLoadRegions(region.getId());
	}

	public void forceLoadRegions(String regionId) {
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

	public boolean shouldHandle(HasLocation location) {
		if (!isAtEvent(location))
			return false;

		return isEventActive();
	}

	public boolean shouldHandle(Player player) {
		if (!isAtEvent(player))
			return false;

		return isEventActive(player);
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

	@EventHandler
	public void on(CustomBoundingBoxEntityInteractEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		if (!event.getEntity().getId().contains("quest_board"))
			return;

		if (event.getOriginalEvent() instanceof Cancellable cancellable)
			cancellable.setCancelled(true);

		if (!new CooldownService().check(event.getPlayer().getUniqueId(), "quest_board", 20, false))
			return;

		PlayerUtils.runCommand(event.getPlayer(), this.getName().toLowerCase() + " quest progress");

		new CooldownService().check(event.getPlayer(), "quest_board", 20);
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

	public void registerInteractHandlers() {
	}

	@EventHandler
	public void _onNPCRightClick(NPCRightClickEvent event) {
		final Player player = event.getClicker();

		try {
			final InteractableNPC npc = interactableOf(event.getNPC());
			if (npc == null)
				return;

			event.setCancelled(true);

			if (Quester.of(player).tryAdvanceDialog(npc)) {
				return;
			}

			if (interactHandlers.containsKey(npc)) {
				interactHandlers.get(npc).accept(player, npc);
				return;
			}

			new QuesterService().edit(event.getClicker(), quester -> quester.interact(npc, event));
		} catch (EdenException ex) {
			MenuUtils.handleException(player, getPrefix(), ex);
		}
	}

	@EventHandler
	public void _onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		final Player player = event.getPlayer();
		try {
			final InteractableEntity entity = interactableOf(event.getRightClicked());
			if (entity == null)
				return;

			event.setCancelled(true);

			if (Quester.of(player).tryAdvanceDialog(entity)) {
				return;
			}

			if (interactHandlers.containsKey(entity)) {
				interactHandlers.get(entity).accept(player, entity);
				return;
			}

			new QuesterService().edit(player, quester -> quester.interact(entity, event));
		} catch (EdenException ex) {
			MenuUtils.handleException(player, getPrefix(), ex);
		}
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

		if (!shouldHandle(player))
			return;
		if (event.isCancelled())
			return;
		if (canWorldGuardEdit(player))
			return;

		event.setCancelled(true);

		if (breakBlock(event))
			return;
		if (handleBlockBreak(event))
			return;

		if (new CooldownService().check(player, "event_cantbreak", TickTime.MINUTE)) {
			errorMessage(player, EventErrors.CANT_BREAK);
			EventSounds.VILLAGER_NO.play(player);
		}
	}

	@EventHandler
	public void _onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (!shouldHandle(player))
			return;
		if (event.isCancelled())
			return;
		if (canWorldGuardEdit(player))
			return;
		if (placeBlock(event))
			return;
		if (handleBlockPlace(event))
			return;

		event.setCancelled(true);

		if (new CooldownService().check(player, "event_cantplace", TickTime.MINUTE)) {
			errorMessage(player, EventErrors.CANT_PLACE);
			EventSounds.VILLAGER_NO.play(player);
		}
	}

	@EventHandler
	public void _onBlockDropItemEvent(BlockDropItemEvent event) {
		Player player = event.getPlayer();
		if (!shouldHandle(player))
			return;

		new QuesterService().edit(player, quester -> quester.handleBlockEvent(event));
	}

	@EventHandler
	public void _onLivingEntityKilledByPlayerEvent(LivingEntityKilledByPlayerEvent event) {
		Player player = event.getAttacker();
		if (!shouldHandle(player))
			return;

		new QuesterService().edit(player, quester -> quester.handleEntityEvent(event));
	}

	public boolean handleBlockBreak(BlockBreakEvent event) {
		return false;
	}

	public boolean handleBlockPlace(BlockPlaceEvent event) {
		return false;
	}

	public void errorMessage(Player player, String message) {
		PlayerUtils.send(player, PREFIX + message);
	}

	public void registerBreakable(EventBreakableBuilder builder) {
		this.breakables.add(builder.build());
	}

	public void registerPlaceable(EventPlaceableBuilder builder) {
		this.placeables.add(builder.build());
	}

	protected void registerBreakableBlocks() {
	}

	protected void registerPlaceableBlocks() {
	}

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

	private @Nullable EventPlaceable getPlaceable(Block block) {
		return placeables.stream().filter(placeable -> {
				if (!placeable.getBlockMaterials().contains(block.getType()))
					return false;

				if (placeable.getBlockPredicate() != null)
					if (!placeable.getBlockPredicate().test(block))
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

			if (tool.getItemMeta() instanceof Damageable damageable)
				if (chanceOf(100 / tool.getEnchantmentLevel(Enchant.UNBREAKING) + 1)) {
					damageable.setDamage(damageable.getDamage() + 1);
					tool.setItemMeta(damageable);
				}

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

	public boolean placeBlock(BlockPlaceEvent event) {
		return getBreakable(event.getBlock()) != null;
	}

	protected void registerFishingLoot() {
	}

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

	@EventHandler
	public void on(AnvilDamagedEvent event) {
		final Location location = event.getInventory().getLocation();
		if (location == null)
			return;

		if (!shouldHandle(location))
			return;

		event.setCancelled(true);
	}

}
