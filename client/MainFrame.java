package client;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import pak.*;

import java.io.*;

public class MainFrame extends MyFrame{
    private static final long serialVersionUID = -1457090319578686341L;

    static SystemTray tray = SystemTray.getSystemTray();

    private static MyFrame mainFrame = new MyFrame();
    final static int MAIN_WIDTH=1332;
    final static int MAIN_HEIGHT=864;

    public static JPanel bgPanel;

    public static ExitThread exitThread;

    static User user;

    public static void mainInit(User u) throws IOException
    {
        user=u;

        firstInit(mainFrame,MAIN_WIDTH,MAIN_HEIGHT);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setVisible(false);
            }
        });

        exitThread=new ExitThread(user);
        Runtime.getRuntime().addShutdownHook(exitThread);
        
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
                mainFrame.setExtendedState(JFrame.ICONIFIED);
            }
        });
        mainFrame.add(hideButton);

        miniTray(mainFrame);

        bgPanel=new JPanel();
        bgPanel = (JPanel) mainFrame.getContentPane();//Set the content panel to the JPanel panel
        bgPanel.setOpaque(false);//Set the panel to transparent
        JLabel bg = new JLabel(setPic("resources/bg/serverFrame_bg.png",MAIN_WIDTH,MAIN_HEIGHT));
        mainFrame.getLayeredPane().add(bg, Integer.valueOf(Integer.MIN_VALUE));//Set JLabel to the lowest level before adding components on JLabel
        bg.setBounds(0, 0, calres(MAIN_WIDTH), calres(MAIN_HEIGHT));
        bgPanel.setLayout(null);

        JButton userAvatar=new JButton(setPic(settingLocal.dataPath+u.getID()+"/avatar.png",56,56));
        userAvatar.setBorderPainted(false);
        userAvatar.setFocusPainted(false);
        userAvatar.setBounds(calres(13),calres(54),calres(56),calres(56));
        mainFrame.add(userAvatar);

        String[] test=new String[1];
        test[0]="多人聊天室";
        //test[1]="system";
        FriendList friendList=new FriendList(u.getID(), test);
        FriendListHashMap.addFriendList(SocketHashMap.getSocket(u.getID()), friendList);
        JPanel friendPanel=friendList.friendInit(mainFrame,u);
        mainFrame.add(friendPanel);

        mainFrame.setVisible(true);
    }

    private static void miniTray(MyFrame mainWindow) // minimize the main window to the taskbar tray
    {
        JDialog popWindow = new JDialog();//Use JDialog as the JPopupMenu's carrier
        popWindow.setUndecorated(true);
        popWindow.setSize(1, 1);//popWindow does not require much size as a JPopupMenu carrier
        
        //create JPopupMenu
        //override firePopupMenuWillBecomeInvisible
        //disappear along with the bound components
        JPopupMenu pop = new JPopupMenu() {
            private static final long serialVersionUID = 1L;

            @Override
            public void firePopupMenuWillBecomeInvisible() {
                popWindow.setVisible(false);
            }
        };
        pop.setSize(calres(140), calres(108));
        //Add menu options
        JMenuItem config = new JMenuItem(langLocal.config);
        config.setFont(new Font("微软雅黑",Font.PLAIN,calres(17)));
        config.setBorderPainted(false);
        config.setFocusPainted(false);
        pop.add(config);
        config.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        ConfigFrame.configInit();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });
            }
        });

        JMenuItem exit = new JMenuItem(langLocal.exit);
        exit.setFont(new Font("微软雅黑",Font.PLAIN,calres(17)));
        exit.setBorderPainted(false);
        exit.setFocusPainted(false);
        pop.add(exit);
        exit.addActionListener(e -> {
            exitThread.start();
            //System.exit(0);
        });

        //Create a tray icon
        Image image = Toolkit.getDefaultToolkit().createImage(iconPath+"logo.png");
        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        //Add mouse monitoring to the tray icon
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //click with left-click
                if (e.getButton() == 1) {
                    mainWindow.setVisible(true);
                } else if (e.getButton() == 3 && e.isPopupTrigger()) {
                    //Right-click to pop up the carrier bound to JPopupMenu and JPopupMenu
                    popWindow.setLocation((int)(e.getX()/mainWindow.getRate() + 5), (int)(e.getY()/mainWindow.getRate() - 5 - 30));
                    popWindow.setVisible(true);
                    pop.show(popWindow, 0, 0);
                }
            }
        });
        //Cancels the default shutdown event and 
        //customizes it to be placed in the system tray in the lower-right corner
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            //@SneakyThrows
            @Override
            public void windowClosing(WindowEvent e) {
                mainWindow.setVisible(false);
            }
        });

        // Add a tray icon to the system's tray instance
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
	}
}
