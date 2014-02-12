import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

class WebServer1{

   
    public static void main(String args[]) throws NumberFormatException, IOException {
        if (args.length != 1) {
            System.out.println("Usage: WebServer <port>");
            System.exit(1);
        }
        
        WebServer1 server = new WebServer1(Integer.parseInt(args[0]));
    
    }

    public WebServer1(int port) throws IOException {
        
        ServerSocket    server = null;
        Socket          sock = null;

        try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
        while (server.isBound() && !server.isClosed()) {
            System.out.println("Ready...");
            try {
                sock = server.accept();
                serverhelp serv = new serverhelp(sock);
                Thread servthread = new Thread(serv, "serverThread");
                servthread.start();
            } catch(Exception e){
            	e.printStackTrace();
            }
        }
        //*** TASK: Open the server socket on the specified port
        //*** Loop forever accepting socket requests
	//***   Process each request in its own thread
        //***   Get the response bytes from createResponse
        //***   Write the bytes to the socket's output stream
        //***   close streams and socket appropriately
	//***   Try to anticipate error conditions (e.g. file not found?)
        
        
    }
    
}

class serverhelp extends Thread{
	Socket sock = null;
	InputStream in = null;
    OutputStream out = null;
	public serverhelp(Socket sock){
		super("serverThread");
		this.sock = sock;
	}
	
	public void run(){
		 try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
	         byte[] response = createResponse(in);
	         System.out.println(response.toString());
	         out.write(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
				try {
					if (out != null)
					out.close();
					 if (in != null)   in.close();
			           if (sock != null) sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           
        }
         
	}
	
	 public byte[] createResponse(InputStream inStream) {

	        byte[] response = null;
	        BufferedReader in = null;

	        try {

	            // Read from socket's input stream.  Must use an
	            // InputStreamReader to bridge from streams to a reader
	            in = new BufferedReader(
	                        new InputStreamReader(inStream, "UTF-8"));

	            // Get header and save the filename from the GET line:
	            //    example GET format: GET /index.html HTTP/1.1

	            String filename = null;
		    String line = in.readLine();
		    System.out.println("Received: " + line);
	            if (line != null && !line.trim().equals("")) {
			// I will use an artificial delay to test your code
			 try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringTokenizer st = new StringTokenizer(line);
			if (st.nextToken().equals("GET") && st.hasMoreTokens()) {
			    filename = st.nextToken();
			    if (filename.startsWith("/")) {
				filename = filename.substring(1);
			    }
			}
	            }
	System.out.println("FINISHED REQUEST, STARING RESPONSE\n");

	            // Generate an appropriate response to the user
	            if (filename == null) {
	                response =
	                    "<html>Illegal request: no GET</html>".getBytes();
	            } else {            
	                File file = new File(filename);
	                if (!file.exists()) {
	                    response = ("<html>File not found: " +
	                                filename + "</html>").getBytes();
	                } else {
	                    response = readFileInBytes(file);
	                }
	            }
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	            response = ("<html>ERROR: " +
	                        e.getMessage() + "</html").getBytes();
	        }
		System.out.println("RESPONSE GENERATED!");
	        return response;
	    }

	    /** Read bytes from a file and return them in the byte array.
	        We read in blocks of 512 bytes for efficiency.
	    */
	    public byte[] readFileInBytes(File f)
	        throws IOException {

	        FileInputStream file = new FileInputStream(f);
	        ByteArrayOutputStream data = new ByteArrayOutputStream(file.available());

	        byte buffer[] = new byte[512];
	        int numRead = file.read(buffer);
	        while (numRead > 0) {
	            data.write(buffer, 0, numRead);
	            numRead = file.read(buffer);
	        }
	        file.close();

	        byte[] result =  data.toByteArray();
	        data.close();

	        return result;
	    }
}
