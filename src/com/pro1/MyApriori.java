package com.pro1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyApriori implements Observer {

    String datasetFilename;

    // < Product, Occurs>
    private Map<Integer, Integer> topProducts;
    private List<int[]> frequentItemSets;
    private long totalProducts = 0;
    private long totalTransactions = 0;

    List<Set<Integer>> transactionsSet = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new MyApriori("res/retail.me.txt").compute(0.4, 0.8);
    }

    MyApriori(String filename) throws IOException {
        datasetFilename = filename;
        frequentItemSets = new LinkedList<>();
        Map<Integer, Integer> productsOccurs = new LinkedHashMap<>();

        String line;
        BufferedReader bf = new BufferedReader(new FileReader(filename));

        while((line = bf.readLine()) != null)
        {
            Set<Integer> transaction = new HashSet<>();
            String[] lineProducts = line.split(" ");
            for (String product : lineProducts) {
                int productCode = Integer.parseInt(product);

                transaction.add(productCode);
                productsOccurs.putIfAbsent(productCode, 0);
                productsOccurs.put(productCode, productsOccurs.get(productCode) + 1);
            }
            transactionsSet.add(transaction);
            totalTransactions++;
        }

        totalProducts = productsOccurs.size();
        topProducts = new LinkedHashMap<>();
        productsOccurs.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEachOrdered(x -> topProducts.put(x.getKey(), x.getValue()));
    }


    public void compute(double support, double confidence) throws Exception {
        Apriori ap = new Apriori(new String[] {datasetFilename, support+""}, this);

        Set<Integer> frequentItemset = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Rule> rules = GetRules(frequentItemset, 0.8);

        for (Rule rule : rules) {
            System.out.println(rule);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        int[] itemSet = (int[]) arg;
        frequentItemSets.add(itemSet);

        // Associative rules
        if (itemSet.length > 1) {
            //
        }
    }

    public Set<Rule> GetRules(Set<Integer> frequentItemset, double minConf) {
        Set<Rule> output = new HashSet<>();
        Set<Integer> prunedRules = new HashSet<>();

        //Create empty rule from frequentItemset
        Rule rootRule = new Rule(frequentItemset, new HashSet<>());

        Set<Rule> rulesToCheck = Permutation.getNextRulesTreeLevel(rootRule, prunedRules);
        while (rulesToCheck.size() > 0) {
            Rule candidate = rulesToCheck.iterator().next();
            rulesToCheck.remove(candidate);

            System.out.println("checking "+candidate);
            double confidence = countConfidence(candidate, transactionsSet);
            if (confidence >= minConf) {
                output.add(candidate);
                System.out.println("OK!: conf"+confidence);

                if(candidate.getAntecedent().size() > 1) {
                    Set<Rule> children = Permutation.getNextRulesTreeLevel(candidate, prunedRules);
                    rulesToCheck.addAll(children);
                }
            }
            else
            {
                prunedRules.addAll(candidate.getAntecedent()); //TODO
                System.out.println("conf: "+confidence+" add to prune rules: "+candidate.getAntecedent());
            }
        }

        return output;
    }

    public double countConfidence(Rule rule, List<Set<Integer>> transactionsSet) {
        Set<Integer> sum = new HashSet<>();
        sum.addAll(rule.getAntecedent());
        sum.addAll(rule.getConsequent());

        double sumOccurs = 0;
        double antecedentOccurs = 0;

        for (Set<Integer> transaction : transactionsSet) {
            if (transaction.containsAll(sum))
                sumOccurs++;

            if(transaction.containsAll(rule.getAntecedent()))
                antecedentOccurs++;
        }
        return sumOccurs / antecedentOccurs;
    }

    public Map<Integer, Integer> getTopProducts() {
        return topProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }
}
