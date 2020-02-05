package uppaalUtil;

import rta.Location;
import rta.RTA;
import rta.Transition;
import uppaal.*;

import java.util.*;

public class UppaalBuilder {
    public static Template buildAnTemplate(RTA rta){
        String name = rta.getName();
        String declaration = "clock x;";
        List<UppaalLocation> uppaalLocationList = new ArrayList<>();
        List<UppaalTransition> uppaalTransitionList = new ArrayList<>();
        UppaalLocation initLocation = null;

        Map<Location,UppaalLocation> uppaalLocationMap = new HashMap<>();
        for(Location location: rta.getLocationList()){
            UppaalLocation uppaalLocation = new UppaalLocation(String.valueOf(location.getId()),location.getName());
            uppaalLocationList.add(uppaalLocation);
            uppaalLocationMap.put(location,uppaalLocation);
            if(location == rta.getInitLocation()){
                initLocation = uppaalLocation;
            }
        }

        for(Transition transition: rta.getTransitionList()){
            UppaalLocation source = uppaalLocationMap.get(transition.getSourceLocation());
            UppaalLocation target = uppaalLocationMap.get(transition.getTargetLocation());
            Label synchronisation = new Label("synchronisation",transition.getAction());
            Label guard = new Label("kind",transition.getTimeGuard().toExpression());
            Label assignment = new Label("assignment","x:=0");
            UppaalTransition uppaalTransition = new UppaalTransition(source,target,synchronisation,guard,assignment);
            uppaalTransitionList.add(uppaalTransition);
        }

        return new Template(name,declaration,initLocation,uppaalLocationList,uppaalTransitionList);
    }

//    public static NTA buildAnNTA(RTA... rtas){
//
//        Set<String> broadcaseChanSet = new HashSet<>();
//        Set<String> chanSet = new HashSet<>();
//        Map<String,RTA> stringRTAMap = new HashMap<>();
//        for(int i = 0; i <rtas.length; i++){
//            Set<String> sigma = rtas[i].getSigma();
//            for(String action:sigma){
//                broadcaseChanSet.add(action);
//            }
//        }
//    }
}
