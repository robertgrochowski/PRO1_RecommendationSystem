package com.pro1;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class Permutation {

    //https://www.geeksforgeeks.org/print-subsets-given-size-set/
    private Permutation() { }

    private Set<Set<Integer>> combinations = new HashSet<>();
    private void combinationUtil(Integer arr[], int n, int r,
                                int index, int data[], int i)
    {
        // Current combination is ready to be printed,
        // print it
        if (index == r)
        {
            Set<Integer> comb = new HashSet<>();
            for (int j = 0; j < r; j++)
            {
                //System.out.print(data[j] + " ");
                comb.add(data[j]);
            }
            //System.out.println();
            combinations.add(comb);
            return;
        }

        // When no more elements are there to put in data[]
        if (i >= n)
            return;

        // current is included, put next at next
        // location
        data[index] = arr[i];
        combinationUtil(arr, n, r, index + 1,
                data, i + 1);

        // current is excluded, replace it with
        // next (Note that i+1 is passed, but
        // index is not changed)
        combinationUtil(arr, n, r, index, data, i + 1);
    }

    // The main function that prints all combinations
    // of size r in arr[] of size n. This function
    // mainly uses combinationUtil()
    private void getCombination(Set<Integer> set, int n, int r)
    {
        // A temporary array to store all combination
        // one by one
        int data[] = new int[r];

        // Print all combination using temprary
        // array 'data[]
        Integer[] setArray = new Integer[set.size()];
        set.toArray(setArray);

        combinationUtil(setArray, n, r, 0, data, 0);
        //System.out.println();
    }

    public static Set<Rule> getNextRulesTreeLevel(Rule rule, Set<Rule> ignoreItems)
    {
        Permutation p = new Permutation();
        p.getCombination(rule.getAntecedent(), rule.getAntecedent().size(),rule.getAntecedent().size()-1);

        Set<Rule> ruleSet = new LinkedHashSet<>();

        for (Set<Integer> items : p.combinations) {
            boolean ignore = false;

            for (Rule ignoreRule : ignoreItems) {
                if (ignoreRule.getAntecedent().containsAll(items)) {
                    ignore = true;
                    break;
                }
            }
            if(ignore) continue;

            Set<Integer> antecedent = new HashSet<>(items);
            Set<Integer> consequence = rule.getElements();
            consequence.removeAll(antecedent);

            ruleSet.add(new Rule(antecedent, consequence));
        }
        return ruleSet;
    }
}
