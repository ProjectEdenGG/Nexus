package gg.projecteden.nexus.models.costume;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.store.gallery.GalleryPackage;
import gg.projecteden.nexus.features.store.gallery.StoreGallery;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.CostumeConverter;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorTask;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import gg.projecteden.nexus.utils.nms.packet.EntityDestroyPacket;
import gg.projecteden.nexus.utils.nms.packet.EntityPassengersPacket;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "costume_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, CostumeConverter.class})
public class CostumeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int vouchers;
	private int temporaryVouchers;

	private Map<CostumeType, String> activeCostumes = new ConcurrentHashMap<>();
	private Map<CostumeType, String> activeDisplayCostumes = new ConcurrentHashMap<>();

	private transient ArmorStand backCostumeStand;

	private Set<String> ownedCostumes = new HashSet<>();
	private Set<String> temporarilyOwnedCostumes = new HashSet<>();
	private Set<String> birthdayCostumes = new HashSet<>();

	private Map<String, Color> colors = new ConcurrentHashMap<>();

	private static final List<WorldGroup> DISABLED_WORLDS = List.of(WorldGroup.MINIGAMES);
	private static final List<GameMode> DISABLED_GAMEMODES = List.of(GameMode.SPECTATOR);

	private static final Map<String, String> converter = new HashMap<>() {{
		put("hat/lightsabers/red", "hat/misc/lightsaber");
		put("hat/lightsabers/green", "hat/misc/lightsaber");
		put("hat/lightsabers/purple", "hat/misc/lightsaber");
		put("hat/lightsabers/light_blue", "hat/misc/lightsaber");
		put("hat/lightsabers/dark_blue", "hat/misc/lightsaber");
		put("hand/misc/rainbow_bracelet", "hand/misc/rainbow_bracelet_steve");
		put("hat/ushanka/black", "hat/misc/ushanka");
		put("hat/ushanka/cyan", "hat/misc/ushanka");
		put("hat/ushanka/pink", "hat/misc/ushanka");
		put("hat/ushanka/red", "hat/misc/ushanka");
		put("hat/misc/santa", "hat/misc/santa_hat");
	}};

	@PreLoad
	void convert(DBObject dbObject) {
		List<String> owned = Objects.requireNonNullElseGet((List<String>) dbObject.get("ownedCostumes"), Collections::emptyList);
		Map<String, DBObject> colors = Objects.requireNonNullElseGet((Map<String, DBObject>) dbObject.get("colors"), Collections::emptyMap);
		AtomicInteger vouchers = new AtomicInteger();

		converter.forEach((oldId, newId) -> {
			if (owned.contains(oldId)) {
				owned.remove(oldId);
				if (owned.contains(newId))
					vouchers.incrementAndGet();
				else
					owned.add(newId);
			}

			if (colors.containsKey(oldId))
				colors.put(newId, colors.remove(oldId));
		});

		dbObject.put("vouchers", (int) dbObject.get("vouchers") + vouchers.get());

		// /mcmd db cacheAll CostumeUserService ;; wait 20 ;; db saveCache CostumeUserService
	}

	public boolean hasCostumeActivated(Costume costume) {
		return activeCostumes.containsValue(costume.getId());
	}

	public Costume getActiveCostume(CostumeType type) {
		return Costume.of(activeCostumes.get(type));
	}

	public void setActiveCostumeId(CostumeType type, String activeCostume) {
		if (isNullOrEmpty(activeCostume)) {
			activeCostumes.remove(type);
			sendResetPacket(type);
		} else
			activeCostumes.put(type, activeCostume);
	}

	public void setActiveCostume(CostumeType type, Costume activeCostume) {
		setActiveCostumeId(type, activeCostume == null ? null : activeCostume.getId());
	}

	public boolean hasActiveCostumes() {
		return !isNullOrEmpty(activeCostumes);
	}

	public boolean hasActiveCostume(CostumeType type) {
		return activeCostumes.containsKey(type);
	}

	public boolean owns(CustomModel model) {
		return owns(Costume.of(model));
	}

	public boolean owns(Costume costume) {
		return owns(costume.getId());
	}

	public boolean owns(String id) {
		return ownedCostumes.contains(id) || temporarilyOwnedCostumes.contains(id) || birthdayCostumes.contains(id);
	}

	public void dye(Costume costume, Color color) {
		colors.put(costume.getId(), color);
	}

	public boolean isDyed(Costume costume) {
		return colors.containsKey(costume.getId());
	}

	private Color getColor(Costume costume) {
		return colors.get(costume.getId());
	}

	public ItemStack getCostumeItem(Costume costume) {
		ItemBuilder item = new ItemBuilder(costume.getItem());

		if (costume.getItem().getType() == Material.PLAYER_HEAD)
			item.skullOwner(this);

		else if (costume.isDyeable()) {
			if (isDyed(costume))
				item.dyeColor(getColor(costume));

			final RainbowArmorTask rainbowArmorTask = new RainbowArmorService().get(this).getTask();
			if (rainbowArmorTask != null && rainbowArmorTask.getColor() != null)
				item.dyeColor(rainbowArmorTask.getColor());
		}

		return item.build();
	}

	public ItemStack getCostumeDisplayItem(Costume costume) {
		ItemBuilder item = new ItemBuilder(costume.getModel().getDisplayItem());

		if (costume.getItem().getType() == Material.PLAYER_HEAD)
			item.skullOwner(this);

		else if (costume.isDyeable()) {
			if (isDyed(costume))
				item.dyeColor(getColor(costume));

			item.lore("&eDyeable").itemFlags(ItemFlag.HIDE_DYE);
		}

		return item.build();
	}

	public void sendCostumePacket() {
		if (!shouldSendPacket())
			return;

		activeCostumes.forEach((type, activeCostume) -> {
			final Costume costume = Costume.of(activeCostume);
			if (costume == null)
				return;

			if (costume.getType() == CostumeType.BACK) {
				sendBackPacket(getCostumeItem(costume));
				return;
			}

			sendPacket(getCostumeItem(costume), costume.getType().getSlot());
		});
	}

	public void sendResetPacket(CostumeType type) {
		if (!isOnline())
			return;

		if (type == CostumeType.BACK) {
			sendBackResetPacket();
			return;
		}

		sendPacket(getOnlinePlayer().getInventory().getItem(type.getSlot()), type.getSlot());
	}

	private void sendPacket(ItemStack item, EquipmentSlot slot) {
		final Player player = getOnlinePlayer();
		final List<Player> players = player.getWorld().getPlayers();
		PacketUtils.sendFakeItem(player, players, item, slot);
	}

	private void sendBackPacket(ItemStack item) {
		final Player player = getOnlinePlayer();
		Location spawnLoc = player.getLocation().clone().add(0, 1.5, 0);

		if (this.backCostumeStand == null || !this.backCostumeStand.isValid()) {
			this.backCostumeStand = player.getWorld().spawn(spawnLoc, org.bukkit.entity.ArmorStand.class, stand -> {
				stand.setRightArmPose(EulerAngle.ZERO);
				stand.setLeftArmPose(EulerAngle.ZERO);
				stand.setHeadPose(EulerAngle.ZERO);
				stand.setVisible(false);
				stand.setInvulnerable(true);
				stand.setGravity(false);
				stand.setBasePlate(false);
				stand.setDisabledSlots(EquipmentSlot.values());
			});
		}

		this.backCostumeStand.getEquipment().setHelmet(item);
		this.backCostumeStand.teleport(player.getLocation().clone().add(0, 1.5, 0));

		final List<Player> players = player.getWorld().getPlayers();
		for (Player _player : players) {
			new EntityPassengersPacket(player.getEntityId(), this.backCostumeStand.getEntityId()).send(_player);
		}
	}

	private void sendBackResetPacket() {
		if (this.backCostumeStand == null)
			return;

		final Player player = getOnlinePlayer();
		final List<Player> players = player.getWorld().getPlayers();
		int entityId = this.backCostumeStand.getEntityId();

		for (Player _player : players) {
			new EntityDestroyPacket(entityId).send(_player);
		}

		this.backCostumeStand.remove();
		this.backCostumeStand = null;
	}

	private boolean shouldSendPacket() {
		if (!isOnline())
			return false;

		final Player player = getOnlinePlayer();
		if (!hasActiveCostumes())
			return false;

		if (DISABLED_GAMEMODES.contains(player.getGameMode()))
			return false;

		if (DISABLED_WORLDS.contains(WorldGroup.of(player)))
			return false;

		if (AFK.get(this).isLimbo())
			return false;

		return true;
	}

	public boolean hasDisplayCostumeActivated(Costume costume) {
		return activeDisplayCostumes.containsValue(costume.getId());
	}

	public Costume getActiveDisplayCostume(CostumeType type) {
		return Costume.of(activeDisplayCostumes.get(type));
	}

	public void setActiveDisplayCostumeId(CostumeType type, String activeDisplayCostume) {
		if (isNullOrEmpty(activeDisplayCostume)) {
			activeDisplayCostumes.remove(type);
			sendResetDisplayPacket(type);
		} else
			activeDisplayCostumes.put(type, activeDisplayCostume);
	}

	public void setActiveDisplayCostume(CostumeType type, Costume activeDisplayCostume) {
		setActiveDisplayCostumeId(type, activeDisplayCostume == null ? null : activeDisplayCostume.getId());
	}

	public boolean hasActiveDisplayCostumes() {
		return !isNullOrEmpty(activeDisplayCostumes);
	}

	public boolean hasActiveDisplayCostume(CostumeType type) {
		return activeDisplayCostumes.containsKey(type);
	}

	public void sendDisplayCostumePacket() {
		if (!shouldSendDisplayPacket())
			return;

		activeDisplayCostumes.forEach((type, activeDisplayCostume) -> {
			final Costume costume = Costume.of(activeDisplayCostume);
			if (costume == null)
				return;

			sendDisplayPacket(getCostumeItem(costume), costume.getType().getSlot());
		});
	}

	public void sendResetDisplayPacket(CostumeType type) {
		if (!isOnline())
			return;

		sendDisplayPacket(new ItemStack(Material.AIR), type.getSlot());
	}

	private void sendDisplayPacket(ItemStack item, EquipmentSlot slot) {
		final var entity = GalleryPackage.COSTUMES.entity();
		if (entity == null)
			return;

		PacketUtils.sendFakeItem(entity, getOnlinePlayer(), item, slot);
	}

	private boolean shouldSendDisplayPacket() {
		if (!isOnline())
			return false;

		if (!hasActiveDisplayCostumes())
			return false;

		if (!getOnlinePlayer().getWorld().equals(StoreGallery.getWorld()))
			return false;

		return true;
	}

	public boolean hasAnyVouchers() {
		return hasVouchers() || hasTemporaryVouchers();
	}

	public boolean hasVouchers() {
		return vouchers > 0;
	}

	public boolean hasTemporaryVouchers() {
		return temporaryVouchers > 0;
	}

	public void addVouchers(int amount) {
		setVouchers(vouchers + amount);
	}

	public void takeVouchers(int amount) {
		setVouchers(vouchers - amount);
	}

	public void setVouchers(int amount) {
		if (amount < 0)
			throw new InvalidInputException("You do not have enough vouchers"); // TODO NegativeBalanceException?

		vouchers = amount;
	}

	public void addTemporaryVouchers(int amount) {
		setTemporaryVouchers(temporaryVouchers + amount);
	}

	public void takeTemporaryVouchers(int amount) {
		setTemporaryVouchers(temporaryVouchers - amount);
	}

	public void setTemporaryVouchers(int amount) {
		if (amount < 0)
			throw new InvalidInputException("You do not have enough vouchers"); // TODO NegativeBalanceException?

		temporaryVouchers = amount;
	}

}
