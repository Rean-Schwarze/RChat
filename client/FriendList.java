package client;

import pak.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class FriendList extends MyFrame implements ActionListener, MouseListener {

    private static final long serialVersionUID = 5355175509355674700L;
    private static JPanel friendPanel = new JPanel();
    final static int WIDTH=381;
    final static int HEIGHT=772;
    
    JScrollPane jScrollPane;
    String id, friend;
    int friendNumber=0;
    JLabel[] jLabels; // used to store 30 friends(or group)
    String[] friendlist;
    JLabel friendName=new JLabel();

    private static MyFrame mainFrame;
    private static User user;

    public void updateFriend(String mass) {
        if (!"null".equals(mass)) {
            // split the message sent from the server
            friendlist = mass.split("@");
            for (int i = 0; i < friendlist.length; i++) {
                String List = friendlist[i].replaceFirst("@", "");
                friendlist[i] = List.trim();
            }

            // update the label list
            for (int i = 0; i < friendlist.length; i++) {
                for (int j = 1; j < jLabels.length; j++) {
                    if (friendlist[i].equals(jLabels[j].getText())) {
                        jLabels[j].setEnabled(true);
                    }
                }
            }
        }
    }

    public FriendList(String i,String[] friends)
    {
        id=i;
        friendlist=friends;
        for (int j = 0; j < friends.length; j++) {
            if (!("null".equals(friends[j]))&&friends[j]!=""){
                friendNumber ++;
            }
        }
    }

    public JPanel friendInit(MyFrame mf, User u)
    {
        mainFrame=mf;
        user=u;
        try {
            initializeData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        friendPanel.setSize(calres(WIDTH),calres(HEIGHT));
        friendPanel.setLocation(calres(80),calres(92));
        friendPanel.setBackground(new Color(230, 230, 230));
        friendPanel.setOpaque(false);
        friendPanel.setLayout(null);

        JPanel listPanel=new JPanel();
        listPanel.setSize(calres(WIDTH),calres(HEIGHT));
        listPanel.setLocation(calres(0),calres(0));
        listPanel.setBackground(new Color(230, 230, 230));
        listPanel.setOpaque(false);
        listPanel.setLayout(null);
        jLabels = new JLabel[friendNumber];
        for(int i=0;i<jLabels.length;i++)
        {
            jLabels[i] = new JLabel(friendlist[i]);
            jLabels[i].setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
            jLabels[i].setBackground(new Color(230,230,230));
            jLabels[i].setBounds(calres(0),calres(i*96),calres(381),calres(96));
            jLabels[i].setBorder(null);
            jLabels[i].setBorder(new CompoundBorder(jLabels[i].getBorder(),new EmptyBorder(calres(0),calres(95),22,0)));
            jLabels[i].setEnabled(true);
            jLabels[i].setOpaque(true);

            jLabels[i].addMouseListener(this);
            listPanel.add(jLabels[i]);
        }
        jScrollPane=new JScrollPane();//create a scroll pane to put the textarea
		jScrollPane.setBounds(calres(0),calres(0),calres(WIDTH),calres(HEIGHT));
		jScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setViewportView(listPanel);
        friendPanel.add(jScrollPane);

        return friendPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==1)
        {
            //get the name that the friend has been clicked
            friend = ((JLabel) e.getSource()).getText();
            //String friendID=new SearchInMySQL().getIDbyName(friend);

            ChatFrame chatFrame=new ChatFrame(friend,user);
            ChatFrameHashMap.addChatFrame(SocketHashMap.getSocket(user.getID()), chatFrame);
            JPanel chatPanel=chatFrame.chatInit();
            mainFrame.add(chatPanel);

            if(!friendName.getText().isEmpty()) friendName.setText(friend);
            else
            {
                friendName=new JLabel();
                friendName.setText(friend);
                friendName.setFont(new Font("微软雅黑",Font.PLAIN,calres(28)));
                friendName.setBounds(calres(505),calres(28),calres(648),calres(38));
                mainFrame.add(friendName);
            }

            SwingUtilities.updateComponentTreeUI(mainFrame);//refresh the mainframe

            //get user's socket
            // Socket socket = SocketHashMap.getSocket(user.getID());
            // try {
            //     //set the message and send to the server
            //     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //     Message mes = new Message();

            //     //judge
            //     if ("多人聊天室".equals(friend)){
            //         mes.setSender(user.getID());
            //         mes.setReceiver(friendID);
            //         mes.setDate(new Date());
            //         mes.setMessage(user.getID()+" 加入聊天室");
            //     }else{
            //         mes.setSender(user.getID());
            //         mes.setReceiver(friendID);
            //         mes.setDate(new Date());
            //         mes.setMessage("我上线啦!!!");
            //     }
            //     mes.setMessageType(MessageType.MESSAGE_COMM_AVG);
            //     mes.setDate(new Date());
            //     oos.writeObject(mes);
            // } catch (IOException ex) {
            //     ex.printStackTrace();
            // }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JLabel j1 = (JLabel)e.getSource();
        j1.setBackground(new Color(216,216,216));
        String text=j1.getText();
        j1.setEnabled(false);
        for(int i=0;i<jLabels.length;i++)
        {
            if(jLabels[i].getText()!=text) jLabels[i].setEnabled(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JLabel j1 = (JLabel)e.getSource();
        j1.setBackground(new Color(230,230,230));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JLabel j1 = (JLabel)e.getSource();
        j1.setBackground(new Color(216,216,216));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JLabel j1 = (JLabel)e.getSource();
        j1.setBackground(new Color(230,230,230));
    }
}
