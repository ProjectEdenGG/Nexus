package me.pugabyte.bncore.features.commands.staff;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.regions.Region;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SymbolBanner;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Permission("group.staff")
public class TestCommand extends CustomCommand implements Listener {

	public TestCommand(CommandEvent event) {
		super(event);
	}

	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		if (event.getRegion().getId().equalsIgnoreCase("test_water")) {
			event.getPlayer().setSprinting(true);
		}
	}

	@Path("gravity fix")
	public void fixgravity() {
		player().setGravity(true);
	}

	@Path("thunder water")
	public void thunderWater() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player());
		WorldEditUtils WEUtils = new WorldEditUtils(player());

		String colorRg = "test_water";
		Region region = WGUtils.getRegion(colorRg);
		List<Block> blocks = WEUtils.getBlocks(region);

		Material air = Material.AIR;

		for (Block block : blocks) {
			player().sendBlockChange(block.getLocation(), air.createBlockData());
		}
	}

	@Path("thunder ship")
	public void thunderShip() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player());
		WorldEditUtils WEUtils = new WorldEditUtils(player());

		String colorRg = "test_color";
		Region region = WGUtils.getRegion(colorRg);
		List<Block> blocks = WEUtils.getBlocks(region);

		Material concreteType = RandomUtils.randomMaterial(MaterialTag.CONCRETES);

		for (Block block : blocks) {
			if (block.getType().equals(Material.WHITE_CONCRETE))
				player().sendBlockChange(block.getLocation(), concreteType.createBlockData());
		}
	}

	@Path("banner")
	public void banner() {
		ItemStack banner1 = new ItemBuilder(Material.YELLOW_BANNER).symbolBanner(SymbolBanner.Symbol.A, DyeColor.BLACK).build();
		Utils.giveItem(player(), banner1);

		ItemStack banner2 = new ItemBuilder(Material.YELLOW_BANNER).symbolBanner('A', DyeColor.BLACK).build();
		Utils.giveItem(player(), banner2);
	}

	@Path("BFParticipants")
	public void bearfair() {
		BearFairService bearFairService = new BearFairService();
		DiscordService discordService = new DiscordService();
		List<BearFairUser> all = bearFairService.getAll();
		List<BearFairUser> left = new ArrayList<>();
		AtomicInteger count = new AtomicInteger();

		all.stream()
				.filter(user -> user.getTotalPoints() > 0)
				.sorted(Comparator.comparing(BearFairUser::getTotalPoints).reversed())
				.forEach(user -> {
					String uuid = user.getOfflinePlayer().getUniqueId().toString();
					DiscordUser discordUser = discordService.get(uuid);
					if (!isNullOrEmpty(discordUser.getUserId())) {
						Discord.addRole(discordUser.getUserId(), DiscordId.Role.BEAR_FAIR_PARTICIPANT);
						count.incrementAndGet();
					} else
						left.add(user);
				});

		send("Added: " + count.get());
		send("Left: " + left.size());
		send("");
		send("Left: ");
		for (BearFairUser user : left) {
			send("- " + user.getOfflinePlayer().getName());
		}
	}


}
