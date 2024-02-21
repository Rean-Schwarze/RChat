package client;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import pak.*;

public class SignUpFrame extends MyFrame{
    private static final long serialVersionUID = 5888997053611845988L;
    private static MyFrame signUpFrame = new MyFrame();
    final static int WIDTH = 420;
    final static int HEIGHT = 570;

    static JButton avatarSelecteButton=new JButton();
    static JTextField nickNameField=new JTextField();
    static JTextField phoneField=new JTextField();
    static String nickName;
    static JPasswordField passwordBox=new JPasswordField(25);
    static ImageIcon avatarIcon = new ImageIcon(iconPath+"avatar_default.png");;
    static File avatarFile;
    static String password;
    static String phone;

    static MyDialogFrame dialogFrame=new MyDialogFrame();

    public static void signUpInit()
    {
        firstInit(signUpFrame,WIDTH,HEIGHT);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUpFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        LoginFrame.loginInit();
                    } catch (IOException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                });
            }
        });

        JLabel welcome=new JLabel(langLocal.welcomeSignUp);
        welcome.setFont(new Font("微软雅黑",Font.PLAIN,calres(30)));
        welcome.setBounds(calres(148),calres(39),calres(127),calres(34));
        signUpFrame.add(welcome);

        avatarSelecteButton.setIcon(setIcon("avatar_frame_normal.png",calres(126),calres(126)));
        avatarSelecteButton.setRolloverIcon(setIcon("avatar_frame_hover.png",calres(126),calres(126)));
        avatarSelecteButton.setPressedIcon(setIcon("avatar_frame_press.png",calres(126),calres(126)));
        avatarSelecteButton.setBorderPainted(false);
        avatarSelecteButton.setFocusPainted(false);
        avatarSelecteButton.setBorder(null);
        avatarSelecteButton.setBounds(calres(146), calres(87), calres(127),calres(127));
        avatarSelecteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc=new JFileChooser();
                FileSystemView fsv = FileSystemView .getFileSystemView();
                jfc.setCurrentDirectory(fsv.getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.setFileFilter(new PictureFileFilter());
                if (jfc.showOpenDialog(signUpFrame)==JFileChooser.APPROVE_OPTION){ 
                    avatarFile=jfc.getSelectedFile();
                    if(avatarFile.isFile())
                    {
                        ImageIcon avatarIcon=new ImageIcon(avatarFile.getPath());
                        avatarIcon.setImage(avatarIcon.getImage().getScaledInstance(calres(126), calres(126), Image.SCALE_SMOOTH));
                        avatarSelecteButton.setIcon(avatarIcon);
                        avatarSelecteButton.setRolloverIcon(avatarIcon);
                        avatarSelecteButton.setPressedIcon(avatarIcon);
                    }
                    else
                    {
                        MyDialogFrame dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.avatarHint);
                        });
                    }
                }
            }
        });
        signUpFrame.add(avatarSelecteButton);

        JLabel hintAvatar=new JLabel(langLocal.hintAvatar);
        hintAvatar.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
        hintAvatar.setBounds(calres(155),calres(224),calres(127),calres(34));
        hintAvatar.setForeground(new Color(179,179,179));
        signUpFrame.add(hintAvatar);

        nickNameField.setBounds(calres(74),calres(270),calres(272),calres(54));
        nickNameField.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
        nickNameField.addFocusListener(new MyTextFieldHintListener(nickNameField, langLocal.hintNickName));
        nickNameField.setBorder(BorderFactory.createLineBorder(Color.black,1));
        nickNameField.setBorder(new CompoundBorder(nickNameField.getBorder(),new EmptyBorder(0,calres(15),0,0)));
        signUpFrame.add(nickNameField);

        passwordBox.setBounds(calres(74),calres(336),calres(272),calres(54));
        passwordBox.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
        passwordBox.addFocusListener(new MyTextFieldHintListener(passwordBox, ""));
        passwordBox.setBorder(BorderFactory.createLineBorder(Color.black,1));
        passwordBox.setBorder(new CompoundBorder(passwordBox.getBorder(),new EmptyBorder(0,calres(15),0,0)));
        signUpFrame.add(passwordBox);

        phoneField.setBounds(calres(74),calres(403),calres(272),calres(54));
        phoneField.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
        phoneField.addFocusListener(new MyTextFieldHintListener(phoneField, langLocal.hintPhone));
        phoneField.setBorder(BorderFactory.createLineBorder(Color.black,1));
        phoneField.setBorder(new CompoundBorder(phoneField.getBorder(),new EmptyBorder(0,calres(15),0,0)));
        signUpFrame.add(phoneField);

        JButton signUpButton = new JButton(setIcon("loginBtn_normal.png",calres(272),calres(54)));
        signUpButton.setRolloverIcon(setIcon("loginBtn_hover.png",calres(272),calres(54)));
        signUpButton.setPressedIcon(setIcon("loginBtn_press.png",calres(272),calres(54)));
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setBounds(calres(75), calres(473), calres(272), calres(54));
        signUpButton.setFont(new Font("微软雅黑",Font.PLAIN,calres(24)));
        signUpButton.setText(langLocal.signUp);
        signUpButton.setHorizontalTextPosition(SwingConstants.CENTER);
        signUpButton.setVerticalTextPosition(SwingConstants.CENTER);
        signUpButton.setForeground(new Color(243,225,193));
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean check=false,check2=false,check3=false;
                nickName=nickNameField.getText();
                password=new String(passwordBox.getPassword());
                phone=phoneField.getText();

                if(password.length()<=25) check2=true;

                if(!check2)
                {
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("small",langLocal.pwdTooLong);
                    });
                    return;
                }

                if(phone.length()==11) check3=true;
                if(!check3)
                {
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("small",langLocal.phoneLengthWrong);
                    });
                    return;
                }

                if(nickName.contains(" ")||password.contains(" ")||phone.contains(" ")||nickName.isEmpty()||password.isEmpty()||phone.isEmpty()||!(avatarFile.isFile()))
                {
                    check=false;
                }
                else check=true;

                if(check)
                {
                    UserSignUp u=new UserSignUp(avatarFile.getPath(), nickName, password,phone);
                    C2StoSignUp c2ss=new C2StoSignUp();
                    boolean checkSignUp=c2ss.signUp(u);
                    if(checkSignUp)
                    {
                        dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.signUpSuccess+c2ss.getID()+"</p></body></html>");
                        });
                        signUpFrame.dispose();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                LoginFrame.loginInit();
                            } catch (IOException ex) {
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                        });
                    }
                    else{
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.signUpFail2);
                        });
                    }
                }
                else
                {
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("medium",langLocal.signUpFail);
                    });
                }
            }
        });
        signUpFrame.add(signUpButton);

        signUpFrame.setVisible(true);
        System.gc();//inform JVM to recycle
    }
}
