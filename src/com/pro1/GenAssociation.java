package com.pro1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GenAssociation {
    public static void main(String[] args) throws IOException {

        HashSet<Integer> set = new HashSet<>(Arrays.asList(0,1,2,3));
        HashSet<Integer> set2 = new HashSet<>(Arrays.asList(1,2,3));

        System.out.println(set.containsAll(set2));

    }


}
