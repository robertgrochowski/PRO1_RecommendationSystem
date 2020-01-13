package com.pro1;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class Permutation {

    //https://www.geeksforgeeks.org/print-subsets-given-size-set/
    /* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */

    private Permutation() {

    }

    private Set<Set<Integer>> combinations = new HashSet<>();
    private void combinationUtil(Integer arr[], int n, int r,
                                int index, int data[], int i, Set<Integer> ignoreItems)
    {
        // Current combination is ready to be printed,
        // print it
        if (index == r)
        {
            Set<Integer> comb = new HashSet<>();
            for (int j = 0; j < r; j++)
            {
                if(ignoreItems.contains(data[j])) //TODO!!
                    return;

                System.out.print(data[j] + " ");
                comb.add(data[j]);
            }
            System.out.println();
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
                data, i + 1, ignoreItems);

        // current is excluded, replace it with
        // next (Note that i+1 is passed, but
        // index is not changed)
        combinationUtil(arr, n, r, index, data, i + 1, ignoreItems);
    }

    // The main function that prints all combinations
    // of size r in arr[] of size n. This function
    // mainly uses combinationUtil()
    private void getCombination(Set<Integer> set, int n, int r, Set<Integer> ignoreItems)
    {
        // A temporary array to store all combination
        // one by one
        int data[] = new int[r];

        // Print all combination using temprary
        // array 'data[]
        Integer[] setArray = new Integer[set.size()];
        set.toArray(setArray);

        combinationUtil(setArray, n, r, 0, data, 0, ignoreItems);
        System.out.println();
    }

    public static Set<Rule> getNextRulesTreeLevel(Rule rule, Set<Integer> ignoreItems)
    {
        Permutation p = new Permutation();
        p.getCombination(rule.getAntecedent(), rule.getAntecedent().size(),rule.getAntecedent().size()-1, ignoreItems);

        Set<Rule> ruleSet = new LinkedHashSet<>();

        for (Set<Integer> items : p.combinations) {
            Set<Integer> antecedent = new HashSet<>(items);
            Set<Integer> consequence = rule.getElements();
            consequence.removeAll(antecedent);

            ruleSet.add(new Rule(antecedent, consequence));
        }
        return ruleSet;
    }

    /* Driver function to check for above function */
    public static void main(String[] args)
    {
        Set<Integer> antecedent = new HashSet<>();
        Set<Integer> consequence = new HashSet<>();

        antecedent.add(1); antecedent.add(2); antecedent.add(3);
        consequence.add(4);

        Rule rule = new Rule(antecedent, consequence);

        Permutation per = new Permutation();
        Set<Rule> rules = per.getNextRulesTreeLevel(rule, new HashSet<>());
        System.out.println();
    }
}
