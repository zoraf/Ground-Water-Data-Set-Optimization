import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Particles {
    private List<Swarm> listOfSwarms;
    private double globalBest;

    public Particles() {
        this.listOfSwarms = new ArrayList<Swarm>();
    }

    public Particles(List<Swarm> listOfSwarms) {
        this.listOfSwarms = listOfSwarms;
    }


    public List<Swarm> getListOfSwarms() {
        return listOfSwarms;
    }

    public void setListOfSwarms(List<Swarm> listOfSwarms) {
        this.listOfSwarms = listOfSwarms;
    }


    public double getGlobalBest() {
        return globalBest;
    }

    public void setGlobalBest(double globalBest) {
        this.globalBest = globalBest;
    }

    public void populateList(String fileLocation) {
        try {
            File file = new File(fileLocation);
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
                int counter = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (counter == 5) {
                        Swarm swarm = new Swarm();
                        swarm.setX(Double.parseDouble(cell.getStringCellValue()));
                        swarm.setY(Utils.Y);
                        swarm.setBestKnownX(swarm.getX());
                        swarm.setVelocity(swarm.getX());
                        listOfSwarms.add(swarm);
                    }
                    counter++;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateGlobalBest() {
        double sum = 0;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            sum += listOfSwarms.get(i).getX();
        }
        double average = sum / listOfSwarms.size();
        double difference = Utils.DIF_MAX;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            if (Math.abs(average - listOfSwarms.get(i).getX()) < difference) {
                difference = Math.abs(average - listOfSwarms.get(i).getX());
                globalBest = listOfSwarms.get(i).getX();
            }
        }
    }

    public Swarm getLocalBest(List<Swarm> listOfClosestSwarms) {
        double sum = 0;
        Swarm closeSwarm = new Swarm();
        for (int i = 0; i < listOfSwarms.size(); i++) {
            sum += listOfSwarms.get(i).getX();
        }
        double average = sum / listOfSwarms.size();
        double difference = Utils.DIF_MAX;
        for (int i = 0; i < listOfClosestSwarms.size(); i++) {
            if (Math.abs(average - listOfClosestSwarms.get(i).getX()) < difference) {
                difference = Math.abs(average - listOfClosestSwarms.get(i).getX());
                closeSwarm = listOfClosestSwarms.get(i);
            }
        }

        return closeSwarm;
    }

    public void runPSOOptimization() {
        try {

            for (int i = 0; i < 500; i++) {

                for (int k = 0; k < listOfSwarms.size(); k++) {

                    Swarm swarm = listOfSwarms.get(k);
                    double standardDeviation = calculateSD();
                    List<Swarm> listOfClosestSwarms = new ArrayList<Swarm>();
                    for (int j = 0; j < listOfSwarms.size(); j++) {
                        if (j == k) continue;
                        else {
                            Swarm swarm2 = listOfSwarms.get(j);
                            double distance = calculateAbsDistance(swarm, swarm2);
                            if (distance < standardDeviation) {
                                listOfClosestSwarms.add(swarm2);
                            }
                        }
                    }
                    Swarm closeSwarm = getLocalBest(listOfClosestSwarms);
                    swarm.setVelocity( Math.random() * swarm.getVelocity() * (closeSwarm.getX() - swarm.getX())  * (globalBest - swarm.getX()));
                    swarm.setX((swarm.getX() +  swarm.getVelocity()));

                    double newStandardDeviation = calculateSD();
                    if (newStandardDeviation < standardDeviation) {
                        swarm.setBestKnownX(swarm.getX());
                        calculateGlobalBest();
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double calculateAbsDistance(Swarm a, Swarm b) {
        double distance = Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()));
        return distance;
    }

    public double calculateDistance(Swarm a, Swarm b) {
        double distance = a.getX() - b.getX();
        return distance;
    }

    public double calculateSD() {
        double sum = 0;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            sum += listOfSwarms.get(i).getX();
        }

        double mean = sum / listOfSwarms.size();

        double deviation = 0;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            deviation += (listOfSwarms.get(i).getX() - mean) * (listOfSwarms.get(i).getX() - mean);
        }
        double stantdardDeviation = Math.sqrt(deviation / listOfSwarms.size());
        return stantdardDeviation;
    }

    public void showList() {
        try {
            System.out.println("Size ::  " + listOfSwarms.size());
            for (int i = 0; i < listOfSwarms.size(); i++) {
                Swarm swarm = listOfSwarms.get(i);
                System.out.println("X:: " + swarm.getX() +
                         "  Best Known::  " + swarm.getBestKnownX());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
