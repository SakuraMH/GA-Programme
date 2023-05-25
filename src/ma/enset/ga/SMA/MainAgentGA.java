package ma.enset.ga.SMA;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainAgentGA extends Agent {
    List<AgentFitness> agentsFitness=new ArrayList<>();
    Random rnd=new Random();
    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setType("ga");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] agentsDescriptions = DFService.search(this, dfAgentDescription);
            System.out.println(agentsDescriptions.length);
            for (DFAgentDescription dfAD:agentsDescriptions) {
                agentsFitness.add(new AgentFitness(dfAD.getName(),0));
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        calculateFintness();
        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new Behaviour()
        {
            int cpt=0;
            @Override
            public void action() {
                ACLMessage receivedMSG = receive();
                if (receivedMSG!=null){
                    cpt++;
                    System.out.println(cpt);
                    int fintess=Integer.parseInt(receivedMSG.getContent());
                    AID sender=receivedMSG.getSender();
                    //System.out.println(sender.getName()+" "+fintess);
                    setAgentFintess(sender,fintess);
                    if(cpt==GAUtils.POPULATION_SIZE){
                        Collections.sort(agentsFitness,Collections.reverseOrder());
                        showPopulation();
                    }
                }else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return  cpt==GAUtils.POPULATION_SIZE;
            }

        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int it=0;
            AgentFitness agent1;
            AgentFitness agent2;
            @Override
            public void action() {
                selection();
                crossover();
                Collections.sort(agentsFitness,Collections.reverseOrder());
                sendMessage(agentsFitness.get(0).getAid(),"chromosome",ACLMessage.REQUEST);
                ACLMessage aclMessage=blockingReceive();
                System.out.println(it+" "+aclMessage.getContent()+" : "+agentsFitness.get(0).getFitness());
                it++;
            }
            private void selection(){
                //System.out.println("***** Selection ****");
                agent1=agentsFitness.get(0);
                agent2=agentsFitness.get(1);
                sendMessage(agent1.getAid(),"chromosome",ACLMessage.REQUEST);
                sendMessage(agent2.getAid(),"chromosome",ACLMessage.REQUEST);
            }
            private void crossover(){
                ACLMessage aclMessage1=blockingReceive();
                ACLMessage aclMessage2=blockingReceive();

                int pointCroisment=rnd.nextInt(GAUtils.Max_Fitness-2);
                //System.out.println(pointCroisment);
                pointCroisment++;
                char []chromosomParent1= aclMessage1.getContent().toCharArray();
                char []chromosomParent2=aclMessage2.getContent().toCharArray();
                char []chromosomOffstring1=new char[GAUtils.Max_Fitness];
                char [] chromosomOffstring2=new char[GAUtils.Max_Fitness];
                for (int i=0;i<chromosomParent1.length;i++) {
                    chromosomOffstring1[i]=chromosomParent1[i];
                    chromosomOffstring2[i]=chromosomParent2[i];
                }
                for (int i=0;i<pointCroisment;i++) {
                    chromosomOffstring1[i]=chromosomParent2[i];
                    chromosomOffstring2[i]=chromosomParent1[i];
                }

                int fitness=0;
                for (int i=0;i<GAUtils.Max_Fitness;i++) {
                    if(chromosomOffstring1[i]==GAUtils.SOLUTION.charAt(i))
                        fitness+=1;
                }
                agentsFitness.get(GAUtils.POPULATION_SIZE-2).setFitness(fitness);

                fitness=0;
                for (int i=0;i<GAUtils.Max_Fitness;i++) {
                    if(chromosomOffstring2[i]==GAUtils.SOLUTION.charAt(i))
                        fitness+=1;
                }
                agentsFitness.get(GAUtils.POPULATION_SIZE-1).setFitness(fitness);




                sendMessage(agentsFitness.get(GAUtils.POPULATION_SIZE-2).getAid(),new String(chromosomOffstring1),ACLMessage.REQUEST);

                sendMessage(agentsFitness.get(GAUtils.POPULATION_SIZE-1).getAid(),new String(chromosomOffstring2),ACLMessage.REQUEST);

                ACLMessage receivedMsg1=blockingReceive();
                ACLMessage receivedMsg2=blockingReceive();
                setAgentFintess(receivedMsg1.getSender(),Integer.parseInt(receivedMsg1.getContent()));
                setAgentFintess(receivedMsg2.getSender(),Integer.parseInt(receivedMsg2.getContent()));

            }
            @Override
            public boolean done() {
                return it==GAUtils.MAX_IT || agentsFitness.get(0).getFitness()==GAUtils.Max_Fitness;
            }
        });
        addBehaviour(sequentialBehaviour);

    }
private void calculateFintness(){
    ACLMessage message=new ACLMessage(ACLMessage.REQUEST);

    for (AgentFitness agf:agentsFitness) {
        message.addReceiver(agf.getAid());
    }
    message.setContent("fitness");
    send(message);

}
private void setAgentFintess(AID aid,int fitness){
        for (int i=0;i<GAUtils.POPULATION_SIZE;i++){
            if(agentsFitness.get(i).getAid().equals(aid)){
                agentsFitness.get(i).setFitness(fitness);
                //System.out.println(fitness+"=:="+agentsFitness.get(i).getFitness());
                break;
            }
        }
}
private void sendMessage(AID aid,String content,int performative){
        ACLMessage message=new ACLMessage(performative);
        message.setContent(content);
        message.addReceiver(aid);
        send(message);

}
private void showPopulation(){
    for (AgentFitness agentFitness:agentsFitness) {
        System.out.println(agentFitness.getAid().getName()+" "+agentFitness.getFitness());
    }
}
}
