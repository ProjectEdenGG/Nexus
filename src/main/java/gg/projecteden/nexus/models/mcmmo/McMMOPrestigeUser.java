package gg.projecteden.nexus.models.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestige.McMMOResetReward;
import gg.projecteden.nexus.features.mcmmo.mastery.McMMOPrestigeListener;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "mcmmo_prestige_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class McMMOPrestigeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<PrimarySkillType, Double> prestigeExperience = new HashMap<>();
	private Map<PrimarySkillType, List<ClaimedReward>> claimedRewards = new HashMap<>();

	public void awardExperience(PrimarySkillType skill, double experience) {
		double before = prestigeExperience.getOrDefault(skill, 0D);
		prestigeExperience.put(skill, before + experience);

		if (before >= McMMOPrestigeListener.XP_TO_MASTERY_REWARD) return;
		if ((before + experience < McMMOPrestigeListener.XP_TO_MASTERY_REWARD)) return;

		if (!isOnline()) return;

		new JsonBuilder(StringUtils.getPrefix("McMMO") + "&3You have unlocked a prestige reward in &e" + StringUtils.camelCase(skill))
			.send(getPlayer());

		new SoundBuilder(Sound.BLOCK_ENCHANTMENT_TABLE_USE).location(getPlayer().getLocation()).receiver(getPlayer()).play();
		new SoundBuilder(Sound.BLOCK_BEACON_POWER_SELECT).volume(.8f).location(getPlayer().getLocation()).receiver(getPlayer()).play();
		new SoundBuilder(Sound.UI_TOAST_CHALLENGE_COMPLETE).location(getPlayer().getLocation()).receiver(getPlayer()).play();
	}

	public void claimReward(PrimarySkillType skill, McMMOResetReward reward) {
		double before = prestigeExperience.getOrDefault(skill, 0D);
		prestigeExperience.put(skill, Math.max(0, before - McMMOPrestigeListener.XP_TO_MASTERY_REWARD));

		claimedRewards.computeIfAbsent(skill, k -> new ArrayList<>()).add(new ClaimedReward(reward.id(), LocalDateTime.now()));
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ClaimedReward {
		private String id;
		private LocalDateTime claimTime;
	}

}
