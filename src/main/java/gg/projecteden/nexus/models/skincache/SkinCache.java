package gg.projecteden.nexus.models.skincache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.dbassett.skullcreator.SkullCreator;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gg.projecteden.utils.StringUtils.isNullOrEmpty;
import static gg.projecteden.utils.StringUtils.uuidUnformat;

@Data
@Builder
@Entity(value = "skin_cache", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class SkinCache implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String value;
	private String signature;
	private LocalDateTime timestamp;
	private LocalDateTime lastChanged;

	public static SkinCache of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static SkinCache of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static SkinCache of(UUID uuid) {
		return new SkinCacheService().get(uuid);
	}

	public boolean isCached() {
		return !isNullOrEmpty(value);
	}

	public ItemStack getHead() {
		if (!isCached())
			update();

		if (!isCached())
			return getHeadFromPlayer();

		return new ItemBuilder(SkullCreator.itemFromBase64(value)).name(getNickname() + "'s Head").build();
	}

	public SkullMeta getHeadMeta() {
		return (SkullMeta) getHead().getItemMeta();
	}

	public static final Pattern TEXTURE_URL_REGEX = Pattern.compile("http://textures\\.minecraft\\.net/texture/[a-f\\d]{64}");

	public String getTextureUrl() {
		return getTextureUrl(this.value);
	}

	public String getTextureUrl(String value) {
		if (value == null)
			return "Null value";

		final String decoded = new String(Base64.getDecoder().decode(value));
		final Matcher matcher = TEXTURE_URL_REGEX.matcher(decoded);
		if (matcher.find())
			return matcher.group();

		return "Could not find url: " + value;
	}

	public boolean update() {
		String previous = this.value;
		ProfileProperty property;
		try {
			property = getCurrentProperty();
		} catch (NexusException ex) {
			Nexus.warn("Error getting " + getNickname() + "'s current player profile: " + ex.getMessage());
			return false;
		}

		this.timestamp = LocalDateTime.now();
		this.value = property.getValue();
		this.signature = property.getSignature();
		new SkinCacheService().save(this);

		final boolean changed = !getTextureUrl().equals(getTextureUrl(previous));

		if (changed)
			this.lastChanged = LocalDateTime.now();

		return changed;
	}

	@Data
	private static class FakePlayerProfile {
		private String id;
		private String name;
		private List<ProfileProperty> properties;

		private UUID getUuid() {
			return UUID.fromString(StringUtils.uuidFormat(id));
		}

		PlayerProfile getPlayerProfile() {
			PlayerProfile profile = Bukkit.getServer().createProfile(getUuid(), name);
			profile.setProperties(properties);
			return profile;
		}

	}

	private static String URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

	@SneakyThrows
	private @NotNull ProfileProperty getCurrentProperty() {
		if (isOnline())
			return getTextureProperty(getOnlinePlayer().getPlayerProfile());

		FakePlayerProfile fakeProfile = HttpUtils.mapJson(FakePlayerProfile.class, URL, uuidUnformat(uuid.toString()));
		return getTextureProperty(fakeProfile.getPlayerProfile());
	}

	private @NotNull ProfileProperty getTextureProperty(PlayerProfile profile) {
		if (profile != null)
			for (ProfileProperty property : profile.getProperties())
				if (property.getName().equals("textures"))
					return property;

		throw new NexusException("No texture property:\n" + StringUtils.toPrettyString(profile));
	}

	private ItemStack getHeadFromPlayer() {
		return new ItemBuilder(Material.PLAYER_HEAD).skullOwnerActual(getOfflinePlayer()).build();
	}

}
