public class PSO {

//    private static String dataFileLocation = "D:\\H\\MSc\\Meta Heuristic\\PSO\\Data 2018.xlsx";

    private static String dataFileLocation = "Data 2018.xlsx";
//    private static String dataFileLocation = "Nawabganj_all_upzila_timeseries.xlsx";

    public static void main(String[] args) {
        Particles particles = new Particles();
        particles.populateList(dataFileLocation);
        particles.calculateGlobalBest();
        particles.getMinAndMax();
        particles.showList();
        System.out.println("Standard Deviation Before PSO :: " + particles.calculateSD());

        particles.runPSOOptimization();
        particles.showList();
        particles.writeFile();
        System.out.println("Standard Deviation After PSO :: " + particles.calculateStandardDeviationGlobalBest());

    }
}
