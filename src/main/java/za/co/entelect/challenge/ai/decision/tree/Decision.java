package za.co.entelect.challenge.ai.decision.tree;

public abstract class Decision<T> {

    protected Decision trueNode;
    protected Decision falseNode;
    protected T testValue;

    public abstract Decision getBranch();

    public Decision makeDecision() {
        Decision branch = getBranch();
        return branch.makeDecision();
    }
}
