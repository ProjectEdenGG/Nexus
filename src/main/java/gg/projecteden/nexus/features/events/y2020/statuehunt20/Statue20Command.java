package gg.projecteden.nexus.features.events.y2020.statuehunt20;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.statuehunt.StatueHunt;
import gg.projecteden.nexus.models.statuehunt.StatueHuntService;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Disabled
@HideFromWiki
@NoArgsConstructor
public class Statue20Command extends _WarpCommand implements Listener {
	private boolean disabled = true;

	public String header = StringUtils.colorize("&1[StatueHunt 20]");
	public String enchant = "Gears 1";

	public Statue20Command(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.STATUE_HUNT20;
	}

	@Path("check [player]")
	void check(@Arg("self") OfflinePlayer player) {
		StatueHuntService service = new StatueHuntService();
		StatueHunt statueHunt = service.get(player);

		int found = statueHunt.getFound().size();
		send(PREFIX + player.getName() + " has found &e" + found + " &3statue" + (found > 1 ? "s" : ""));
		if (isStaff() && found > 0) {
			String foundString = "";
			for (String s : statueHunt.getFound()) {
				foundString += "&3" + s + "\n";
			}
			send(json("&eClick here to view the found statues").hover(foundString));
		}
	}

	@Override
	@Path("(teleport|tp|warp) <name>")
	@Permission(Group.STAFF)
	public void teleport(Warp warp) {
		super.teleport(warp);
	}

	@Override
	@Path("<name>")
	@Permission(Group.STAFF)
	public void tp(Warp warp) {
		super.tp(warp);
	}

	@Path("tp nearest")
	@Override
	@Permission(Group.STAFF)
	public void teleportNearest() {
		super.teleportNearest();
	}

	@Path("nearest")
	@Override
	@Permission(Group.STAFF)
	public void nearest() {
		super.nearest();
	}

	@Permission(Group.STAFF)
	@Path("sign <player>")
	void sign(String player) {
		Sign sign = getTargetSignRequired();
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
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		if (!header.equals(sign.getLine(0))) return;

		final String PREFIX = StringUtils.getPrefix("StatueHunt");
		final String DISCORD_PREFIX = "**[StatueHunt]** ";

		if (disabled) {
			send(event.getPlayer(), PREFIX + "You've found a statue from the &e2020 Statue Hunt!");
			return;
		}

		String line = sign.getLine(1);

		StatueHuntService service = new StatueHuntService();
		StatueHunt statueHunt = service.get(event.getPlayer());

		if (statueHunt.getFound().contains(line)) {
			send(event.getPlayer(), PREFIX + "You have already found this statue");
			return;
		}

		statueHunt.getFound().add(line);
		service.save(statueHunt);
		new BankerService().deposit(event.getPlayer(), 1000, ShopGroup.SURVIVAL, TransactionCause.EVENT);
		send(event.getPlayer(), PREFIX + "&e$1000 &3has been added to your account for finding &e" + line);

		int found = statueHunt.getFound().size();

		if (found == 5) {
			new VoterService().edit(event.getPlayer(), voter -> voter.givePoints(10));
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

	@Rows(3)
	@Title("Statue Hunt Reward")
	public class StatueHuntPrizeMenu extends InventoryProvider {

		@Override
		public void init() {

			ItemStack beePet = new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_Bee").name("&eBee Pet").lore("&3Click here to receive")
					.lore("&3the bee pet: &c/pets").build();
			contents.set(1, 3, ClickableItem.of(beePet, e -> {
				PermissionChange.set().player(viewer).permissions("miniaturepets.pet.Bee").runAsync();
				send(viewer, "&3You have claimed the &eBee Pet");
				new StatueHuntService().edit(viewer, statueHunt -> statueHunt.setClaimed(true));
				viewer.closeInventory();
			}));

			ItemStack beeDis = new ItemBuilder(Material.PLAYER_HEAD).skullOwner("MHF_Bee").name("&eBee Disguise").lore("&3Click here to receive")
					.lore("&3the bee disguise: &c/disguise bee").build();

			contents.set(1, 5, ClickableItem.of(beeDis, e -> {
				PermissionChange.set().player(viewer).permissions("libsdisguises.disguise.bee.setBeeAnger.setFlipped.setHasNectar.setHasStung.setSleeping.setUpsideDown.setSitting.setArrowsSticking.setEnraged.setViewSelfDisguise.setBaby.setBurning").runAsync();
				send(viewer, "&3You have claimed the &eBee Disguise");
				new StatueHuntService().edit(viewer, statueHunt -> statueHunt.setClaimed(true));
				viewer.closeInventory();
			}));

		}
	}

}
