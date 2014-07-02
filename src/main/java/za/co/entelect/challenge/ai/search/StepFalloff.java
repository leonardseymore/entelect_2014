package za.co.entelect.challenge.ai.search;

public class StepFalloff implements Falloff {

    private int maxDist;

    public StepFalloff(int maxDist) {
        this.maxDist = maxDist;
    }

    @Override
    public float get(double dist) {
        if (dist > maxDist) {
            return 0;
        }
        return (float) (1 - dist / (float) maxDist);
    }
}
