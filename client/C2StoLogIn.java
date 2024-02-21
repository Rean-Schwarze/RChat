package client;

import pak.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;

public class C2StoLogIn {
    public Socket socket;
    public String name;

    public int CheckUser(Object u) 
    {
        int b=0;//0 for id&pwd wrong, 1 for success, 2 for server is closed, 3 for repeating log in
        try {
            socket = new Socket("127.0.0.1", 1145);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            User user = (User)u;
            user.getMessage().setMessageType(MessageType.MESSAGE_LOGIN_SUCCESS);//?what does it mean??
            user.getMessage().setDate(new Date());
            Object o = (User) user;
            
            oos.writeObject(o);//send the object to server
            //System.out.println("The user:"+user+" has been sent to the server");
            
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //System.out.println("Message：");
            Message Message = (Message)ois.readObject();//read the message return from the server

            if (Message.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
                //System.out.println("登录成功！");
                name=Message.getMessage();
                user.setName(name);
                //create a thread persists the connection between user and server
                C2SThread ccst = new C2SThread(user.getID(),socket);

                //store the socket and thread by hash map
                SocketHashMap.addSocket(user.getID(),socket);
                C2SThreadHashMap.addC2SThread(user.getID(), ccst);

                //start the thread
                ccst.start();
                b = 1;
                //oos.close();
                //ois.close();
            } 
            else if((Message.getMessageType().equals(MessageType.MESSAGE_LOGIN_ID_PWD_WRONG)))
            {
                b=0;
            }
            else if((Message.getMessageType().equals(MessageType.MESSAGE_REPEATING_LOGIN)))
            {
                b=3;
            }
        } catch (ConnectException e) {
            b=2;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
}
