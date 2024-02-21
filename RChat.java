import javax.swing.*;

import client.*;

import java.io.*;

public class RChat
{
    public RChat() throws IOException
    {
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame.loginInit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws IOException
    {
        RChat main=new RChat();
    }
}