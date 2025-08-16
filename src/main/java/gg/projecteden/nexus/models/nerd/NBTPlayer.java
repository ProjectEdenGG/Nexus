package gg.projecteden.nexus.models.nerd;

import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Timer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class NBTPlayer implements PlayerOwnedObject {
	private UUID uuid;

	public NBTPlayer(HasUniqueId uuid) {
		this.uuid = uuid.getUniqueId();
	}

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private NBTFile nbtFile;

	public @NotNull NBTFile getNbtFile() {
		if (isOnline())
			return loadNbtFile();

		if (nbtFile == null)
			nbtFile = loadNbtFile();

		return nbtFile;
	}

	@NotNull
	private NBTFile loadNbtFile() {
		try {
			File file = Paths.get(Bukkit.getServer().getWorlds().getFirst().getName() + "/playerdata/" + uuid + ".dat").toFile();
			if (file.exists())
				return new NBTFile(file);
			throw new InvalidInputException("[Nerd]" + getNickname() + "'s data file does not exist");
		} catch (Exception ex) {
			throw new InvalidInputException("[Nerd] Error opening " + getNickname() + "'s data file");
		}
	}

	public World getWorld() {
		final String NBT_KEY = "Dimension";

		final NBTType dataType = getNbtFile().getType(NBT_KEY);

		switch (dataType) {
			case NBTTagString -> {
				final String dimension = getNbtFile().getString(NBT_KEY).replace("minecraft:", "");

				if ("overworld".equals(dimension))
					return Bukkit.getWorlds().getFirst();

				final World result = Bukkit.getWorld(dimension);
				if (result != null)
					return result;
			}
			case NBTTagInt -> {
				final int dimension = getNbtFile().getInteger(NBT_KEY);
				if (dimension == 0)
					return Bukkit.getWorld("survival");
			}
		}

		final long most = getNbtFile().getLong("WorldUUIDMost");
		final long least = getNbtFile().getLong("WorldUUIDLeast");
		final UUID worldUuid = new UUID(most, least);
		final World world = Bukkit.getWorld(worldUuid);
		if (world != null)
			return world;

		final NBTCompound compound = getNbtFile().getCompound(NBT_KEY);
		throw new InvalidInputException("[Nerd] %s is not in a valid world (Type: %s, Value: %s, UUID: %s)".formatted(
			getNickname(), StringUtils.camelCase(dataType),
			compound == null ? "null" : compound.toString(),
			uuid == null ? "null" : uuid.toString()
		));
	}

	public Location getOfflineLocation() {
		World world = getWorld();
		if (world == null)
			throw new InvalidInputException("[Nerd] " + getNickname() + " is not in a valid world");

		NBTList<Double> pos = getNbtFile().getDoubleList("Pos");
		NBTList<Float> rotation = getNbtFile().getFloatList("Rotation");

		return new Location(world, pos.get(0), pos.get(1), pos.get(2), rotation.get(0), rotation.get(1));
	}

	public List<ItemStack> getOfflineInventory() {
		final String NBT_KEY = "Inventory";
		final ItemStack[] contents = new ItemStack[41];

		if (getNbtFile().hasTag(NBT_KEY)) {
			final NBTCompoundList inventory = getNbtFile().getCompoundList(NBT_KEY);

			for (ReadWriteNBT nbt : inventory) {
				int slot = nbt.getInteger("Slot");
				slot = switch (slot) {
					case 100, 101, 102, 103 -> slot - 64;
					case -106 -> 40;
					default -> slot;
				};

				contents[slot] = NBTItem.convertNBTtoItem((NBTCompound) nbt);
			}
		}

		return Arrays.asList(contents);
	}

	public List<ItemStack> getOfflineEnderChest() {
		final String NBT_KEY = "EnderItems";
		final ItemStack[] contents = new ItemStack[27];

		if (getNbtFile().hasTag(NBT_KEY))
			for (ReadWriteNBT compound : getNbtFile().getCompoundList(NBT_KEY))
				contents[compound.getInteger("Slot")] = NBTItem.convertNBTtoItem((NBTCompound) compound);

		return Arrays.asList(contents);
	}

	public List<ItemStack> getOfflineArmor() {
		final List<ItemStack> inventory = new NBTPlayer(this).getOfflineInventory();
		if (inventory.size() >= 37)
			return inventory.subList(36, Math.min(inventory.size() - 1, 40));

		return Collections.emptyList();
	}

	public ItemStack getOfflineOffHand() {
		final List<ItemStack> inventory = new NBTPlayer(this).getOfflineInventory();
		if (inventory.size() >= 41)
			return inventory.get(40);

		return new ItemStack(Material.AIR);
	}

	private void setPosition(Location location) {
		final NBTList<Double> pos = getNbtFile().getDoubleList("Pos");
		pos.set(0, location.getX());
		pos.set(1, location.getY());
		pos.set(2, location.getZ());
	}

	private void setRotation(Location location) {
		final NBTList<Float> rotation = getNbtFile().getFloatList("Rotation");
		rotation.set(0, location.getYaw());
		rotation.set(1, location.getPitch());
	}

	private void setWorld(Location location) {
		final UUID worldUuid = location.getWorld().getUID();
		getNbtFile().setLong("WorldUUIDMost", worldUuid.getMostSignificantBits());
		getNbtFile().setLong("WorldUUIDLeast", worldUuid.getLeastSignificantBits());
	}

}
