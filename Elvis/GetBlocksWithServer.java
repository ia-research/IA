package Elvis;

import java.io.PrintWriter;
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
	
	IAServerInterface ias;
	
	public GetBlocksWithServer() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(8001);
			ias = (IAServerInterface) registry.lookup("IAServer");
		} catch (Exception ex) {}
	}
	
	// goTo(<PlaceID>)
	@Override
	public void goTo(String placeID) {
		try {
			Parameter[] idParam = Translator.getInstance().translate2Parameter(placeID); // e.g. RoomA1, DropZone
			server.performEntityAction(bot, new Action("goTo", idParam));
			/*
			LinkedList<Percept> percepts = null;

			outerLoop:
			while (true) {
				percepts = server.getAllPerceptsFromEntity(bot);
				for (Percept p: percepts)
					if (p.toProlog().equals("state(arrived)")) // arrived room
						break outerLoop;
			}

			// get all colors from room
			percepts = server.getAllPerceptsFromEntity(bot);
			for (Percept p: percepts)
				System.out.println(p.toProlog());
			*/
		} catch (Exception ex) {
			System.err.println("Exception: goTo(<PlaceID>)");
		}
	}
	
	public void traverse() {
		int i = 0;
		long t;
		LinkedList<Percept> percepts = null;
		
		try {
			while (true) {
				goTo(rooms[i++ % rooms.length]);

				t = System.currentTimeMillis();
				
				outerLoop:
				while (true) {
					percepts = server.getAllPerceptsFromEntity(bot);
					// if (percepts.isEmpty() && System.currentTimeMillis() - t > 3000)
					if (System.currentTimeMillis() - t > 3000) // overtime
						break;
					for (Percept p: percepts)
						if (p.toProlog().equals("state(arrived)")) // arrived room
							break outerLoop;
					// System.out.println(System.currentTimeMillis() - t);
				}

				// get all colors from room
				percepts = server.getAllPerceptsFromEntity(bot);
				for (Percept p: percepts) {
					System.out.println(p.toProlog());
				}
				
				// pick & drop color
				String color = ias.getCurrentColor();
				System.out.println(bot + " need: " + color);
				for (Percept p: percepts) {
					if (color.equals(getBlockColor(p.toProlog()))) {
						
						PrintWriter pw = new PrintWriter("log.txt");
						pw.println(bot + ":" + p.toProlog());
						pw.close();
						
						goToBlock(getBlockId(p.toProlog()));
						Thread.sleep(500);
						pickUp();
						Thread.sleep(500);
						goTo("DropZone");
						Thread.sleep(3000);
						putDown();
						ias.putBox(this.getBlockColor(p.toProlog()));
						Thread.sleep(500);
						break;
					}
				}
				
				// stop traversal
				if (ias.getCurrent() >= ias.getColors().length)
					break;
			}
		} catch (Exception ex) {
			System.err.println("Exception: traverse()");
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
	
	public static void main(String[] args) {
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
