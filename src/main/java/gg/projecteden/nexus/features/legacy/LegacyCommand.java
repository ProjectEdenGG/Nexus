package gg.projecteden.nexus.features.legacy;

import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.legacy.menus.homes.LegacyHomesMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemPendingMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReceiveMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReviewMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemTransferMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ReviewableMenu;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.legacy.LegacyUser;
import gg.projecteden.nexus.models.legacy.LegacyUserService;
import gg.projecteden.nexus.models.legacy.homes.LegacyHome;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeOwner;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeService;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUserService;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.vaults.VaultUser;
import gg.projecteden.nexus.models.vaults.VaultUserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Environments(Env.TEST)
@Permission(Group.STAFF)
public class LegacyCommand extends _WarpSubCommand {
	private final LegacyHomeService legacyHomeService = new LegacyHomeService();
	private LegacyHomeOwner legacyHomeOwner;

	public LegacyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			legacyHomeOwner = legacyHomeService.get(player());
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.LEGACY;
	}

	// Items

	@Path("items transfer")
	@Description("Submit legacy items for transfer review")
	void items_transfer() {
		// TODO 1.19 Only in legacy
		new ItemTransferMenu(player());
	}

	@Path("items pending")
	@Description("View legacy items pending transfer approval")
	void items_pending() {
		new ItemPendingMenu(player()).open(player());
	}

	@Path("items review [player]")
	@Description("Review pending items")
	@Permission(Group.ADMIN)
	void items_review(LegacyItemTransferUser user) {
		if (user == null)
			new ReviewableMenu().open(player());
		else
			new ItemReviewMenu(user).open(player());
	}

	@Path("items receive")
	@Description("Receive transfer approved legacy items")
	void items_receive() {
		new ItemReceiveMenu(player());
	}

	// Homes

	@Path("homes (teleport|tp) [home]")
	void homes_teleport(@Arg(value = "home", tabCompleter = LegacyHome.class) String name) {
		if (legacyHomeOwner.getHomes().size() == 0)
			error("You do not have any legacy homes");

		Optional<LegacyHome> home = legacyHomeOwner.getHome(name);
		if (home.isEmpty())
			error("You do not have a legacy home named &e" + name);

		home.get().teleportAsync(player());
	}

	@Path("homes (teleport|tp) <player> <home>")
	void homes_teleport(OfflinePlayer player, @Arg(context = 1) LegacyHome legacyHome) {
		legacyHome.teleportAsync(player());
	}

	@Path("homes <player>")
	void homes(LegacyHomeOwner legacyHomeOwner) {
		new LegacyHomesMenu(legacyHomeOwner).open(player());
	}

	@Path("homes setItem <home> <material>")
	void homes_setItem(LegacyHome home, Material material) {
		home.setItem(new ItemStack(material));
		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + "Legacy home display item set to " + camelCase(material));
	}

	@Path("homes set <name>")
	void homes_set(String legacyHomeName) {
		Optional<LegacyHome> home = legacyHomeOwner.getHome(legacyHomeName);

		String message;
		if (home.isPresent()) {
			home.get().setLocation(location());
			message = "Updated location of legacy home &e" + legacyHomeName + "&3";
		} else {
			legacyHomeOwner.add(LegacyHome.builder()
				.uuid(legacyHomeOwner.getUuid())
				.name(legacyHomeName)
				.location(location())
				.build());
			message = "Legacy home &e" + legacyHomeName + "&3 set to current location. Return with &c/legacy homes tp " + legacyHomeName;
		}

		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + message);
	}

	@Permission(Group.STAFF)
	@Path("homes set <player> <name>")
	void homes_set(LegacyHomeOwner legacyHomeOwner, String legacyHomeName) {
		Optional<LegacyHome> home = legacyHomeOwner.getHome(legacyHomeName);
		String message;
		if (home.isPresent()) {
			home.get().setLocation(location());
			message = "Updated location of legacy home &e" + legacyHomeName + "&3";
		} else {
			legacyHomeOwner.add(LegacyHome.builder()
				.uuid(legacyHomeOwner.getUuid())
				.name(legacyHomeName)
				.location(location())
				.build());
			message = "Legacy home &e" + legacyHomeName + "&3 set to current location";
		}

		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + message);
	}

	@Path("homes delete <name>")
	void homes_delete(@Arg("home") LegacyHome legacyHome) {
		legacyHomeOwner.delete(legacyHome);
		legacyHomeService.save(legacyHomeOwner);

		send(PREFIX + "Legacy home &e" + legacyHome.getName() + "&3 deleted");
	}

	@Permission(Group.STAFF)
	@Path("homes delete <player> <name>")
	void homes_delete(LegacyHomeOwner legacyHomeOwner, @Arg(context = 1) LegacyHome legacyHome) {
		legacyHomeOwner.delete(legacyHome);
		legacyHomeService.save(legacyHomeOwner);

		send(PREFIX + "Legacy home &e" + legacyHome.getName() + "&3 deleted");
	}

	@ConverterFor(LegacyHome.class)
	LegacyHome convertToLegacyHome(String value, OfflinePlayer context) {
		if (context == null) context = player();
		return legacyHomeService.get(context).getHome(value).orElseThrow(() -> new InvalidInputException("That legacy home does not exist"));
	}

	@TabCompleterFor(LegacyHome.class)
	public List<String> tabCompleteLegacyHome(String filter, OfflinePlayer context) {
		if (context == null) context = player();
		return legacyHomeService.get(context).getNames(filter);
	}

	// Vaults

	@Path("[page] [user]")
	void open(@Arg(value = "1", min = 1) int page, @Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
		if (WorldGroup.of(player()) != WorldGroup.LEGACY && !isSeniorStaff())
			error("You can't open vaults here");

		new LegacyVaultMenu(player(), user, page);
	}

	@Path("limit [user]")
	void limit(@Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
		send(PREFIX + (isSelf(user) ? "You own" : user.getNickname() + " owns") + " &e" + user.getLimit() + " &3legacy vaults");
	}

	public static class LegacyVaultMenu implements TemporaryMenuListener {
		private final LegacyVaultUserService service = new LegacyVaultUserService();
		@Getter
		private final Player player;
		private final LegacyVaultUser user;
		private final int page;

		@Getter
		private final LegacyVaultHolder inventoryHolder = new LegacyVaultHolder();

		public LegacyVaultMenu(Player player, LegacyVaultUser user, int page) {
			this.player = player;
			this.user = user;
			this.page = page;

			open(user.get(page, player));
		}

		@Override
		public String getTitle() {
			return "Vault #" + page;
		}

		public static class LegacyVaultHolder extends CustomInventoryHolder {}

		@Override
		public boolean keepAirSlots() {
			return true;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			user.update(page, contents);
			service.save(user);
		}
	}

	// Archival

	@Async
	@Path("archive homes")
	@Permission(Group.ADMIN)
	void archive_homes() {
		int count = 0;
		final HomeService homeService = new HomeService();
		for (HomeOwner uuid : homeService.getAll()) {
			HomeOwner homeOwner = homeService.get(uuid);

			final List<Home> homes = homeOwner.getHomes();
			if (homes.isEmpty())
				continue;

			for (Home home : new ArrayList<>(homes)) {
				if (home.getWorldGroup() != WorldGroup.SURVIVAL)
					continue;

				legacyHomeOwner.add(LegacyHome.builder()
					.uuid(home.getUniqueId())
					.name(home.getName())
					.location(home.getLocation())
					.item(home.getItem())
					.build());

				++count;

				// TODO 1.19 Delete original home
				// homeOwner.delete(home);
			}

			legacyHomeService.save(legacyHomeOwner);
			homeService.save(homeOwner);
		}

		send(PREFIX + "Archived " + count + " survival homes");
	}

	@Async
	@Path("archive balances")
	@Permission(Group.ADMIN)
	void archive_balances() {
		final LegacyUserService legacyUserService = new LegacyUserService();
		final BankerService bankerService = new BankerService();
		int count = 0;

		for (Banker banker : bankerService.getAll()) {
			if (!banker.getBalances().containsKey(ShopGroup.SURVIVAL))
				continue;

			legacyUserService.edit(banker, legacyUser -> legacyUser.setBalance(banker.getBalance(ShopGroup.SURVIVAL)));

			++count;

			// TODO 1.19 zero balance
			// banker.getBalances().remove(ShopGroup.SURVIVAL);
			// bankerService.save(banker);
		}

		send(PREFIX + "Archived " + count + " balances");
	}

	@Async
	@Path("archive vaults")
	@Permission(Group.ADMIN)
	void archive_vaults() {
		final LegacyVaultUserService legacyVaultService = new LegacyVaultUserService();
		final VaultUserService vaultService = new VaultUserService();
		AtomicInteger countVaults = new AtomicInteger();
		AtomicInteger countUsers = new AtomicInteger();

		for (VaultUser uuid : vaultService.getAll()) {
			VaultUser user = vaultService.get(uuid);

			legacyVaultService.edit(user, legacyUser -> {
				legacyUser.setVaults(user.getVaults());
				legacyUser.setLimit(user.getLimit());
				countVaults.getAndAdd(legacyUser.getVaults().size());
			});

			countUsers.getAndIncrement();

			// TODO 1.19 delete items
			// user.getVaults().clear();
			// vaultService.save(user);
		}

		send(PREFIX + "Archived " + countVaults + " vaults for " + countUsers + " users");
	}

	@Async
	@Path("archive mcmmo")
	@Permission(Group.ADMIN)
	void archive_mcmmo() {
		final LegacyUserService legacyUserService = new LegacyUserService();
		int countLevels = 0;
		int countUsers = 0;

		for (Nerd nerd : new NerdService().getAll()) {
			final LegacyUser legacyUser = legacyUserService.get(nerd);
			final PlayerProfile mcmmoPlayer = mcMMO.getDatabaseManager().loadPlayerProfile(nerd.getUniqueId());

			for (PrimarySkillType skill : PrimarySkillType.values()) {
				final int level = mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name()));
				if (level == 0)
					continue;

				legacyUser.getMcmmo().put(skill.name(), level);

				++countLevels;

				// TODO 1.19 zero level
				// previous.modifySkill(skill, 0);
			}

			if (!legacyUser.getMcmmo().isEmpty()) {
				legacyUserService.save(legacyUser);
				++countUsers;
			}
		}

		send(PREFIX + "Archived " + countLevels + " mcmmo levels for " + countUsers + " users");
	}

	@Disabled
	@Path("reset mail")
	void reset_mail() {
		int count = 0;

		final MailerService service = new MailerService();
		for (Mailer uuid : service.getAll()) {
			Mailer mailer = service.get(uuid);
			count += mailer.getMail().remove(WorldGroup.SURVIVAL).size();
			mailer.getPendingMail().remove(WorldGroup.SURVIVAL);
			service.save(mailer);
		}

		send(PREFIX + "Reset " + count + " mail");
	}

	@Disabled
	@Path("reset shops")
	void reset_shops() {
		final ShopService service = new ShopService();
		final AtomicInteger countProduct = new AtomicInteger();
		final AtomicInteger countShop = new AtomicInteger();

		for (Shop uuid : service.getAll()) {
			service.edit(uuid, shop -> {
				countProduct.getAndAdd(shop.getProducts(ShopGroup.SURVIVAL).size());
				if (shop.getProducts().removeIf(product -> product.getShopGroup() == ShopGroup.SURVIVAL))
					countShop.getAndIncrement();
			});
		}

		send(PREFIX + "Archived " + countProduct + " products for " + countShop + " users");
	}

}
