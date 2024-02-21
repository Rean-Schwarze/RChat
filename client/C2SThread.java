package client;

import pak.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

public class C2SThread extends Thread{
    private Socket socket;
    private String id;
    public boolean isRunning=true;

    public C2SThread(String id,Socket s) {
        this.id = id;
        this.socket = s;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message m = (Message) ois.readObject();
                //System.out.println("收到："+m.getMessage());

                if (m.getMessageType().equals(MessageType.MESSAGE_COMM_AVG)) 
                {

                    //String senderName=new SearchInMySQL().getNamebyID(m.getSender());

                    //get the chat frame through socket
                    if (ChatFrameHashMap.hashMap.containsKey(socket)) {
                        ChatFrame cjf = ChatFrameHashMap.getChatFrame(socket);
                        cjf.updateText(m);
                    }
                }
                else if(m.getMessageType().equals(MessageType.MESSAGE_FRIEND))
                {
                    String fff = m.getReceiver();
                    String[] split = fff.split("&");
                    for (int i = 0; i < split.length; i++) {
                        String substring = split[i].substring(0);
                        if (substring.length()!=0) {
                            split[i] = substring;
                        }
                    }
                    if (!FriendListHashMap.hashMap.containsKey(socket)){
                        //create the friend list panel
                        FriendList wc = new FriendList(id,split);
                        //put it into the hash map
                        FriendListHashMap.addFriendList(socket,wc);
                    }
                    //modify the friend list.
                    //Update real-time friend availability to the friends list
                    Iterator<Socket> it = FriendListHashMap.hashMap.keySet().iterator();
                    String updateuser  = m.getMessage();
                    while (it.hasNext()){
                        FriendList Friendlist = FriendListHashMap.getFriendList(it.next());
                        Friendlist.updateFriend(updateuser);
                    }
                    FriendUpdate.updateUser = updateuser;
                    FriendUpdate.updateUserArray = split;
                }
                //ois.close();
            } catch(SocketException se)
            {
                isRunning=false;
                break;
            } catch (IOException e) {
                // try {
                //     socket.close();
                // } catch (IOException ex) {
                //     ex.printStackTrace();
                // }
                // JOptionPane.showMessageDialog(null,"连接已中断！");
                // System.exit(0);
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
