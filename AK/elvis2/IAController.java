package elvis2;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import eis.eis2java.translation.Translator;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
/*import nl.tudelft.bw4t.network.BW4TServerActions;*/
import nl.tudelft.bw4t.server.BW4TServerActions;

public class IAController extends UnicastRemoteObject implements IAControllerInterface { // BW4TAgent

    protected String bot = null;
    protected BW4TServerActions server = null;
    // private BW4TClientActions client = null;
    protected IAServerInterface ias;
    
    protected static String[] rooms = new String[]{"RoomA1", "RoomA2", "RoomA3",
        "RoomB1", "RoomB2", "RoomB3",
        "RoomC1", "RoomC2", "RoomC3"};
    public static final int ROOMS = rooms.length;
    protected long[] timeStamp = new long[ROOMS];
    protected int next = 0;
    protected Map<String, Integer> nameToIndex;
    protected Map<String, Set<String>> memory;
    protected int totalActionPerformed;    
    
    public void setBot(String bot) {
        this.bot = bot;

        try {
            ias.registerBot(bot, this);
        } catch (Exception ex) {
            System.err.println("Exception: ias.registerBot(bot, this)");
            ex.printStackTrace();
        }
    }

    public String getBot() {
        return bot;
    }

    public int getTotal(){
        return totalActionPerformed;
    }
    
