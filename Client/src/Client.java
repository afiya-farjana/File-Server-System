import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import java.io.*;
import java.net.Socket;
import static java.lang.Integer.parseInt;

public class Client extends JFrame{

    Client(){
        componets();
    }
    public void componets(){
        ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
        this.setIconImage(icon.getImage());
    }

    static final int PORT = 4000;
    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    public static void main(String[] args) {
        Client frm = new Client();
        String ip="127.0.0.1";
        String commandIndex = "INDEX";
        String commandGet = "GET ";
        String commandSend = "SEND ";
        final int[] bytesRead = new int[1];
        final int[] current = {0};
        final FileOutputStream[] fos = {null};
        final BufferedOutputStream[] bos = {null};
        final FileInputStream[] fis = {null};
        final BufferedInputStream[] bis = {null};
        final OutputStream[] os = {null};
        final Socket[] connectionSocket = new Socket[1];
        final PrintWriter[] out = new PrintWriter[1];
        final BufferedReader[] in = new BufferedReader[1];
        final File[] sendFile = new File[1];
        final File[] receiveFile = new File[1];

        frm.setSize(706, 432);
        frm.setTitle("File Server System");
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JTextField t1;
        t1 = new JTextField("");
        t1.setBounds(300, 20, 140,27);
        t1.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JLabel s = new JLabel("Port: ");
        s.setBounds(220,20,53,20);
        s.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JLabel s1 = new JLabel("Status: ");
        s1.setBounds(220,80,153,20);
        s1.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JLabel t2 = new JLabel("Not Connected");
        t2.setBounds(300,80,153,20);
        t2.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));


        JButton b1 = new JButton("Connect");
        b1.setBounds(560, 20, 95, 27);
        //b1.setBackground(new Color(0, 153, 102));
        b1.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));
        final Boolean[] connected = {false};
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(available(parseInt(t1.getText())))
                {
                    t2.setText("Port is not open");
                }
                else
                {
                    t2.setText("Connected to the port");
                }
            }
        });

        JTextField tt=new JTextField("Choose file to send");
        tt.setForeground(Color.LIGHT_GRAY);
        tt.setBounds(343,290,200,27);
        tt.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JTextField t=new JTextField("Choose file to download");
        t.setForeground(Color.LIGHT_GRAY);
        t.setBounds(343,330,200,27);
        t.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JButton upload = new JButton("Upload");
        upload.setBounds(560, 290, 99, 25);
        upload.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JButton browse = new JButton("Browse");
        browse.setBounds(220, 290, 109, 25);
        browse.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JButton showFile = new JButton("Server Files") ;
        showFile.setBounds(220,330,109,25);
        showFile.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JButton download = new JButton("Download") ;
        download.setBounds(560,330,99,25);
        download.setFont(new Font("VAGRounded BT", Font.PLAIN, 14));

        JPanel j = new JPanel();
        j.setSize(195, 432);
        j.setBackground(new Color(0, 102, 102));

        JLabel p = new JLabel("Welcome");

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("");
                String currentDirectoryPath = file.getAbsolutePath();
                JFileChooser choose = new JFileChooser(currentDirectoryPath);
                choose.setDialogTitle("Choose a file");
                if (choose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    sendFile[0] = choose.getSelectedFile();
                    tt.setText(sendFile[0].getName());
                    tt.setForeground(Color.BLACK);
                    t.setText("Choose file to download");
                    t.setForeground(Color.LIGHT_GRAY);
                }
            }
        });


        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sendFile[0] == null) {
                    JOptionPane.showMessageDialog(null, "Please Select a file!");
                } else {
                    try {
                        connectionSocket[0] = new Socket(ip,4000);
                        in[0] = new BufferedReader(new InputStreamReader(connectionSocket[0].getInputStream()));
                        out[0] = new PrintWriter(connectionSocket[0].getOutputStream());
                        out[0].println(commandSend);
                        out[0].println(sendFile[0].getName());
                        out[0].flush();
                        //FileInputStream fileInputStream = new FileInputStream(sendFile[0].getAbsolutePath());
                        //putStream in = new FileInputStream(sendFile[0].getAbsolutePath());
                        //OutputStream outt = connectionSocket[0].getOutputStream();
                        File myFile = new File (sendFile[0].getName());
                        byte [] mybytearray  = new byte [(int)myFile.length()];
                        fis[0] = new FileInputStream(myFile);
                        bis[0] = new BufferedInputStream(fis[0]);
                        bis[0].read(mybytearray,0,mybytearray.length);
                        os[0] = connectionSocket[0].getOutputStream();
                        System.out.println("Sending " + sendFile[0].getName() + "(" + mybytearray.length + " bytes)");
                        os[0].write(mybytearray,0,mybytearray.length);
                        os[0].flush();
                        System.out.println("Done.");

                        out[0].close();
                        bis[0].close();
                        os[0].close();
                        if (out[0].checkError())
                            System.out.println("Error");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        showFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    connectionSocket[0] = new Socket(ip, parseInt(t1.getText()));
                    in[0] = new BufferedReader(new InputStreamReader(connectionSocket[0].getInputStream()));
                    out[0] = new PrintWriter(connectionSocket[0].getOutputStream());
                    out[0].println(commandIndex);
                    out[0].flush();
                    File filefordirectory = new File("Server Files");
                    filefordirectory.mkdir();
                    String directory = filefordirectory.getAbsolutePath();
                    while (true) {
                        String line = in[0].readLine();
                        if (line == null)
                            break;
                        File filelist = new File(directory,line);
                        filelist.createNewFile();
                    }
                    out[0].close();

                    JFileChooser choose = new JFileChooser(directory);
                    choose.setDialogTitle("Choose a file to Download.");
                    if (choose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        receiveFile[0] = choose.getSelectedFile();
                        t.setText(receiveFile[0].getName());
                        t.setForeground(Color.BLACK);
                        tt.setText("Choose file to send");
                        tt.setForeground(Color.LIGHT_GRAY);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    connectionSocket[0] = new Socket(ip, parseInt(t1.getText()));
                    in[0] = new BufferedReader(new InputStreamReader(connectionSocket[0].getInputStream()));
                    out[0] = new PrintWriter(connectionSocket[0].getOutputStream());
                    out[0].println(commandGet + receiveFile[0].getName());
                    out[0].flush();
                    String message = in[0].readLine();
                    System.out.println(message);
                    if (!message.equalsIgnoreCase("OK")) {
                        System.out.println("File not found");
                        System.out.println("Message from server: " + message);
                        return;
                    }

                    File fileto = new File("");
                    String path = fileto.getAbsolutePath()+"/Downloaded";
                    System.out.println(path);
                    PrintWriter fileOut;
                    String filename = in[0].readLine();
                    File downoladedfile = new File(path,filename);

                    byte [] mybytearray  = new byte [1048576];
                    InputStream is = connectionSocket[0].getInputStream();
                    fos[0] = new FileOutputStream(downoladedfile);
                    bos[0] = new BufferedOutputStream(fos[0]);
                    bytesRead[0] = is.read(mybytearray,0,mybytearray.length);
                    current[0] = bytesRead[0];

                    do {
                        bytesRead[0] =
                                is.read(mybytearray, current[0], (mybytearray.length- current[0]));
                        if(bytesRead[0] >= 0) current[0] += bytesRead[0];
                    } while(bytesRead[0] > -1);

                    bos[0].write(mybytearray, 0 , current[0]);
                    bos[0].flush();
                    System.out.println("File " + downoladedfile
                            + " downloaded (" + current[0] + " bytes read)");
                     fos[0].close();
                    bos[0].close();
                    out[0].close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });


        frm.add(tt);
        frm.add(t);
        frm.add(upload);
        frm.add(browse);
        frm.add(showFile);
        frm.add(download);
        frm.add(t1) ;
        frm.add(b1);
        frm.add(s);
        frm.add(s1);
        frm.add(t2);
        frm.add(j);
        frm.add(p);
        frm.setVisible(true);
    }

}

