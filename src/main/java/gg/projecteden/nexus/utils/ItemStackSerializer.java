package gg.projecteden.nexus.utils;

import com.mojang.serialization.Dynamic;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.ItemStack;

public class ItemStackSerializer {

	public static String serialize(ItemStack itemStack) {
		net.minecraft.world.item.ItemStack nms = NMSUtils.toNMS(itemStack);
		CompoundTag tag = (CompoundTag) nms.save(((CraftServer) Bukkit.getServer()).getServer().registryAccess());
		tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
		return tag.toString();
	}

	public static ItemStack deserialize(String string) {
		try {
			CompoundTag updated = deserializeToTag(string);
			net.minecraft.world.item.ItemStack fixed = net.minecraft.world.item.ItemStack.parse(MinecraftServer.getServer().registryAccess(), updated).orElse(null);
			if (fixed == null)
				throw new NullPointerException();
			return fixed.asBukkitCopy();
		} catch (Exception ex) {
			Nexus.warn("Failed to parse ItemStack from String:");
			ex.printStackTrace();
			return null;
		}
	}

	public static CompoundTag deserializeToTag(String string) {
		try {
			CompoundTag tag = TagParser.parseTag(string);
			return update(tag);
		} catch (Exception ex) {
			Nexus.warn("Failed to parse ItemStack from String:");
			ex.printStackTrace();
			return new CompoundTag();
		}
	}

	@SneakyThrows
	public static CompoundTag update(@NonNull CompoundTag data) {
		return (CompoundTag) DataFixers.getDataFixer().update(
			References.ITEM_STACK,
			new Dynamic<>(NbtOps.INSTANCE, data),
			data.contains("DataVersion") ? data.getInt("DataVersion") : 3700,
			SharedConstants.getCurrentVersion().getDataVersion().getVersion()
		).getValue();
	}

	public static ListTag update(ListTag data) {
		ListTag updated = new ListTag();
		for (int i = 0; i < data.size(); i++) {
			CompoundTag item = data.getCompound(i);
			updated.add(update(item));
		}
		return updated;
	}

}
