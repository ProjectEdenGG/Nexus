package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.statistics.StatisticsUser;
import gg.projecteden.nexus.utils.SerializationUtils.NBT;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import org.bukkit.inventory.ItemStack;

@Permission(Group.ADMIN)
public class DataFixerUpperCommand extends CustomCommand {

	public DataFixerUpperCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("item serialize")
	@Description("Serialize your held item to NBT")
	void serialize() {
		ItemStack item = getToolRequired();
		String url = StringUtils.paste(NBT.serializeItemStack(item));
		send(json(url).url(url).hover(url));
	}

	@Path("item deserialize <paste>")
	@Description("Deserialize an item from NBT and give it to yourself")
	void deserialize(String paste) {
		giveItem(NBT.deserializeItemStack(StringUtils.getPaste(paste)));
	}

	@SneakyThrows
	@Path("item fix <paste>")
	@HideFromWiki
	@HideFromHelp
	void fix(String paste) {
		CompoundTag tag = TagParser.parseCompoundFully(StringUtils.getPaste(paste));
		ListTag list = tag.getCompound("tag").get().getCompound("ProjectEden").get().getList("Items").get();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag item = list.getCompound(i).get();
			ItemStack fixed = net.minecraft.world.item.ItemStack.CODEC.parse(NbtOps.INSTANCE, item).getOrThrow().asBukkitCopy();
			giveItem(fixed);
		}
	}

	@SneakyThrows
	@Path("stats fix <player>")
	@HideFromWiki
	@HideFromHelp
	void fix(StatisticsUser user) {
		String url = StringUtils.paste(user.getFileFixed());
		send(json(url).url(url).hover(url));
	}

}
