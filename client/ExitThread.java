package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;

import pak.*;

public class ExitThread extends Thread {
    User user;

    public ExitThread(User u)
    {
        user=u;
    }

    @Override
    public void run()
    {
        Iterator<String> iterator = C2SThreadHashMap.hashMap.keySet().iterator();
        while(iterator.hasNext())
        {
            String id=iterator.next().toString();
            if(id==user.getID())
            {
                try (ObjectOutputStream oos = new ObjectOutputStream(C2SThreadHashMap.getC2SThread(user.getID()).getSocket().getOutputStream())) {
                    Message message=new Message();
                    message.setMessageType(MessageType.MESSAGE_EXIT);
                    message.setSender(user.getID());
                    message.setReceiver("10001");
                    message.setDate(new Date());
                    oos.writeObject(message);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                C2SThreadHashMap.getC2SThread(id).isRunning=false;
                C2SThreadHashMap.removeC2SThread(id);
            }
            
        }
        iterator = S2CThreadHashMap.hashMap.keySet().iterator();
        while(iterator.hasNext())
        {
            String id=iterator.next().toString();
            if(id==user.getID()) S2CThreadHashMap.removeS2CThread(id);
        }
        
        iterator = SocketHashMap.hashMap.keySet().iterator();
        while(iterator.hasNext())
        {
            String id=iterator.next().toString();
            if(id==user.getID())
            {
                ChatFrameHashMap.removeChatFrame(SocketHashMap.getSocket(id));
                FriendListHashMap.removeFriendList(SocketHashMap.getSocket(id));
                MainFrameHashMap.removeMainFrame(SocketHashMap.getSocket(id));
                try {
                    SocketHashMap.getSocket(id).close();
                    //*********** VERY VERY VERY VERY IMPORTANT!!!!!!!!*******
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(SocketHashMap.getSocket(id).isClosed())
                {
                    SocketHashMap.removeSocket(id);
                    System.exit(0);
                }
                
            }
        }
    }
}
