package com.pro1;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Rule {

    Set<Integer> antecedent;
    Set<Integer> consequent;

    public Rule(){
        antecedent = new HashSet<>();
        consequent = new HashSet<>();
    }

    public Rule(Set<Integer> antecedent, Set<Integer> consequent) {
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    public Set<Integer> getAntecedent() {
        return antecedent;
    }

    public Set<Integer> getConsequent() {
        return consequent;
    }

    public Set<Integer> getElements(){
        Set<Integer> elements = new HashSet<>(antecedent);
        elements.addAll(consequent);
        return elements;
    }

    @Override
    public String toString() {
        return getAntecedent() + "=>" + getConsequent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(antecedent, consequent);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Rule))
            return false;

        Rule ruleObj = (Rule) obj;
        return ruleObj.getAntecedent().equals(getAntecedent()) && ruleObj.getConsequent().equals(getConsequent());

    }
}
