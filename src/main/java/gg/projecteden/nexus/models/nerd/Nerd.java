package gg.projecteden.nexus.models.nerd;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.LocalDateConverter;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.commands.BirthdaysCommand;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNicknamed;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.freeze.FreezeService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "nerd", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class, LocalDateTimeConverter.class})
public class Nerd extends gg.projecteden.api.mongodb.models.nerd.Nerd implements PlayerOwnedObject, IsColoredAndNicknamed, Colored {
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Location location;

	private Set<WorldGroup> visitedWorldGroups = new HashSet<>();
	private Set<SubWorldGroup> visitedSubWorldGroups = new HashSet<>();

	// Set to null after they have moved
	private Location loginLocation;
	private Location teleportOnLogin;

	@PreLoad
	void preLoad(DBObject dbObject) {
		List<String> visitedWorldGroups = (List<String>) dbObject.get("visitedWorldGroups");
		if (visitedWorldGroups != null && visitedWorldGroups.remove("ONEBLOCK"))
			visitedWorldGroups.add("SKYBLOCK");
		List<String> visitedSubWorldGroups = (List<String>) dbObject.get("visitedSubWorldGroups");
		if (visitedSubWorldGroups != null && visitedSubWorldGroups.remove("LEGACY"))
			visitedSubWorldGroups.add("LEGACY1");

		List<String> pronouns = (List<String>) dbObject.get("pronouns");
		if (!isNullOrEmpty(pronouns)) {
			List<String> fixed = new ArrayList<>() {{
				for (String pronoun : pronouns) {
					final Pronoun of = Pronoun.of(pronoun);
					if (of != null)
						add(of.name());
				}
			}};

			fixed.removeIf(Objects::isNull);
			dbObject.put("pronouns", fixed);
		}

		List<String> aliases = (List<String>) dbObject.get("aliases");
		if (!isNullOrEmpty(aliases))
			dbObject.put("aliases", aliases.stream().map(String::toLowerCase).toList());
	}

	@PostLoad
	void fix() {
		if (!isNullOrEmpty(preferredName)) {
			preferredNames.add(preferredName);
			preferredName = null;
		}
	}

	public Nerd(@NonNull UUID uuid) {
		super(uuid);
	}

	public static Nerd of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static Nerd of(HasUniqueId uuid) {
		OfflinePlayer offlinePlayer = PlayerUtils.getPlayer(uuid.getUniqueId());
		Nerd nerd = new NerdService().get(offlinePlayer);
		nerd.fromPlayer(offlinePlayer);
		return nerd;
	}

	public static Nerd of(UUID uuid) {
		return new NerdService().get(uuid);
	}

	public static List<Nerd> of(Collection<UUID> uuids) {
		return uuids.stream().map(Nerd::of).collect(Collectors.toList());
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId();
		name = Name.of(uuid);
		if (player.getFirstPlayed() > 0) {
			LocalDateTime newFirstJoin = Utils.epochMilli(player.getFirstPlayed());
			if (firstJoin == null || firstJoin.isBefore(EARLIEST_JOIN) || newFirstJoin.isBefore(firstJoin))
				firstJoin = newFirstJoin;
		}
	}

	@Override
	public @NotNull String getName() {
		String name = super.getName();
		if (name.length() <= 16) // ignore "api-<uuid>" names
			Name.put(uuid, name);
		return name;
	}

	@Override
	public void setBirthday(LocalDate birthday) {
		Discord.staffLog("**[Birthdays]** " + getNickname() + "'s birthday " + (birthday == null ? "reset" : "set to " + BirthdaysCommand.getFormatter().format(birthday)));
		super.setBirthday(birthday);
	}

	public boolean hasMoved() {
		if (isOnline() && loginLocation != null)
			if (AFK.isSameLocation(loginLocation, getOnlinePlayer().getLocation()))
				return false;
			else
				loginLocation = null;

		return true;
	}

	// this is just here for the ToString.Include
	@ToString.Include
	@NotNull
	@Override
	public Rank getRank() {
		return PlayerOwnedObject.super.getRank();
	}

	/**
	 * Returns the user's name formatted with a color formatting code
	 * @deprecated you're probably looking for {@link Nerd#getColoredName()}
	 */
	@ToString.Include
	@Deprecated
	public String getNameFormat() {
		return getRank().getChatColor() + getName();
	}

	/**
	 * Returns the user's nickname with their rank color prefixed. Formerly known as getNicknameFormat.
	 */
	@Override
	public @NotNull String getColoredName() {
		if (isKoda())
			return Koda.getColoredName();
		return getChatColor() + getNickname();
	}

	public @NotNull Color getColor() {
		return getRank().colored().getColor();
	}

	private boolean isKoda() {
		return Dev.KODA.is(this);
	}

