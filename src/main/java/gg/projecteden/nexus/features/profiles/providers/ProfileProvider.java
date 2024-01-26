package gg.projecteden.nexus.features.profiles.providers;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.commands.BankCommand;
import gg.projecteden.nexus.features.homes.providers.EditHomesProvider;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.mcmmo.McMMOCommand;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.commands.SocialMediaCommand;
import gg.projecteden.nexus.features.trust.providers.TrustPlayerProvider;
import gg.projecteden.nexus.features.trust.providers.TrustProvider;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.models.party.PartyManager;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmor;
import gg.projecteden.nexus.models.rainbowarmor.RainbowArmorService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.FontUtils.FontType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
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

// TODO: Customization
@Rows(6)
@SuppressWarnings({"deprecation", "unused"})
public class ProfileProvider extends InventoryProvider {
	private static final SocialMediaUserService socialMediaUserService = new SocialMediaUserService();
	private static final FriendsUserService friendService = new FriendsUserService();
	private static final CostumeUserService costumeService = new CostumeUserService();
	private static final RainbowArmorService rbaService = new RainbowArmorService();
	private static final HomeService homeService = new HomeService();
	private static final TrustService trustService = new TrustService();

	InventoryProvider previousMenu = null;
	private final Nerd target;
	private ItemStack armorHelmet = null;
	private ItemStack armorChestplate = null;
	private ItemStack armorLeggings = null;
	private ItemStack armorBoots = null;
	private ItemStack costumeHat = null;
	private ItemStack costumeHand = null;

	public ProfileProvider(OfflinePlayer offlinePlayer, @Nullable InventoryProvider previousMenu) {
		this(offlinePlayer);
		this.previousMenu = previousMenu;
	}

	public ProfileProvider(OfflinePlayer offlinePlayer) {
		this.target = Nerd.of(offlinePlayer);

		fillCostumes(target);
		fillArmor(target);
	}

	@AllArgsConstructor
	private enum SlotTexture {
		// @formatter:off
		ARMOR_HELMET(		"委", new SlotPos(1, 8)),
		ARMOR_CHESTPLATE(	"晕", new SlotPos(2, 8)),
		ARMOR_LEGGINGS(		"鸱", new SlotPos(3, 8)),
		ARMOR_BOOTS(		"粞", new SlotPos(4, 8)),
		COSTUME_HAT(		"疕", new SlotPos(1, 7)),
		COSTUME_HAND(		"楂", new SlotPos(2, 7)),
		;
		// @formatter:on

		final String texture;
		final SlotPos itemSlot;

		public String getMenuTexture(ProfileProvider provider) {
			ItemStack itemStack = getItem(provider);

			if (Nullables.isNotNullOrAir(itemStack))
				return "";

			return FontUtils.getNextMenuTexture(this.texture, 6);
		}

		public void setItem(ProfileProvider provider, InventoryContents contents) {
			ItemStack itemStack = getItem(provider);

			if (Nullables.isNullOrAir(itemStack))
				return;

			contents.set(this.itemSlot, ClickableItem.empty(itemStack.clone()));
		}

		private ItemStack getItem(ProfileProvider provider) {
			return switch (this) {
				case ARMOR_HELMET -> provider.armorHelmet;
				case ARMOR_CHESTPLATE -> provider.armorChestplate;
				case ARMOR_LEGGINGS -> provider.armorLeggings;
				case ARMOR_BOOTS -> provider.armorBoots;
				case COSTUME_HAT -> provider.costumeHat;
				case COSTUME_HAND -> provider.costumeHand;
			};
		}
	}

	@AllArgsConstructor
	private enum RankTexture {
		UNKNOWN("笞"),
		GUEST("砫"),
		MEMBER("鼫"),
		TRUSTED("廒"),
		ELITE("婪"),
		VETERAN("愆"),
		NOBLE("棽"),
		BUILDER("所"),
		ARCHITECT("砗"),
		MODERATOR("超"),
		OPERATOR("笸"),
		ADMIN("棘"),
		OWNER("爷"),
		;

		final String texture;

