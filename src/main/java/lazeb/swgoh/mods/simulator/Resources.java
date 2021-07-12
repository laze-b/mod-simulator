package lazeb.swgoh.mods.simulator;

class Resources {

    private final long dailyEnergyBudget;
    private final long dailyCreditBudget;

    private long energyBalance;
    private long creditBalance;

    Resources(long dailyEnergyBudget, long dailyCreditBudget) {
        this.dailyEnergyBudget = dailyEnergyBudget;
        this.dailyCreditBudget = dailyCreditBudget;
    }

    long getEnergyBalance() {
        return energyBalance;
    }

    long getCreditBalance() {
        return creditBalance;
    }

    void addDailyResources() {
        energyBalance += dailyEnergyBudget;
        creditBalance += dailyCreditBudget;
    }

    void substractModInvestment(Mod mod, boolean keep) {
        energyBalance -= mod.getEnergySpent();
        creditBalance -= mod.getCreditsSpent();
        if(!keep) {
            creditBalance += mod.creditValueIfSold();
        }
    }

    @Override
    public String toString() {
        return "Resources{" +
                "dailyEnergyBudget=" + dailyEnergyBudget +
                ", dailyCreditBudget=" + dailyCreditBudget +
                ", energyBalance=" + energyBalance +
                ", creditBalance=" + creditBalance +
                '}';
    }
}
