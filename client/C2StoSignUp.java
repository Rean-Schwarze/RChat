package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pak.*;

public class C2StoSignUp
{
    int id;
    boolean result=false;
    Object object;
    Message me;
    boolean signUp(UserSignUp u)
    {
        try {
            try (Socket socket = new Socket("127.0.0.1", 1145)) {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //send the information of sign up to server
                oos.writeObject(u);
                
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());//receive the information from server.
                object=ois.readObject();//if sign up success, receive message and u from server in order.
                if(object instanceof Message)
                {
                    me = (Message)object;
                }
                else if(object instanceof UserSignUp)
                {
                    u=(UserSignUp)object;
                }

                object=ois.readObject();
                if(object instanceof Message)
                {
                    me = (Message)object;
                }
                else if(object instanceof UserSignUp)
                {
                    u=(UserSignUp)object;
                }

                if (me.getMessageType().equals(MessageType.MESSAGE_SIGNUP_SUCCESS)){
                    result = true;
                    id=u.getID();
                }
                oos.close();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getID()
    {
        return id;
    }
}