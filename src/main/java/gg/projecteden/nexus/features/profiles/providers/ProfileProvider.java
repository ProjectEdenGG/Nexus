package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.commands.BankCommand;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.commands.SocialMediaCommand;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmor;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.FontUtils.FontType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;

// TODO:
//	- /PARTY FRIENDS -> INVITE ALL FRIENDS TO PARTY
//	- FRIEND REQUEST REFRESH (IF OPENED PROFILE = REQUESTED PLAYER):
//		- ON REQUEST: UPDATE ITEM TO X BUTTON, TO CANCEL REQUEST
//		- ON ACCEPT: UPDATE MENU -> BUTTON WILL CHANGE TO - BUTTON
//		- ON REMOVE: UPDATE MENU -> BUTTON WILL CHANGE TO + BUTTON
@Rows(6)
public class ProfileProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	private static final SocialMediaUserService socialMediaUserService = new SocialMediaUserService();
	private static final FriendsUserService friendService = new FriendsUserService();
	private static final CostumeUserService costumeService = new CostumeUserService();
	private static final RainbowArmorService rbaService = new RainbowArmorService();

	private final OfflinePlayer target;
	private ItemStack helmet = null;
	private ItemStack chestplate = null;
	private ItemStack leggings = null;
	private ItemStack boots = null;
	private ItemStack cosmeticHat = null;
	private ItemStack cosmeticHand = null;

	public ProfileProvider(OfflinePlayer target, @Nullable InventoryProvider previousMenu) {
		this(target);
		this.previousMenu = previousMenu;
	}

	public ProfileProvider(OfflinePlayer target) {
		this.target = target;

		fillCostumes(target);
		fillArmor(target);
	}

	private void fillCostumes(OfflinePlayer target) {
		CostumeUser costumeUser = costumeService.get(target);
		RainbowArmor rainbowArmor = rbaService.get(target);

		this.cosmeticHat = getCosmetic(Costume.CostumeType.HAT, costumeUser, rainbowArmor);
		this.cosmeticHand = getCosmetic(Costume.CostumeType.HAND, costumeUser, rainbowArmor);
	}

	private void fillArmor(OfflinePlayer target) {
		if (target.isOnline() && target.getPlayer() != null) {
			PlayerInventory inventory = target.getPlayer().getInventory();
			this.helmet = getHelmet(inventory.getHelmet());
			this.chestplate = inventory.getChestplate();
			this.leggings = inventory.getLeggings();
			this.boots = inventory.getBoots();
		}
	}

	private String properSpacing(String title) {
		return title.toLowerCase().chars()
			.mapToObj(c -> (char) c + "ꈃ")
			.collect(Collectors.joining());
	}

	@Override
	public JsonBuilder getTitleComponent() {
		int rows = 6;
		String texture = FontUtils.getMenuTexture("砗", rows);

		// @formatter:off
		if(!hasHelmet()) 			texture += FontUtils.getNextMenuTexture("委", rows);
		if(!hasChestplate()) 		texture += FontUtils.getNextMenuTexture("晕", rows);
		if(!hasLeggings()) 			texture += FontUtils.getNextMenuTexture("鸱", rows);
		if(!hasBoots()) 			texture += FontUtils.getNextMenuTexture("粞", rows);
		if(!hasCosmeticHat())		texture += FontUtils.getNextMenuTexture("疕", rows);
		if(!hasCosmeticHand())		texture += FontUtils.getNextMenuTexture("楂", rows);
		// @formatter:on

		String title = "&f" + properSpacing(ProfileMenuItem.nickname(target));
		return new JsonBuilder(texture).group().next(title).font(FontType.PROFILE_TITLE).group();
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		for (ProfileProvider.ProfileMenuItem menuItem : ProfileProvider.ProfileMenuItem.values()) {
			menuItem.setClickableItem(viewer, target, contents, this);
			menuItem.setExtraClickableItems(viewer, target, contents, this);
		}

		// @formatter:off
		if (hasHelmet()) 			contents.set(new SlotPos(1, 8), ClickableItem.empty(this.helmet.clone()));
		if (hasChestplate()) 		contents.set(new SlotPos(2, 8), ClickableItem.empty(this.chestplate.clone()));
		if (hasLeggings()) 			contents.set(new SlotPos(3, 8), ClickableItem.empty(this.leggings.clone()));
		if (hasBoots()) 			contents.set(new SlotPos(4, 8), ClickableItem.empty(this.boots.clone()));
		if (hasCosmeticHat())		contents.set(new SlotPos(1, 7), ClickableItem.empty(this.cosmeticHat.clone()));
		if (hasCosmeticHand())		contents.set(new SlotPos(2, 7), ClickableItem.empty(this.cosmeticHand.clone()));
		// @formatter:on
	}

	@Getter
	@AllArgsConstructor
	enum ProfileMenuItem {
		// TODO?
		RANK(1, 3, CustomMaterial.GUI_PROFILE_RANK_UNKNOWN) { // TODO: Make this a font thing instead, so it doesn't have a hover?
			private static final List<CustomMaterial> ranks = List.of(
				CustomMaterial.GUI_PROFILE_RANK_GUEST, CustomMaterial.GUI_PROFILE_RANK_MEMBER,
				CustomMaterial.GUI_PROFILE_RANK_TRUSTED, CustomMaterial.GUI_PROFILE_RANK_ELITE,
				CustomMaterial.GUI_PROFILE_RANK_VETERAN, CustomMaterial.GUI_PROFILE_RANK_NOBLE,
				CustomMaterial.GUI_PROFILE_RANK_BUILDER, CustomMaterial.GUI_PROFILE_RANK_ARCHITECT,
				CustomMaterial.GUI_PROFILE_RANK_MODERATOR, CustomMaterial.GUI_PROFILE_RANK_OPERATOR,
				CustomMaterial.GUI_PROFILE_RANK_ADMIN, CustomMaterial.GUI_PROFILE_RANK_OWNER);

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				return getRankMaterial(target).getModelId();
			}

			private CustomMaterial getRankMaterial(OfflinePlayer player) {
				String playerRankName = Rank.of(player).name();

				for (CustomMaterial _rankMaterial : ranks) {
					String[] split = _rankMaterial.name().split("_");
					String rankName = split[split.length - 1];

					if (playerRankName.equalsIgnoreCase(rankName))
						return _rankMaterial;
				}

				return CustomMaterial.GUI_PROFILE_RANK_UNKNOWN;
			}

		},

		PLAYER(2, 1, CustomMaterial.GUI_PROFILE_PLAYER_HEAD) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return getPresenceLore(viewer, target);
			}

			@Override
			public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
				return super.getItemBuilder(viewer, target).skullOwner(target);
			}

			@Override
			public List<SlotPos> getExtraSlots(Player viewer, OfflinePlayer target) {
				int row = getRow();
				int row0 = row - 1;
				int row1 = row + 1;
				int col = getCol();
				int col0 = col - 1;
				int col1 = col + 1;

				return List.of(
					SlotPos.of(row0, col0), SlotPos.of(row0, col), /* 		Presence		*/
					SlotPos.of(row, col0), /* 		Player Head		*/ SlotPos.of(row, col1),
					SlotPos.of(row1, col0), SlotPos.of(row1, col), SlotPos.of(row1, col1)
				);
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getInvisibleCopy(viewer, target);
			}
		},

		PRESENCE(1, 2, CustomMaterial.PRESENCE_OFFLINE) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				return Presence.of(target, viewer).getModelId();
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return getPresenceLore(viewer, target);
			}
		},

		WALLET(3, 4, CustomMaterial.GOLD_COINS_9) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return BankCommand.getLines(Nerd.of(viewer), Nerd.of(target));
			}
		},

		// TODO
		LEVELS(3, 5, Material.EXPERIENCE_BOTTLE, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}
		},

		// TODO
		VIEW_SOCIAL_MEDIA(3, 6, Material.BOOK, 0) {  // TODO: change item

			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() != 0;
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				SocialMediaCommand.open(viewer, target, "/profile v2 " + nickname(target));
			}
		},

		// TODO
		LINK_SOCIAL_MEDIA(3, 6, Material.BOOK, 0) {  // TODO: change item

			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (!isSelf(viewer, target))
					return false;

				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() == 0;
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				viewer.closeInventory();
				PlayerUtils.runCommand(viewer, "socialmedia help"); // TODO: make gui if possible
			}
		},

		MODIFY_FRIEND(4, 1, CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_ADD) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				return !isSelf(viewer, target);
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				if (isFriendsWith(friendService.get(target), viewer))
					return "&cRemove Friend";

				return "&aSend Friend Request";
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				if (isFriendsWith(friendService.get(target), viewer))
					return List.of("&eClick &3to remove &e" + nickname(target));

				if (hasSentRequest(viewer, target))
					return List.of("&3Friend request &ealready sent&3!");

				return List.of("&eClick &3to send request to &e" + nickname(target));
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				if (isFriendsWith(friendService.get(target), viewer))
					return CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_REMOVE.getModelId();

				return CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_ADD.getModelId();
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				if (hasSentRequest(viewer, target))
					return;

				FriendsUser userFriend = friendService.get(viewer);
				FriendsUser targetFriend = friendService.get(target);
				if (isFriendsWith(friendService.get(target), viewer))
					userFriend.removeFriend(targetFriend);
				else {
					if (userFriend.getRequests_sent().contains(targetFriend.getUuid()))
						return;

					userFriend.sendRequest(targetFriend);
				}
			}

			private boolean hasSentRequest(Player viewer, OfflinePlayer target) {
				return friendService.get(viewer).getRequests_sent().contains(target.getUniqueId());
			}
		},

		VIEW_FRIENDS(4, 2, CustomMaterial.GUI_PROFILE_ICON_FRIENDS) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				if (hasNoFriends(target))
					return List.of(Nickname.of(target) + " has no friends ):");

				return super.getLore(viewer, target);
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				if (hasNoFriends(target))
					return CustomMaterial.GUI_PROFILE_ICON_FRIENDS_ERROR.getModelId();

				return super.getModelId(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				if (hasNoFriends(target))
					return;

				new FriendsProvider(target, viewer, previousMenu).open(viewer);
			}

			private boolean hasNoFriends(OfflinePlayer target) {
				return friendService.get(target).getFriends().size() == 0;
			}
		},

		// TODO
		PARTY(4, 3, CustomMaterial.GUI_PROFILE_ICON_PARTY) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (getParty(target) == null) {
					if (!isPartyOwner(getParty(viewer), viewer))
						return false;
				}

				return super.shouldShow(viewer, target);
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return CustomMaterial.GUI_PROFILE_ICON_PARTY_MODIFY_ADD.getModelId();
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return CustomMaterial.GUI_PROFILE_ICON_PARTY_MODIFY_REMOVE.getModelId();
					}
				}

				return super.getModelId(viewer, target);
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return "&aInvite to Party";
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return "&cRemove from Party";
					}
				}

				return "&eView Party";
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return List.of("&3Invite &e" + nickname(target) + "&3 to your party");
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return List.of("&3Remove &e" + nickname(target) + "&3 from your party");
					}
				}

				return super.getLore(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							super.onClick(viewer, target, previousMenu); // TODO - INVITE TO PARTY
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							super.onClick(viewer, target, previousMenu); // TODO - REMOVE FROM PARTY
					}
				}

				super.onClick(viewer, target, previousMenu); // TODO - VIEW PARTY
			}

			//

			private Party getParty(OfflinePlayer player) {
				return PartyManager.of(player);
			}

			private boolean isInSameParty(Party party, Player viewer, OfflinePlayer target) {
				if (party == null)
					return false;

				return party.getAllMembers().contains(viewer) && party.getAllMembers().contains(target);
			}

			private boolean isPartyOwner(Party party, OfflinePlayer player) {
				if (party == null)
					return false;

				return party.getOwner() == player.getUniqueId();
			}
		},

		TELEPORT(4, 6, Material.ENDER_PEARL, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (ProfileMenuItem.isSelf(viewer, target))
					return false;

				return super.shouldShow(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				if (Rank.of(viewer).isStaff())
					return List.of("&eClick &3to teleport to &e" + nickname(target));

				return List.of("&eClick &3to Send a teleport request to &e" + nickname(target));
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				PlayerUtils.runCommand(viewer, "tp " + target.getName());
			}
		},

		VIEW_SHOP(4, 4, Material.EMERALD, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				Shop.ShopGroup shopGroup = Shop.ShopGroup.of(viewer.getWorld());
				if (shopGroup == null)
					return false;

				Shop shop = new ShopService().get(target);
				if (shop.getProducts(shopGroup).isEmpty())
					return false;

				return super.shouldShow(viewer, target);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				PlayerUtils.runCommand(viewer, "shop " + target.getName());
			}
		},

		// TODO
		VIEW_HOMES(4, 0, CustomMaterial.GUI_PROFILE_ICON_HOMES) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (ProfileMenuItem.isSelf(viewer, target))
					return true;

				return super.shouldShow(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO: Make GUI to view available homes to tp to, check trusts
			}
		},

		// TODO
		EDIT_TRUSTS(4, 5, Material.KNOWLEDGE_BOOK, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				return !isSelf(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},
		;

		private static boolean isSelf(Player viewer, OfflinePlayer target) {
			return PlayerUtils.isSelf(viewer, target);
		}

		private static boolean isFriendsWith(FriendsUser friend, Player viewer) {
			return friend.isFriendsWith(friendService.get(viewer));
		}

		@NotNull
		private static List<String> getPresenceLore(Player viewer, OfflinePlayer target) {
			Nerd targetNerd = Nerd.of(target);

			List<String> lines = new ArrayList<>();

			// Current Time
			GeoIP geoip = new GeoIPService().get(target);
			if (GeoIP.exists(geoip))
				lines.add("&3Local Time: &e" + geoip.getCurrentTimeShort());

			// Last Seen / Online For
			if (target.isOnline() && PlayerUtils.canSee(viewer, target)) {
				LocalDateTime lastJoin = targetNerd.getLastJoin(viewer);
				lines.add("&3Online for: &e" + Timespan.of(lastJoin).format());
			} else {
				LocalDateTime lastQuit = targetNerd.getLastQuit(viewer);
				if (lastQuit != null)
					lines.add("&3Last seen: &e" + Timespan.of(lastQuit).format());
			}

			// First Join
			lines.add("&3First join: &e" + shortDateTimeFormat(targetNerd.getFirstJoin()));

			// Hours
			Hours hours = new HoursService().get(target);
			lines.add("&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format());
			return lines;
		}

		private final int row, col;
		private final Material material;
		private final int modelId;

		private static final String TODO = "&cTODO";

		ProfileMenuItem(int row, int col, CustomMaterial customMaterial) {
			this(row, col, customMaterial.getMaterial(), customMaterial.getModelId());
		}

		public int getCol(Player viewer, OfflinePlayer target) {
			return col;
		}

		public int getRow(Player viewer, OfflinePlayer target) {
			return row;
		}

		public boolean shouldShow(Player viewer, OfflinePlayer target) {
			return true;
		}

		public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
		}

		public SlotPos getSlotPos(Player viewer, OfflinePlayer target) {
			return SlotPos.of(getRow(viewer, target), getCol(viewer, target));
		}

		public String getName(Player viewer, OfflinePlayer target) {
			return "&e" + StringUtils.camelCase(this);
		}

		public int getModelId(Player viewer, OfflinePlayer target) {
			return modelId;
		}

		public List<String> getLore(Player viewer, OfflinePlayer target) {
			return Collections.emptyList();
		}

		public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
			int modelId = getModelId(viewer, target);
			if (modelId == -1)
				return null;

			return new ItemBuilder(getMaterial())
				.modelId(getModelId(viewer, target))
				.name(getName(viewer, target))
				.lore(getLore(viewer, target))
				.clone();
		}

		public List<SlotPos> getExtraSlots(Player viewer, OfflinePlayer target) {
			return Collections.emptyList();
		}

		public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone();
		}

		public ItemBuilder getInvisibleCopy(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone().material(CustomMaterial.INVISIBLE);
		}

		public void setClickableItem(Player player, OfflinePlayer target, InventoryContents contents, InventoryProvider previousMenu) {
			ClickableItem clickableItem = getClickableItem(player, target, previousMenu);
			if (clickableItem == null)
				return;

			contents.set(getSlotPos(player, target), clickableItem);
		}

		public void setExtraClickableItems(Player viewer, OfflinePlayer target, InventoryContents contents, InventoryProvider previousMenu) {
			for (SlotPos slotPos : getExtraSlots(viewer, target)) {
				ClickableItem clickableItem = getClickableItem(viewer, target, getExtraSlotItemBuilder(viewer, target), previousMenu);
				if (clickableItem == null)
					continue;

				contents.set(slotPos, clickableItem);
			}
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target), previousMenu);
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, ItemBuilder itemBuilder, InventoryProvider previousMenu) {
			if (Nullables.isNullOrAir(itemBuilder) || !shouldShow(viewer, target))
				return null;

			return ClickableItem.of(itemBuilder, e -> onClick(viewer, target, previousMenu));
		}

		public static String nickname(OfflinePlayer player) {
			return Nickname.of(player);
		}
	}

	// Costume hat can sometimes also be actual helmet, which we want to prevent showing twice
	private ItemStack getHelmet(ItemStack helmet) {

		if (Nullables.isNullOrAir(helmet))
			return helmet;

		if (!hasCosmeticHat())
			return helmet;

		if (helmet.getType() != this.cosmeticHat.getType())
			return helmet;

		int helmetModelId = ItemBuilder.ModelId.of(helmet);
		if (helmetModelId == 0)
			return helmet;

		if (helmetModelId != ItemBuilder.ModelId.of(this.cosmeticHat))
			return helmet;

		return null;
	}

	private ItemStack getCosmetic(Costume.CostumeType type, CostumeUser user, RainbowArmor rainbowArmor) {
		Costume costume = user.getActiveCostume(type);
		if (costume == null || Nullables.isNullOrAir(costume.getItem()))
			return null;

		boolean isRainbowEnabled = false;

		if (type == Costume.CostumeType.HAT)
			isRainbowEnabled = rainbowArmor.isSlotEnabled(PlayerUtils.ArmorSlot.HELMET);

		ItemBuilder _item = new ItemBuilder(costume.getModel().getDisplayItem().clone());
		if (_item.isDyeable() && isRainbowEnabled)
			_item.dyeColor(Color.RED);

		return _item.build();
	}

	private boolean hasHelmet() {
		return Nullables.isNotNullOrAir(this.helmet);
	}

	private boolean hasChestplate() {
		return Nullables.isNotNullOrAir(this.chestplate);
	}

	private boolean hasLeggings() {
		return Nullables.isNotNullOrAir(this.leggings);
	}

	private boolean hasBoots() {
		return Nullables.isNotNullOrAir(this.boots);
	}

	private boolean hasCosmeticHat() {
		return Nullables.isNotNullOrAir(this.cosmeticHat);
	}

	private boolean hasCosmeticHand() {
		return Nullables.isNotNullOrAir(this.cosmeticHand);
	}

}
