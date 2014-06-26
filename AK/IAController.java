package AK;

import java.rmi.Naming;
// import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import eis.eis2java.translation.Translator;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import nl.tudelft.bw4t.BW4TEnvironmentListener;
import nl.tudelft.bw4t.client.BW4TClientActions;
import nl.tudelft.bw4t.client.BW4TRemoteEnvironment;
import nl.tudelft.bw4t.map.BlockColor;

import nl.tudelft.bw4t.server.BW4TServerActions;

public class IAController { // BW4TAgent

    private String bot = null;
    static private BW4TServerActions server = null;
    static private BW4TClientActions client = null;
    private ArrayList<BlockColor> sequence;
    private int traverseCounter=0;
    private String[] rooms = new String[]{"RoomC1", "RoomC2", "RoomC3", "RoomB1", "RoomB2", "RoomB3", "RoomA1", "RoomA2", "RoomA3"};
    static private BW4TRemoteEnvironment env;
    
    
    public void setBot(String bot) {
        this.bot = bot;
    }

    public String getBot() {
        return bot;
    }

    public IAController() { // is rmi safe?
		/*
         try {
         LocateRegistry.createRegistry(Integer.parseInt("2000")); // client port 2000
         } catch (Exception ex) {
         System.err.println("Exception: Registry already created");
         }
         */

        try {
            server = (BW4TServerActions) Naming.lookup("rmi://localhost:8000/BW4TServer");
        } catch (Exception ex) {
            System.err.println("Exception: Failed to connect to server");
        }


        try {
            client = (BW4TClientActions) Naming.lookup("rmi://localhost:2000/BW4TClient");
        } catch (Exception ex) {
            System.err.println("Exception: Failed to connect to client");
            ex.printStackTrace();
        }

    }

    // goTo(X, Y)
    public void goTo(double X, double Y) {
        try {
            Parameter[] xParam = null;
            Parameter[] yParam = null;
            xParam = Translator.getInstance().translate2Parameter(Double.valueOf(X));
            yParam = Translator.getInstance().translate2Parameter(Double.valueOf(Y));

            Parameter[] parameters = new Parameter[2];
            parameters[0] = xParam[0];
            parameters[1] = yParam[0];

            server.performEntityAction(bot, new Action("goTo", parameters));
        } catch (Exception ex) {
            System.err.println("Exception: goTo(X, Y)");
        }
    }

    // goTo(<PlaceID>)
    public void goTo(String placeID) {
        try {
            Parameter[] idParam = Translator.getInstance().translate2Parameter(placeID); // e.g. RoomA1, DropZone
            Percept p = server.performEntityAction(bot, new Action("goTo", idParam));
            //System.out.println(p);
        } catch (Exception ex) {
            System.err.println("Exception: goTo(<PlaceID>)");

        }
    }

