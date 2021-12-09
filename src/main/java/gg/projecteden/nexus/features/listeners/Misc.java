package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.FakePlayerInteractEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Golem;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.ItemUtils.getTool;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

public class Misc implements Listener {

	static {
		for (World world : Bukkit.getWorlds()) {
			// Skip main world
			if (world.equals(Bukkit.getWorlds().get(0)))
				continue;

			world.setKeepSpawnInMemory(false);
		}
	}

	@EventHandler
	public void onPlaceOnLight(BlockPlaceEvent event) {
		if (event.getBlockReplacedState().getType() != Material.LIGHT)
			return;

		if (event.getBlock().getType() == Material.LIGHT)
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), "&c&lHey! &7There's a light block there.");
	}

	@EventHandler
	public void onLightInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;

		final Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block) || block.getType() != Material.LIGHT)
			return;

		final ItemStack item = event.getItem();
		if (ItemUtils.isNullOrAir(item) || item.getType() != Material.LIGHT)
			return;

		if (!new BlockBreakEvent(block, event.getPlayer()).callEvent())
			return;

		block.setType(Material.AIR);
		block.getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), new ItemStack(Material.LIGHT));
	}

	@EventHandler
	public void onPlayerVanishStateChange(PlayerVanishStateChangeEvent event) {
		Nexus.getOpenInv().setPlayerSilentChestStatus(Bukkit.getOfflinePlayer(event.getUUID()), event.isVanishing());
	}

	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Projectile)
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof ItemFrame)
			if (event.getDamager() instanceof Projectile)
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamageArmorStand(EntityDamageByEntityEvent event) {
		if (!event.getEntityType().equals(EntityType.ARMOR_STAND))
			return;

		if (!event.getCause().equals(DamageCause.ENTITY_EXPLOSION))
			return;

		if (event.getDamager() instanceof Player)
			return;

		if (!WorldGroup.of(event.getEntity()).equals(WorldGroup.SURVIVAL))
			return;

		event.setCancelled(true);
	}

	private static final Map<MaterialTag, EquipmentSlot> slots = Map.of(
		MaterialTag.ALL_HELMETS, EquipmentSlot.HEAD,
		new MaterialTag(MaterialTag.ALL_CHESTPLATES).append(Material.ELYTRA), EquipmentSlot.CHEST,
		MaterialTag.ALL_LEGGINGS, EquipmentSlot.LEGS,
		MaterialTag.ALL_BOOTS, EquipmentSlot.FEET
	);

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event instanceof FakePlayerInteractEvent)
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		// Ignore McMMO blocks
		if (event.getClickedBlock() != null)
			if (List.of(Material.IRON_BLOCK, Material.GOLD_BLOCK).contains(event.getClickedBlock().getType()))
				return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final ItemStack item = event.getItem();
		final PlayerInventory inventory = event.getPlayer().getInventory();

		for (MaterialTag tag : slots.keySet()) {
			final EquipmentSlot slot = slots.get(tag);
			if (tag.isTagged(item)) {
				final ItemStack existing = ItemUtils.clone(inventory.getItem(slot));
				if (new PlayerArmorChangeEvent(event.getPlayer(), PlayerArmorChangeEvent.SlotType.valueOf(slot.name()), existing, item).callEvent()) {
					inventory.setItem(EquipmentSlot.HAND, existing);
					inventory.setItem(slot, ItemUtils.clone(item));
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onEatGlowBerry(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.MINIGAMES.equals(WorldGroup.of(player)))
			return;

		if (event.getItem().getType() != Material.GLOW_BERRIES)
			return;

		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.GLOWING).duration(TickTime.MINUTE.x(1.5)).build());
	}

	@EventHandler
	public void onBeeCatch(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Bee bee))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Player player = event.getPlayer();
		ItemStack tool = getTool(player);
		if (isNullOrAir(tool))
			return;
		if (!MaterialTag.ALL_BEEHIVES.isTagged(tool.getType()))
			return;

		final BlockStateMeta meta = (BlockStateMeta) tool.getItemMeta();
		final Beehive beehive = (Beehive) meta.getBlockState();
		int max = beehive.getMaxEntities();
		int current = beehive.getEntityCount();

		if (current < max) {
			beehive.addEntity(bee);
			meta.setBlockState(beehive);

			if (tool.getAmount() == 1)
				tool.setItemMeta(meta);
			else {
				tool = ItemBuilder.oneOf(tool).build();
				player.getInventory().removeItem(tool);
				tool.setItemMeta(meta);
				PlayerUtils.giveItem(player, tool);
			}

			new SoundBuilder(Sound.BLOCK_BEEHIVE_ENTER)
				.location(player.getLocation())
				.category(SoundCategory.BLOCKS)
				.play();
		}
	}

	@EventHandler
	public void onCoralDeath(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (MaterialTag.ALL_CORALS.isTagged(block.getType())) {
			WorldGroup worldGroup = WorldGroup.of(block.getWorld());
			if (WorldGroup.CREATIVE.equals(worldGroup) || WorldGroup.ADVENTURE.equals(worldGroup) || WorldGroup.MINIGAMES.equals(worldGroup))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;

		final EntityType attackerType = event.getEntity().getType();
		final EntityType targetType = event.getTarget().getType();

		if (attackerType != EntityType.AXOLOTL && attackerType != EntityType.FOX)
			return;

		if (targetType != EntityType.TROPICAL_FISH)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse)
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
	}

	@EventHandler
	public void onWanderingTraderSpawn(EntitySpawnEvent event) {
		List<EntityType> types = Arrays.asList(EntityType.WANDERING_TRADER, EntityType.TRADER_LLAMA);
		List<WorldGroup> worlds = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);
		if (types.contains(event.getEntity().getType()))
			if (!worlds.contains(WorldGroup.of(event.getLocation().getWorld())))
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getEntity() instanceof Player player)) return;

		if (event.getCause() == DamageCause.VOID)
			if (!(Arrays.asList(WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK).contains(WorldGroup.of(player))
				|| player.getWorld().getEnvironment() == Environment.THE_END)) {
				if (PlayerManager.get(player).getMatch() != null)
					Warps.spawn((Player) event.getEntity());
			}
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
		if (event.getPlayer().getUniqueId().equals(event.getAttacker().getUniqueId()))
			event.getOriginalEvent().setCancelled(true);
	}

	@EventHandler
	public void onFireworkDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Firework firework))
			return;

		if (!firework.hasMetadata(FireworkLauncher.METADATA_KEY_DAMAGE))
			return;

		for (MetadataValue value : firework.getMetadata(FireworkLauncher.METADATA_KEY_DAMAGE))
			if (!value.asBoolean())
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlaceChest(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.CHEST))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			PlayerUtils.send(event.getPlayer(), Koda.getDmFormat() + "Your chest is protected with LWC! Use /lwcinfo to learn more. " +
					"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	@EventHandler
	public void onPlaceFurnace(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.FURNACE))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			PlayerUtils.send(event.getPlayer(), Koda.getDmFormat() + "Your furnace is protected with LWC! Use /lwcinfo to learn more. " +
					"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	private static final String CHAT_DISABLED_WARNING = "&4&lWARNING: &4You have chat disabled! If this is by mistake, please turn it on in your settings.";
	private static final int WARNING_LENGTH_TICKS = TickTime.MINUTE.x(1);

	@EventHandler
	public void onJoinWithChatDisabled(PlayerJoinEvent event) {
		Tasks.wait(TickTime.SECOND.x(3), () -> {
			Player player = event.getPlayer();
			ChatVisibility setting = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (Arrays.asList(ChatVisibility.SYSTEM, ChatVisibility.HIDDEN).contains(setting)) {
				PlayerUtils.send(player, "");
				PlayerUtils.send(player, CHAT_DISABLED_WARNING);
				PlayerUtils.send(player, "");
				ActionBarUtils.sendActionBar(player, CHAT_DISABLED_WARNING, WARNING_LENGTH_TICKS);
			}
		});
	}

	@EventHandler
	public void onPlacePotionLauncherHopper(BlockPlaceEvent event) {
		if (!event.getBlockPlaced().getType().equals(Material.HOPPER))
			return;

		NBTItem itemNBT = new NBTItem(event.getItemInHand());
		if (!itemNBT.hasNBTData())
			return;

		if (itemNBT.asNBTString().contains("&8Potion Launcher"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBreakEmptyShulkerBox(BlockBreakEvent event) {
		if (!MaterialTag.SHULKER_BOXES.isTagged(event.getBlock().getType()))
			return;

		if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			return;

		NBTTileEntity tileEntityNBT = new NBTTileEntity(event.getBlock().getState());
		if (!tileEntityNBT.asNBTString().contains("Items:[")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}

	private static final List<UUID> toSpawn = new ArrayList<>();

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		Nerd nerd = Nerd.of(event.getUniqueId());
		try {
			World world = nerd.getOfflineWorld();
			if (world == null) return;

			if (WorldGroup.isResourceWorld(world)) {
				nerd = Nerd.of(event.getUniqueId());
				if (nerd.getLastQuit().isBefore(YearMonth.now().atDay(1).atStartOfDay()))
					toSpawn.add(event.getUniqueId());
			}
		} catch (InvalidInputException ignore) {}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (toSpawn.contains(event.getPlayer().getUniqueId())) {
			WarpType.NORMAL.get("spawn").teleportAsync(event.getPlayer());
			Nexus.log("Teleporting resource world player " + Name.of(event.getPlayer()) + " to spawn");
			toSpawn.remove(event.getPlayer().getUniqueId());
		}

		Tasks.wait(5, () -> {
			if (toSpawn.contains(event.getPlayer().getUniqueId())) {
				WarpType.NORMAL.get("spawn").teleportAsync(event.getPlayer());
				Nexus.log("Teleporting resource world player " + Name.of(event.getPlayer()) + " to spawn [2]");
				toSpawn.remove(event.getPlayer().getUniqueId());
			}

			WorldGroup worldGroup = WorldGroup.of(event.getPlayer());
			if (worldGroup == WorldGroup.MINIGAMES)
				joinMinigames(event.getPlayer());
			else if (worldGroup == WorldGroup.CREATIVE)
				joinCreative(event.getPlayer());
		});
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		final WorldGroup oldWorldGroup = WorldGroup.of(event.getFrom());
		final WorldGroup newWorldGroup = WorldGroup.of(player);

		if (newWorldGroup == WorldGroup.MINIGAMES)
			Tasks.wait(5, () -> joinMinigames(player));
		else if (newWorldGroup == WorldGroup.CREATIVE)
			Tasks.wait(5, () -> joinCreative(player));

		Tasks.wait(10, player::resetPlayerTime);

		if (!Rank.of(player).isStaff()) {
			SpeedCommand.resetSpeed(player);
			player.setAllowFlight(false);
			player.setFlying(false);
		}

		if (oldWorldGroup == WorldGroup.CREATIVE) {
			if (Nerd.of(player).isVanished())
				if (player.hasPermission("essentials.fly")) {
					player.setFallDistance(0);
					player.setAllowFlight(true);
					player.setFlying(true);
				}
		}

		if (event.getFrom().getName().equalsIgnoreCase("donortrial"))
			Tasks.wait(20, () -> {
				PlayerUtils.send(player, "Removing pets, disguises and ptime changes");
				PlayerUtils.runCommandAsConsole("undisguiseplayer " + player.getName());
				PlayerUtils.runCommandAsConsole("petadmin remove " + player.getName());
				PlayerUtils.runCommandAsConsole("mpet remove " + player.getName());
				PlayerUtils.runCommandAsOp(player, "particles stopall");
				PlayerUtils.runCommandAsConsole("speed walk 1 " + player.getName());
				player.resetPlayerTime();
			});

		if (player.getWorld().getName().equalsIgnoreCase("staff_world"))
			Tasks.wait(20, () -> PlayerUtils.runCommand(player, "cheats off"));

		if (DisguiseAPI.isDisguised(player))
			if (oldWorldGroup != WorldGroup.MINIGAMES)
				if (newWorldGroup == WorldGroup.MINIGAMES)
					DisguiseAPI.undisguiseToAll(player);
	}

	public void joinMinigames(Player player) {
		PlayerUtils.runCommand(player, "ch join m");
	}

	public void joinCreative(Player player) {
		PlayerUtils.runCommand(player, "ch join c");
	}

	public static class PlayerDamageByPlayerEvent extends PlayerEvent {
		@NonNull
		@Getter
		final Player attacker;
		@NonNull
		@Getter
		final EntityDamageByEntityEvent originalEvent;

		@SneakyThrows
		public PlayerDamageByPlayerEvent(@NotNull Player victim, @NotNull Player attacker, @NotNull EntityDamageByEntityEvent event) {
			super(victim);
			this.attacker = attacker;
			this.originalEvent = event;
		}

		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@NotNull
		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

	}

	public static class LivingEntityDamageByPlayerEvent extends EntityEvent {
		@NonNull
		@Getter
		final LivingEntity entity;
		@NonNull
		@Getter
		final Player attacker;
		@NonNull
		@Getter
		final EntityDamageByEntityEvent originalEvent;

		@SneakyThrows
		public LivingEntityDamageByPlayerEvent(@NotNull LivingEntity victim, @NotNull Player attacker, @NotNull EntityDamageByEntityEvent event) {
			super(victim);
			this.entity = victim;
			this.attacker = attacker;
			this.originalEvent = event;
		}

		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@NotNull
		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		Player attacker = null;
		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Player)
				attacker = (Player) projectile.getShooter();
		}

		if (attacker == null)
			return;

		if (event.getEntity() instanceof Player player)
			new PlayerDamageByPlayerEvent(player, attacker, event).callEvent();
		else if (event.getEntity() instanceof LivingEntity livingEntity)
			new LivingEntityDamageByPlayerEvent(livingEntity, attacker, event).callEvent();
	}

	// ImageOnMap rotating frames on placement; rotate back one before placement to offset
	@EventHandler
	public void onMapHang(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame itemFrame))
			return;

		ItemStack tool = getTool(event.getPlayer());
		if (tool == null)
			return;

		if (tool.getType() != Material.FILLED_MAP)
			return;

		int mapId = ((MapMeta) tool.getItemMeta()).getMapId();
		if (!Paths.get("plugins/ImageOnMap/images/map" + mapId + ".png").toFile().exists())
			return;

		if (!isNullOrAir(itemFrame.getItem()))
			return;

		itemFrame.setRotation(itemFrame.getRotation().rotateCounterClockwise());
	}

	@Getter
	public static class FixedCraftItemEvent extends CraftItemEvent {
		private final ItemStack resultItemStack;

		public FixedCraftItemEvent(@NotNull ItemStack resultItemStack, @NotNull Recipe recipe, @NotNull InventoryView what, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
			super(recipe, what, type, slot, click, action);
			this.resultItemStack = resultItemStack;
		}

		public FixedCraftItemEvent(@NotNull ItemStack resultItemStack, @NotNull Recipe recipe, @NotNull InventoryView what, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, int key) {
			super(recipe, what, type, slot, click, action, key);
			this.resultItemStack = resultItemStack;
		}

	}

	// Stolen from https://github.com/ezeiger92/QuestWorld2/blob/70f2be317daee06007f89843c79b3b059515d133/src/main/java/com/questworld/extension/builtin/CraftMission.java
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (event instanceof FixedCraftItemEvent) return;

		ItemStack item = event.getRecipe().getResult().clone();
		ClickType click = event.getClick();

		int recipeAmount = item.getAmount();

		switch (click) {
			case NUMBER_KEY:
				if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null)
					recipeAmount = 0;
				break;

			case DROP:
			case CONTROL_DROP:
				ItemStack cursor = event.getCursor();
				if (!ItemUtils.isNullOrAir(cursor))
					recipeAmount = 0;
				break;

			case SHIFT_RIGHT:
			case SHIFT_LEFT:
				if (recipeAmount == 0)
					break;

				int maxCraftable = getMaxCraftAmount(event.getInventory());
				int capacity = fits(item, event.getView().getBottomInventory());

				if (capacity < maxCraftable)
					maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;

				recipeAmount = maxCraftable;
				break;
			default:
		}

		if (recipeAmount == 0)
			return;

		item.setAmount(recipeAmount);

		new FixedCraftItemEvent(item, event.getRecipe(), event.getView(), event.getSlotType(), event.getSlot(), event.getClick(), event.getAction(), event.getHotbarButton()).callEvent();
	}

	public static int getMaxCraftAmount(CraftingInventory inv) {
		if (inv.getResult() == null)
			return 0;

		int resultCount = inv.getResult().getAmount();
		int materialCount = Integer.MAX_VALUE;

		for (ItemStack item : inv.getMatrix())
			// this can in fact be null
			if (item != null && item.getAmount() < materialCount)
				materialCount = item.getAmount();

		return resultCount * materialCount;
	}

	public static int fits(ItemStack stack, Inventory inv) {
		ItemStack[] contents = inv.getContents();
		int result = 0;

		for (ItemStack item : contents)
			if (item == null)
				result += stack.getMaxStackSize();
			else if (item.isSimilar(stack))
				result += Math.max(stack.getMaxStackSize() - item.getAmount(), 0);

		return result;
	}

	@EventHandler
	public void onSpawnIronGolem(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().contains("bingo"))
			return;

		Player player = event.getPlayer();
		if (event.getBlock().getType().equals(Material.PUMPKIN)) {
			Location HEAD = event.getBlock().getLocation();

			Location TORSO = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ());
			Location LEGS = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 2, HEAD.getZ());

			Location ARM1_X = new Location(HEAD.getWorld(), HEAD.getX() - 1, HEAD.getY() - 1, HEAD.getZ());
			Location ARM2_X = new Location(HEAD.getWorld(), HEAD.getX() + 1, HEAD.getY() - 1, HEAD.getZ());

			Location ARM1_Z = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ() - 1);
			Location ARM2_Z = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ() + 1);

			if (Material.IRON_BLOCK.equals(TORSO.getBlock().getType()) && Material.IRON_BLOCK.equals(LEGS.getBlock().getType())) {
				if (Material.IRON_BLOCK.equals(ARM1_X.getBlock().getType()) && Material.IRON_BLOCK.equals(ARM2_X.getBlock().getType())) {
					event.setCancelled(true);
					HEAD.getBlock().setType(Material.AIR);
					TORSO.getBlock().setType(Material.AIR);
					LEGS.getBlock().setType(Material.AIR);
					ARM1_X.getBlock().setType(Material.AIR);
					ARM2_X.getBlock().setType(Material.AIR);

					Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
					final IronGolem golem = location.getWorld().spawn(location, IronGolem.class);
					if (golem.isValid())
						new IronGolemBuildEvent(player, golem).callEvent();
				} else if (Material.IRON_BLOCK.equals(ARM1_Z.getBlock().getType()) && Material.IRON_BLOCK.equals(ARM2_Z.getBlock().getType())) {
					event.setCancelled(true);
					HEAD.getBlock().setType(Material.AIR);
					TORSO.getBlock().setType(Material.AIR);
					LEGS.getBlock().setType(Material.AIR);
					ARM1_Z.getBlock().setType(Material.AIR);
					ARM2_Z.getBlock().setType(Material.AIR);

					Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
					final IronGolem golem = location.getWorld().spawn(location, IronGolem.class, SpawnReason.BUILD_IRONGOLEM);
					if (golem.isValid())
						new IronGolemBuildEvent(player, golem).callEvent();
				}
			}
		}
	}

	@EventHandler
	public void onSpawnSnowGolem(BlockPlaceEvent event) {
		if (!event.getBlock().getWorld().getName().contains("bingo"))
			return;

		Player player = event.getPlayer();
		if (List.of(Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN).contains(event.getBlock().getType())) {
			Location HEAD = event.getBlock().getLocation();

			Location TORSO = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 1, HEAD.getZ());
			Location LEGS = new Location(HEAD.getWorld(), HEAD.getX(), HEAD.getY() - 2, HEAD.getZ());

			if (Material.SNOW_BLOCK.equals(TORSO.getBlock().getType()) && Material.SNOW_BLOCK.equals(LEGS.getBlock().getType())) {
				event.setCancelled(true);
				HEAD.getBlock().setType(Material.AIR);
				TORSO.getBlock().setType(Material.AIR);
				LEGS.getBlock().setType(Material.AIR);

				Location location = event.getBlock().getLocation().add(0, -1, 0).toCenterLocation();
				final Snowman golem = location.getWorld().spawn(location, Snowman.class, SpawnReason.BUILD_IRONGOLEM);
				if (golem.isValid())
					new SnowGolemBuildEvent(player, golem).callEvent();
			}
		}
	}

	public static class GolemBuildEvent extends PlayerEvent {
		@NonNull
		@Getter
		final Golem entity;

		public GolemBuildEvent(@NotNull Player who, @NonNull Golem entity) {
			super(who);
			this.entity = entity;
		}

		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@NotNull
		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

	}

	public static class IronGolemBuildEvent extends GolemBuildEvent {

		public IronGolemBuildEvent(@NotNull Player who, @NonNull IronGolem entity) {
			super(who, entity);
		}

	}

	public static class SnowGolemBuildEvent extends GolemBuildEvent {

		public SnowGolemBuildEvent(@NotNull Player who, @NonNull Snowman entity) {
			super(who, entity);
		}

	}

}
