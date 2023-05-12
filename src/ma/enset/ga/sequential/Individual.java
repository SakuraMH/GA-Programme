package ma.enset.ga.sequential;

import java.util.Random;

public class Individual implements Comparable{
    //Bonjour
    //chromosome
    private char genes[]=new char[7];
    private int fitness;

    public Individual() {
        Random rnd=new Random();
        for (int i=0;i<genes.length;i++){
            Random rand = new Random();
            char c = (char)(rand.nextInt(26) + 97);
            //System.out.println(c);
            genes[i]= c;
        }
    }
    public void calculateFitness(){
        fitness=0;
        char str[]={'b','o','n','j','o','u','r'};
        int  i=0;
        for (int gene:genes) {
                if(gene==str[i]){
                    fitness=fitness+1;
                }
                i++;
        }
    }

    public int getFitness() {
        return fitness;
    }

    public char[] getGenes() {
        return genes;
    }

    @Override
    public int compareTo(Object o) {
        Individual individual=(Individual) o;
        if (this.fitness>individual.fitness)
            return 1;
        else if(this.fitness<individual.fitness){
            return -1;
        }else
            return 0;
    }
}
