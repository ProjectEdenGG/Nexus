package gg.projecteden.nexus.features.profiles;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.commands.SocialMediaCommand;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProfileMenuProvider extends InventoryProvider {
	private static final SocialMediaUserService socialMediaUserService = new SocialMediaUserService();
	private final SocialMediaUser socialMediaUser;
	private static final FriendsUserService friendService = new FriendsUserService();
	private final OfflinePlayer target;

	private int ticks = 0;
	private int index = 0;
	private final int maxIndex;


	public ProfileMenuProvider(OfflinePlayer target) {
		this.target = target;
		socialMediaUser = socialMediaUserService.get(target);
		maxIndex = Arrays.stream(SocialMediaSite.values()).filter(site -> socialMediaUser.getConnection(site) != null).toList().size() - 1;
	}

	@Override
	protected int getRows(Integer page) {
		return 5;
	}

	@Override
	public String getTitle() {
		String whose = PlayerUtils.isSelf(player, target) ? "Your" : Nickname.of(target) + "'s";
		return "&3" + whose + " Profile &c(WIP)";
	}

	@Override
	public void init() {
		addCloseItem();

		// TODO: do rank using magic spaces, and text

		for (ProfileMenuItem menuItem : ProfileMenuItem.values()) {
			menuItem.setClickableItem(player, target, contents);
			menuItem.setExtraClickableItems(player, target, contents);
		}

	}

	@Override
	public void update() {
		if (maxIndex == 0)
			return;

		ticks++;
		if (ticks == TickTime.SECOND.x(3))
			ticks = 0;

		if (ticks != 1)
			return;

		SlotPos slotPos = ProfileMenuItem.VIEW_SOCIAL_MEDIA.getSlotPos();
		ClickableItem clickableItem = ProfileMenuItem.VIEW_SOCIAL_MEDIA.getClickableItem(player, target, index);
		contents.set(slotPos, clickableItem);

		index++;
		if (index > maxIndex)
			index = 0;
	}

	@Getter
	@AllArgsConstructor
	private enum ProfileMenuItem {
		PLAYER(2, 2, Material.PLAYER_HEAD, 2) {
			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Nerd.of(target).getNickname();
			}

			@Override
			public ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target) {
				return super.getItemBuilder(viewer, target).skullOwner(target);
			}

			@Override
			public List<SlotPos> getExtraSlots() {
				return List.of(
					SlotPos.of(1, 1), SlotPos.of(1, 2), SlotPos.of(1, 3),
					SlotPos.of(2, 1), /* 						*/ SlotPos.of(2, 3),
					SlotPos.of(3, 1), SlotPos.of(3, 2), SlotPos.of(3, 3)
				);
			}

			@Override
			public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
				return getItemBuilder(viewer, target).material(Material.PAPER).modelId(8500);
			}
		},

		PRESENCE(1, 7, Material.PAPER, 25000) {
			@Override
			public int getModelId(Player viewer, OfflinePlayer target) {
				return Presence.of(target, viewer).getModelId();
			}

			@Override
			public String getName(Player viewer, OfflinePlayer target) {
				return "&e" + Presence.of(target).getName();
			}
		},

		LEVELS(3, 4, Material.EXPERIENCE_BOTTLE, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}
		},

		BALANCES(3, 5, Material.PAPER, 1510) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}
		},

		TIMES(3, 6, Material.CLOCK, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}
		},

		VIEW_SOCIAL_MEDIA(3, 7, Material.PAPER, 901) {
			@Override
			public int getModelId(Player viewer, OfflinePlayer target, int index) {
				SocialMediaUser user = socialMediaUserService.get(target);

				List<SocialMediaSite> sites = Arrays.stream(SocialMediaSite.values()).filter(site -> user.getConnection(site) != null).toList();
				if (sites.size() == 0)
					return -1;

				return sites.get(index).getModelId();
			}

			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				SocialMediaCommand.open(viewer, target, "/profile " + Nickname.of(target));
			}
		},

		ADD_FRIEND(4, 1, Material.STONE_BUTTON, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (PlayerUtils.isSelf(viewer, target))
					return false;

				return !isFriendsWith(friendService.get(target), viewer);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},

		REMOVE_FRIEND(4, 1, Material.STONE_BUTTON, 0) {
			@Override
			public boolean shouldShow(Player viewer, OfflinePlayer target) {
				if (PlayerUtils.isSelf(viewer, target))
					return false;

				return isFriendsWith(friendService.get(target), viewer);
			}

			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},

		VIEW_HOMES(4, 4, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},

		VIEW_SHOP(4, 5, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},

		VIEW_TRUSTS(4, 6, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},

		TELEPORT(4, 7, Material.STONE_BUTTON, 0) {
			@Override
			public List<String> getLore(Player viewer, OfflinePlayer target) {
				return List.of(soon);
			}

			// TODO
			@Override
			public void onClick(Player viewer, OfflinePlayer target) {
				super.onClick(viewer, target);
			}
		},
		;

		private final int row, col;
		private final Material material;
		private final int modelId;

		private static final String soon = "&3Coming Soon!";

		public boolean shouldShow(Player viewer, OfflinePlayer target) {
			return true;
		}

		public void onClick(Player viewer, OfflinePlayer target) {}

		public SlotPos getSlotPos() {
			return SlotPos.of(row, col);
		}

		public String getName(Player viewer, OfflinePlayer target) {
			return "&e" + StringUtils.camelCase(this);
		}

		public int getModelId(Player viewer, OfflinePlayer target) {
			return modelId;
		}

		public int getModelId(Player viewer, OfflinePlayer target, int index) {
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

		public @Nullable ItemBuilder getItemBuilder(Player viewer, OfflinePlayer target, int index) {
			int modelId = getModelId(viewer, target, index);
			if (modelId == -1)
				return null;

			return new ItemBuilder(getMaterial())
				.modelId(modelId)
				.name(getName(viewer, target))
				.lore(getLore(viewer, target))
				.clone();
		}

		public List<SlotPos> getExtraSlots() {
			return Collections.emptyList();
		}

		public ItemBuilder getExtraSlotItemBuilder(Player viewer, OfflinePlayer target) {
			return getItemBuilder(viewer, target).clone();
		}


		private static boolean isFriendsWith(FriendsUser friend, Player viewer) {
			return friend.isFriendsWith(viewer);
		}

		public void setClickableItem(Player player, OfflinePlayer target, InventoryContents contents) {
			ClickableItem clickableItem = getClickableItem(player, target);
			if (clickableItem == null)
				return;

			contents.set(getSlotPos(), clickableItem);
		}

		public void setExtraClickableItems(Player viewer, OfflinePlayer target, InventoryContents contents) {
			for (SlotPos slotPos : getExtraSlots()) {
				ClickableItem clickableItem = getClickableItem(viewer, target, getExtraSlotItemBuilder(viewer, target));
				if (clickableItem == null)
					continue;

				contents.set(slotPos, clickableItem);
			}
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target));
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, int index) {
			return getClickableItem(viewer, target, getItemBuilder(viewer, target, index));
		}

		public ClickableItem getClickableItem(Player viewer, OfflinePlayer target, ItemBuilder itemBuilder) {
			if (Nullables.isNullOrAir(itemBuilder) || !shouldShow(viewer, target))
				return null;

			return ClickableItem.of(itemBuilder, e -> onClick(viewer, target));
		}
	}


}
