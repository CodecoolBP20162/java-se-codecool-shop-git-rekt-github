package com.codecool.shop.dbconnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This class is responsible for reading and parsing database authentication data.
 */
public class DBPassword {

    /**
     * Reads target text file which contains the password.
     * @return Parsed data for database authentication
     */
    public static ArrayList<String> readFile() {
        String filename = "db_config.txt";
        ArrayList<String> records = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;

        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }
}

