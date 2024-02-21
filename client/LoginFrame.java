package client;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

import pak.*;

import com.alibaba.fastjson.*;
import java.io.*;

import java.util.ArrayList;

public class LoginFrame extends MyFrame{
    private static final long serialVersionUID = 8340836025955747992L;

    private static MyFrame loginFrame = new MyFrame();
    final static int LOGIN_WIDTH = 420;
    final static int LOGIN_HEIGHT = 570;

    static JComboBox<Object> accountBox=new JComboBox<>();
    static JPasswordField passwordBox=new JPasswordField(25);
    static String password;
    static ImageIcon avatarIcon;
    static File accountFile;

    static boolean isRememberPd=false;

    public static void loginInit() throws IOException
    {
        firstInit(loginFrame,LOGIN_WIDTH,LOGIN_HEIGHT);
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JButton qrButton=new JButton(setIcon("qrcode.png",calres(108),calres(108)));
        qrButton.setBorderPainted(false);
        qrButton.setFocusPainted(false);
        qrButton.setRolloverIcon(setIcon("qrcode_hp.png",calres(108),calres(108)));
        qrButton.setPressedIcon(setIcon("qrcode_hp.png",calres(108),calres(108)));
        qrButton.setSize(calres(108),calres(108));
        qrButton.setLocation(0,0);
        qrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog d=new JDialog(loginFrame,langLocal.warning,false);
                d.setBounds((dimension.width - calres(300)) / 2,(dimension.height - calres(180)) / 2,calres(300),calres(180));
                JLabel l=new JLabel(langLocal.staytuned,JLabel.CENTER);
                l.setBounds(0,0,calres(300),calres(180));
                l.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
                d.add(l);
                d.setVisible(true);
                d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        });
        loginFrame.add(qrButton);

        passwordBox.setBounds(calres(74),calres(292),calres(272),calres(54));
        passwordBox.setFont(new Font("微软雅黑",Font.PLAIN,calres(17)));
        passwordBox.addFocusListener(new MyTextFieldHintListener(passwordBox, ""));
        passwordBox.setBorder(BorderFactory.createLineBorder(Color.black,1));
        passwordBox.setBorder(new CompoundBorder(passwordBox.getBorder(),new EmptyBorder(0,calres(15),0,0)));

        ArrayList<account> accountList = new ArrayList<>();
        File accountPath=new File(settingLocal.dataPath+"account");
        if(!accountPath.exists())
        {
            accountPath.mkdirs();
        }
        accountFile=new File(settingLocal.dataPath+"account/account.json");
        if (!accountFile.exists()) {
            try {
                accountFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(accountFile)));
            String jsonStr=reader.readLine();
            JSONArray arr = JSON.parseArray(jsonStr);
            
            if(arr!=null)
            {
                for(int i=0;i<arr.size();i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    account a=new account(obj.getIntValue("id"),obj.getString("password"));
                    accountList.add(a);
                }
            }
            reader.close();
        }
        if(!accountList.isEmpty())
        {
            File tempPath;
            for(int i=0;i<accountList.size();i++)
            {
                accountBox.addItem(accountList.get(i).id);
                tempPath=new File(settingLocal.dataPath+accountList.get(i).id);
                if(!tempPath.exists())
                {
                    tempPath.mkdirs();
                }
            }
            
            String password=accountList.get(0).password;
            passwordBox.setText(password);
            tempPath=new File(settingLocal.dataPath+accountList.get(0).id+"/avatar.png");
            if (!tempPath.exists()) {
                avatarIcon=new ImageIcon(iconPath+"avatar_default.png");
            }
            else avatarIcon=setPic(settingLocal.dataPath+accountList.get(0).id+"/avatar.png",120,120);
        }
        else
        {
            //passwordBox=new JPasswordField(25);
            avatarIcon=new ImageIcon(iconPath+"avatar_default.png");
        }
        
        loginFrame.add(passwordBox);

        JLabel avatar=new JLabel(avatarIcon);
        avatar.setBounds(calres(149),calres(83),calres(120),calres(120));
        loginFrame.add(avatar);

        accountBox.setEditable(true);
        accountBox.setBounds(calres(74),calres(226),calres(272),calres(54));
        accountBox.setFont(new Font("微软雅黑",Font.PLAIN,calres(17)));
        accountBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pd=new String();
                if(!accountList.isEmpty()&&accountBox.getSelectedIndex()>=0&&accountBox.getSelectedIndex()<accountList.size())
                {
                    pd=accountList.get(accountBox.getSelectedIndex()).password;
                    String avatarPath=settingLocal.dataPath+accountBox.getSelectedItem()+"/avatar.png";
                    File avatarFile=new File(avatarPath);
                    if(avatarFile.exists()) avatar.setIcon(setPic(avatarPath,120,120));
                }
                if(!pd.isEmpty()) passwordBox.setText(pd);
            }
        });
        loginFrame.add(accountBox);

        JCheckBox rempd=new JCheckBox(langLocal.rememberPd);
        rempd.setFont(new Font("微软雅黑",Font.PLAIN,calres(17)));
        rempd.setBackground(new Color(245,245,245));
        rempd.setBounds(calres(157),calres(363),calres(200),calres(20));
        if(!accountList.isEmpty()&&!accountList.get(0).password.isEmpty()) rempd.setSelected(true);
        rempd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(rempd.isSelected()) isRememberPd=true;
                else isRememberPd=false;
            }
        });
        loginFrame.add(rempd);

        JButton loginButton = new JButton(setIcon("loginBtn_normal.png",calres(272),calres(54)));
        loginButton.setRolloverIcon(setIcon("loginBtn_hover.png",calres(272),calres(54)));
        loginButton.setPressedIcon(setIcon("loginBtn_press.png",calres(272),calres(54)));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(calres(75), calres(403), calres(272), calres(54));
        loginButton.setFont(new Font("微软雅黑",Font.PLAIN,calres(24)));
        loginButton.setText(langLocal.login);
        loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
        loginButton.setVerticalTextPosition(SwingConstants.CENTER);
        loginButton.setForeground(new Color(243,225,193));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                password=new String(passwordBox.getPassword());
                if(password.length()<=25) password=MD5Util.md5(password);
                
                if(accountBox.getSelectedItem()==null||password.isEmpty())
                {
                    MyDialogFrame dialogFrame=new MyDialogFrame();
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("small",langLocal.loginFail);
                    });
                }
                else
                {
                    String id=accountBox.getSelectedItem().toString();
                    User u=new User(id,password);
                    
                    C2StoLogIn login=new C2StoLogIn();
                    int check=login.CheckUser(u);
                    if(check==1)
                    {
                        //save account info
                        boolean flag=false;
                        for(int i=0;i<accountList.size();i++)
                        {
                            if(Integer.parseInt(id)==accountList.get(i).id) flag=true;
                        }
                        if(!flag)
                        {
                            u.setName(login.name);
                            account a3;
                            if(isRememberPd) a3=new account(Integer.valueOf(id),password,login.name);
                            else a3=new account(Integer.valueOf(id),"",login.name);
                            accountList.add(a3);
                            try (
                                JSONWriter writer = new JSONWriter(new FileWriter(accountFile))) {
                                writer.startArray();
                                for (int i = 0; i < accountList.size(); i++) {
                                    account a=accountList.get(i);
                                    //System.out.println("name="+a.getName());
                                    writer.writeValue(a);
                                }
                                writer.endArray();
                                //writer.close();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                        //invoke the main frame
                        SwingUtilities.invokeLater(() -> {
                            try {
                                MainFrame.mainInit(u);
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        });
                        loginFrame.dispose();
                    }
                    else if(check==0)
                    {
                        MyDialogFrame dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.loginFail);
                        });
                    }
                    else if(check==2)
                    {
                        MyDialogFrame dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.serverIsClose);
                        });
                    }
                    else if(check==3)
                    {
                        MyDialogFrame dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.repeatingLogIn);
                        });
                    }
                }
                
            }
        });
        loginFrame.add(loginButton);

        JButton signUpButton=new JButton(langLocal.signup);
        signUpButton.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
        signUpButton.setForeground(new Color(220,188,96));
        signUpButton.setBounds(calres(95), calres(481), calres(94), calres(25));
        signUpButton.setMargin(new Insets(0,0,0,0));//Set the top, bottom, left, and right space outside the border to 0
        signUpButton.setIconTextGap(0);//Set interval between the text and the icon to 0
        signUpButton.setBorderPainted(false);
        signUpButton.setBorder(null);
        signUpButton.setFocusPainted(false);
        signUpButton.setContentAreaFilled(false);//Remove the default background fill
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(SignUpFrame::signUpInit);
                loginFrame.dispose();
            }
        });
        loginFrame.add(signUpButton);

        JButton codeButton=new JButton(langLocal.code);
        codeButton.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
        codeButton.setForeground(new Color(220,188,96));
        codeButton.setBounds(calres(215), calres(481), calres(117), calres(25));
        codeButton.setMargin(new Insets(0,0,0,0));//Set the top, bottom, left, and right space outside the border to 0
        codeButton.setIconTextGap(0);//Set interval between the text and the icon to 0
        codeButton.setBorderPainted(false);
        codeButton.setBorder(null);
        codeButton.setFocusPainted(false);
        codeButton.setContentAreaFilled(false);//Remove the default background fill
        codeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog d=new JDialog(loginFrame,langLocal.warning,false);
                d.setBounds((dimension.width - calres(300)) / 2,(dimension.height - calres(180)) / 2,calres(300),calres(180));
                JLabel l=new JLabel(langLocal.staytuned,JLabel.CENTER);
                l.setBounds(0,0,calres(300),calres(180));
                l.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
                d.add(l);
                d.setVisible(true);
                d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        });
        loginFrame.add(codeButton);

        loginFrame.setVisible(true);
    }
}