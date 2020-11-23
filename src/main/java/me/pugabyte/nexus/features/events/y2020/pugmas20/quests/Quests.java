package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import org.bukkit.event.Listener;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Quests {
	public Quests() {
		new Reflections(getClass().getPackage().getName()).getSubTypesOf(Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					Nexus.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@Accessors(fluent = true)
	public enum Pugmas20Quest {
		GIFT_GIVER(Pugmas20User::getGiftGiverStage, Pugmas20User::setGiftGiverStage),
		LIGHT_THE_TREE(Pugmas20User::getLightTreeStage, Pugmas20User::setLightTreeStage),
		ORNAMENT_VENDOR(Pugmas20User::getOrnamentVendorStage, Pugmas20User::setOrnamentVendorStage),
		THE_MINES(Pugmas20User::getMinesStage, Pugmas20User::setMinesStage),
		TOY_TESTING(Pugmas20User::getToyTestingStage, Pugmas20User::setToyTestingStage);

		@Getter
		private final Function<Pugmas20User, QuestStage> getter;
		@Getter
		private final BiConsumer<Pugmas20User, QuestStage> setter;

		Pugmas20Quest(Function<Pugmas20User, QuestStage> getQuestStage, BiConsumer<Pugmas20User, QuestStage> setQuestStage) {
			this.getter = getQuestStage;
			this.setter = setQuestStage;
		}
	}
}
