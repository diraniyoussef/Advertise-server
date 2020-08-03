import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

//This is how you run it from the terminal         java workspace/ControlLinker_v0.6/bin/ad.setup 

public class Setup {	
	static Connection conn;
	
	ServerSocketListener[] mob = new ServerSocketListener[1];
//	ServerSocketListener[] mod = new ServerSocketListener[2];
	
	static public volatile String keyboard_input = "";
	static public volatile boolean force_stop = false; //related to the db I believe
	
	public static void main(String[] args) {
		new Setup().starter();
		System.out.println(new Timestamp(System.currentTimeMillis()) + " Setup.java " + 
				"main     Main thread has ended.");
	}
	
	private void starter() {
		//Connecting to database
		conn = this.connectDb();
		if (conn == null) {
			System.out.println("no connection to database! Ending.");
			return; //should never be.
		}
		
		//mob and mod are just names, and nothing prevents me from letting a mob connects through the mod ports or vice versa
		mob[0] = new ServerSocketListener(11359); //120
//		mob[1] = new ServerSocketListener(11360); //121
		
//		mod[0] = new ServerSocketListener(11359); //3558 3559
//		mod[1] = new ServerSocketListener(11360);

		//this is about start listening...
		mob[0].start();
//		mob[1].start();
//		mod[0].start();
//		mod[1].start();

		System.out.println(new Timestamp(System.currentTimeMillis()) + " Setup.java " +
				"starter     The servers have just started.");
		
		//Instruct the program through keyboard input
		getKeyboardInput();

	}

	private Connection connectDb() {
        // SQLite connection string
		String url = "jdbc:sqlite:./db/my_db.db"; //entities_v01.db
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);           
            if( conn == null ) {
            	System.out.println("No connection to database!"); //we shouldn't enter here
            } else {            
            	System.out.println("Database is connected...");
            	//WAL mode
            	String sql = "pragma journal_mode=wal"; //Sqlite library must be >= 3.7.0 //https://stackoverflow.com/questions/6653648/how-to-implement-write-ahead-logging-of-sqlite-in-java-program
            	Statement stmt  = conn.createStatement();
                stmt.executeQuery(sql); //ResultSet rs = 
                //creating the table having the last update date of the whole database
                sql = "CREATE TABLE IF NOT EXISTS '' ( " +
                        "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +                       
                        "last_update INTEGER, " +//we cannot name "index" or 'index' they're reserved for some reason.
                        "owner_name TEXT, " +
                        "owner_phonenumber TEXT);";
                stmt  = conn.createStatement();
                stmt.executeQuery(sql);
                //creating the table having the last update date
                sql = "CREATE TABLE IF NOT EXISTS 'table_last_update' ( " +
                        "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "table_name TEXT, " +
                        "last_update TEXT, " +//made here String; in the app it's long
                        "table_str TEXT);";
                stmt  = conn.createStatement();
                stmt.executeQuery(sql);
                System.out.println("Should have now 2 main tables...");
            }
        } catch( SQLException e ) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Exception: Youssef, please be sure that the folder db exists inside the project's directory");
            /*odd enough, if the database doesn't exist, it would still make a connection and not throw an exception.
             * Even worse, it will create a file with the name of the database!
             */
        }
        return conn;
    }
	
	private void getKeyboardInput() {
		Scanner keyboard = new Scanner(System.in);
		do {
			System.out.println(new Timestamp(System.currentTimeMillis()) + " Setup.java " + 
					"getKeyboardInput   PLEASE enter 'stop' to stop execution. And do not close it casually!");
			keyboard_input = keyboard.next();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " Setup.java " + 
			"getKeyboardInput   The entered keyboard string is: " + keyboard_input); 
			//here you can execute many things according to the entry you input in the keyboard
			//...
			
			
		} while( !keyboard_input.equals("stop") && !force_stop );		
		System.out.println( new Timestamp(System.currentTimeMillis()) + " Setup.java " + 
				"getKeyboardInput   Closing keyboard.");
		keyboard.close();
		
		//closing the database
		try {
			conn.close();
			System.out.println("Connection to database is closed gracefully...");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		//  https://stackoverflow.com/questions/2983835/how-can-i-interrupt-a-serversocket-accept-method
		mob[0].closeSocket(); //This will let the .accept() in Servers.java throw an exception I believe and this 
//		mob[1].closeSocket(); // is very normal. I fact it is desired since we want to stop of halting
//		mod[0].closeSocket(); // execution
//		mod[1].closeSocket();
				
		mob = null; //no need but ok
//		mod = null;				
		System.gc();
	}	
}




	

