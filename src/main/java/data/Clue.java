package data;

import java.util.List;

public record Clue(String clue, int count, List<Integer> refers, int risk) {}