	public JsonBuilder getChatFormat(Chatter viewer) {
		String prefix = getFullPrefix(viewer);

		final ChatColor rankColor = isKoda() ? Koda.getChatColor() : getRank().getChatColor();
		final JsonBuilder badge = new BadgeUserService().get(this).getBadgeJson(viewer);

		return badge.next(prefix).next(rankColor + getNickname());
	}

	private String getFullPrefix(Chatter viewer) {
		if (isKoda())
			return "";

		String prefix = this.prefix;

		if (isNullOrEmpty(prefix))
			prefix = getRank().getPrefix();

		if (viewer != null)
			if (getRank().isMod() && new FreezeService().get(viewer).isFrozen())
				prefix = getRank().getPrefix();

		if (!isNullOrEmpty(prefix))
			prefix = "&8&l[&f" + prefix + "&8&l] ";

		return prefix;
	}

	public LocalDateTime getLastJoin(Player viewer) {
		if (isOnline()) {
			if (PlayerUtils.canSee(viewer, this))
				return super.getLastJoin();

			return super.getLastUnvanish();
		}

		return super.getLastJoin();
	}

	@Override
	public void setLastJoin(LocalDateTime when) {
		super.setLastJoin(when);
		super.setLastUnvanish(when);
	}

	public LocalDateTime getLastQuit(Player viewer) {
		if (isOnline()) {
			if (PlayerUtils.canSee(viewer, this))
				return super.getLastQuit();

			return super.getLastVanish();
		}

		return super.getLastQuit();
	}

	@Override
	public void setLastQuit(LocalDateTime when) {
		super.setLastQuit(when);
		super.setLastVanish(when);
	}

	@ToString.Include
	public boolean isVanished() {
		if (!isOnline())
			return false;
		return PlayerUtils.isVanished(getOnlinePlayer());
	}

	public @NotNull WorldGroup getWorldGroup() {
		return WorldGroup.of(getLocation());
	}

	public World getWorld() {
		if (isOnline())
			return getOnlinePlayer().getWorld();

		return new NBTPlayer(this).getWorld();
	}

	public @NotNull Location getLocation() {
		if (isOnline())
			return getOnlinePlayer().getPlayer().getLocation();

		return getOfflineLocation();
	}

	public Location getOfflineLocation() {
		if (true)
			return new NBTPlayer(this).getOfflineLocation();

		// TODO 1.19 Remove if nbt is reliable
		if (location != null)
			return location;

		try {
			location = new NBTPlayer(this).getOfflineLocation();
			new NerdService().save(this);
			return location;
		} catch (Exception ex) {
			throw new InvalidInputException("Could not get location of offline player " + name + ": " + ex.getMessage());
		}
	}

	public List<ItemStack> getInventory() {
		if (isOnline())
			return Arrays.asList(getOnlinePlayer().getInventory().getContents());

		return new NBTPlayer(this).getOfflineInventory();
	}

	public List<ItemStack> getEnderChest() {
		if (isOnline())
			return Arrays.asList(getOnlinePlayer().getEnderChest().getContents());

		return new NBTPlayer(this).getOfflineEnderChest();
	}

	public List<ItemStack> getArmor() {
		if (isOnline())
			return Arrays.asList(getOnlinePlayer().getInventory().getArmorContents());

		return new NBTPlayer(this).getOfflineArmor();
	}

	public ItemStack getOffHand() {
		if (isOnline())
			return getOnlinePlayer().getInventory().getItemInOffHand();

		return new NBTPlayer(this).getOfflineOffHand();
	}

	public void addPronoun(Pronoun pronoun) {
		addPronoun(pronoun, null);
	}

	public void addPronoun(Pronoun pronoun, @Nullable String executor) {
		pronouns.add(pronoun);
		logPronoun(executor, pronoun, "added");
	}

	public void removePronoun(Pronoun pronoun) {
		removePronoun(pronoun, null);
	}

	public void removePronoun(Pronoun pronoun, @Nullable String executor) {
		pronouns.remove(pronoun);
		logPronoun(executor, pronoun, "removed");
	}

	private void logPronoun(@Nullable String executor, @NotNull Pronoun pronoun, @NotNull String verb) {
		String log;
		if (executor == null || executor.equals(getNickname()))
			log = getNickname() + " " + verb + " the";
		else
			log = executor + " " + verb + " " + getNickname() + "'s";
		Discord.staffLog(StringUtils.getDiscordPrefix("Pronouns") + log + " pronoun `" + pronoun + "`");
		updatePronouns();
	}

	public void updatePronouns() {
		new DiscordUserService().get(this).updatePronouns(pronouns);
		new NerdService().save(this);
	}

	@Data
	public static class StaffMember implements PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}
