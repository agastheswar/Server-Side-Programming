import java.net.*;
import java.io.*;

class SockClient2 {
     public static void main (String args[]) throws Exception {
        Socket          sock = null;
        OutputStream    out = null;
        InputStream     in = null;
        byte[] i1;

	if (args.length != 1) {
	    System.out.println("USAGE: java SockClient int1");
	    System.exit(1);
	}
		if(!args[0].equals("reset")){
			try{
				 int testInteger = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException nfe) {
			    System.out.println("Command line args must be integers");
			    System.exit(2);
			}	
		}
		
        try {
        	i1 = args[0].getBytes("UTF-8");
            sock = new Socket("localhost", 8888);
            out = sock.getOutputStream();
            in = sock.getInputStream();
            out.write(i1);
            byte[] result_byte = new byte[100];
            in.read(result_byte);
            String result_string = new String(result_byte, "UTF-8");
            int result = Integer.parseInt(result_string.trim());
            System.out.println("Result is " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)  out.close();
            if (in != null)   in.close();
            if (sock != null) sock.close();
        }
    }

}