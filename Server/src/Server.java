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
        String cmd = "Command not read";
        BufferedReader ashse;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter out = new PrintWriter( connection.getOutputStream() );
            cmd = in.readLine();
            System.out.println(cmd);
            if (cmd.equalsIgnoreCase("index")) {
                sendIndex(directory, out);
            }
            else if (cmd.toLowerCase().startsWith("get")){
                String fileName = cmd.substring(3).trim();
                sendFile(fileName, directory, out);
            }
            else if(cmd.toLowerCase().startsWith("send")){
                receiveFile(in);
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

    private static void sendFile(String fileName, File directory, PrintWriter out) throws Exception {
        File file = new File(directory,fileName);
        if ( (! file.exists()) || file.isDirectory() ) {
            out.println("ERROR");
        }
        else {
            out.println("OK");
            out.println(fileName);
            BufferedReader fileIn = new BufferedReader( new FileReader(file) );
            while (true) {
                String line = fileIn.readLine();
                if (line == null)
                    break;
                out.println(line);
            }
        }
        out.flush();
        out.close();
        if (out.checkError())
            throw new Exception("Error while transmitting data.");
    }

    private static void receiveFile(BufferedReader in) throws Exception {
        File file = new File(in.readLine());
        PrintWriter print = new PrintWriter(new FileWriter(file));
        while(true)
        {
            String line = in.readLine();
            if(line == null)
                break;
            print.println(line);
            System.out.println(line);
        }
        if (print.checkError())
            throw new Exception("Error while transmitting data.");
    }
}
