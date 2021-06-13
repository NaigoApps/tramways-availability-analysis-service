package it.tramways.analysis.roadmap;

import java.util.List;

public interface Node<T extends Arc> {
	List<T> getSources();
	List<T> getDestinations();
}
