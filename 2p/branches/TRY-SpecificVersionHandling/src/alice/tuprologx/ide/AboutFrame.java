package alice.tuprologx.ide;

import javax.swing.*;

import alice.util.VersionInfo;
import alice.util.VersionInfoFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AboutFrame
    extends GenericFrame
{
    
    private static final long serialVersionUID = 1L;
    private static final String PLATFORM_VERSION = "0";
    
    private String getPlatformVersion()
    {
    	return "0";
    }

    public AboutFrame(JFrame mainWindow)
    {
        super("About tuProlog IDE", mainWindow, 275, 135, true, true);
        initComponents();
    }

    private void initComponents()
    {
        Container c=this.getContentPane();
        JLabel icon=new JLabel();
        URL urlImage = getClass().getResource("img/tuProlog.gif");
        icon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        
        JLabel versionSystem=new JLabel(" tuProlog engine version " + alice.tuprolog.Prolog.getVersion());
        
        String platformMessage = VersionInfoFactory.getVersionInfo().getPlatform();
        platformMessage += " platform version ";
        platformMessage += alice.tuprolog.Prolog.getVersion() + "." + VersionInfoFactory.getVersionInfo().getSpecificVersion();
        JLabel versionIDE=new JLabel(platformMessage);
        
        JLabel copyright=new JLabel(" tuProlog is (C) Copyright 2001-2011 aliCE team");
        JLabel deis=new JLabel(" DEIS, Universita' di Bologna, Italy.");
        JLabel url=new JLabel(" URL: http://tuprolog.alice.unibo.it");
        
        c.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        c.add(icon,constraints);
        constraints.gridy=1;
        c.add(versionSystem,constraints);
        constraints.gridy=2;
        c.add(versionIDE,constraints);
        constraints.gridy=3;
        c.add(new JLabel(" "),constraints);
        constraints.gridy=4;
        c.add(copyright,constraints);
        constraints.gridy=5;
        c.add(deis,constraints);
        constraints.gridy=6;
        c.add(url,constraints);
        pack();
        setVisible(true);
    }
}
