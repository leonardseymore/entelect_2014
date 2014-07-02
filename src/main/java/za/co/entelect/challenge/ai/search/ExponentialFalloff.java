package za.co.entelect.challenge.ai.search;

public class ExponentialFalloff implements Falloff {
    @Override
    public float get(double dist) {
        return 1 / (float) (dist + 1);
    }
}
