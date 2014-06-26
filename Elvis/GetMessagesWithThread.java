package Elvis;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Scanner;

import eis.eis2java.translation.Translator;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.iilang.Percept;

public class GetMessagesWithThread extends IAController {
	private String[] rooms = new String[] {"RoomA1", "RoomA2", "RoomA3",
			"RoomB1", "RoomB2", "RoomB3",
			"RoomC1", "RoomC2", "RoomC3"};
	
	IAServerInterface ias;
	
	public GetMessagesWithThread() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(8001);
			ias = (IAServerInterface) registry.lookup("IAServer");
		} catch (Exception ex) {}
		
		// Thread for receiving messages
		new Thread() {
			public void run() {
				LinkedList<Percept> percepts = null;
				try {
					System.setErr(new PrintStream("log.txt"));
				} catch (Exception ex) {}
				
				while (true) {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {}
					if (bot == null)
						continue;
					//System.out.println(bot);
					try {
						percepts = server.getAllPerceptsFromEntity(bot);
						for (Percept p: percepts)
							if (p.toProlog().contains("message")) // receive messages
								System.out.println(p.toProlog());
					} catch (Exception ex) {
						System.err.println("Exception: Thread() " + bot);
					}
				}
			}
		}.start();
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
			System.err.println("Exception: goTo(<PlaceID>) " + bot);
		}
	}
	
	public void traverse() {
		int i = 0;
		long t;
		LinkedList<Percept> percepts = null;
		
		try {
			System.setErr(new PrintStream("IA_err.txt"));
		} catch (Exception ex) {}
		
		//try {
			while (true) {
				goTo(rooms[i++ % rooms.length]);

				t = System.currentTimeMillis();
				
				outerLoop:
				while (true) {
					try {
						percepts = server.getAllPerceptsFromEntity(bot);
						// if (percepts.isEmpty() && System.currentTimeMillis() - t > 3000)
						if (System.currentTimeMillis() - t > 3000) // overtime
							break;
						for (Percept p: percepts)
							if (p.toProlog().equals("state(arrived)")) // arrived room
								break outerLoop;
						// System.out.println(System.currentTimeMillis() - t);
					} catch (Exception ex) {
						System.err.println("Exception: traverse() - 1 " + bot);
					}
				}

				// get all colors from room
				try {
					percepts = server.getAllPerceptsFromEntity(bot);
					for (Percept p: percepts) {
						System.out.println(p.toProlog());
					}
				} catch (Exception ex) {
					System.err.println("Exception: traverse() - 2 " + bot);
				}
				
				// pick & drop color
				try {
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
				} catch (Exception ex) {
					System.err.println("Exception: traverse() - 3 " + bot);
				}
			}
		//} catch (Exception ex) {
		//	System.err.println("Exception: traverse() " + bot);
		//}
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
		GetMessagesWithThread controller = new GetMessagesWithThread();
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
