package gg.projecteden.nexus.features.legacy;

import com.onarandombox.multiverseinventories.profile.PlayerProfile;
import com.onarandombox.multiverseinventories.profile.ProfileTypes;
import com.onarandombox.multiverseinventories.share.Sharable;
import com.onarandombox.multiverseinventories.share.Sharables;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.legacy.menus.homes.LegacyHomesMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReceiveMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemReviewMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemStatusMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ItemTransferMenu;
import gg.projecteden.nexus.features.legacy.menus.itemtransfer.ReviewableMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.legacy.homes.LegacyHome;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeOwner;
import gg.projecteden.nexus.models.legacy.homes.LegacyHomeService;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUser;
import gg.projecteden.nexus.models.legacy.vaults.LegacyVaultUserService;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environments({Env.PROD, Env.UPDATE})
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

	private void legacyOnly() {
		if (worldGroup() != WorldGroup.LEGACY)
			error("You can only run this command in the legacy world");
	}

	// Items

	@Path("items transfer")
	@Description("Submit legacy items for transfer review")
	void items_transfer() {
		legacyOnly();
		new ItemTransferMenu(player());
	}

	@Path("items list <status> [player]")
	@Description("View legacy items in each state")
	void items_list(ReviewStatus status, @Arg(value = "self", permission = Group.ADMIN) LegacyItemTransferUser user) {
		new ItemStatusMenu(user, status).open(player());
	}

	@Permission(Group.ADMIN)
	@Path("items review [player]")
	@Description("Review pending items")
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

	@Permission(Group.ADMIN)
	@Path("items reset <player>")
	@Description("Reset all items to pending status")
	void items_reset(LegacyItemTransferUser user) {
		for (ReviewStatus status : EnumUtils.valuesExcept(ReviewStatus.class, ReviewStatus.PENDING))
			user.getItems(ReviewStatus.PENDING).addAll(Objects.requireNonNullElseGet(user.getItems().remove(status), Collections::emptyList));
		new LegacyItemTransferUserService().save(user);
		send(PREFIX + "Reset all of " + user.getNickname() + "'s items to pending status");
	}

	// Homes

	@Path("home <home>")
	@Description("Teleport to your legacy homes")
	void home(LegacyHome legacyHome) {
		legacyHome.teleportAsync(player());
	}

	@Path("home visit <player> <home>")
	@Description("Teleport to another player's legacy homes")
	void home_visit(OfflinePlayer player, @Arg(context = 1) LegacyHome legacyHome) {
		legacyHome.teleportAsync(player());
	}

	@Path("homes [player]")
	@Description("View a list of legacy homes")
	void homes(@Arg("self") LegacyHomeOwner legacyHomeOwner) {
		new LegacyHomesMenu(legacyHomeOwner).open(player());
	}

	@Path("homes setItem <home> <material>")
	@Description("Set the display item for a legacy home")
	void homes_setItem(LegacyHome home, Material material) {
		home.setItem(new ItemStack(material));
		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + "Legacy home display item set to " + camelCase(material));
	}

	@Path("homes set <name> [player]")
	@Description("Set a new legacy home")
	void homes_set(String legacyHomeName, @Arg(value = "self", permission = Group.STAFF) LegacyHomeOwner legacyHomeOwner) {
		legacyOnly();

		Optional<LegacyHome> home = legacyHomeOwner.getHome(legacyHomeName);

		String message;
		if (home.isPresent()) {
			home.get().setLocation(location());
			message = "Updated location of legacy home &e" + legacyHomeName + "&3";
		} else {
			legacyHomeOwner.add(LegacyHome.builder()
				.uuid(legacyHomeOwner.getUuid())
				.name(legacyHomeName)
				.location(location()));
			message = "Legacy home &e" + legacyHomeName + "&3 set to current location. Return with &c/legacy home " + legacyHomeName;
		}

		legacyHomeService.save(legacyHomeOwner);
		send(PREFIX + message);
	}

	@Path("homes delete <name> [player]")
	@Description("Delete a legacy home")
	void homes_delete(@Arg("home") LegacyHome legacyHome, @Arg(value = "self", permission = Group.STAFF) LegacyHomeOwner legacyHomeOwner) {
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

	@Path("vaults [page] [user]")
	@Description("Open a legacy vault")
	void vaults(@Arg(value = "1", min = 1) int page, @Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
		legacyOnly();

		new LegacyVaultMenu(player(), user, page);
	}

	@Path("vaults limit [user]")
	@Description("View how many legacy vaults you own")
	void vaults_limit(@Arg(value = "self", permission = Group.SENIOR_STAFF) LegacyVaultUser user) {
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

	/*
	static final List<SubWorldGroup> subWorldGroups = Arrays.asList(SubWorldGroup.SURVIVAL, SubWorldGroup.LEGACY1, SubWorldGroup.LEGACY2, SubWorldGroup.LEGACY);

	@Async
	@Path("archive homes")
	@Permission(Group.ADMIN)
	void archive_homes() {
		int count = 0;
		final HomeService homeService = new HomeService();
		final LegacyHomeService legacyHomeService = new LegacyHomeService();

		for (HomeOwner uuid : homeService.getAll()) {
			HomeOwner homeOwner = homeService.get(uuid);
			LegacyHomeOwner legacyHomeOwner = legacyHomeService.get(uuid);

			final List<Home> homes = homeOwner.getHomes();
			if (homes.isEmpty())
				continue;

			for (Home home : new ArrayList<>(homes)) {
				if (!subWorldGroups.contains(SubWorldGroup.of(home.getLocation())))
					continue;

				legacyHomeOwner.getHomes().add(LegacyHome.builder()
					.uuid(home.getUniqueId())
					.name(home.getName())
					.location(home.getLocation())
					.item(home.getItem())
					.build());

				++count;

				homeOwner.getHomes().removeIf(_home -> home.getName().equals(_home.getName()));
			}
		}

		legacyHomeService.saveCache();
		homeService.saveCache();

		send(PREFIX + "Archived " + count + " survival homes");
	}

	@Async
	@Path("archive votepoints")
	@Permission(Group.ADMIN)
	void archive_balances() {
		final LegacyUserService legacyUserService = new LegacyUserService();
		final VoterService voterService = new VoterService();
		int countVoters = 0;
		int countPoints = 0;

		for (Voter uuid : voterService.getAll()) {
			final Voter voter = voterService.get(uuid);

			if (voter.getPoints() == 0)
				continue;

			legacyUserService.edit(voter, legacyUser -> legacyUser.setVotePoints(voter.getPoints()));

			++countVoters;
			countPoints += voter.getPoints();

			voter.setPoints(0);
			voterService.save(voter);
		}

		send(PREFIX + "Archived " + countPoints + " vote points for " + countVoters + " voters");
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

			banker.getBalances().remove(ShopGroup.SURVIVAL);
			bankerService.save(banker);
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

			user.getVaults().clear();
			vaultService.save(user);
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

				mcmmoPlayer.modifySkill(skill, 0);
			}

			if (!legacyUser.getMcmmo().isEmpty()) {
				legacyUserService.save(legacyUser);
				++countUsers;
			}
		}

		send(PREFIX + "Archived " + countLevels + " mcmmo levels for " + countUsers + " users");
	}

	@Async
	@Path("archive mail")
	@Permission(Group.ADMIN)
	void archive_mail() {
		AtomicInteger count = new AtomicInteger();

		final MailerService service = new MailerService();
		final LegacyMailerService legacyService = new LegacyMailerService();
		for (Mailer uuid : service.getAll()) {
			service.edit(uuid, mailer -> {
				legacyService.edit(uuid, legacyMailer -> {
					legacyMailer.setMail(mailer.getMail(WorldGroup.SURVIVAL));
					legacyMailer.setPendingMail(mailer.getPendingMail().getOrDefault(WorldGroup.SURVIVAL, null));
				});

				count.addAndGet(mailer.getMail(WorldGroup.SURVIVAL).size());
				mailer.getMail().remove(WorldGroup.SURVIVAL);
				mailer.getPendingMail().remove(WorldGroup.SURVIVAL);
			});
		}

		send(PREFIX + "Archived " + count + " mail");
	}

	@Async
	@Path("archive shops")
	@Permission(Group.ADMIN)
	void archive_shops() {
		final ShopService service = new ShopService();
		final LegacyShopService legacyService = new LegacyShopService();
		final AtomicInteger countProduct = new AtomicInteger();
		final AtomicInteger countShop = new AtomicInteger();

		for (Shop uuid : service.getAll()) {
			service.edit(uuid, shop -> {
				countProduct.getAndAdd(shop.getProducts(ShopGroup.SURVIVAL).size());

				legacyService.edit(uuid, legacyShop -> {
					legacyShop.setProducts(shop.getProducts(ShopGroup.SURVIVAL));
					legacyShop.setHolding(shop.getHolding());
				});

				final boolean hasProducts = !shop.getProducts(ShopGroup.SURVIVAL).isEmpty();
				final boolean hasHolding = !shop.getHolding().isEmpty();
				if (hasProducts || hasHolding)
					countShop.getAndIncrement();

				shop.getProducts().removeIf(product -> product.getShopGroup() == ShopGroup.SURVIVAL);
				shop.getHolding().clear();
			});
		}

		send(PREFIX + "Archived " + countProduct + " products for " + countShop + " users");
	}

	@Async
	@Path("archive streaks")
	@Permission(Group.ADMIN)
	void archive_streaks() {
		final DailyRewardUserService dailyUserService = new DailyRewardUserService();

		for (DailyRewardUser uuid : dailyUserService.getAll()) {
			dailyUserService.edit(uuid, user -> {
				int day = 1;
				while (user.getCurrentStreak().canClaim(day))
					user.getCurrentStreak().claim(day++);
			});
		}
	}
	*/

	@SuppressWarnings("SameParameterValue")
	@Data
	private static class InvData {
		private final Nerd nerd;
		private final GameMode gamemode;

		public InvData(Nerd nerd, GameMode gamemode) {
			this.nerd = nerd;
			this.gamemode = gamemode;
		}

		private ItemStack[] getInventory(WorldGroup group) {
			final PlayerProfile survivalData = getProfile(group);

			ItemStack[] inventory = survivalData.get(Sharables.INVENTORY);

			if (nerd.getWorldGroup() == group)
				inventory = nerd.getInventory().toArray(ItemStack[]::new);

			return inventory;
		}

		private ItemStack[] getEnderChest(WorldGroup group) {
			final PlayerProfile survivalData = getProfile(group);

			ItemStack[] inventory = survivalData.get(Sharables.ENDER_CHEST);

			if (nerd.getWorldGroup() == group)
				inventory = nerd.getEnderChest().toArray(ItemStack[]::new);

			return inventory;
		}

		private ItemStack[] getArmor(WorldGroup group) {
			final PlayerProfile survivalData = getProfile(group);

			ItemStack[] inventory = survivalData.get(Sharables.ARMOR);

			if (nerd.getWorldGroup() == group)
				inventory = nerd.getArmor().toArray(ItemStack[]::new);

			return inventory;
		}

		private ItemStack getOffHand(WorldGroup group) {
			final PlayerProfile survivalData = getProfile(group);

			ItemStack item = survivalData.get(Sharables.OFF_HAND);

			if (nerd.getWorldGroup() == group)
				item = nerd.getOffHand();

			return item;
		}

		public PlayerProfile getProfile(WorldGroup group) {
			var survivalGroup = Nexus.getMultiverseInventories().getGroupManager().getGroup(StringUtils.camelCase(group));
			return survivalGroup.getGroupProfileContainer().getPlayerData(ProfileTypes.forGameMode(gamemode), nerd.getOfflinePlayer());
		}
	}

	public static boolean cancelled = false;

	@Path("inventories archive cancel")
	@Permission(Group.ADMIN)
	@Description("Cancel inventory archive task")
	void inventories_archive_cancel() {
		cancelled = true;
		send(PREFIX + "Cancelling task...");
	}

	@Async
	@SneakyThrows
	@Permission(Group.ADMIN)
	@Path("inventories verify all")
	@Description("Verify all survival inventories are empty")
	void inventories_verify_all() {
		for (Nerd nerd : new NerdService().getAll()) {
			try {
				if (cancelled) {
					send(PREFIX + "Cancelled");
					cancelled = false;
					return;
				}

				final InvData data = new InvData(nerd, GameMode.SURVIVAL);

				if (!ItemUtils.nonNullOrAir(data.getInventory(WorldGroup.SURVIVAL)).isEmpty())
					Dev.GRIFFIN.send(nerd.getNickname() + " has non null survival inventory contents");
				if (!ItemUtils.nonNullOrAir(data.getEnderChest(WorldGroup.SURVIVAL)).isEmpty())
					Dev.GRIFFIN.send(nerd.getNickname() + " has non null survival ender chest contents");
			} catch (NexusException ex) {
				send(ex.getMessage());
			} catch (Exception ex) {
				send(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	@Async
	@SneakyThrows
	@Permission(Group.ADMIN)
	@Path("inventories archive all")
	@Description("Archive all legacy inventories")
	@Disabled
	void inventories_archive_all() {
		for (Nerd nerd : new NerdService().getAll()) {
			try {
				if (cancelled) {
					send(PREFIX + "Cancelled");
					cancelled = false;
					return;
				}

				final InvData data = new InvData(nerd, GameMode.SURVIVAL);

				new Timer("Archive inventory of " + nerd.getNickname(), () -> {
					AtomicBoolean archive = new AtomicBoolean(true);
					new Timer("  Check legacy inventories of " + nerd.getNickname(), () -> {
						if (!ItemUtils.nonNullOrAir(data.getInventory(WorldGroup.LEGACY)).isEmpty()) {
							Dev.GRIFFIN.send(nerd.getNickname() + " has non null legacy inventory contents, skipping");
							archive.set(false);
						}
						if (!ItemUtils.nonNullOrAir(data.getEnderChest(WorldGroup.LEGACY)).isEmpty()) {
							Dev.GRIFFIN.send(nerd.getNickname() + " has non null legacy ender chest contents, skipping");
							archive.set(false);
						}
					});

					if (archive.get())
						inventories_archive(nerd);
				});
			} catch (NexusException ex) {
				send(ex.getMessage());
			} catch (Exception ex) {
				send(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	@Permission(Group.ADMIN)
	@Path("inventories archive <player>")
	@Description("Archive a player's legacy inventory")
	void inventories_archive(Nerd nerd) {
		final InvData data = new InvData(nerd, GameMode.SURVIVAL);

		new Timer("  Read & modify MVINV data", () -> {
			final PlayerProfile legacy = data.getProfile(WorldGroup.LEGACY);
			final PlayerProfile survival = data.getProfile(WorldGroup.SURVIVAL);

			legacy.set(Sharables.INVENTORY, data.getInventory(WorldGroup.SURVIVAL));
			legacy.set(Sharables.ARMOR, data.getArmor(WorldGroup.SURVIVAL));
			legacy.set(Sharables.OFF_HAND, data.getOffHand(WorldGroup.SURVIVAL));
			legacy.set(Sharables.ENDER_CHEST, data.getEnderChest(WorldGroup.SURVIVAL));

			for (Sharable<?> sharable : Sharables.allOf())
				survival.set(sharable, null);

			new Timer("    Save MVINV data", () -> {
				Nexus.getMultiverseInventories().getData().updatePlayerData(legacy);
				Nexus.getMultiverseInventories().getData().updatePlayerData(survival);
			});
		});

		final NBTPlayer nbtPlayer = new NBTPlayer(nerd);

		new Timer("  Read player.data", nbtPlayer::getNbtFile);

		new Timer("  Modify player.data", () -> {
			final WorldGroup worldGroup = nerd.getWorldGroup();
			if (worldGroup == WorldGroup.LEGACY || worldGroup == WorldGroup.SURVIVAL) {
				nbtPlayer.getNbtFile().removeKey("Inventory");
				nbtPlayer.getNbtFile().removeKey("EnderItems");
				nbtPlayer.getNbtFile().removeKey("XpLevel");
				nbtPlayer.getNbtFile().removeKey("XpP");
				nbtPlayer.getNbtFile().removeKey("XpSeed");
				nbtPlayer.getNbtFile().removeKey("XpTotal");
				nbtPlayer.getNbtFile().removeKey("foodExhaustionLevel");
				nbtPlayer.getNbtFile().removeKey("foodLevel");
				nbtPlayer.getNbtFile().removeKey("foodSaturationLevel");
				nbtPlayer.getNbtFile().removeKey("foodTickTimer");
				nbtPlayer.getNbtFile().removeKey("recipeBook");
				nbtPlayer.getNbtFile().removeKey("seenCredits");
				nbtPlayer.getNbtFile().removeKey("SleepTimer");
				nbtPlayer.getNbtFile().removeKey("Fire");
				nbtPlayer.getNbtFile().removeKey("FallDistance");
				nbtPlayer.getNbtFile().removeKey("AbsorptionAmount");
				nbtPlayer.setLocation(WarpType.NORMAL.get("hub").getLocation());
			}

			nbtPlayer.getNbtFile().removeKey("Score");
			nbtPlayer.getNbtFile().removeKey("SpawnAngle");
			nbtPlayer.getNbtFile().removeKey("SpawnDimension");
			nbtPlayer.getNbtFile().removeKey("SpawnForced");
			nbtPlayer.getNbtFile().removeKey("SpawnX");
			nbtPlayer.getNbtFile().removeKey("SpawnY");
			nbtPlayer.getNbtFile().removeKey("SpawnZ");
		});

		new Timer("  Save player.data", () -> {
			try {
				nbtPlayer.getNbtFile().save();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		send(PREFIX + "Migrated inventory for " + nerd.getNickname());
	}

	private static final Function<ItemStack[], String> debugInventory = contents -> {
		if (contents == null)
			return "";
		return Arrays.stream(contents)
			.filter(Nullables::isNotNullOrAir)
			.map(StringUtils::pretty)
			.collect(Collectors.joining(", "));
	};

	@Async
	@Permission(Group.ADMIN)
	@Path("inventories debug <player> <gamemode>")
	@Description("View a list of items in a player's survival and legacy inventories")
	void inventories_debug(Nerd nerd, GameMode gamemode) {
		var data = new InvData(nerd, gamemode);

		line(2);
		send("legacyInventory: &7" + debugInventory.apply(data.getInventory(WorldGroup.LEGACY)));
		line();
		send("survivalInventory: " + debugInventory.apply(data.getInventory(WorldGroup.SURVIVAL)));
	}

	@Permission(Group.ADMIN)
	@Path("inventories view <player> <gamemode> <group>")
	@Description("View a list of items in a player's survival and legacy inventories")
	void inventories_view(Nerd nerd, GameMode gamemode, WorldGroup worldGroup) {
		new InventoryMenu(nerd, gamemode, worldGroup).open(player());
	}

	private enum InventoryType {
		INVENTORY,
		ENDER_CHEST,
	}

	@Permission(Group.ADMIN)
	@Path("inventories get <player> <gamemode> <group> <type>")
	@Description("Get the items in a player's survival and legacy inventories")
	void inventories_get(Nerd nerd, GameMode gamemode, WorldGroup worldGroup, InventoryType type) {
		final InvData data = new InvData(nerd, gamemode);

		switch (type) {
			case INVENTORY -> {
				player().getInventory().setContents(data.getInventory(worldGroup));
				player().getInventory().setArmorContents(data.getArmor(worldGroup));
				player().getInventory().setItemInOffHand(data.getOffHand(worldGroup));
			}
			case ENDER_CHEST ->
				player().getInventory().setContents(data.getEnderChest(worldGroup));
		}
	}

	@Data
	public static class InventoryMenu extends InventoryProvider {
		private final Nerd nerd;
		private final GameMode gamemode;
		private final WorldGroup worldGroup;

		@Override
		public String getTitle() {
			return StringUtils.camelCase(gamemode) + " inventory for " + nerd.getNickname();
		}

		@Override
		public void init() {
			List<ClickableItem> items = new ArrayList<>();

			for (ItemStack itemStack : new InvData(nerd, gamemode).getInventory(worldGroup))
				items.add(ClickableItem.empty(itemStack));

			paginator().items(items).perPage(5 * 9).startingRow(0).build();
		}
	}

}
