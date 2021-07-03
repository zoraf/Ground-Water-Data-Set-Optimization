public class PSO {

//    private static String dataFileLocation = "D:\\H\\MSc\\Meta Heuristic\\PSO\\Data 2018.xlsx";

    private static String dataFileLocation = "Data 2018.xlsx";
    public static void main(String[]args){
        Particles particles = new Particles();
        particles.populateList(dataFileLocation);
        particles.calculateGlobalBest();
        particles.showList();
        System.out.println("SD :: " + particles.calculateSD());
        System.out.println("GLobal Best :: " + particles.getGlobalBest());
        particles.runPSOOptimization();
        particles.showList();
        System.out.println("SD :: " + particles.calculateSD());
        System.out.println("GLobal Best :: " + particles.getGlobalBest());
    }
}
