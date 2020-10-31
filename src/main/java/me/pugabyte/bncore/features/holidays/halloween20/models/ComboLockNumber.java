package me.pugabyte.bncore.features.holidays.halloween20.models;

import me.pugabyte.bncore.features.holidays.halloween20.Halloween20;
import me.pugabyte.bncore.features.holidays.halloween20.quest.menus.Halloween20Menus;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum ComboLockNumber {

	PUZZLE_TWO(new Location(Bukkit.getWorld("safepvp"), 327.00, 56.00, -1956.00, .00F, .00F), 2) {
		@Override
		public void onFind(Player player) {
			Halloween20Menus.openPicturePuzzle(player, this);
		}
	},
	ZERO(new Location(Bukkit.getWorld("safepvp"), 319.00, 60.00, -1924.00, .00F, .00F), 0),
	SEVEN(new Location(Bukkit.getWorld("safepvp"), 364.00, 113.00, -1919.00, .00F, .00F), 5),
	PUZZLE_EIGHT(new Location(Bukkit.getWorld("safepvp"), 394.00, 103.00, -1952.00, .00F, .00F), 6) {
		@Override
		public void onFind(Player player) {
			Halloween20Menus.openPicturePuzzle(player, this);
		}
	},
	ONE(new Location(Bukkit.getWorld("safepvp"), 374.00, 112.00, -1963.00, .00F, .00F), 1),
	PUZZLE_EIGHT_2(new Location(Bukkit.getWorld("safepvp"), 368.00, 152.00, -1933.00, .00F, .00F), 6) {
		@Override
		public void onFind(Player player) {
			Halloween20Menus.openFlashCardPuzzle(player, this);
		}
	},
	ONE_2(new Location(Bukkit.getWorld("safepvp"), 329.00, 163.00, -1949.00, .00F, .00F), 1),
	SIX(new Location(Bukkit.getWorld("safepvp"), 391.00, 154.00, -1958.00, .00F, .00F), 4),
	ONE_3(new Location(Bukkit.getWorld("safepvp"), 285.00, 223.00, -1949.00, .00F, .00F), 1),
	THREE(new Location(Bukkit.getWorld("safepvp"), 309.00, 202.00, -1925.00, .00F, .00F), 3),
	PUZZLE_ZERO(new Location(Bukkit.getWorld("safepvp"), 326.00, 216.00, -1949.00, .00F, .00F), 0) {
		@Override
		public void onFind(Player player) {
			Halloween20Menus.openFlashCardPuzzle(player, this);
		}
	};

	Location loc;
	int yOff;

	ComboLockNumber(Location loc, int yOff) {
		this.loc = loc;
		this.yOff = yOff;
	}

	public ItemStack getItem() {
		return new Location(Bukkit.getWorld("safepvp"), 279.00, 4.00, -1784.00, .00F, .00F)
				.add(0, yOff, 0).getBlock().getDrops().stream().findFirst().orElse(null);
	}

	public void onFind(Player player) {
		String PREFIX = StringUtils.getPrefix("Halloween20");
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		switch (user.getCombinationStage()) {
			case NOT_STARTED:
				Utils.send(player, PREFIX + "This looks like a number from the combination lock close to where I entered this place...");
				break;
			case STARTED:
				if (user.getFoundComboLockNumbers().contains(this)) {
					Utils.send(player, PREFIX + "You have already found this number");
					break;
				}
				user.getFoundComboLockNumbers().add(this);
				Utils.send(player, PREFIX + "You have found " + user.getFoundComboLockNumbers().size() + "/11 numbers for the combination lock.");
				service.save(user);
				if (user.getFoundComboLockNumbers().size() == 11)
					Utils.send(player, Halloween20.PREFIX + "You have found all the numbers for the combination lock. Return to see if you can crack the code!");
				break;
		}
	}

	public static ComboLockNumber getByLocation(Location loc) {
		for (ComboLockNumber number : values()) {
			if (loc.equals(number.loc))
				return number;
		}
		return null;
	}

}
