package Elvis;

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
        while (i <= rooms.length) {
            goTo(rooms[i++ % rooms.length]);

            if (!isArrived()) {
                continue;
            }
            // get all colors from room
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                // for memory
                addToMemory(percepts, i - 1);
            } catch (Exception ex) {
            }
        }
    }

    public void traverse() {
        int i = 0;
        List<Percept> percepts = null;

        try {
            System.setErr(new PrintStream("IA_err.txt"));
        } catch (Exception ex) {
        }

        while (true) {
            // stop traversal (prevent from exception)
            try {
                if (ias.getCurrent() >= ias.getColors().length) {
                    break;
                }
            } catch (Exception ex) {
            }

            goTo(rooms[i++ % rooms.length]);

            if (!isArrived()) {
                continue;
            }
            // for memory

            // get all colors from room
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                for (Percept p : percepts) {
                    //System.out.println(p.toProlog());
                    String color = p.toProlog().substring(p.toProlog().indexOf(',') + 1, p.toProlog().indexOf(")"));
                    addToMemory(percepts, i - 1);
                }
            } catch (Exception ex) {
                System.err.println("Exception: traverse() - 1 " + bot);
            }
            // pick & drop color
            try {
                String color = ias.getCurrentColor();
                System.out.println(bot + " need: " + color);
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
                        } while (!isArrived()/* && ias.getCurrent() < ias.getColors().length*/);

                        putDown();
                        ias.putBox(getBlockColor(p.toProlog()));
                        Thread.sleep(200);
                        break;
                    }
                }
                /*
                 // stop traversal
                 if (ias.getCurrent() >= ias.getColors().length)
                 break;
                 */
            } catch (Exception ex) {
                System.err.println("Exception: traverse() - 2 " + bot);
            }
        }
    }

    private boolean isArrived() {
        long t = System.currentTimeMillis();
        List<Percept> percepts = null;

        while (true) {
            try {
                percepts = server.getAllPerceptsFromEntity(bot);
                if (System.currentTimeMillis() - t > 3000) // overtime
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

        super.handleAction(action);
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