		public static String getMenuTexture(ProfileProvider provider) {
			String rankName = provider.target.getRank().name();
			RankTexture rankTexture = Arrays.stream(RankTexture.values())
				.filter(_rankTexture -> _rankTexture.name().equalsIgnoreCase(rankName))
				.findFirst()
				.orElse(RankTexture.UNKNOWN);

			return FontUtils.getNextMenuTexture(rankTexture.texture, 6);
		}
	}

	@Override
	public JsonBuilder getTitleComponent() {
		String titleName = "&f" + getProfileTitle(target.getNickname());
		StringBuilder texture = new StringBuilder(FontUtils.getMenuTexture("升", 6));

		for (SlotTexture slotTexture : SlotTexture.values())
			texture.append(slotTexture.getMenuTexture(this));

		texture.append(RankTexture.getMenuTexture(this));

		return new JsonBuilder(texture.toString()).group().next(titleName).font(FontType.PROFILE_TITLE).group();
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		for (ProfileProvider.ProfileMenuItem menuItem : ProfileProvider.ProfileMenuItem.values()) {
			menuItem.setClickableItem(viewer, target, contents, this);
			menuItem.setExtraClickableItems(viewer, target, contents, this);
		}

		for (SlotTexture slotTexture : SlotTexture.values()) {
			slotTexture.setItem(this, contents);
		}
	}

	@Getter
	@AllArgsConstructor
	enum ProfileMenuItem {
		PLAYER(2, 1, CustomMaterial.GUI_PROFILE_PLAYER_HEAD) {
			@Override
			public String getName(Player viewer, Nerd target) {
				String prefix = target.getFullPrefix(Chatter.of(target));
				final ChatColor rankColor = target.isKoda() ? Koda.getChatColor() : target.getRank().getChatColor();
				return prefix + rankColor + target.getNickname();
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				List<String> lines = new ArrayList<>();

				if (target.hasNickname())
					lines.add("&3Real name: &e" + target.getName());

				if (Nullables.isNotNullOrEmpty(target.getPronouns()))
					lines.add("&3Pronouns: " + target.getPronouns().stream().map(pronoun -> "&e" + pronoun + "&3").collect(Collectors.joining(", ")));

				if (PlayerUtils.canSee(viewer, target))
					lines.add("&3WorldGroup: &e" + StringUtils.camelCase(target.getWorldGroup()));

				return lines;
			}

			@Override
			public ItemBuilder getItemBuilder(Player viewer, Nerd target) {
				return super.getItemBuilder(viewer, target).skullOwner(target);
			}

			@Override
			public List<SlotPos> getExtraSlots(Player viewer, Nerd target) {
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
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, Nerd target) {
				return getInvisibleCopy(viewer, target);
			}
		},

		PRESENCE(1, 2, CustomMaterial.PRESENCE_OFFLINE) {
			@Override
			public String getName(Player viewer, Nerd target) {
				return "&e" + target.getPresence().getName();
			}

			@Override
			public int getModelId(Player viewer, Nerd target) {
				return Presence.of(target.getOfflinePlayer(), viewer).getModelId();
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				return getPresenceLore(viewer, target);
			}

			@NotNull
			private List<String> getPresenceLore(Player viewer, Nerd target) {
				List<String> lines = new ArrayList<>();

				// Current Time
				GeoIP geoip = new GeoIPService().get(target);
				if (GeoIP.exists(geoip))
					lines.add("&3Local Time: &e" + geoip.getCurrentTimeShort());

				// Last Seen / Online For
				if (target.isOnline() && PlayerUtils.canSee(viewer, target)) {
					LocalDateTime lastJoin = target.getLastJoin(viewer);
					lines.add("&3Online for: &e" + Timespan.of(lastJoin).format());
				} else {
					LocalDateTime lastQuit = target.getLastQuit(viewer);
					if (lastQuit != null)
						lines.add("&3Last seen: &e" + Timespan.of(lastQuit).format());
				}

				// First Join
				lines.add("&3First join: &e" + shortDateTimeFormat(target.getFirstJoin()));

				// Hours
				Hours hours = new HoursService().get(target);
				lines.add("&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format());
				return lines;
			}
		},

		WALLET(3, 4, CustomMaterial.GOLD_COINS_9) {
			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				return BankCommand.getLines(Nerd.of(viewer), target);
			}
		},

