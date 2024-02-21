package server;

import pak.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Server {
    Object object;
    ServerFrame serverFrame;
    S2CThread thread;

    public Server(ServerFrame sf)
    {
        serverFrame=sf;
        try
        {
            ServerSocket serverSocket = new ServerSocket(1145);
            sf.serverLabel.setText(sf.serverName+"  IP：127.0.0.1  Port："+String.valueOf(serverSocket.getLocalPort()));
            Date date=new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            serverFrame.textAreaLog.append("------------------------------【"+formatter.format(date)+"】------------------------------\n");
            //System.out.println("The server has been started successfully.");
            //run until the program exit
            while(true)
            {
                //flushThread(sf);
                //get the socket
                Socket socket = serverSocket.accept();
                //read the ObjectInputStream
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                object = ois.readObject();
                //System.out.println("The server received an object.");

                if (object instanceof User){
                    //System.out.println("Trying to log in...");
                    User u = (User) object;
                    SocketHashMapInServer.addSocket(u.getID(), socket);
                    Message me = new Message();
                    String id = u.getID();
                    String pwd =u.getPassword();
                    ToMySQLLogIn login=new ToMySQLLogIn(id,pwd);
                    if (login.ifright()) {
                        if (!S2CThreadHashMap.hashMap.containsKey(id)&&u.getMessage().getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
                            me.setMessageType(MessageType.MESSAGE_LOGIN_SUCCESS);
                            me.setMessage(login.getAccount().getName());//return the name
                            //The login is successful, and a thread is created to maintain contact with this client
                            thread = new S2CThread(socket,serverFrame);
                            //put the thread into hashmap
                            S2CThreadHashMap.addS2CThread(u.getID(), thread);
                            serverFrame.userNumber.setText(serverFrame.userNumberText+S2CThreadHashMap.hashMap.size());
                            serverFrame.freshUserList();
                            serverFrame.textAreaLog.append("["+u.getMessage().getDate() + "] "+"用户【"+ id + "】已上线" + "  [IP: " + SocketHashMapInServer.getSocket(u.getID()).getInetAddress() + "]\n");
                            thread.start();
                        }else{
                            //Set the information for the login failure
                            me.setMessageType(MessageType.MESSAGE_REPEATING_LOGIN);
                            me.setSender("server");
                            me.setReceiver(id);
                            me.setMessage(MessageType.MESSAGE_REPEATING_LOGIN);
                            me.setDate(new Date());
                            serverFrame.textAreaLog.append("["+u.getMessage().getDate() + "] "+"用户【"+ id + "】重复登录" + "  [IP: " + SocketHashMapInServer.getSocket(u.getID()).getInetAddress() + "]\n");
                            SocketHashMapInServer.removeSocket(id);
                        }
                    } else {
                        //Set the information for the login failure
                        me.setMessageType(MessageType.MESSAGE_LOGIN_ID_PWD_WRONG);
                        me.setSender("server");
                        me.setReceiver(id);
                        me.setMessage(MessageType.MESSAGE_LOGIN_ID_PWD_WRONG);
                        me.setDate(new Date());
                        serverFrame.textAreaLog.append("["+u.getMessage().getDate() + "] "+"用户【"+ id + "】账号密码错误" + "  [IP: " + SocketHashMapInServer.getSocket(u.getID()).getInetAddress() + "]\n");
                        SocketHashMapInServer.removeSocket(id);
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(me);
                    //oos.close();
                }

                if (object instanceof UserSignUp)
                {
                    UserSignUp u = (UserSignUp)object;
                    Message me = new Message();
                    
                    //if the object is UserSignUp，sign up the information of UserSignUp in sql
                    if (new ToMySQLSignUp().toMysqlSignUp(u)){
    
                        //if sign up successfully, send a message to client
                        me.setMessageType(MessageType.MESSAGE_SIGNUP_SUCCESS);
                        Message Message = new Message();
                        Message.setDate(new Date());
                        //print the sign up log to server
                        serverFrame.textAreaLog.append("["+Message.getDate() + "] "+"用户【"+ u.getName() + "】注册成功" + "\n");
                    }
                    else
                    {
                        //if fail to sign up, also send a message to client
                        me.setMessageType(MessageType.MESSAGE_SIGNUP_FAIL);
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(me);
                    oos.writeObject(u);
                    //oos.close();
                }

                // if (object instanceof Exit)
                // {
                //     System.out.println("Trying to exit...");
                //     Exit e = (Exit) object;
                //     ThreadInToHashMap.getS2CThread(e.id).isRunning=false;
                //     ThreadInToHashMap.removeS2CThread(e.id);
                //     serverFrame.userNumber.setText(serverFrame.userNumberText+ThreadInToHashMap.hashMap.size());
                //     serverFrame.freshUserList();
                //     serverFrame.textAreaLog.append("用户："+ e.id + "已下线" + "  [" + e.getMessage().getDate() + "]" + "\r\n");
                // }
                //ois.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {
        ServerFrame serverFrame=new ServerFrame();
        serverFrame.serverFrameInit();
        Server server=new Server(serverFrame);
    }
}
