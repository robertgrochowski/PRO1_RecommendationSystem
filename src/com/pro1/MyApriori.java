package com.pro1;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

public class MyApriori implements Observer, Callable<Void> {

    String datasetFilename;

    //< Product, Occurs>
    private Map<Integer, Integer> topProducts;
    private Set<Set<Integer>> frequentItemSets;
    private Set<Rule> rules;
    private List<Set<Integer>> transactionsSet = new ArrayList<>();

    private long totalProducts = 0;
    private long totalTransactions = 0;
    private double minSupport = 0.4;
    private double minConfidence = 0.8;

    MyApriori(String filename) throws IOException, NumberFormatException {
        this.datasetFilename = filename;
        this.frequentItemSets = new HashSet<>();
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


    @Override
    public Void call() throws Exception
    {
        this.rules = new HashSet<>();
        this.frequentItemSets.clear();

        new Apriori(new String[] {datasetFilename, minSupport+""}, this);

        for (Set<Integer> frequentItemset : frequentItemSets) {
            if(frequentItemset.size() <= 1)
                continue;

            rules.addAll(GetRules(frequentItemset));
        }
        return null;
    }

    void setVariables(double minSupport, double minConfidence) throws InvalidArgumentException
    {
        if(minSupport <= 0 || minSupport >= 1)
            throw new InvalidArgumentException(new String[]{"Invalid value for minimum support"});
        if(minConfidence <= 0 || minConfidence >= 1)
            throw new InvalidArgumentException(new String[]{"Invalid value for minimum confidence"});

        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
    }

    @Override
    public void update(Observable o, Object arg) {
        int[] itemSet = (int[]) arg;
        Set<Integer> frequentItemset = new HashSet<>();
        for (int item : itemSet)
            frequentItemset.add(item);

        frequentItemSets.add(frequentItemset);
    }

    private Set<Rule> GetRules(Set<Integer> frequentItemset)
    {
        Set<Rule> output = new HashSet<>();
        Set<Rule> prunedRules = new HashSet<>();
        Rule rootRule = new Rule(frequentItemset, new HashSet<>());

        Set<Rule> rulesToCheck = Permutation.getNextRulesTreeLevel(rootRule, prunedRules);
        while (rulesToCheck.size() > 0) {
            Rule candidate = rulesToCheck.iterator().next();
            rulesToCheck.remove(candidate);

            if (countConfidence(candidate, transactionsSet) >= minConfidence) {
                output.add(candidate);

                if (candidate.getAntecedent().size() > 1) {
                    Set<Rule> children = Permutation.getNextRulesTreeLevel(candidate, prunedRules);
                    rulesToCheck.addAll(children);
                }
            } else {
                prunedRules.add(candidate);
            }
        }
        return output;
    }

    private double countConfidence(Rule rule, List<Set<Integer>> transactionsSet) {
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

    public Set<Set<Integer>> getFrequentItemSets() {
        return frequentItemSets;
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public void printResults() {
        System.out.println("--- Frequent Itemsets ---");
        frequentItemSets.forEach(System.out::println);
        System.out.println("--- Rules ---");
        rules.forEach(System.out::println);
    }
}
