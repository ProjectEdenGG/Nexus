package gg.projecteden.nexus.features.recipes.functionals;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.handler.NBTHandlers;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvisibleItemFrame extends FunctionalRecipe {

	private static final NamespacedKey KEY = new NamespacedKey("survivalinvisiframes", "invisible");
	private final Set<DroppedFrameLocation> droppedFrames = new HashSet<>();

	@Getter
	public static ItemBuilder item = new ItemBuilder(Material.ITEM_FRAME)
		.name("Invisible Item Frame")
		.model(ItemModelType.INVISIBLE_ITEM_FRAME)
		.nbt(nbt -> {
			ReadWriteNBT bukkit = NBT.createNBTObject();
			bukkit.setByte("survivalinvisiframes:invisible", (byte) 1);
			nbt.set("PublicBukkitValues", bukkit, NBTHandlers.STORE_READWRITE_TAG);
		});

	@Override
	public ItemStack getResult() {
		return item.clone().amount(8).build();
	}

	@Override
	public @NonNull Recipe getRecipe() {
		return RecipeBuilder.surround(CustomRecipes.getLingeringInvisibilityPotionItems())
			.with(Material.ITEM_FRAME)
			.toMake(getItem().clone().amount(8).build())
			.build()
			.getRecipe();
	}

	@Override
	public void onStart() {
		Tasks.sync(this::forceRecheck);
	}

	public void forceRecheck() {
		for (World world : Bukkit.getWorlds())
			for (ItemFrame frame : world.getEntitiesByClass(ItemFrame.class))
				if (frame.getPersistentDataContainer().has(KEY, PersistentDataType.BYTE))
					if (frame.getItem().getType() == Material.AIR) {
						frame.setGlowing(true);
						frame.setVisible(true);
					}
					else {
						frame.setGlowing(false);
						frame.setVisible(false);
					}
	}

	private boolean isFrameEntity(Entity entity) {
		return entity != null && entity.getType() == EntityType.ITEM_FRAME;
	}

	@EventHandler
	public void onSplashPotion(LingeringPotionSplashEvent event) {
		List<PotionEffectType> potionEffectTypes = new ArrayList<>();
		event.getEntity().getEffects().forEach(e -> potionEffectTypes.add(e.getType()));
		if (!potionEffectTypes.contains(PotionEffectType.INVISIBILITY)) return;
		List<Entity> entities = Arrays.stream(event.getEntity().getNearbyEntities(2, 2, 2).toArray(Entity[]::new)).filter(e -> e.getType() == EntityType.ITEM_FRAME).collect(Collectors.toList());

		if (!new WorldGuardUtils(event.getEntity().getWorld()).getRegionsAt(event.getEntity().getLocation()).isEmpty())
			return;

		for (Entity entity : entities) {
			ItemFrame itemFrame = (ItemFrame) entity;
			itemFrame.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
			if (Nullables.isNullOrAir(itemFrame.getItem())) {
				itemFrame.setVisible(true);
				itemFrame.setGlowing(true);
			} else
				itemFrame.setVisible(false);
		}
	}

	@EventHandler
	public void onDye(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		final Player player = event.getPlayer();

		if (player.isSneaking())
			return;

		if (!Rank.of(player).isStaff() && WorldGroup.of(player) != WorldGroup.STAFF)
			return;

		if (entity.getType() != EntityType.ITEM_FRAME)
			return;

		if (!entity.isGlowing())
			return;

		ItemStack dye = player.getInventory().getItem(event.getHand());

		if (dye == null || !MaterialTag.DYES.isTagged(dye.getType()))
			return;

		final ColorType color = ColorType.of(dye.getType());

		if (color == null)
			return;

		DyeColor dyeColor = color.getDyeColor();

		if (dyeColor == null)
			return;

		event.setCancelled(true);

		final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		final String teamId = "ifc-" + dyeColor.name().toLowerCase();

		Team team = scoreboard.getTeam(teamId);
		if (team == null)
			team = scoreboard.registerNewTeam(teamId);

		team.color(color.getNamedColor());
		team.addEntry(entity.getUniqueId().toString());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onHangingPlace(HangingPlaceEvent event) {
		if (!isFrameEntity(event.getEntity()) || event.getPlayer() == null)
			return;

		// Get the frame item that the player placed
		ItemStack frame;
		Player p = event.getPlayer();
		if (p.getInventory().getItemInMainHand().getType() == Material.ITEM_FRAME)
			frame = p.getInventory().getItemInMainHand();
		else if (p.getInventory().getItemInOffHand().getType() == Material.ITEM_FRAME)
			frame = p.getInventory().getItemInOffHand();
		else
			return;

		// If the frame item has the invisible tag, make the placed item frame invisible
		if (frame.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE)) {
			if (!p.hasPermission("survivalinvisiframes.place")) {
				event.setCancelled(true);
				return;
			}
			ItemFrame itemFrame = (ItemFrame) event.getEntity();
			itemFrame.setVisible(true);
			itemFrame.setGlowing(true);
			event.getEntity().getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, (byte) 1);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onHangingBreak(HangingBreakEvent event) {
		if (!isFrameEntity(event.getEntity()) || !event.getEntity().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE))
			return;

		// This is the dumbest possible way to change the drops of an item frame
		// Apparently, there's no api to change the dropped item
		// So this sets up a bounding box that checks for items near the frame and converts them
		DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(event.getEntity().getLocation());
		droppedFrames.add(droppedFrameLocation);

		droppedFrameLocation.setRemoval(Tasks.wait(20, () -> droppedFrames.remove(droppedFrameLocation)));
	}

	@EventHandler
	private void onItemSpawn(ItemSpawnEvent event) {
		Item item = event.getEntity();
		if (item.getItemStack().getType() != Material.ITEM_FRAME)
			return;

		Iterator<DroppedFrameLocation> iter = droppedFrames.iterator();
		while (iter.hasNext()) {
			DroppedFrameLocation droppedFrameLocation = iter.next();
			if (droppedFrameLocation.isFrame(item)) {
				ItemStack frame = getItem().build();
				event.getEntity().setItemStack(frame);

				Tasks.cancel(droppedFrameLocation.getRemoval());
				iter.remove();
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (isFrameEntity(event.getRightClicked()) && event.getRightClicked().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE)) {
			ItemFrame frame = (ItemFrame) event.getRightClicked();
			Tasks.wait(1, () -> {
				if(frame.getItem().getType() != Material.AIR) {
					frame.setGlowing(false);
					frame.setVisible(false);
				}
			});
		}
	}

	@EventHandler(ignoreCancelled = true)
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (isFrameEntity(event.getEntity()) && event.getEntity().getPersistentDataContainer().has(KEY, PersistentDataType.BYTE)) {
			ItemFrame frame = (ItemFrame) event.getEntity();
			Tasks.wait(1, () -> {
				if(frame.getItem().getType() == Material.AIR) {
					frame.setGlowing(true);
					frame.setVisible(true);
				}
			});
		}
	}

	@Data
	public static class DroppedFrameLocation {
		private final BoundingBox box;
		private int removal;

		public DroppedFrameLocation(Location location) {
			this.box = BoundingBox.of(location, 1.0, 1.0, 1.0);
		}

		public boolean isFrame(Item item) {
			return box.contains(item.getBoundingBox());
		}
	}

}
