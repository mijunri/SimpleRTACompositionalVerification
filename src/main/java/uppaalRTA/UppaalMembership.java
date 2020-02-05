package uppaalRTA;


import learning.Membership;
import rta.Location;
import rta.RTA;
import rta.TimeGuard;
import rta.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UppaalMembership implements Membership {
    private RTA m1;
    private RTA m2;
    private RTA property;
    private Set<String> sigma;
    public UppaalMembership(RTA m1, RTA m2, RTA property){
        this.m1 = m1;
        this.m2 = m2;
        this.property = property;
        sigma = new HashSet<>();
        sigma.addAll(m1.getSigma());
        sigma.addAll(property.getSigma());
        sigma.retainAll(m2.getSigma());
    }

    @Override
    public boolean answer(TimeWords words) {
        RTA traceRTA = traceRTA(words);
        return answer(traceRTA);
    }

    private boolean answer(RTA traceRTA){
        UppaalNTA1 uppaalNTA1 = new UppaalNTA1(m1,traceRTA,property);
        uppaalNTA1.toXml();
        uppaalNTA1.generateMemProperty();
        uppaalNTA1.toBat();
        return UppaalCheck.isSatisFied();
    }

    private RTA traceRTA(TimeWords words){
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();
        List<TimeWord> wordList = words.getWordList();
        for(int i = 0; i <= wordList.size(); i ++){
            Location location = new Location(i+1,"trace"+(i+1),false,false);
            if(i==0){
                location.setInit(true);
            }
            locationList.add(location);
        }
        for(int i = 0; i < wordList.size(); i ++){
            TimeWord word = wordList.get(i);
            TimeGuard timeGuard = new TimeGuard(word);
            String action = word.getAction();
            Transition transition = new Transition(locationList.get(i),locationList.get(i+1),timeGuard,action);
            transitionList.add(transition);
        }

        return new RTA("trace",sigma,locationList,transitionList);
    }
}
