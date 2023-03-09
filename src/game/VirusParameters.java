package game;

public class VirusParameters {
    final int transmissionPercent;
    final int lifetimeInDays;
    final int transmissionRange;
    final int lethality;

    /**
     * Virus parameters
     *
     * @param transmissionPercent Percentage of transmissibility
     * @param lifetimeInDays      Life-time in a host until get immunity
     */
    public VirusParameters(int transmissionPercent, int lifetimeInDays, int transmissionRange, int lethality) {
        this.transmissionPercent = transmissionPercent;
        this.lifetimeInDays = lifetimeInDays;
        this.transmissionRange = transmissionRange;
        this.lethality = lethality;
    }

}
