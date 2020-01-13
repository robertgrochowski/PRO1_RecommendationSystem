package com.pro1;

import java.io.*;
import java.util.*;

public class FileConverter {



    public static void main(String[] args) throws IOException {

        List<String> allProducts = new ArrayList<>();

        BufferedReader bf = new BufferedReader(new FileReader("res/retail.dat.txt"));

        String line;
        //Catch all products
        while((line = bf.readLine()) != null)
        {
            String[] lineProducts = line.split(" ");
            for (int i = 0; i < lineProducts.length; i++) {
                if(!allProducts.contains(lineProducts[i]))
                    allProducts.add(lineProducts[i]);
            }
        }

        FileWriter writer = new FileWriter("app.log");
        BufferedWriter bw = new BufferedWriter(writer, 32768);

        for (int i = 0; i < allProducts.size(); i++) {
            bw.write(allProducts.get(i));
            if (i < allProducts.size() - 1) {
                bw.write(",");
            }
        }
        bw.write('\n');

        bf.close();

        bf = new BufferedReader(new FileReader("res/retail.dat.txt"));
        while((line = bf.readLine()) != null)
        {
            Set<String> productsOnLine = new HashSet<>(Arrays.asList(line.split(" ")));

            for (int i = 0; i < allProducts.size(); i++) {
                if(productsOnLine.contains(allProducts.get(i)))
                    bw.write("yes");

                if(i < allProducts.size()-1)
                    bw.write(',');

            }
            bw.write('\n');
        }
        bw.flush();
        bf.close();
        bw.close();

    }
}
