package gg.projecteden.nexus.models.perkowner;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.perks.HideParticle;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@Entity(value = "perk_owner", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class PerkOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	protected UUID uuid;
	private Map<PerkType, Boolean> purchasedPerks = new ConcurrentHashMap<>();
	private int tokens = 0;
	private int dailyTokens = 0;
	private LocalDate tokenDate = LocalDate.now();
	private LocalDate randomGiftDate = LocalDate.of(1970, 1, 1);
	private HideParticle hideParticle = HideParticle.NONE;

	private static final int MAX_DAILY_TOKENS = 30;
	public static int getMaxDailyTokens(UUID uuid) {
		return (int) Math.round(MAX_DAILY_TOKENS * Booster.getTotalBoost(uuid, Boostable.MINIGAME_DAILY_TOKENS) * (LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY ? 1.5 : 1));
	}

	public Set<PerkType> getEnabledPerks() {
		return new HashSet<>() {{
			purchasedPerks.forEach((perkType, enabled) -> {
				if (enabled)
					add(perkType);
			});
		}};
	}

	public <T extends Perk> Set<T> getEnabledPerksByClass(Class<T> tClass) {
		return getEnabledPerks().stream().filter(perkType -> tClass.isInstance(perkType.getPerk())).map(perkType -> tClass.cast(perkType.getPerk())).collect(Collectors.toSet());
	}

	public Set<? extends Perk> getEnabledPerksByCategory(PerkCategory category) {
		return getEnabledPerks().stream().filter(perkType -> perkType.getPerkCategory() == category).map(PerkType::getPerk).collect(Collectors.toSet());
	}

	public Set<PerkType> getPurchasedPerkTypesByCategory(PerkCategory category) {
		return purchasedPerks.keySet().stream().filter(perkType -> perkType.getPerkCategory() == category).collect(Collectors.toSet());
	}

	public boolean equals(PerkOwner other) {
		return uuid.equals(other.getUuid());
	}

	public void takeTokens(int amount) {
		this.tokens -= amount;
	}

	public void giveTokens(int amount) {
		this.tokens += amount;
	}

	/**
	 * Purchases a perk from the store. Specifically, this checks to see if the player doesn't already have the perk and
	 * has enough tokens to purchase the perk. If it does, it takes the tokens from this player and adds the perk to
	 * their purchased perks.
	 * @param perk the perk to purchase
	 * @return whether or not the purchase was successful
	 */
	public boolean purchase(PerkType perk) {
		if (purchasedPerks.containsKey(perk))
			return false;
		if (perk.getPerk().getPrice() > tokens)
			return false;
		takeTokens(perk.getPrice());
		purchasedPerks.put(perk, false);
		save();
		return true;
	}

	/**
	 * Rewards the user for winning a minigame
	 * @param arenaName name of the arena the minigamer was playing on to display in chat
	 * @return false if the user has reached their max daily earnings
	 */
	public boolean reward(String arenaName) {
		LocalDate date = LocalDate.now();
		if (date.isAfter(tokenDate)) {
			tokenDate = date;
			dailyTokens = 0;
		}
		int amount = Math.min(getMaxDailyTokens(uuid)-dailyTokens, RandomUtils.randomInt(5, 10));
		if (amount > 0) {
			giveTokens(amount);
			dailyTokens += amount;
			try {
				if (getOnlinePlayer() != null)
					SoundUtils.Jingle.PING.play(getOnlinePlayer()); // TODO: unique jingle
				PlayerUtils.send(uuid, Minigames.PREFIX + "You won &e" + amount + StringUtils.plural(" token", amount) + "&3 for scoring in &e" + arenaName);
				if (dailyTokens >= MAX_DAILY_TOKENS)
					PlayerUtils.send(uuid, Minigames.PREFIX + "You've earned the maximum tokens for today");
			} catch (NullPointerException ignored) {/*failsafe to allow PerkOwnerTest to function*/}
		}
		save();
		return amount > 0;
	}

	/**
	 * Rewards the user for winning a minigame
	 * @param arena arena the minigamer was playing on to display in chat
	 * @return false if the user has reached their max daily earnings
	 */
	public boolean reward(Arena arena) {
		return reward(arena.getDisplayName());
	}

	/**
	 * Enables or disables a perk
	 * @return false if the owner doesn't have the perk
	 */
	public boolean toggle(PerkType perkType) {
		if (!purchasedPerks.containsKey(perkType))
			return false;

		boolean setTo = !purchasedPerks.get(perkType);
		// disable other perk types if this is being enabled and this is part of an exclusive perk category
		if (setTo && perkType.getPerk().getPerkCategory().isExclusive())
			(new HashSet<>(purchasedPerks.keySet())).stream().filter(perkType::excludes).forEach(otherType -> purchasedPerks.put(otherType, false));

		purchasedPerks.put(perkType, setTo);

		save();
		return true;
	}

	protected void save() {
		new PerkOwnerService().save(this);
	}
}
