package elvis2;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Scanner;

import eis.iilang.Percept;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class GetBlocksWithServer extends IAController {

    public GetBlocksWithServer() throws RemoteException {
    }

    public void random() {
        boolean[] isSelected = new boolean[rooms.length];
        for (int i = 0; i < rooms.length; i++) {
            isSelected[i] = false;
        }

        String[] randomRooms = new String[rooms.length];
        System.arraycopy(rooms, 0, randomRooms, 0, rooms.length);

        int i = 0;
        while (i < rooms.length) {
            int random = (int) (Math.random() * rooms.length);
            if (!isSelected[random]) {
                isSelected[random] = true;
                rooms[i] = randomRooms[random];
                i++;
            }
        }

        traverse();
    }

    public void travelRooms() {
        int i = 0;
        List<Percept> percepts = null;
        while (i < rooms.length) {
            goTo(rooms[i++ % rooms.length]);

            if (!isArrived(3000)) {
                continue;
            }
            // get all colors from room
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                // for memory
                addToMemory(percepts, rooms[(i-1)%rooms.length]);
            } catch (Exception ex) {
            }
        }
    }

    public void traverse() {
        int i = 0;
        String room=null;
        List<Percept> percepts = null;
        String color=null;
        int impossibleCount=0;
        try {
            //System.setErr(new PrintStream("IA_err.txt"));
        } catch (Exception ex) {
        }

        while (true) {
            // stop traversal (prevent from exception)
            try {
                if (ias.getCurrent() >= ias.getColors().length) {
                    break;
                }
                color = ias.getCurrentColor();
                System.out.println(bot + " need: " + color);
            } catch (Exception ex) {
            }
            
            try{
                room = ias.askForColor(this,color);
            }catch(Exception ex){
                goTo(rooms[i++ % rooms.length]);
                room=rooms[(i-1)%rooms.length];
            }
            if (!isArrived(3000)) {
                //retry
                goTo(room);
                if(!isArrived(500))
                    continue;
            }
            // for memory

            // get all colors from room
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                /*for (Percept p : percepts) {
                    //System.out.println(p.toProlog());
                    String color = p.toProlog().substring(p.toProlog().indexOf(',') + 1, p.toProlog().indexOf(")"));
                    addToMemory(percepts, i - 1);
                }*/
            } catch (Exception ex) {
                System.err.println("Exception: traverse() - 1 " + bot);
            }
            // pick & drop color
            try {
                
                if(!goToBlock(percepts,color)){
                    //reset
                    impossibleCount++;
                    ias.noSuchColor(room,color);
                    goTo("FrontDropZone");
                    Thread.sleep(300);
                }else{
                    impossibleCount=0;
                }
                
                if(impossibleCount>=rooms.length){
                    System.out.println("Impossible");
                    break;
                }
                /*
                 // stop traversal
                 if (ias.getCurrent() >= ias.getColors().length)
                 break;
                 */
            } catch (Exception ex) {
                System.err.println("Exception: traverse() - 2 " + bot);
            }finally{
                addToMemory(percepts, room);
            }
        }
        goTo("FrontDropZone");
    }

    public void printMemory() {
        for (int i = 0; i < rooms.length; i++) {
            System.out.println(rooms[i]);
            try {
                System.out.println("Time stamp: " + timeStamp[nameToIndex.get(rooms[i])]);
                System.out.println("colors");
                System.out.println(memory.get(rooms[i]));
            } catch (Exception ex) {
                System.out.println("not traveled");
            }
            System.out.println();
        }
    }

    @Override
    public void handleAction(String action) {
        if (action.contains("traverse")) {
            traverse();
            return;
        }

        if (action.contains("random")) {
            random();
            return;
        }

        if (action.contains("printMemory")) {
            printMemory();
            return;
        }

        if (action.contains("travelRooms")) {
            travelRooms();
            return;
        }

        if (action.contains("askForColor")) {
            String color = action.substring(action.indexOf("(")+1, action.indexOf(")"));
            try {
                ias.askForColor(this, color);
            } catch (RemoteException re) {
            }
            return;
        }
        
        if(action.contains("total")){
            System.out.println(totalActionPerformed);
            return;
        }

        super.handleAction(action);
    }



    public static void main(String[] args) throws RemoteException {
        GetBlocksWithServer controller = new GetBlocksWithServer();
        Scanner scanner = new Scanner(System.in);
        String action = null;

        do {
            System.out.println("Enter [Bot1, Bot2, Bot3]");
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
