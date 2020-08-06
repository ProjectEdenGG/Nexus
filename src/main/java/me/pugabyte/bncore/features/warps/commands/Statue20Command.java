package me.pugabyte.bncore.features.warps.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.statuehunt.StatueHunt;
import me.pugabyte.bncore.models.statuehunt.StatueHuntService;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor
public class Statue20Command extends _WarpCommand implements Listener {

	public String header = StringUtils.colorize("&1[StatueHunt 20]");
	public String enchant = "Gears 1";

	public Statue20Command(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.STATUE_HUNT20;
	}

	private Sign getTargetSign(Player player) {
		Block targetBlock = player.getTargetBlockExact(10);
		Material material = targetBlock.getType();
		if (Utils.isNullOrAir(material) || !MaterialTag.SIGNS.isTagged(material))
			error("You must be looking at a sign!");
		return (Sign) targetBlock.getState();
	}

	@Path("check [player]")
	void check(@Arg("self") OfflinePlayer player) {
		StatueHuntService service = new StatueHuntService();
		StatueHunt statueHunt = service.get(player);

		int found = statueHunt.getFound().size();
		send(PREFIX + player.getName() + " has found &e" + found + " &3statue" + (found > 1 ? "s" : ""));
		if (player().hasPermission("group.staff") && found > 0) {
			String foundString = "";
			for (String s : statueHunt.getFound()) {
				foundString += "&3" + s + "\n";
			}
			send(json("&eClick here to view the found statues").hover(foundString));
		}
	}

	@Override
	@Path("(teleport|tp|warp) <name>")
	@Permission("group.staff")
	public void teleport(Warp warp) {
		warp.teleport(player());
		send(PREFIX + "&3Warping to &e" + warp.getName());
	}

	@Path("<name>")
	@Override
	@Permission("group.staff")
	public void tp(Warp warp) {
		teleport(warp);
	}

	@Path("tp nearest")
	@Override
	@Permission("group.staff")
	public void teleportNearest() {
		getNearestWarp(player().getLocation()).ifPresent(this::teleport);
	}

	@Path("nearest")
	@Override
	@Permission("group.staff")
	public void nearest() {
		Optional<Warp> warp = getNearestWarp(player().getLocation());
		if (!warp.isPresent())
			error("No nearest warp found");
		send(PREFIX + "Nearest warp is &e" + warp.get().getName() + " &3(&e" + (int) warp.get().getLocation().distance(player().getLocation()) + " &3blocks away)");
	}

	@Permission("group.staff")
	@Path("sign <player>")
	void sign(String player) {
		Sign sign = getTargetSign(player());
		sign.setLine(0, header);
		sign.setLine(1, player);
		sign.update();
	}

	@Path("claim")
	void claim() {
		StatueHuntService service = new StatueHuntService();
		StatueHunt statueHunt = service.get(event.getPlayer());

		if (statueHunt.isClaimed())
			error("You have already claimed the reward for finding all the statues");

		if (statueHunt.getFound().size() != 21)
			error("You have not found all of the statues. Keep hunting!");

		new StatueHuntPrizeMenu().open(player());
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction())) return;
		if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!header.equals(sign.getLine(0))) return;

		final String PREFIX = StringUtils.getPrefix("StatueHunt");
		final String DISCORD_PREFIX = "**[StatueHunt]** ";

		String line = sign.getLine(1);

		StatueHuntService service = new StatueHuntService();
		StatueHunt statueHunt = service.get(event.getPlayer());

		if (statueHunt.getFound().contains(line)) {
			send(event.getPlayer(), PREFIX + "You have already found this statue");
			return;
		}

		statueHunt.getFound().add(line);
		service.save(statueHunt);
		BNCore.getEcon().depositPlayer(event.getPlayer(), 1000);
		send(event.getPlayer(), PREFIX + "&e$1000 &3has been added to your account for finding &e" + line);

		int found = statueHunt.getFound().size();

		if (found == 5) {
			VoteService voteService = new VoteService();
			Voter voter = voteService.get(event.getPlayer());
			voter.addPoints(10);
			voteService.save(voter);
			send(event.getPlayer(), PREFIX + "You also received &e10 Vote Points &3for finding 5 statues in total");
			return;
		}

		if (found % 10 == 0) {
			send(event.getPlayer(), PREFIX + "You can now claim a map from &c/warp gallery&3. Please make a &e/ticket &3when you know what map you want");
			logStaff(DISCORD_PREFIX + event.getPlayer().getName() + " has found " + found + " statues and can now claim a map from /warp gallery");
			return;
		}

		if (found == 15) {
			send(event.getPlayer(), PREFIX + "For finding 15 statues, you can now choose an item to put the custom enchantment &e" + enchant + " &3on. Please " +
					"make a &e/ticket &3to apply the item");
			logStaff(DISCORD_PREFIX + event.getPlayer().getName() + " has found 15 statues and can now put " + enchant + " on any item");
			return;
		}

		if (found == 21) {
			send(event.getPlayer(), PREFIX + "You have found all the statues! You can now claim either a &eBee Pet &3 or &eBee Disguise&3. Use &e/statue20 claim &3to claim your prize.");
			return;
		}
	}

	public void logStaff(String message) {
		Discord.staffLog(message);
		Discord.staffBridge(message);
	}

	public class StatueHuntPrizeMenu extends MenuUtils implements InventoryProvider {

		public void open(Player player) {
			SmartInventory.builder().size(3, 9).title("Statue Hunt Reward").provider(this).build().open(player);
		}

		@Override
		public void init(Player player, InventoryContents contents) {

			ItemStack beePet = new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_Bee").name("&eBee Pet").lore("&3Click here to receive")
					.lore("&3the bee pet: &c/pets").build();
			contents.set(1, 3, ClickableItem.from(beePet, e -> {
				runCommandAsConsole("lp user " + player.getName() + " permission set miniaturepets.pet.Bee true");
				send(player, "&3You have claimed the &eBee Pet");
				StatueHuntService service = new StatueHuntService();
				StatueHunt statueHunt = service.get(player);

				statueHunt.setClaimed(true);
				service.save(statueHunt);
				player.closeInventory();
			}));

			ItemStack beeDis = new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_Bee").name("&eBee Disguise").lore("&3Click here to receive")
					.lore("&3the bee disguise: &c/disguise bee").build();
			contents.set(1, 5, ClickableItem.from(beeDis, e -> {
				runCommandAsConsole("lp user " + player.getName() + " permission set libsdisguises.disguise.bee.setBeeAnger.setFlipped.setHasNectar.setHasStung.setSleeping.setUpsideDown.setSitting.setArrowsSticking.setEnraged.setViewSelfDisguise.setBaby.setBurning true");
				send(player, "&3You have claimed the &eBee Disguise");
				StatueHuntService service = new StatueHuntService();
				StatueHunt statueHunt = service.get(player);

				statueHunt.setClaimed(true);
				service.save(statueHunt);
				player.closeInventory();
			}));

		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {

		}
	}

}
