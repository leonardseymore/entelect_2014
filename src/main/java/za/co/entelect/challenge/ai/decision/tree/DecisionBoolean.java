package za.co.entelect.challenge.ai.decision.tree;

public class DecisionBoolean extends Decision<Boolean> {
    public Decision getBranch() {
        return testValue ? trueNode : falseNode;
    }
}
