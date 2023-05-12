package ma.enset.ga.sequential;

import java.util.*;

public class Population {

    List<Individual> individuals=new ArrayList<>();
    Individual firstFitness;
    Individual secondFitness;
    Random rnd=new Random();
    public void initialaizePopulation(){
        for (int i=0;i<30;i++){
           individuals.add(new Individual());
        }
    }
    public void calculateIndFintess(){
        for (int i=0;i<30;i++){
            individuals.get(i).calculateFitness();
        }

    }
    public void selection(){
        firstFitness=individuals.get(0);
        secondFitness=individuals.get(1);
    }
    //croisement
    public void crossover(){

        int pointCroisment=rnd.nextInt(6);
        pointCroisment++;
        Individual individual1=new Individual();
        Individual individual2=new Individual();
        for (int i=0;i<individual1.getGenes().length;i++) {
            individual1.getGenes()[i]=firstFitness.getGenes()[i];
            individual2.getGenes()[i]=secondFitness.getGenes()[i];
        }
        for (int i=0;i<pointCroisment;i++) {
            individual1.getGenes()[i]=secondFitness.getGenes()[i];
            individual2.getGenes()[i]=firstFitness.getGenes()[i];
        }
        System.out.println(Arrays.toString(individual1.getGenes()));
        System.out.println(Arrays.toString(individual2.getGenes()));

        individuals.set(individuals.size()-2,individual1);
        individuals.set(individuals.size()-1,individual2);
    }
    public void mutation(){
       int index=rnd.nextInt(7);
        Random rand = new Random();
        char c = (char)(rand.nextInt(26) + 97);
       if(individuals.get(individuals.size()-2).getGenes()[index]==c){
           Random rand1 = new Random();
           char c1 = (char)(rand1.nextInt(26) + 97);
           individuals.get(individuals.size()-2).getGenes()[index]=c1;
       }else{
           individuals.get(individuals.size()-2).getGenes()[index]=c;
       }
        index=rnd.nextInt(7);
        Random rand2 = new Random();
        char c2 = (char)(rand2.nextInt(26) + 97);
        if(individuals.get(individuals.size()-1).getGenes()[index]==c2){
            Random rand3 = new Random();
            char c3 = (char)(rand3.nextInt(26) + 97);
            individuals.get(individuals.size()-1).getGenes()[index]=c3;
        }else{
            individuals.get(individuals.size()-1).getGenes()[index]=c2;
        }
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }
    public void sortPopulation(){
        Collections.sort(individuals,Collections.reverseOrder());
    }
    public Individual getFitnessIndivd(){
        return individuals.get(0);
    }
}
