package com.binary_studio.dependency_detector;

import java.util.*;
import java.util.stream.Collectors;

public final class DependencyDetector {

	private DependencyDetector() {
	}

	public static boolean canBuild(DependencyList libraries) {
		for (String library : libraries.libraries) {
			Set<String> visited = new HashSet<>();
			Queue<String> dependenciesQueue = new ArrayDeque<>();
			dependenciesQueue.add(library);
			while (dependenciesQueue.peek() != null) {
				String index = dependenciesQueue.poll();
				if (visited.contains(index)) {
					return false;
				}
				visited.add(index);

				dependenciesQueue.addAll(libraries.dependencies.stream().filter(d -> Objects.equals(index, d[0]))
						.map(d -> d[1]).collect(Collectors.toSet()));
			}
		}
		return true;
	}

}
