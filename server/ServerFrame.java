package server;

import pak.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.swing.*;

public class ServerFrame extends MyFrame {
	private static final long serialVersionUID = -1944003023527090391L;

	public static MyFrame serverFrame=new MyFrame();
	final static int MAIN_WIDTH=1332;
    final static int MAIN_HEIGHT=864;

	MyDialogFrame dialogFrame=new MyDialogFrame();

	JLabel userNumber,serverLabel;
	String serverName;
	public String userNumberText;

	public JTextArea textAreaLog;

	public JList<String> userList;
	JScrollPane scrollpaneUser;

	public void flushThread(String id)
    {
		Message m=new Message();
		m.setDate(new Date());
		userNumber.setText(userNumberText+S2CThreadHashMap.hashMap.size());
		try {
			freshUserList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		textAreaLog.append("["+m.getDate() + "] "+"用户【"+ id + "】已下线" + "\n");
    }

	protected void freshUserList() throws IOException
	{
		Iterator<String> iterator = S2CThreadHashMap.hashMap.keySet().iterator();
		String[] userListArray=new String[30];
		int count=0;
		while (iterator.hasNext()){
			userListArray[count]=iterator.next();
			count++;
		}
		if(count==0) userListArray=new String[30];
		userList.setListData(userListArray);
	}

	protected void clearUserList() throws IOException
	{
		Iterator<String> iterator = S2CThreadHashMap.hashMap.keySet().iterator();
		String[] userListArray=new String[30];
		while (iterator.hasNext()){
			S2CThreadHashMap.removeS2CThread(iterator.next());
		}
		userList.setListData(userListArray);
	}

	protected void closeServer() {
		Iterator<String> iterator = S2CThreadHashMap.hashMap.keySet().iterator();
		while (iterator.hasNext()){
			try {
				S2CThreadHashMap.getS2CThread(iterator.next()).socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.dispose();
		System.exit(0);
	}

    protected void saveLog() {
		Date date=new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			FileOutputStream fileoutput = new FileOutputStream("log "+formatter.format(date)+".txt", true);
			String temp = textAreaLog.getText();
			fileoutput.write(temp.getBytes());
			fileoutput.close();
			SwingUtilities.invokeLater(() -> {
				dialogFrame.dialogInit("small",langLocal.saveLogSuccess);
			});
			//JOptionPane.showMessageDialog(null, "记录保存在log.txt");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void serverFrameInit()
	{
		firstInit(serverFrame, MAIN_WIDTH, MAIN_HEIGHT);

        JLabel leftBar=new JLabel(setPic("resources/bg/serverFrame_leftbar.png",81,MAIN_HEIGHT));
        leftBar.setBounds(0,0,calres(81),calres(MAIN_HEIGHT));
        serverFrame.add(leftBar);

		closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JButton hideButton=new JButton(setIcon("hide_normal.png",calres(50),calres(38)));
        hideButton.setBorderPainted(false);
        hideButton.setFocusPainted(false);
        hideButton.setRolloverIcon(setIcon("hide_hover.png",calres(50),calres(38)));
        hideButton.setPressedIcon(setIcon("hide_press.png",calres(50),calres(38)));
        hideButton.setSize(calres(50),calres(38));
        hideButton.setLocation(calres(MAIN_WIDTH-50*2),0);
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        serverFrame.add(hideButton);

		userNumberText=langLocal.userNumber;
        userNumber=new JLabel(userNumberText);
        userNumber.setFont(new Font("微软雅黑",Font.PLAIN,calres(28)));
        userNumber.setBounds(calres(125),calres(28),calres(356),calres(38));
        serverFrame.add(userNumber);

		serverLabel=new JLabel();
		serverLabel.setFont(new Font("微软雅黑",Font.PLAIN,calres(28)));
		serverLabel.setBounds(calres(510),calres(28),calres(648),calres(38));
		serverName=new String(langLocal.serverInfo+"RChat");
		serverFrame.add(serverLabel);

		JLabel serverLogText=new JLabel(langLocal.serverLog);
		serverLogText.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
        serverLogText.setBounds(calres(856),calres(92),calres(111),calres(35));
        serverFrame.add(serverLogText);

		JButton closeServerBtn=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		closeServerBtn.setBorderPainted(false);
        closeServerBtn.setFocusPainted(false);
		closeServerBtn.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		closeServerBtn.setBounds(calres(964),calres(796),calres(150),calres(48));
		closeServerBtn.setText(langLocal.closeServer);
		closeServerBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        closeServerBtn.setVerticalTextPosition(SwingConstants.CENTER);
		closeServerBtn.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		closeServerBtn.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		closeServerBtn.addActionListener((e) -> closeServer());
		serverFrame.add(closeServerBtn);

		JButton saveLogBtn=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		saveLogBtn.setBorderPainted(false);
        saveLogBtn.setFocusPainted(false);
		saveLogBtn.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		saveLogBtn.setBounds(calres(1133),calres(796),calres(150),calres(48));
		saveLogBtn.setText(langLocal.saveLog);
		saveLogBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        saveLogBtn.setVerticalTextPosition(SwingConstants.CENTER);
		saveLogBtn.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		saveLogBtn.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		saveLogBtn.addActionListener((e) -> saveLog());
		serverFrame.add(saveLogBtn);

		JButton clearBtn=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
		clearBtn.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		clearBtn.setBounds(calres(795),calres(796),calres(150),calres(48));
		clearBtn.setText(langLocal.clearList);
		clearBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        clearBtn.setVerticalTextPosition(SwingConstants.CENTER);
		clearBtn.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		clearBtn.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		clearBtn.addActionListener((e) -> {
			try {
				clearUserList();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		serverFrame.add(clearBtn);

		JScrollPane scrollpaneLog=new JScrollPane();//create a scroll pane to put the textarea
		scrollpaneLog.setBounds(calres(505),calres(140),calres(777),calres(635));
		scrollpaneLog.getVerticalScrollBar().setUI(new MyScrollBarUI());
		scrollpaneLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		textAreaLog=new JTextArea();
		textAreaLog.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
		textAreaLog.setBounds(calres(505),calres(140),calres(777),calres(635));
		textAreaLog.setBackground(new Color(245,245,245));
		textAreaLog.setLineWrap(true);
        textAreaLog.setWrapStyleWord(true);
		scrollpaneLog.setViewportView(textAreaLog);//put textarea to the scroll pane
		serverFrame.add(scrollpaneLog);

		JLabel userListText=new JLabel(langLocal.userList);
		userListText.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
        userListText.setBounds(calres(233),calres(92),calres(140),calres(35));
        serverFrame.add(userListText);

		scrollpaneUser=new JScrollPane();//create a scroll pane to put the textarea
		scrollpaneUser.setBounds(calres(105),calres(140),calres(381),calres(719));
		scrollpaneUser.setBackground(new Color(202,202,202));
		scrollpaneUser.getVerticalScrollBar().setUI(new MyScrollBarUI());
		scrollpaneUser.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		userList=new JList<String>();
		userList.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
		userList.setFixedCellWidth(calres(355));
		userList.setFixedCellHeight(calres(35));
		scrollpaneUser.setViewportView(userList);
		serverFrame.add(scrollpaneUser);

        serverFrame.setVisible(true);
	}
}
