package Elvis;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Scanner;

import eis.eis2java.translation.Translator;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.iilang.Percept;

public class GetBlocksWithServer extends IAController {
	private String[] rooms = new String[] {"RoomA1", "RoomA2", "RoomA3",
			"RoomB1", "RoomB2", "RoomB3",
			"RoomC1", "RoomC2", "RoomC3"};
	
	public GetBlocksWithServer() throws RemoteException {}
	
	public void traverse() {
		int i = 0;
		LinkedList<Percept> percepts = null;
		
		try {
			System.setErr(new PrintStream("IA_err.txt"));
		} catch (Exception ex) {}
		
		while (true) {
			// stop traversal (prevent from exception)
			try {
				if (ias.getCurrent() >= ias.getColors().length)
					break;
			} catch (Exception ex) {}
			
			goTo(rooms[i++ % rooms.length]);
			
			if (!isArrived())
				continue;

			// get all colors from room
			try {
				percepts = server.getAllPerceptsFromEntity(bot);
				for (Percept p: percepts) {
					System.out.println(p.toProlog());
				}
			} catch (Exception ex) {
				System.err.println("Exception: traverse() - 1 " + bot);
			}
			
			// pick & drop color
			try {
				String color = ias.getCurrentColor();
				System.out.println(bot + " need: " + color);
				for (Percept p: percepts) {
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
		LinkedList<Percept> percepts = null;
		
		while (true) {
			try {
				percepts = server.getAllPerceptsFromEntity(bot);
				if (System.currentTimeMillis() - t > 3000) // overtime
					return false;
				for (Percept p: percepts)
					if (p.toProlog().equals("state(arrived)")) // arrived room
						return true;
			} catch (Exception ex) {
				System.err.println("Exception: checkArrived() " + bot);
			}
		}
	}

	@Override
	public void handleAction(String action) {
		if (action.contains("traverse")) {
			traverse();
			return;
		}
		
		super.handleAction(action);
	}
	
	public long getBlockId(String block) {
		if (block == null)
			return -1;
		return Long.parseLong(block.substring(6, block.indexOf(",")));
	}
	
	public String getBlockColor(String block) {
		if (block == null)
			return null;
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
			if (action.equals("Quit"))
				break;
			controller.handleAction(action);
		}
		
		System.out.println("You have quit");
		scanner.close();
	}
}
