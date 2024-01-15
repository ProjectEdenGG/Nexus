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
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.FontUtils.FontType;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;

@Rows(6)
public class ProfileProviderV2 extends InventoryProvider {
	InventoryProvider previousMenu = null;
	private static final SocialMediaUserService socialMediaUserService = new SocialMediaUserService();
	private static final FriendsUserService friendService = new FriendsUserService();
	private final OfflinePlayer target;

	public ProfileProviderV2(OfflinePlayer target, @Nullable InventoryProvider previousMenu) {
		this(target);
		this.previousMenu = previousMenu;
	}

	public ProfileProviderV2(OfflinePlayer target) {
		this.target = target;
	}

	private String titleName(String title) {
		StringBuilder result = new StringBuilder();
		for (char c : title.toLowerCase().toCharArray()) {
			result.append(c).append("ꈃ");
		}

		return result.toString();
	}

	@Override
	public JsonBuilder getTitleComponent() {
		String texture = FontUtils.getMenuTexture("升", 6);
		String title = Rank.of(target).getChatColor() + titleName(ProfileMenuItem.nickname(target));
		return new JsonBuilder(texture).group().next(title).font(FontType.PROFILE_TITLE).group();
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		for (ProfileProviderV2.ProfileMenuItem menuItem : ProfileProviderV2.ProfileMenuItem.values()) {
			menuItem.setClickableItem(viewer, target, contents, this);
			menuItem.setExtraClickableItems(viewer, target, contents, this);
		}

		// Armor
		if (target.isOnline() && target.getPlayer() != null) {
			PlayerInventory inventory = target.getPlayer().getInventory();
			ItemStack helmet = inventory.getHelmet();
			ItemStack chestplate = inventory.getChestplate();
			ItemStack leggings = inventory.getLeggings();
			ItemStack boots = inventory.getBoots();

			if (Nullables.isNotNullOrAir(helmet)) contents.set(new SlotPos(1, 8), ClickableItem.empty(helmet.clone()));
			if (Nullables.isNotNullOrAir(chestplate))
				contents.set(new SlotPos(2, 8), ClickableItem.empty(chestplate.clone()));
			if (Nullables.isNotNullOrAir(leggings))
				contents.set(new SlotPos(3, 8), ClickableItem.empty(leggings.clone()));
			if (Nullables.isNotNullOrAir(boots)) contents.set(new SlotPos(4, 8), ClickableItem.empty(boots.clone()));
		}
	}

	@Getter
	@AllArgsConstructor
	enum ProfileMenuItem {
		RANK(1, 3, CustomMaterial.GUI_PROFILE_V2_RANK_UNKNOWN) {
			private static final List<CustomMaterial> ranks = List.of(
				CustomMaterial.GUI_PROFILE_V2_RANK_GUEST, CustomMaterial.GUI_PROFILE_V2_RANK_MEMBER,
				CustomMaterial.GUI_PROFILE_V2_RANK_TRUSTED, CustomMaterial.GUI_PROFILE_V2_RANK_ELITE,
				CustomMaterial.GUI_PROFILE_V2_RANK_VETERAN, CustomMaterial.GUI_PROFILE_V2_RANK_NOBLE,
				CustomMaterial.GUI_PROFILE_V2_RANK_BUILDER, CustomMaterial.GUI_PROFILE_V2_RANK_ARCHITECT,
				CustomMaterial.GUI_PROFILE_V2_RANK_MODERATOR, CustomMaterial.GUI_PROFILE_V2_RANK_OPERATOR,
				CustomMaterial.GUI_PROFILE_V2_RANK_ADMIN, CustomMaterial.GUI_PROFILE_V2_RANK_OWNER);

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

				return CustomMaterial.GUI_PROFILE_V2_RANK_UNKNOWN;
			}

		},

		PLAYER(2, 1, Material.PLAYER_HEAD, 2) {
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

		LEVELS(3, 5, Material.EXPERIENCE_BOTTLE, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}
		},

		VIEW_SOCIAL_MEDIA(3, 6, Material.BOOK, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> connectedSites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				return connectedSites.size() != 0;
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				SocialMediaCommand.open(viewer, target, "/profile " + nickname(target));
			}
		},

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

		MODIFY_FRIEND(4, 1, CustomMaterial.GUI_PROFILE_V2_ICON_FRIEND_MODIFY_ADD) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				return !isSelf(viewer, target);
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				if (isFriendsWith(friendService.get(target), viewer))
					return "&3Remove Friend";

				return "&3Send Friend Request";
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
					return CustomMaterial.GUI_PROFILE_V2_ICON_FRIEND_MODIFY_REMOVE.getModelId();

				return CustomMaterial.GUI_PROFILE_V2_ICON_FRIEND_MODIFY_ADD.getModelId();
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

		VIEW_FRIENDS(4, 2, CustomMaterial.GUI_PROFILE_V2_ICON_FRIENDS) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				if (hasNoFriends(target))
					return List.of(Nickname.of(target) + " has no friends ):");

				return super.getLore(viewer, target);
			}

			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				if (hasNoFriends(target))
					return CustomMaterial.GUI_PROFILE_V2_ICON_FRIENDS_ERROR.getModelId();

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

		PARTY(4, 3, CustomMaterial.GUI_PROFILE_V2_ICON_PARTY) { // TODO

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				if (isSelf(viewer, target))
					return "View Party";

				return "TODO";
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				if (isSelf(viewer, target))
					return;

				super.onClick(viewer, target, previousMenu); // TODO
			}
		},

		//

		TELEPORT(4, 6, Material.ENDER_PEARL, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},

		VIEW_SHOP(4, 4, Material.EMERALD, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO
			}
		},

		VIEW_HOMES(4, 0, Material.OAK_DOOR, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(TODO);
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target, InventoryProvider previousMenu) {
				super.onClick(viewer, target, previousMenu); // TODO: make gui if possible
			}
		},

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

		public List<SlotPos> getCentered3Wide(Player viewer, OfflinePlayer target) {
			return List.of(/* 		Main Item		*/ SlotPos.of(getRow(), getCol() + 1), SlotPos.of(getRow(), getCol(viewer, target) + 2));
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

}