		LEVELS(3, 5, Material.EXPERIENCE_BOTTLE, 0) {
			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				List<String> lines = new ArrayList<>();

				if (target.getPlayer() != null)
					lines.add("&3Exp: &e" + target.getPlayer().getLevel());

				lines.add("&3McMMO:");
				for (PrimarySkillType skill : PrimarySkillType.values()) {
					lines.add("&3- " + StringUtils.camelCase(skill) + ": &e" + McMMOCommand.getSkillLevel(target, skill));
				}

				return lines;
			}
		},

		VIEW_SOCIAL_MEDIA(3, 6, CustomMaterial.GUI_PROFILE_ICON_SOCIAL_MEDIA) {
			@Override
			public boolean shouldShow(Player viewer, Nerd target) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() != 0;
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				SocialMediaCommand.open(viewer, target.getOfflinePlayer(), "/profile " + target.getNickname());
			}
		},

		LINK_SOCIAL_MEDIA(3, 6, CustomMaterial.GUI_PROFILE_ICON_SOCIAL_MEDIA_LINK) {
			@Override
			public boolean shouldShow(Player viewer, Nerd target) {
				if (!isSelf(viewer, target))
					return false;

				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() == 0;
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				viewer.closeInventory();
				PlayerUtils.runCommand(viewer, "socialmedia help");
			}
		},

		MODIFY_FRIEND(4, 1, CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_ADD) {
			@Override
			public boolean shouldShow(Player viewer, Nerd target) {
				return !isSelf(viewer, target);
			}

			@Override
			public String getName(Player viewer, Nerd target) {
				if (hasSentRequest(viewer, target))
					return "&eRequest Sent";

				if (isFriendsWith(friendService.get(target), viewer))
					return "&eRemove Friend";

				return "&eSend Friend Request";
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				if (hasSentRequest(viewer, target))
					return List.of("&eClick &3to cancel friend request");

				if (isFriendsWith(friendService.get(target), viewer))
					return List.of("&eClick &3to remove as friend");

				return List.of("&eClick &3to send a friend request");
			}

			@Override
			public int getModelId(Player viewer, Nerd target) {
				if (isFriendsWith(friendService.get(target), viewer))
					return CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_REMOVE.getModelId();

				return CustomMaterial.GUI_PROFILE_ICON_FRIEND_MODIFY_ADD.getModelId();
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				FriendsUser userFriend = friendService.get(viewer);
				FriendsUser targetFriend = friendService.get(target);

				if (hasSentRequest(viewer, target)) {
					userFriend.cancelSent(targetFriend);
					reopenMenu(viewer, target, previousMenu);
					return;
				}

				if (isFriendsWith(friendService.get(target), viewer)) {
					userFriend.removeFriend(targetFriend);
				} else {
					userFriend.sendRequest(targetFriend);
				}

				reopenMenu(viewer, target, previousMenu);
			}

			private boolean hasSentRequest(Player viewer, Nerd target) {
				return friendService.get(viewer).getRequests_sent().contains(target.getUniqueId());
			}

			private boolean isFriendsWith(FriendsUser friend, Player viewer) {
				return friend.isFriendsWith(friendService.get(viewer));
			}
		},

		VIEW_FRIENDS(4, 2, CustomMaterial.GUI_PROFILE_ICON_FRIENDS) {
			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				if (hasNoFriends(target))
					return List.of("&c" + target.getNickname() + " has no friends ):");

				return super.getLore(viewer, target);
			}

			@Override
			public int getModelId(Player viewer, Nerd target) {
				if (hasNoFriends(target))
					return CustomMaterial.GUI_PROFILE_ICON_FRIENDS_ERROR.getModelId();

				return super.getModelId(viewer, target);
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				if (hasNoFriends(target))
					return;

				new FriendsProvider(target.getOfflinePlayer(), viewer, previousMenu).open(viewer);
			}

			private boolean hasNoFriends(Nerd target) {
				return friendService.get(target).getFriends().size() == 0;
			}
		},

		PARTY(4, 3, CustomMaterial.GUI_PROFILE_ICON_PARTY) {
			@Override
			public boolean shouldShow(Player viewer, Nerd target) {
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (isSelf(viewer, target))
					return partyViewer != null;

				if (partyViewer != null) {
					if (partyTarget != null) {
						return isSameParty;
					}

					return isViewerPartyOwner;
				}

				return true;
			}

			@Override
			public int getModelId(Player viewer, Nerd target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return CustomMaterial.GUI_PROFILE_ICON_PARTY_MODIFY_ADD.getModelId();

						if (partyViewer == null)
							return CustomMaterial.GUI_PROFILE_ICON_PARTY_MODIFY_ADD.getModelId();
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return CustomMaterial.GUI_PROFILE_ICON_PARTY_MODIFY_REMOVE.getModelId();
					}
				}

				return CustomMaterial.GUI_PROFILE_ICON_PARTY.getModelId();
			}

			@Override
			public String getName(Player viewer, Nerd target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return "&eInvite to Party";

						if (partyViewer == null)
							return "&eInvite to Party";
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return "&eRemove from Party";
					}
				}

				return "&eView Party";
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner)
							return List.of("&3Invite &e" + target.getNickname() + "&3 to your party");

						if (partyViewer == null)
							return List.of("&3Create a party and Invite &e" + target.getNickname());
					}

					if (isSameParty) {
						if (isViewerPartyOwner)
							return List.of("&3Remove &e" + target.getNickname() + "&3 from your party");
					}
				}

				return super.getLore(viewer, target);
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				boolean isSelf = isSelf(viewer, target);
				Party partyTarget = getParty(target);
				Party partyViewer = getParty(viewer);
				boolean isSameParty = isInSameParty(partyTarget, viewer, target);
				boolean isViewerPartyOwner = isPartyOwner(partyViewer, viewer);

				if (!isSelf) {
					if (partyTarget == null) {
						if (partyViewer != null && isViewerPartyOwner) {
							PlayerUtils.runCommand(viewer, "party invite " + target.getName());
							return;
						}

						if (partyViewer == null) {
							PlayerUtils.runCommand(viewer, "party invite " + target.getName());
							return;
						}
					}

					if (isSameParty) {
						if (isViewerPartyOwner) {
							PlayerUtils.runCommand(viewer, "party kick " + target.getName());
							return;
						}
					}
				}

				new PartyProvider(viewer, partyViewer, previousMenu).open(viewer);
			}

			//

			private Party getParty(Nerd nerd) {
				return PartyManager.of(nerd.getOfflinePlayer());
			}

			private Party getParty(Player player) {
				return PartyManager.of(player);
			}

			private boolean isInSameParty(Party party, Player viewer, Nerd target) {
				if (party == null)
					return false;

				return party.getAllMembers().contains(viewer) && party.getAllMembers().contains(target.getOfflinePlayer());
			}

			private boolean isPartyOwner(Party party, OfflinePlayer player) {
				if (party == null)
					return false;

				return party.getOwner().equals(player.getUniqueId());
			}
		},

		TELEPORT(4, 6, Material.ENDER_PEARL, 0) {
			@Override
			public boolean shouldShow(Player viewer, Nerd target) {
				if (ProfileMenuItem.isSelf(viewer, target))
					return false;

				return super.shouldShow(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				if (Rank.of(viewer).isStaff())
					return List.of("&eClick &3to teleport to &e" + target.getNickname());

				return List.of("&eClick &3to Send a teleport request to &e" + target.getNickname());
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				PlayerUtils.runCommand(viewer, "tp " + target.getName());
			}
		},

		VIEW_SHOP(4, 4, Material.EMERALD, 0) {
			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				Shop.ShopGroup shopGroup = Shop.ShopGroup.of(viewer.getWorld());
				if (shopGroup == null)
					return List.of("&cShops are not enabled in this world");

				Shop shop = new ShopService().get(target);
				if (shop.getProducts(shopGroup).isEmpty())
					return List.of("&c" + target.getNickname() + " has no products in their shop");

				return super.getLore(viewer, target);
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				Shop.ShopGroup shopGroup = Shop.ShopGroup.of(viewer.getWorld());
				if (shopGroup == null)
					return;

				Shop shop = new ShopService().get(target);
				if (shop.getProducts(shopGroup).isEmpty())
					return;

				// TODO: Allow viewer to go "back" using-- new PlayerShopProvider(null, shop).open(player());
				PlayerUtils.runCommand(viewer, "shop " + target.getName());
			}
		},

		VIEW_HOMES(4, 0, CustomMaterial.GUI_PROFILE_ICON_HOMES) {
			@Override
			public String getName(Player viewer, Nerd target) {
				if (ProfileMenuItem.isSelf(viewer, target))
					return "&eEdit Homes";

				return super.getName(viewer, target);
			}

			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				if (ProfileMenuItem.isSelf(viewer, target))
					super.getLore(viewer, target);

				HomeOwner targetOwner = homeService.get(target);
				if (targetOwner.getHomes().size() == 0)
					return List.of("&c" + target.getNickname() + " has no homes set");

				if (AccessibleHomesProvider.getAccessibleHomes(viewer, targetOwner).size() == 0)
					return List.of("&cYou don't have access to any of " + target.getNickname() + " homes");

				return super.getLore(viewer, target);
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				HomeOwner targetOwner = homeService.get(target);

				if (ProfileMenuItem.isSelf(viewer, target)) {
					new EditHomesProvider(targetOwner, previousMenu).open(viewer);
					return;
				}

				if (targetOwner.getHomes().size() == 0)
					return;

				if (AccessibleHomesProvider.getAccessibleHomes(viewer, targetOwner).size() == 0)
					return;

				new AccessibleHomesProvider(targetOwner, previousMenu).open(viewer);
			}
		},

		EDIT_TRUSTS(4, 5, CustomMaterial.GUI_PROFILE_ICON_TRUSTS) {
			@Override
			public List<String> getLore(Player viewer, Nerd target) {
				if (isSelf(viewer, target))
					return super.getLore(viewer, target);

				return List.of("&3Change &e" + target.getNickname() + "&3's trust permissions");
			}

			@Override
			public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
				if (isSelf(viewer, target)) {
					new TrustProvider(previousMenu).open(viewer);
					return;
				}

				new TrustPlayerProvider(target.getOfflinePlayer(), previousMenu).open(viewer);
			}
		},
		;

		private static boolean isSelf(Player viewer, Nerd target) {
			return PlayerUtils.isSelf(viewer, target);
		}

		private final int row, col;
		private final Material material;
		private final int modelId;

		ProfileMenuItem(int row, int col, CustomMaterial customMaterial) {
			this(row, col, customMaterial.getMaterial(), customMaterial.getModelId());
		}

		public int getCol(Player viewer, Nerd target) {
			return col;
		}

		public int getRow(Player viewer, Nerd target) {
			return row;
		}

		public boolean shouldShow(Player viewer, Nerd target) {
			return true;
		}

		public void onClick(ItemClickData e, Player viewer, Nerd target, InventoryProvider previousMenu) {
		}

		public SlotPos getSlotPos(Player viewer, Nerd target) {
			return SlotPos.of(getRow(viewer, target), getCol(viewer, target));
		}

		public String getName(Player viewer, Nerd target) {
			return "&e" + StringUtils.camelCase(this);
		}

		public int getModelId(Player viewer, Nerd target) {
			return modelId;
		}

		public List<String> getLore(Player viewer, Nerd target) {
			return Collections.emptyList();
		}

		public ItemBuilder getItemBuilder(Player viewer, Nerd target) {
			int modelId = getModelId(viewer, target);
			if (modelId == -1)
				return null;

			return new ItemBuilder(getMaterial())
				.modelId(getModelId(viewer, target))
				.name(getName(viewer, target))
				.lore(getLore(viewer, target))
				.clone();
		}

		public List<SlotPos> getExtraSlots(Player viewer, Nerd target) {
			return Collections.emptyList();
		}

		public ItemBuilder getExtraSlotItemBuilder(Player viewer, Nerd target) {
			return getItemBuilder(viewer, target).clone();
		}

		public ItemBuilder getInvisibleCopy(Player viewer, Nerd target) {
			return getItemBuilder(viewer, target).clone().material(CustomMaterial.INVISIBLE);
		}

		public void setClickableItem(Player player, Nerd target, InventoryContents contents, InventoryProvider previousMenu) {
			ClickableItem clickableItem = getClickableItem(player, target, previousMenu);
			if (clickableItem == null)
				return;

			contents.set(getSlotPos(player, target), clickableItem);
		}

		public void setExtraClickableItems(Player viewer, Nerd target, InventoryContents contents, InventoryProvider previousMenu) {
			for (SlotPos slotPos : getExtraSlots(viewer, target)) {
				ClickableItem clickableItem = getClickableItem(viewer, target, getExtraSlotItemBuilder(viewer, target), previousMenu);
				if (clickableItem == null)
					continue;

				contents.set(slotPos, clickableItem);
			}
		}

		public ClickableItem getClickableItem(Player viewer, Nerd target, InventoryProvider previousMenu) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target), previousMenu);
		}

		public ClickableItem getClickableItem(Player viewer, Nerd target, ItemBuilder itemBuilder, InventoryProvider previousMenu) {
			if (Nullables.isNullOrAir(itemBuilder) || !shouldShow(viewer, target))
				return null;

			return ClickableItem.of(itemBuilder, e -> onClick(e, viewer, target, previousMenu));
		}

		public void refresh(Player viewer, Nerd target, InventoryContents contents, InventoryProvider previousMenu) {
			this.setClickableItem(viewer, target, contents, previousMenu);
			this.setExtraClickableItems(viewer, target, contents, previousMenu);
		}
	}

	//

	private void fillCostumes(Nerd target) {
		CostumeUser costumeUser = costumeService.get(target);
		RainbowArmor rainbowArmor = rbaService.get(target);

		this.costumeHat = getCostumeItem(Costume.CostumeType.HAT, costumeUser, rainbowArmor);
		this.costumeHand = getCostumeItem(Costume.CostumeType.HAND, costumeUser, rainbowArmor);
	}

	private void fillArmor(Nerd target) {
		Player player = target.getPlayer();
		if (player != null) {
			PlayerInventory inventory = player.getInventory();
			this.armorHelmet = getHelmet(inventory.getHelmet());
			this.armorChestplate = inventory.getChestplate();
			this.armorLeggings = inventory.getLeggings();
			this.armorBoots = inventory.getBoots();
		}
	}

	public static String getProfileTitle(String nickname) {
		return nickname.toLowerCase().chars()
			.mapToObj(c -> (char) c + "ꈃ")
			.collect(Collectors.joining());
	}

	// Costume hat can sometimes also be actual helmet, which we want to prevent showing twice
	private ItemStack getHelmet(ItemStack helmet) {

		if (Nullables.isNullOrAir(helmet))
			return helmet;

		if (Nullables.isNullOrAir(this.costumeHat))
			return helmet;

		if (helmet.getType() != this.costumeHat.getType())
			return helmet;

		int helmetModelId = ItemBuilder.ModelId.of(helmet);
		if (helmetModelId == 0)
			return helmet;

		if (helmetModelId != ItemBuilder.ModelId.of(this.costumeHat))
			return helmet;

		return null;
	}

	private ItemStack getCostumeItem(Costume.CostumeType type, CostumeUser user, RainbowArmor rainbowArmor) {
		Costume costume = user.getActiveCostume(type);
		if (costume == null)
			return null;

		ItemStack item = costume.getItem();
		if (Nullables.isNullOrAir(item))
			return null;

		boolean isRainbowEnabled = false;

		if (type == Costume.CostumeType.HAT)
			isRainbowEnabled = rainbowArmor.isSlotEnabled(PlayerUtils.ArmorSlot.HELMET);

		ItemBuilder _item = new ItemBuilder(costume.getModel().getDisplayItem().clone());
		if (costume.isDyeable()) {
			if (isRainbowEnabled)
				_item.dyeColor(Color.RED);
			else
				_item.dyeColor(user.getColors().get(costume.getId()));
		}

		return _item.build();
	}

	private static void reopenMenu(Player viewer, Nerd target, InventoryProvider previousMenu) {
		new ProfileProvider(target.getOfflinePlayer(), previousMenu).open(viewer);
	}

}
