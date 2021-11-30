import com.opencsv.CSVWriter;
import com.sun.javafx.binding.StringFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Particles {
    private List<Swarm> listOfSwarms;
    private double globalBest;
    private double min = Integer.MAX_VALUE;
    private double max = Integer.MIN_VALUE;


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
        DataFormatter dataFormatter = new DataFormatter();

        try {
            File file = new File(fileLocation);
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            if (itr.hasNext()) {
                Row row = itr.next();
            }
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
                int counter = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (counter == 4) {
                        Swarm swarm = new Swarm();
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            swarm.setX(cell.getNumericCellValue());
                        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            swarm.setX(Double.parseDouble(cell.getStringCellValue()));
                        }

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

    public void getMinAndMax() {
        for (int i = 0; i < listOfSwarms.size(); i++) {
            if (listOfSwarms.get(i).getX() < min) {
                min = listOfSwarms.get(i).getX();
            }

            if (listOfSwarms.get(i).getX() > max) {
                max = listOfSwarms.get(i).getX();
            }
        }

        min = min - Math.random();
        max = max + Math.random();
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

            for (int i = 0; i < 10000; i++) {

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
                    swarm.setVelocity(Math.random() * swarm.getVelocity() * (closeSwarm.getX() - swarm.getX()) * (globalBest - swarm.getX()));

//                    double newSwarmPosition = Math.round((swarm.getX() + swarm.getVelocity()) * 1000) / 1000;
                    double newSwarmPosition = swarm.getX() + swarm.getVelocity();
                    if (newSwarmPosition >= min && newSwarmPosition <= max) {
                        swarm.setX(newSwarmPosition);
                        double newStandardDeviation = calculateSD();
                        if (newStandardDeviation < standardDeviation) {
                            swarm.setBestKnownX(swarm.getX());
                            calculateGlobalBest();
                        }
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

    public double calculateStandardDeviationGlobalBest() {
        double sum = 0;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            sum += listOfSwarms.get(i).getBestKnownX();
        }

        double mean = sum / listOfSwarms.size();

        double deviation = 0;
        for (int i = 0; i < listOfSwarms.size(); i++) {
            deviation += (listOfSwarms.get(i).getBestKnownX() - mean) * (listOfSwarms.get(i).getBestKnownX() - mean);
        }
        double stantdardDeviation = Math.sqrt(deviation / listOfSwarms.size());
        return stantdardDeviation;

    }

    public void showList() {
        try {
            System.out.println("Size ::  " + listOfSwarms.size());
            for (int i = 0; i < listOfSwarms.size(); i++) {
                Swarm swarm = listOfSwarms.get(i);
                System.out.println("Ground Water Level:: " + swarm.getX() +
                        "  Best Known::  " + swarm.getBestKnownX());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeFile() {

        File file = new File("output.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = {"Predicted Ground Water Level"};
            writer.writeNext(header);

            List<String[]> data1 = new ArrayList<String[]>();
            for (int i = 0; i < listOfSwarms.size(); i++) {
                Swarm swarm = listOfSwarms.get(i);
                data1.add(new String[]{Double.toString(swarm.getBestKnownX())});
            }
            // add data to csv

            writer.writeAll((List<String[]>) data1);

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