    public IAController() throws RemoteException {
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

        /*
         try {
         client = (BW4TClientActions) Naming.lookup("rmi://localhost:2000/BW4TClient");
         } catch (Exception ex) {
         System.err.println("Exception: Failed to connect to client");
         }
         */

        try {
            Registry registry = LocateRegistry.getRegistry(8001);
            ias = (IAServerInterface) registry.lookup("IAServer");
        } catch (Exception ex) {
            System.err.println("Exception: Failed to connect to IAServer");
        }

        nameToIndex = new HashMap<>();
        memory = new HashMap<>();
        totalActionPerformed=0;
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
            totalActionPerformed++;
        } catch (Exception ex) {
            System.err.println("Exception: goTo(X, Y) " + bot);
        }finally{
            totalActionPerformed++;
        }
    }

    // goTo(<PlaceID>)
    public void goTo(String placeID) {
        try {
            Parameter[] idParam = Translator.getInstance().translate2Parameter(placeID); // e.g. RoomA1, DropZone
            server.performEntityAction(bot, new Action("goTo", idParam));
        } catch (Exception ex) {
            System.err.println("Exception: goTo(<PlaceID>) " + bot);
        }finally{
            totalActionPerformed++;
        }
    }

    // goToBlock(<BlockID>)
    public void goToBlock(long blockID) { // how to get the block ID? and which room contains the block?
        try {
            Parameter[] idParam = Translator.getInstance().translate2Parameter(Long.valueOf(blockID));
            server.performEntityAction(bot, new Action("goToBlock", idParam));
        } catch (Exception ex) {
            System.err.println("Exception: goToBlock(<BlockID>) " + bot);
        }finally{
            totalActionPerformed++;
        }
    }

    // pickUp()
    public void pickUp() {
        try {
            server.performEntityAction(bot, new Action("pickUp"));
        } catch (Exception ex) {
            System.err.println("Exception: pickUp() " + bot);
        }finally{
            totalActionPerformed++;
        }
    }

    // putDown()
    public void putDown() {
        try {
            server.performEntityAction(bot, new Action("putDown"));
        } catch (Exception ex) {
            System.err.println("Exception: putDown() " + bot);
        }finally{
            totalActionPerformed++;
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
            System.err.println("Exception: sendMessage(<PlayerID>, <Content>) " + bot);
        }finally{
            totalActionPerformed++;
        }
    }

    @Override
    public void removeFromMemory(String room,String color) throws RemoteException{
        memory.get(room).remove(color);
    }
    
    @Override
    public RoomTime colorInRoom(String color) throws RemoteException{
        PriorityQueue<RoomTime> queue = new PriorityQueue<RoomTime>(1,new Compare());
        //String rm="";
        for (int i = 0; i < rooms.length; i++) {
            try{
                if (memory.get(rooms[i]).contains(color)) {
                    /*
                    rm+=rooms[i];
                    rm+=" : ";
                    rm+=Calendar.getInstance().getTimeInMillis() - timeStamp[nameToIndex.get(rooms[i])];
                    rm+=" ms ago\n";
                    */
                    RoomTime temp = new RoomTime(rooms[i],Calendar.getInstance().getTimeInMillis() - timeStamp[nameToIndex.get(rooms[i])]);
                    queue.add(temp);
                }
            }catch(NullPointerException npe){}
        }
        totalActionPerformed++;
        return queue.peek();
    }
    
    @Override
    public void goToMostPossibleExistRoom(String room) throws RemoteException,NullPointerException{
        if(room == null)
            throw new NullPointerException();
        System.out.println(room);
        goTo(room);
    }

    public void addToMemory(List<Percept> percepts, String room) {
        if (nameToIndex.containsKey(room)) {
            timeStamp[nameToIndex.get(room)] = Calendar.getInstance().getTimeInMillis();
        } else {
            timeStamp[next] = Calendar.getInstance().getTimeInMillis();
            nameToIndex.put(room, next++);
        }
        if (memory.keySet().contains(room)) {
            memory.get(room).clear();
        } else {
            memory.put(room, new HashSet<String>());
        }
        for (Percept p : percepts) {
            //System.out.println(p.toProlog());
            String color = p.toProlog().substring(p.toProlog().indexOf(',') + 1, p.toProlog().indexOf(")"));
            memory.get(room).add(color);
        }
    }
    
    protected boolean isArrived(long timeLimit) {
        long t = System.currentTimeMillis();
        List<Percept> percepts = null;

        while (true) {
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                if (System.currentTimeMillis() - t > timeLimit) // overtime
                {
                    return false;
                }
                for (Percept p : percepts) {
                    if (p.toProlog().equals("state(arrived)")) // arrived room
                    {
                        return true;
                    }
                }
            } catch (Exception ex) {
                System.err.println("Exception: checkArrived() " + bot);
            }
        }
    }
    
    public boolean goToBlock(List<Percept> percepts, String color) throws Exception{
        for (Percept p : percepts) {
                    if (color.equals(getBlockColor(p.toProlog()))) {

                        //PrintWriter pw = new PrintWriter("log.txt");
                        //pw.println(bot + ":" + p.toProlog());
                        //pw.close();

                        goToBlock(getBlockId(p.toProlog()));
                        Thread.sleep(200);
                        pickUp();
                        Thread.sleep(200);

                        do {
                            goTo("DropZone");
                        } while (!isArrived(3000)/* && ias.getCurrent() < ias.getColors().length*/);

                        putDown();
                        ias.putBox(getBlockColor(p.toProlog()));
                        Thread.sleep(200);
                        //break;
                        return true;
                    }
                }
        return false;
    }

    public long getBlockId(String block) {
        if (block == null) {
            return -1;
        }
        return Long.parseLong(block.substring(6, block.indexOf(",")));
    }

    public String getBlockColor(String block) {
        if (block == null) {
            return null;
        }
        return block.substring(block.indexOf(",") + 1, block.length() - 1);
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

        /*
         if (action.contains("sendMessage")) {
         String param = action.substring(12, action.length() - 1);
         String[] params = param.split(", ");
         sendMessage(params[0], params[1]);
         return;
         }
         */

        if (action.contains("sendMessage")) {
            String param = action.substring(12, action.length() - 1);
            String[] params = param.split(", ");
            try {
                ias.sendMessage(params[0], params[1]);
                //ias.sendMessage(params[1]); // params[0] <PlayerID>, params[1] <Content>
            } catch (Exception e) {
            }
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

        System.err.println("Wrong action " + bot);
    }

    @Override
    public void receiveMessage(String s,String sender) throws RemoteException {
        //System.out.println(bot + ":" + s);
        String action = s.substring(0, 2);
        if (action.equals("00")) // display message
        {
            System.out.println(sender + ":" + s.substring(3));
        }
        if (action.equals("01")) // go to rooms
        {
            goTo(s.substring(3));
        }
    }

    public static void main(String[] args) throws RemoteException {
        IAController controller = new IAController();
        Scanner scanner = new Scanner(System.in);
        String action = null;

        do {
            System.out.println("Enter [Human_1, Human_2, Human_3]");
            controller.setBot(scanner.nextLine());
            System.out.println(controller.getBot() + ", y/n?");
        } while (!scanner.nextLine().equals("y"));

        System.out.println("Enter \"Quit\" to quit");

        while (true) {
            action = scanner.nextLine();
            if (action.equals("Quit")) {
                break;
            }
            controller.handleAction(action);
        }

        System.out.println("You have quit");
        scanner.close();
    }
}
