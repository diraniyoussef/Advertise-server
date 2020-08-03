//This class is actually about client sockets

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

class ClientSocket extends Thread { //the main role is a message listener
	Socket client_socket;
	final String InfoRequest_str = "R?table_last_update", /*This will be preceded by the app sending its table_last_update
             * to the server, then the server will know which tables to send to the app, and will actually send them
             * according to the following string constant.
             */
             InfoTableReply_str = "table_last_update:";
	
	
	PrintWriter print_writer;
	
	MessageOperations message_operations = new MessageOperations( this );
	
	boolean is_new_client_socket = true; //This is used to know if the object is declared for the first time or not.
	
	BufferedReader buffered_reader;
	static final int BufferSize = 8192;//trying...
	
	//private Timer timer;
	//volatile boolean timer_reached = false;	
	
	//int attempt_number_to_close = 0; //just for debugging
	//String mod_id; //this is per message and it's not wrong since each new message controls the thread.  
	
	ClientSocket( Socket client_socket_arg ) {
		client_socket = client_socket_arg;
		/*
		timer = new Timer();
		timer.schedule( new TimerTask() {
			  @Override
			  public void run() {
				  System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
							"Closing client_socket after 2 minutes and a half have passed.");
				  timer_reached = true;	//this variable will provoke the end of the thread of this ClientSocket instance			
			  }
			}, (long) (3 * 60 * 1000) ); //3 minutes is fine
			*/
	}
	
	boolean isReadyToStart() {
		try {			
			print_writer = new PrintWriter( client_socket.getOutputStream() );
			buffered_reader = new BufferedReader( new InputStreamReader( client_socket.getInputStream() ) );
			//new BufferedReader( new InputStreamReader( client_socket.getInputStream() ), 256 );
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return((print_writer!= null) && (buffered_reader!= null));
	}
	
	void closeSocket() {
		/*
		if( !timer_reached ) {
			System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
					"Cancelling the timer since we're closing the socket gracefully");
			timer.cancel();
		}
		/*attempt_number_to_close++;
		System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
					"closeSocket()       Attempt number " + attempt_number_to_close + " to close mob socket.");
		*/
		
		if( message_operations != null ) {
			message_operations.client = null;
			message_operations = null;
		}				
		System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
				"closeSocket()       Wanting to remove mob printer from array list.");
		//MobPrintListOperations.removePrinter( owner, mob_id, this );
		//if( attempt_number_to_close == 1 ) {
		try {
			PrintListOperations.removePrinter( this );
		} catch( Exception e ) {
			System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
					"MobClientSocket       Caught exception while trying to remove mob printer.");
		}
		//}
		//sender_printer = null; //this should be after the call to removePrinter, and it's not necessary by the way
		try {			
			if( print_writer != null ) {
				print_writer.close(); //returns void
				print_writer = null;
				System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
						"Closing print_writer.");
            }
			if (buffered_reader != null) {
            	buffered_reader.close(); //returns void
            	System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
            			"Closing buffered_reader.\n");               
                buffered_reader = null;
            }
			if( client_socket.isConnected() && !client_socket.isClosed() ) {
				System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
						"Closing client_socket.");
				client_socket.close();				
			}
		} catch (IOException e) {
			System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
					"error closing something in the client socket.");
			e.printStackTrace();
		}
		System.gc();
	}
		
	@Override
	public void run() { //this is about listening (receiving) messages, analyzing and processing them.
		//get a message in a loop 
		
		System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
    			"We've started to listen to this new app.");
		do {			
//client_socket != null  && client_socket.isConnected() && !client_socket.isClosed() && buffered_reader != null 
			int count; //https://stackoverflow.com/questions/5113914/large-file-transfer-with-sockets?rq=1
            char[] buffer = new char[ BufferSize ];
            String s = "", message = "", action = "";
        	
            System.out.println(new Timestamp(System.currentTimeMillis()) + " BufferListeners.java " +
        			"ClientSocket   Getting ready to analyze the message: " + s);

            try {
                while( ( count = buffered_reader.read(buffer) ) > 0 ) {
                	s = String.valueOf( buffer, 0, count );
                	
                }
            } catch (Exception e) {
				
			}

		} while( //!timer_reached && 
				 !Setup.keyboard_input.equals("stop") && !Setup.force_stop );
		
		closeSocket();  	
	}
	

}