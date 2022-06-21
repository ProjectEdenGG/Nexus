package gg.projecteden.nexus.models.nerd;

import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTListCompound;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nickname.Nickname;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
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
			File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + uuid + ".dat").toFile();
			if (file.exists())
				return new NBTFile(file);
			throw new InvalidInputException("[Nerd]" + Nickname.of(uuid) + "'s data file does not exist");
		} catch (Exception ex) {
			throw new InvalidInputException("[Nerd] Error opening " + Nickname.of(uuid) + "'s data file");
		}
	}

	public World getWorld() {
		String dimension = getNbtFile().getString("Dimension").replace("minecraft:", "");

		if ("overworld".equals(dimension))
			return Bukkit.getWorlds().get(0);

		return Bukkit.getWorld(dimension);
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

		if (getNbtFile().hasKey(NBT_KEY)) {
			final NBTCompoundList inventory = getNbtFile().getCompoundList(NBT_KEY);
			for (NBTListCompound compound : inventory) {
				int slot = compound.getInteger("Slot");
				slot = switch (slot) {
					case 100, 101, 102, 103 -> slot - 64;
					case -106 -> 40;
					default -> slot;
				};

				contents[slot] = NBTItem.convertNBTtoItem(compound);
			}
		}

		return Arrays.asList(contents);
	}

	public List<ItemStack> getOfflineEnderChest() {
		final String NBT_KEY = "EnderItems";
		final ItemStack[] contents = new ItemStack[27];

		if (getNbtFile().hasKey(NBT_KEY))
			for (NBTListCompound compound : getNbtFile().getCompoundList(NBT_KEY))
				contents[compound.getInteger("Slot")] = NBTItem.convertNBTtoItem(compound);

		return Arrays.asList(contents);
	}
}
