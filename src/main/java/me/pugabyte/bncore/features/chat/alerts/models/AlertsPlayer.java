package me.pugabyte.bncore.features.chat.alerts.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AlertsPlayer {
	private String uuid;
	private List<Highlight> highlights;
	private boolean muted;
	private boolean dirty;

	public AlertsPlayer(String uuid) {
		this.uuid = uuid;
		this.highlights = new ArrayList<>();
		this.muted = false;
	}

	public AlertsPlayer(String uuid, List<Highlight> highlights, boolean muted) {
		this.uuid = uuid;
		this.highlights = highlights;
		this.muted = muted;
	}

	public String getUuid() {
		return uuid;
	}

	public List<Highlight> getHighlights() {
		return highlights;
	}

	public boolean add(String highlight) {
		return add(highlight, true);
	}

	public boolean add(String highlight, boolean partialMatching) {
		if (!has(highlight)) {
			Highlight newHighlight = new Highlight(highlight, partialMatching);
			highlights.add(newHighlight);
			dirty = true;
			sort();
			return true;
		}
		return false;
	}

	public boolean delete(String highlight) {
		if (has(highlight)) {
			highlights.remove(get(highlight).get());
			dirty = true;
			sort();
			return true;
		}
		return false;
	}

	private void sort() {
		Collections.sort(highlights);
	}

	public boolean has(String highlight) {
		if (highlights == null) {
			highlights = new ArrayList<>();
		}

		return get(highlight).isPresent();
	}

	public Optional<Highlight> get(String highlight) {
		return highlights.stream().filter(_highlight -> {
			return _highlight.get().equalsIgnoreCase(highlight);
		}).findFirst();
	}

	public void clear() {
		highlights = new ArrayList<>();
		dirty = true;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
