package gg.projecteden.nexus.models.resourcepack;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.titan.serverbound.TitanConfig;
import gg.projecteden.nexus.features.titan.serverbound.Versions;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "local_resource_pack", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LocalResourcePackUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private String lastKnownSaturnVersion;
	private String lastKnownTitanVersion;
	private TitanSettings titanSettings;

	@Data
	public static class TitanSettings {
		@SerializedName("titan")
		private String titanVersion;

		@SerializedName("saturn")
		private String saturnVersion;

		private TitanConfig config;

		private boolean useNewMessagingFormat;
	}

	public void setTitanSettings(TitanSettings settings) {
		this.setTitanVerions(new Versions(settings.titanVersion, settings.saturnVersion));
	}

	public void setTitanSettings(TitanConfig config) {
		if (titanSettings == null)
			this.titanSettings = new TitanSettings();

		this.titanSettings.config = config;
	}

	public void setTitanVerions(Versions verions) {
		if (titanSettings == null)
			this.titanSettings = new TitanSettings();

		this.titanSettings.titanVersion = verions.getTitan();
		this.titanSettings.saturnVersion = verions.getSaturn();

		this.enabled = !gg.projecteden.nexus.utils.Nullables.isNullOrEmpty(getSaturnVersion());
		this.lastKnownSaturnVersion = getSaturnVersion();
		this.lastKnownTitanVersion = getTitanVersion();
	}

	public void useNewMessagingFormat() {
		if (titanSettings == null)
			this.titanSettings = new TitanSettings();

		this.titanSettings.useNewMessagingFormat = true;
	}

	public boolean shouldUseNewMessagingFormat() {
		if (titanSettings == null)
			return false;

		return this.titanSettings.useNewMessagingFormat;
	}

	public String getTitanVersion() {
		if (titanSettings == null)
			return null;

		return titanSettings.getTitanVersion();
	}

	public String getSaturnVersion() {
		if (titanSettings == null)
			return null;

		return titanSettings.getSaturnVersion();
	}

	public void forgetVersions() {
		if (titanSettings == null)
			return;

		titanSettings.setSaturnVersion(null);
		titanSettings.setTitanVersion(null);
	}

	@ToString.Include
	public boolean hasTitan() {
		return titanSettings != null && Nullables.isNotNullOrEmpty(getTitanVersion());
	}

	@NotNull
	public String getSaturnStatus() {
		if (!gg.projecteden.nexus.utils.Nullables.isNullOrEmpty(getSaturnVersion()))
			return "&aManual &3(&7" + StringUtils.left(getSaturnVersion(), 7) + "&3)";
		else if (isEnabled())
			return "&aManual";
		else {
			if (isOnline()) {
				Status status = getOnlinePlayer().getResourcePackStatus();
				if (status != null)
					return STATUS_COLORS.get(status) + StringUtils.camelCase(status);
			}
		}

		return "&7Unknown";
	}

	public String getSaturnCommitUrl() {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrEmpty(getSaturnVersion()))
			return "";

		return EdenSocialMediaSite.GITHUB.getUrl() + "/Saturn/commit/" + getSaturnVersion();
	}

	public String getTitanStatus() {
		return hasTitan() ? "&aInstalled &3(&7" + getTitanVersion() + "&3)" : "&cNot installed";
	}

	public String getTitanCommitUrl() {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrEmpty(getTitanVersion()))
			return "";

		return EdenSocialMediaSite.GITHUB.getUrl() + "/Titan/commit/" + getTitanVersion();
	}

	private static final Map<Status, ChatColor> STATUS_COLORS = Map.of(
		Status.SUCCESSFULLY_LOADED, ChatColor.GREEN,
		Status.DECLINED, ChatColor.RED,
		Status.FAILED_DOWNLOAD, ChatColor.RED,
		Status.ACCEPTED, ChatColor.GOLD,
		Status.DOWNLOADED, ChatColor.YELLOW,
		Status.INVALID_URL, ChatColor.RED,
		Status.FAILED_RELOAD, ChatColor.RED,
		Status.DISCARDED, ChatColor.RED
	);

}
