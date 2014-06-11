package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;

public interface SearchCriteria {
    boolean matches(GameState gameState, SearchNode node);
}
