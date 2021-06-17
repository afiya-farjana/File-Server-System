import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    static final int PORT = 4000;
    public static void main(String[] args) {

        File directory;
        File file = new File("");
        directory = new File(file.getAbsolutePath());

        try {
            ServerSocket socket = new ServerSocket(PORT);
            System.out.println("Server has Started");
            System.out.println("Listening on port "+PORT);
            while (true) {
                Socket connectionSocket = socket.accept();
                handleConnection(directory,connectionSocket);
            }
        }
        catch (Exception e) {
            System.out.println("Server shut down unexpectedly");
            System.out.println("Error: " + e);
            return;
        }
    }

    private static void handleConnection(File directory,Socket connection) {
        String cmd = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //InputStream inFromClient = null;
            PrintWriter out = new PrintWriter( connection.getOutputStream() );
            OutputStream output = null;
            //DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
            //ObjectOutputStream oout = new ObjectOutputStream(output);
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            cmd = in.readLine();
            System.out.println(cmd);
            if (cmd.equalsIgnoreCase("index")) {
                sendIndex(directory, out);
            }
            else if (cmd.toLowerCase().startsWith("get")){
                String fileName = cmd.substring(3).trim();
                File file = new File(directory,fileName);
                if ( (! file.exists()) || file.isDirectory() ) {
                    out.println("ERROR");
                }
                else{
                    out.println("OK");
                    out.println(fileName);
                    out.flush();
                    byte [] mybytearray  = new byte [(int)file.length()];
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    output = connection.getOutputStream();
                    System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
                    output.write(mybytearray,0,mybytearray.length);
                    output.flush();
                    out.close();
                    bis.close();
                    output.close();
                    System.out.println("Done.");
                }

            }else if(cmd.toLowerCase().startsWith("send")){
                receiveFile(in,connection,fos,bos);
            }
            else {
                out.println("ERROR unsupported command");
                out.flush();
            }
            System.out.println("OK  " + connection.getInetAddress() + " " + cmd);
        }
        catch (Exception e) {
            System.out.println("ERROR " + connection.getInetAddress() + " " + cmd + " " + e);
        }
        finally {
            try {
                connection.close();
            }
            catch (IOException e) {
            }
        }
    }

    private static void receiveFile(BufferedReader in,Socket connection,FileOutputStream fos,BufferedOutputStream bos) throws Exception{
        String files = null;
        files = in.readLine();
        File f = new File(files);
            long size = files.length();
             int bytesRead;
             int current = 0;
            byte [] mybytearray  = new byte [1048576];
            InputStream is = connection.getInputStream();
            fos = new FileOutputStream(files);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + files
                    + " uploaded (" + current + " bytes read)");
            fos.close();
            bos.close();

    }

    private static void sendIndex(File directory, PrintWriter out) throws Exception {
        String[] fileList = directory.list();

        for (int i = 0; i < fileList.length; i++)
        {
            if(fileList[i].contains("."))
                out.println(fileList[i]);
        }
        out.flush();
        out.close();
        if (out.checkError())
            throw new Exception("Error while transmitting data.");
    }

}
