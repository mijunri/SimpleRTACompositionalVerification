package uppaalRTA;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import rta.RTA;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class UppaalNTA1 {
    private String declaration;
    private List<UppaalTemplate> templateList = new ArrayList<>();
    private List<UppaalTransition> transitionList = new ArrayList<>();
    private Set<String> initIdSet = new HashSet<>();
    private Set<String> alphabets;
    /**
     * 根据两个组件和一个性质自动机构造UppaalNTA
     * @param m1
     * @param m2
     * @param p
     */
    public UppaalNTA1(RTA m1, RTA m2, RTA p){
        //获取组合中的action，作为chan，凡是在三个自动机中出现过两次及以上的action操作都记录起来
        alphabets = getAlphabets(m1,m2,p);


        //根据三个自动机生成UppaalTemplate
        UppaalTemplate templateM1 = new UppaalTemplate(m1);
        UppaalTemplate templateM2 = new UppaalTemplate(m2);
        UppaalTemplate templateP = new UppaalTemplate(p);

        templateList.add(templateM1);
        templateList.add(templateM2);
        templateList.add(templateP);

        initIdSet.add(templateM1.getInit());
        initIdSet.add(templateM2.getInit());
        initIdSet.add(templateP.getInit());

        transitionList.addAll(templateM1.getTransitionList());
        transitionList.addAll(templateM2.getTransitionList());
        transitionList.addAll(templateP.getTransitionList());

        for(String action: alphabets){
            List<UppaalTransition> actionTransitions = getTransitionListFromAction(action);
            String flag = actionTransitions.get(0).getRtaName();
            for(int i = 0; i < actionTransitions.size(); i++){
                UppaalTransition t = actionTransitions.get(i);
                String rtaName = t.getRtaName();
                //将第一个出现的chan的RTA的chan设置为发送信号
                if(flag.equals(rtaName)){
                    t.setSend(true);
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for(String action:alphabets){
            sb.append("chan ").append(action).append("; ");
        }
        stringBuilder.append(sb);
        declaration = stringBuilder.toString();
    }

    /**
     * 根据m2和assume构造一个NTA
     * @param m2
     * @param assumption
     */
    public UppaalNTA1(RTA m2, RTA assumption){

        //获取组合中的action，作为boardcast chan，凡是在三个自动机中出现过两次及以上的action操作都记录起来

        alphabets = getAlphabets(m2,assumption);


        //根据两个个自动机生成UppaalTemplate
        UppaalTemplate templateM2 = new UppaalTemplate(m2);
        UppaalTemplate templateP = new UppaalTemplate(assumption);

        templateList.add(templateM2);
        templateList.add(templateP);

        initIdSet.add(templateM2.getInit());
        initIdSet.add(templateP.getInit());

        transitionList.addAll(templateM2.getTransitionList());
        transitionList.addAll(templateP.getTransitionList());
        for(String action: alphabets){
            List<UppaalTransition> actionTransitions = getTransitionListFromAction(action);
            String flag = actionTransitions.get(0).getRtaName();
            for(int i = 0; i < actionTransitions.size(); i++){
                UppaalTransition t = actionTransitions.get(i);
                String rtaName = t.getRtaName();
                if(flag.equals(rtaName)){
                    t.setSend(true);

                }
            }

        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String action:alphabets){
            stringBuilder.append("chan ");
            stringBuilder.append(action);
            stringBuilder.append(";");
        }
        declaration = stringBuilder.toString();
    }

    private Set<String> getAlphabets(RTA m1, RTA m2, RTA p){
        //获取组合中的action，作为chan，凡是在三个自动机中出现过两次及以上的action操作都记录起来
        Set<String> alphabetsM1 = m1.getSigma();
        Set<String> alphabetsM2 = m2.getSigma();
        Set<String> alphabetsP = p.getSigma();
        Map<String,Integer> actionMap = new HashMap<>();
        Set<String> alphabets = new HashSet<>();
        for(String action: alphabetsM1){
            Integer num = actionMap.getOrDefault(action,0);
            actionMap.put(action,++num);
        }
        for(String action: alphabetsM2){
            Integer num = actionMap.getOrDefault(action,0);
            actionMap.put(action,++num);
        }
        for(String action: alphabetsP){
            Integer num = actionMap.getOrDefault(action,0);
            actionMap.put(action,++num);
        }

        for(String action: actionMap.keySet()){
            if(actionMap.get(action) > 1){
                alphabets.add(action);
            }
        }
        return alphabets;
    }

    private Set<String> getAlphabets(RTA m1, RTA m2){
        //获取组合中的action，作为chan，凡是在三个自动机中出现过两次及以上的action操作都记录起来
        Set<String> alphabetsM1 = m1.getSigma();
        Set<String> alphabetsM2 = m2.getSigma();

        Map<String,Integer> actionMap = new HashMap<>();
        Set<String> alphabets = new HashSet<>();
        for(String action: alphabetsM1){
            Integer num = actionMap.getOrDefault(action,0);
            actionMap.put(action,++num);
        }
        for(String action: alphabetsM2){
            Integer num = actionMap.getOrDefault(action,0);
            actionMap.put(action,++num);
        }

        for(String action: actionMap.keySet()){
            if(actionMap.get(action) > 1){
                alphabets.add(action);
            }
        }
        return alphabets;
    }

    public List<UppaalTransition> getTransitionListFromSourceId(String sourceId){
        List<UppaalTransition> list = new ArrayList<>();
        for (UppaalTransition t:transitionList){
            if(t.getSourceId().equals(sourceId)){
                list.add(t);
            }
        }
        return list;
    }

    public List<UppaalTransition> getTransitionListFromTargetId(String targetId){
        List<UppaalTransition> list = new ArrayList<>();
        for (UppaalTransition t:transitionList){
            if(t.getTargetId().equals(targetId)){
                list.add(t);
            }
        }
        return list;
    }

    public List<UppaalTransition> getTransitionListFromAction(String action){
        List<UppaalTransition> list = new ArrayList<>();
        for (UppaalTransition t:transitionList){
            if(t.getSync().equals(action)){
                list.add(t);
            }
        }
        return list;
    }

    public void toXml(){
        try {
            //添加整体描述
            Document document = DocumentHelper.createDocument();
            document.addDocType("nta","-//Uppaal Team//DTD Flat System 1.1//EN","http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd");
            Element root = document.addElement("nta");
            Element declaration0 = root.addElement("declaration");
            declaration0.addText(declaration);
            StringBuilder systemString = new StringBuilder();
            systemString.append("system ");
            //添加template
            for(UppaalTemplate uppaalTemplate:templateList){
                systemString.append(uppaalTemplate.getName()).append(",");
                Element template = root.addElement("template");
                Element name = template.addElement("name");
                name.addText(uppaalTemplate.getName());
                Element declaration1 = template.addElement("declaration");
                declaration1.addText(uppaalTemplate.getDeclaration());
                List<UppaalLocation> uppaalLocationList = uppaalTemplate.getLocationList();
                //添加location
                for(UppaalLocation uppaalLocation:uppaalLocationList){
                    Element location = template.addElement("location");
                    location.addAttribute("id",uppaalLocation.getId());
                    Element name1 = location.addElement("name");
                    name1.addText(uppaalLocation.getName());
                }
                Element init1 = template.addElement("init");
                init1.addAttribute("ref",uppaalTemplate.getInit());
                //添加迁移
                List<UppaalTransition> uppaalTransitionList = uppaalTemplate.getTransitionList();
                for(UppaalTransition uppaalTransition : uppaalTransitionList){
                    Element tran = template.addElement("transition");
                    Element src = tran.addElement("source");
                    src.addAttribute("ref",uppaalTransition.getSourceId());
                    Element tar = tran.addElement("target");
                    tar.addAttribute("ref",uppaalTransition.getTargetId());


                    //添加chan;
                    String action = uppaalTransition.getSync();
                    if(alphabets.contains(action)){
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(action);
                        Element sycnLabel = tran.addElement("label");
                        sycnLabel.addAttribute("kind", "synchronisation");
                        if(uppaalTransition.isSend()){
                            stringBuilder.append("!");
                        }else {
                            stringBuilder.append("?");
                        }
                        sycnLabel.addText(stringBuilder.toString());
                    }


                    //添加guard
                    Element guardLabel = tran.addElement("label");
                    guardLabel.addAttribute("kind","guard");
                    guardLabel.addText(uppaalTransition.getGuard());
                    //添加update
                    Element updateLabel = tran.addElement("label");
                    updateLabel.addAttribute("kind","assignment");
                    updateLabel.addText(uppaalTransition.getAssignment());
                }
            }
            //添加System
            Element system = root.addElement("system");
            systemString.deleteCharAt(systemString.lastIndexOf(","));
            systemString.append(";");
            system.addText(systemString.toString());

            //持久化处理
            XMLWriter writer = new XMLWriter(    new FileOutputStream(".//uppaalNta.xml"),
                    OutputFormat.createPrettyPrint());
            writer.write(document);
            System.out.println("写出完毕");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateMemProperty() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("A[] not ");
        UppaalTemplate template = templateList.get(templateList.size()-1);
        stringBuilder.append(template.getName());
        List<UppaalLocation> locationList = template.getLocationList();
        UppaalLocation location =locationList.get(locationList.size()-1);
        stringBuilder.append(".");
        stringBuilder.append(location.getName());
        OutputStreamWriter writer = null;
        try{
            writer = new OutputStreamWriter(new FileOutputStream(".//uppaalNta.q"));
            writer.write(stringBuilder.toString());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void toBat(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("verifyta -t1 -S2 -f uppaalNta.xtr  uppaalNta.xml uppaalNta.q > uppaalNta.txt");
        try{
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(".//uppaalRun.bat"));
            writer.write(stringBuilder.toString());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
