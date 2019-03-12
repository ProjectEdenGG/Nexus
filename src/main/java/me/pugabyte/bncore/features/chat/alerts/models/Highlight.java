package me.pugabyte.bncore.features.chat.alerts.models;

public class Highlight implements Comparable<Highlight> {
	private String highlight;
	private boolean partialMatching = true;

	public Highlight(String highlight) {
		this.highlight = highlight;
	}

	public Highlight(String highlight, boolean partialMatching) {
		this.highlight = highlight;
		this.partialMatching = partialMatching;
	}

	public String get() {
		return highlight;
	}

	public void set(String highlight) {
		this.highlight = highlight;
	}

	public boolean isPartialMatching() {
		return partialMatching;
	}

	public void setPartialMatching(boolean partialMatching) {
		this.partialMatching = partialMatching;
	}

	@Override
	public int compareTo(Highlight other) {
		return highlight.compareTo(other.get());
	}
}
