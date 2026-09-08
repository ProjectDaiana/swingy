package com.swingy.view;

import java.util.List;

public record MapState(int size, int heroX, int heroY, List<int[]> villainPositions) {}