    public void getSequence() {
        try {
            LinkedList<Percept> percepts = server.getAllPerceptsFromEntity(bot);
            Iterator<Parameter> paramOcc;
            Iterator<Parameter> y;
            for (Percept p : percepts) {
                String name = p.getName();
                if (name.equals("sequence")) {
                    LinkedList<Parameter> parameters = p.getParameters();
                    for (paramOcc = parameters.iterator(); paramOcc.hasNext(); y.hasNext()) {
                        Parameter i = (Parameter) paramOcc.next();
                        ParameterList list = (ParameterList) i;
                        y = list.iterator();
                        //continue;
                        Parameter j = (Parameter) y.next();
                        char letter = ((Identifier) j).getValue().charAt(0);
                        this.sequence.add(BlockColor.toAvailableColor(Character.valueOf(letter)));
                    }
                }
            }

            for (BlockColor color : sequence) {
                System.out.println(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPercepts() {
        try {
            LinkedList<Percept> ll = server.getAllPerceptsFromEntity(bot);
            LinkedList<Parameter> para;
            String name;
            int first;
            int next;
            for(Percept p:ll){
                System.out.println();
                System.out.println(p);
            }
        } catch (NullPointerException np) {
            np.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void routinGetPercepts() {
        try {
            LinkedList<Percept> ll = server.getAllPerceptsFromEntity(bot);
            LinkedList<Parameter> para;
            String name;
            int first;
            int next;
            try{
                System.out.println(ll.element().getName());
            }catch(Exception ex){
                traverseCounter++;
            }
            /*if(ll.element().getName().equals("state")){
                System.out.println(ll.element().toProlog());
                if(ll.element().toProlog().contains("arrived") || ll.element().toProlog().contains("traveling")){
                    
                    traverseCounter++;
                }
            }*/
            
            for (Percept p : ll) {
                para = p.getParameters();
                name = p.getName();
                //System.out.println(name);
                /*
                 while(!(name.equals("color") && name.equals(""))){
                 System.out.println("in loop");
                 getPercepts();
                 return;
                 }
                 */
                /*if (name.equals("color") || name.equals("")) {
                    

                    for (Parameter s : para) {
                        first = s.toString().indexOf("\"");
                        next = s.toString().indexOf("\"", first + 1);
                        if (s.toString().contains("number")) {
                            System.out.println("number : " + s.toString().substring(first + 1, next));
                        } else if (s.toString().contains("identifier")) {
                            System.out.println("color : " + s.toString().substring(first + 1, next));
                        } else {
                            //System.out.println(s);
                        }
                    }
}*/             
                if(name.equals("color")|| name.equals("")){
                System.out.println(rooms[traverseCounter%rooms.length]);
                System.out.println(p.toProlog());
                }
                /*else{
                 System.out.println();
                 System.out.println(p);
                 System.out.println();
                 }*/
            }
            if(ll.element().getName().equals("color")){
                traverseCounter++;
            }
            
        } catch (NullPointerException np) {
            np.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void routin() {
        /*Thread x = new Thread(){
         public void run(){
         */
        //int times=0;
        while (/*times!=rooms.length*/true) {
            
            
            goTo(rooms[traverseCounter%rooms.length]);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            routinGetPercepts();
            //times++;
        }
        /* }};*/
        /*x.start();*/
    }

    // goToBlock(<BlockID>)
    public void goToBlock(long blockID) { // how to get the block ID? and which room contains the block?
        try {
            Parameter[] idParam = Translator.getInstance().translate2Parameter(Long.valueOf(blockID));
            Percept p = server.performEntityAction(bot, new Action("goToBlock", idParam));
            System.out.println(p);
        } catch (Exception ex) {
            System.err.println("Exception: goToBlock(<BlockID>)");
        }
    }

    // pickUp()
    public void pickUp() {
        try {
            Percept p = server.performEntityAction(bot, new Action("pickUp"));
            System.out.println(p);
        } catch (Exception ex) {
            System.err.println("Exception: pickUp()");
        }
    }

    // putDown()
    public void putDown() {
        try {
            Percept p = server.performEntityAction(bot, new Action("putDown"));
            System.out.println(p);
        } catch (Exception ex) {
            System.err.println("Exception: putDown()");
        }
    }

    // sendMessage(<PlayerID>, <Content>)
    public void sendMessage(String playerID, String content) {
        try {
            if (!(playerID.equals("Bot1") || playerID.equals("Bot2") || playerID.equals("Bot3"))) {
                throw new Exception();
            }
            Parameter[] receiverParam = Translator.getInstance().translate2Parameter(playerID);
            Parameter[] messageParam = Translator.getInstance().translate2Parameter(content);
            Parameter[] parameters = new Parameter[2];
            parameters[0] = receiverParam[0];
            parameters[1] = messageParam[0];
            server.performEntityAction(bot, new Action("sendMessage", parameters));
        } catch (Exception ex) {
            System.err.println("Exception: sendMessage(<PlayerID>, <Content>)");
        }
    }

    public void handleAction(String action) {
        if (action.equals("pickUp")) {
            pickUp();
            return;
        }

        if (action.equals("putDown")) {
            putDown();
            return;
        }

        if (action.contains("sendMessage")) {
            String param = action.substring(12, action.length() - 1);
            String[] params = param.split(", ");
            sendMessage(params[0], params[1]);
            return;
        }

        if (action.contains("goToBlock")) {
            String param = action.substring(10, action.length() - 1);
            goToBlock(Long.parseLong(param));
            return;
        }

        if (action.contains("goTo")) {
            String param = action.substring(5, action.length() - 1);
            if (param.contains(", ")) {
                String[] params = param.split(", ");
                goTo(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
                return;
            } else {
                goTo(param);
                return;
            }
        }

        if (action.contains("getPercept")) {
            getPercepts();
            return;
        }

        if (action.contains("routin")) {
            routin();
            return;
        }

        if (action.contains("getSequence")) {
            getSequence();
            return;
        }
        
        System.out.println("Wrong action");
    }

    public static String findArgument(String[] args, InitParam param) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("-" + param.nameLower())) {
                return args[(i + 1)];
            }
        }
        return param.getDefaultValue();
    }
    
    public static void main(String[] args) {
        /*Map<String, Parameter> initParameters = new HashMap<String, Parameter>();
        try {
            for (InitParam param : InitParam.values()) {
                initParameters.put(param.nameLower(), new Identifier(
                        findArgument(args, param)));
            }
            env = new BW4TRemoteEnvironment();
            env.attachEnvironmentListener(new BW4TEnvironmentListener(env));
            env.init(initParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        IAController controller = new IAController();
        Scanner scanner = new Scanner(System.in);
        String action = null;

        /*
        do {
            System.out.println("Enter [Bot1, Bot2, Bot3]");
            controller.setBot(scanner.nextLine());
            System.out.println(controller.getBot() + ", y/n?");
        } while (!scanner.nextLine().equals("y"));
        */
        controller.setBot("Bot1");
        System.out.println("Enter \"Quit\" to quit");

        while (true) {
            action = scanner.nextLine();
            if (action.equals("Quit")) {
                try{
                server.unregisterClient(client);
                }catch(Exception e){e.printStackTrace();}
                break;
            }
            controller.handleAction(action);
        }

        System.out.println("You have quit");
        scanner.close();
    }
}
