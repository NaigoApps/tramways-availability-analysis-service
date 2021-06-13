package it.tramways.analysis.availability.distributions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeightedDistribution<T> {
	private Map<T, Integer> function;

	public WeightedDistribution() {
		function = new HashMap<>();
	}

	public WeightedDistribution<T> set(T item, int value) {
		function.put(item, value);
		return this;
	}

	public WeightedDistribution<T> set(T item){
		return set(item, 1);
	}

	public WeightedDistribution<T> clear(){
		function.clear();
		return this;
	}

	public Set<T> getDomain(){
		return function.keySet();
	}

	public int get(T item) {
		return function.get(item);
	}

	public void fit(List<T> data) {
		function = new HashMap<>();
		for(T d : data) {
			Integer oldFrequency = function.putIfAbsent(d, 0);
			function.put(d, oldFrequency + 1);
		}
	}
}
