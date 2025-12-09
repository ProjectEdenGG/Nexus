package gg.projecteden.nexus.utils;

import java.util.List;
import java.util.NoSuchElementException;

public class CircularListIterator<T> {
    private List<T> list;
    private int currentIndex;

    public CircularListIterator(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty.");
        }
        this.list = list;
        this.currentIndex = 0;
    }

    public T next() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        T element = list.get(currentIndex);
        currentIndex = (currentIndex + 1) % list.size();
        return element;
    }

    public T previous() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        currentIndex = (currentIndex - 1 + list.size()) % list.size();
        T element = list.get(currentIndex);
        return element;
    }

    public void setStartIndex(int startIndex) {
        if (startIndex < 0 || startIndex >= list.size()) {
            throw new IllegalArgumentException("Invalid start index.");
        }
        this.currentIndex = startIndex;
    }
}
