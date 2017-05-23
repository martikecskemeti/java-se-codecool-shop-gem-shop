package com.codecool.shop.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Generates data for database connection.
 */

public class DatabaseConnectionData {

    private String filePath = "src/main/resources/";
    private String DB_URL;
    private String DB_NAME;
    private String DB_USER;
    private String DB_PASSWORD;

    public DatabaseConnectionData(String filePath){
        try {
            setupUserAndPasswordFromFile(filePath);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public String getDb() {
        return "jdbc:postgresql://" + DB_URL + "/" + DB_NAME;
    }

    public String getDbUser(){
        return DB_USER;
    }

    public String getDbPassword(){
        return DB_PASSWORD;
    }

    private void setupUserAndPasswordFromFile(String fileName) throws IOException{
        List<String> allLinesList = Files.readAllLines(Paths.get(filePath + fileName));
        DB_URL = allLinesList.get(0);
        DB_NAME = allLinesList.get(1);
        DB_USER = allLinesList.get(2);
        DB_PASSWORD = allLinesList.get(3);
    }
}

