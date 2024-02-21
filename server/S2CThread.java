package server;

import pak.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Iterator;

public class S2CThread extends Thread{
    public Socket socket;
    public String receiver;
    public static ServerFrame serverFrame;
    public ObjectInputStream ois;
    public boolean isRunning = true;

    public S2CThread(Socket s, ServerFrame sf)
    {
        socket=s;
        serverFrame=sf;
    }

    public String getReceiver()
    {
        return this.receiver;
    }
    
    @Override
    public void run()
    {
        //run until the program exit
        while (isRunning) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                Message me = (Message) ois.readObject();
                receiver = me.getReceiver();

                //Gets the receiver's communication thread
                //System.out.println("我是："+me.getSender()+":聊天对象："+receiver);

                //judge the type of receiver and the message 
                // id=10002 for "多人聊天室"
                if ("10002".equals(me.getReceiver())&&(me.getMessageType().equals(MessageType.MESSAGE_COMM_AVG)||me.getMessageType().equals(MessageType.MESSAGE_TEST)))
                {
                    //if sends to the group
                    //use iterator to iterate users online, get users' id and forward the message sent by the user
                    Iterator<String> iterator = S2CThreadHashMap.hashMap.keySet().iterator();
                    String speak = "";
                    while (iterator.hasNext()){
                        //get users' id
                        String sn = iterator.next().toString();

                        //get the corresponding thread
                        S2CThread clientThreas = S2CThreadHashMap.getS2CThread(sn);

                        //get the corresponding receiver
                        String fq = clientThreas.getReceiver();
                        if ((!(sn.equals(me.getSender())))&&("10002".equals(fq)))//all the users excluding the sender
                        {
                            //if the user is currently in the group, send the message to the user

                            S2CThread thread = S2CThreadHashMap.getS2CThread(sn);
                            ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                            //me.setMessageType(MessageType.MESSAGE_COMM_AVG);
                            oos.writeObject(me);
                        }
                        speak="["+me.getDate() + "] "+"用户【"+ me.getSender() + "】在【多人聊天室】发送：" + me.getMessage()+ "  [IP: " + SocketHashMapInServer.getSocket(me.getSender()).getInetAddress() + "]\n";
                    }
                    serverFrame.textAreaLog.append(speak);
                }
                else if (me.getMessageType().equals(MessageType.MESSAGE_COMM_AVG))
                {
                    //the message is sent to specific receiver

                    //print the log to server
                    String mass="["+me.getDate() + "] "+"用户【"+ me.getSender() + "】对【"+me.getReceiver()+"】发送：" + me.getMessage()+ "  [IP: " + SocketHashMapInServer.getSocket(me.getSender()).getInetAddress() + "]\n";
                    serverFrame.textAreaLog.append(mass);

                    if (me.getMessageType().equals(MessageType.MESSAGE_COMM_AVG)&&
                            "我上线啦!!!".equals(me.getMessage()))
                    {
                        if (S2CThreadHashMap.hashMap.containsKey(me.getReceiver())) 
                        {
                            S2CThread atc = S2CThreadHashMap.getS2CThread(me.getReceiver());
                            ObjectOutputStream ooss = new ObjectOutputStream(atc.socket.getOutputStream());
                            //me.setReceiver("你");
                            ooss.writeObject(me);
                        }
                    }
                    else
                    {
                    //Determine whether the receiver of the message is online, and if it is not, sent the hint
                        if (S2CThreadHashMap.hashMap.containsKey(me.getReceiver())) 
                        {
                            S2CThread atc = S2CThreadHashMap.getS2CThread(me.getReceiver());
                            ObjectOutputStream ooss = new ObjectOutputStream(atc.socket.getOutputStream());
                            ooss.writeObject(me);
                        }
                        else
                        {
                            ObjectOutputStream oosss = new ObjectOutputStream(socket.getOutputStream());
                            Message mm = new Message();
                            mm.setSender("10001");//id=10001 for system
                            mm.setMessage("对方暂时不在线。");
                            mm.setDate(new Date());
                            mm.setMessageType(MessageType.MESSAGE_COMM_AVG);
                            oosss.writeObject(mm);
                        }
                    }
                }
                else if (me.getMessageType().equals(MessageType.MESSAGE_ON_LINE))
                {

                    SelectMySQLFriends smf = new SelectMySQLFriends();
                    String friends = smf.getFriends();
                    //Determine that the message is to request the Friends Online List
                    String oo = S2CThreadHashMap.getFriendsOnline();

                    //Get current online list of friends
                    //System.out.println("在线："+oo);
                    Message message = new Message();
                    message.setDate(new Date());
                    message.setSender("10001");
                    message.setMessageType(MessageType.MESSAGE_FRIEND);
                    if ("null".equals(oo)){
                        oo = "@";
                    }

                    //Send back messages to all online users
                    message.setMessage(oo);
                    message.setReceiver(friends);//可能会bug
                    Iterator<String> iterator = S2CThreadHashMap.hashMap.keySet().iterator();
                    while (iterator.hasNext()){
                        String sd = iterator.next().toString();
                        S2CThread thread = S2CThreadHashMap.getS2CThread(sd);
                        ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                        oos.writeObject(message);
                    }
                }
            } catch(EOFException eofe){
                String exitID=new String();
                Iterator<String> iterator = SocketHashMapInServer.hashMap.keySet().iterator();
                while (iterator.hasNext()){
                    exitID=iterator.next().toString();
                    if(SocketHashMapInServer.getSocket(exitID).equals(socket))
                    {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        S2CThreadHashMap.removeS2CThread(exitID);
                        SocketHashMapInServer.removeSocket(exitID);
                    }
                }
                serverFrame.flushThread(exitID);
                isRunning=false;
                break;
            } catch(SocketException se){
                String exitID=new String();
                Iterator<String> iterator = SocketHashMapInServer.hashMap.keySet().iterator();
                while (iterator.hasNext()){
                    exitID=iterator.next().toString();
                    System.out.println("exitid="+exitID);
                    System.out.println("exitso="+SocketHashMapInServer.getSocket(exitID));
                    System.out.println("exitsoic="+SocketHashMapInServer.getSocket(exitID).isClosed());
                    System.out.println("exitsoicon="+SocketHashMapInServer.getSocket(exitID).isConnected());
                    if(SocketHashMapInServer.getSocket(exitID).isClosed())
                    {
                        S2CThreadHashMap.removeS2CThread(exitID);
                        SocketHashMapInServer.removeSocket(exitID);
                    }
                }
                System.out.println(SocketHashMapInServer.hashMap.size());
                serverFrame.flushThread(exitID);
                isRunning=false;
                break;
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                try {
                    socket.close();
                    return;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
}
