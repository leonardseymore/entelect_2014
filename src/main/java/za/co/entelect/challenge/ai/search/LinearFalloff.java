package za.co.entelect.challenge.ai.search;

public class LinearFalloff implements Falloff {

    private float strength;

    public LinearFalloff() {
        this(0.9f);
    }

    public LinearFalloff(float strength) {
        this.strength = strength;
    }

    @Override
    public float get(double dist) {
        return (float)Math.pow(0.9, dist);
    }
}
