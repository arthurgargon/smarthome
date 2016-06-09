package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.clunet.ClunetDateTimeResolver;
import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.clunet.utils.DataFormat;
import com.gargon.smarthome.commands.Commands;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SupradinConsole extends javax.swing.JFrame {
    
    private static final Logger LOG = Logger.getLogger(SupradinConsole.class.getName());
    
    private static final KeyStroke ksMuteRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, Event.CTRL_MASK);
    
    private static final KeyStroke ksIncVolRoom = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, Event.CTRL_MASK);
    private static final KeyStroke ksDecVolRoom = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, Event.CTRL_MASK);
    private static final KeyStroke ksIncTrebleRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, Event.CTRL_MASK);
    private static final KeyStroke ksDecTrebleRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, Event.CTRL_MASK);
    private static final KeyStroke ksIncBassRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, Event.CTRL_MASK);
    private static final KeyStroke ksDecBassRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, Event.CTRL_MASK);
    private static final KeyStroke ksIncGainRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, Event.CTRL_MASK);
    private static final KeyStroke ksDecGainRoom = KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, Event.CTRL_MASK);
    
    private static final KeyStroke ksAudioSourceRoomPC = KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK);
    private static final KeyStroke ksAudioSourceRoomBT = KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK);
    private static final KeyStroke ksAudioSourceRoomFM = KeyStroke.getKeyStroke(KeyEvent.VK_4, Event.CTRL_MASK);
    
    private static final KeyStroke ksLightCloackroom = KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK);
    private static final KeyStroke ksLightMirroredBoxBathroom = KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK);
    private static final KeyStroke ksSwitchFanBathroom = KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK);
    
    
    private static final String APP_TRAY_TOOLTIP = "SupradinConsole";

    private static final String ACTIVE_ICON_PATH   = "/com/gargon/smarthome/supradin/resources/house_64.png";
    private static final String INACTIVE_ICON_PATH = "/com/gargon/smarthome/supradin/resources/house_red_64.png";
    
    private TrayIcon trayIcon;
    private SystemTray tray;
    
    private Image activeImage = null;
    private Image inactiveImage = null;
  
    private static SupradinConnection connection;
    private static ScheduledExecutorService connectionChecker;

    public SupradinConsole() {
        initComponents();
        
        
        //filter lists implementation
        MouseListener filterCheckBoxItemMouseListener = new MouseAdapter() {
            
            public void mouseClicked(MouseEvent event) {
                JList<CheckBoxItem> list = (JList<CheckBoxItem>) event.getSource();

                // Get index of item clicked
                int index = list.locationToIndex(event.getPoint());
                CheckBoxItem item = (CheckBoxItem) list.getModel()
                        .getElementAt(index);

                // Toggle selected state
                item.setChecked(!item.isChecked());

                // Repaint cell
                list.repaint(list.getCellBounds(index, index));
            }
        };
        
        
        //filters models
        listFilterSenders.setCellRenderer(new FilterListCellRenderer());
        listFilterSenders.addMouseListener(filterCheckBoxItemMouseListener);
        
        DefaultListModel model = new DefaultListModel();
        listFilterSenders.setModel(model);
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getDevicesList().entrySet()) {
            model.addElement(new CheckBoxItem(entry.getKey(), entry.getValue()));
        }
        
        
        listFilterRecievers.setCellRenderer(new FilterListCellRenderer());
        listFilterRecievers.addMouseListener(filterCheckBoxItemMouseListener);
      
        model = new DefaultListModel();
        listFilterRecievers.setModel(model);
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getDevicesList().entrySet()) {
            model.addElement(new CheckBoxItem(entry.getKey(), entry.getValue()));
        }
        
        
        listFilterCommands.setCellRenderer(new FilterListCellRenderer());
        listFilterCommands.addMouseListener(filterCheckBoxItemMouseListener);
      
        model = new DefaultListModel();
        listFilterCommands.setModel(model);
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getCommandsList().entrySet()) {
            model.addElement(new CheckBoxItem(entry.getKey(), entry.getValue()));
        }
        
        
        try {
            activeImage = new ImageIcon(SupradinConsole.class.getResource(ACTIVE_ICON_PATH)).getImage();
        } catch (Exception e) {
        }

        try {
            inactiveImage = new ImageIcon(SupradinConsole.class.getResource(INACTIVE_ICON_PATH)).getImage();
        } catch (Exception e) {
        }

        if (activeImage != null) {
            setIconImage(activeImage);
        }
        
        if (SystemTray.isSupported()) {
            try {
                tray = SystemTray.getSystemTray();
            } catch (Exception e) {
                tray = null;
            }
        }
        
        //show in center of screen
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);

        //init combos
        DefaultComboBoxModel m = (DefaultComboBoxModel) cbCommand.getModel();
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getCommandsList().entrySet()) {
            m.addElement(new ComboBoxItem(entry.getKey(), entry.getValue()));
        }

        m = (DefaultComboBoxModel) cbAddress.getModel();
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getDevicesList().entrySet()) {
            m.addElement(new ComboBoxItem(entry.getKey(), entry.getValue()));
        }

        m = (DefaultComboBoxModel) cbPriority.getModel();
        for (Map.Entry<Integer, String> entry : ClunetDictionary.getPrioritiesList().entrySet()) {
            m.addElement(new ComboBoxItem(entry.getKey(), entry.getValue()));
        }
       

        //short keys init
        try {
            JIntellitype.getInstance();
            //if (JIntellitype.checkInstanceAlreadyRunning("SupradinConsole")) {
            //    System.exit(1);
            //}
            JIntellitype.getInstance().registerSwingHotKey(1, ksLightCloackroom.getModifiers(), ksLightCloackroom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(2, ksLightMirroredBoxBathroom.getModifiers(), ksLightMirroredBoxBathroom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(3, ksSwitchFanBathroom.getModifiers(), ksSwitchFanBathroom.getKeyCode());
            
            miSoundRoomIncVolume.setAccelerator(ksIncVolRoom);
            JIntellitype.getInstance().registerSwingHotKey(10, ksIncVolRoom.getModifiers(), ksIncVolRoom.getKeyCode());
            
            miSoundRoomDecVolume.setAccelerator(ksDecVolRoom);
            JIntellitype.getInstance().registerSwingHotKey(11, ksDecVolRoom.getModifiers(), ksDecVolRoom.getKeyCode());
            
            miSoundRoomMute.setAccelerator(ksMuteRoom);
            JIntellitype.getInstance().registerSwingHotKey(12, ksMuteRoom.getModifiers(), ksMuteRoom.getKeyCode());
            
            JIntellitype.getInstance().registerSwingHotKey(13, ksIncTrebleRoom.getModifiers(), ksIncTrebleRoom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(14, ksDecTrebleRoom.getModifiers(), ksDecTrebleRoom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(15, ksIncBassRoom.getModifiers(), ksIncBassRoom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(16, ksDecBassRoom.getModifiers(), ksDecBassRoom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(17, ksIncGainRoom.getModifiers(), ksIncGainRoom.getKeyCode());
            JIntellitype.getInstance().registerSwingHotKey(18, ksDecGainRoom.getModifiers(), ksDecGainRoom.getKeyCode());
            
            miSoundRoomSourcePC.setAccelerator(ksAudioSourceRoomPC);
            JIntellitype.getInstance().registerSwingHotKey(19, ksAudioSourceRoomPC.getModifiers(), ksAudioSourceRoomPC.getKeyCode());
            
            miSoundRoomSourceBT.setAccelerator(ksAudioSourceRoomBT);
            JIntellitype.getInstance().registerSwingHotKey(20, ksAudioSourceRoomBT.getModifiers(), ksAudioSourceRoomBT.getKeyCode());
            
            miSoundRoomSourceFM.setAccelerator(ksAudioSourceRoomFM);
            JIntellitype.getInstance().registerSwingHotKey(21, ksAudioSourceRoomFM.getModifiers(), ksAudioSourceRoomFM.getKeyCode());
            
            
            

            JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
                @Override
                public void onHotKey(int i) {
                    switch (i) {
                        case 1:
                            Commands.switchLightInCloackroom(connection);   //переключаем
                            break;
                        case 2:
                            Commands.switchLightInMirroredBoxInBathroom(connection);       //переключаем
                            break;
                        case 3:
                            Commands.switchFanInBathroom(connection);      //переключаем
                            break;
                            
                        case 10:
                            miSoundRoomIncVolumeActionPerformed(null);
                            break;
                        case 11:
                            miSoundRoomDecVolumeActionPerformed(null);
                            break;
                        case 12:
                            miSoundRoomMuteActionPerformed(null);
                            break;
                            
                        case 13:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_TREBLE, true);
                            break;
                        case 14:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_TREBLE, false);
                            break;
                        case 15:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_BASS, true);
                            break;
                        case 16:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_BASS, false);
                            break;
                        case 17:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_GAIN, true);
                            break;
                        case 18:
                            Commands.changeEqualizerOfSoundInRoom(connection, Commands.EQUALIZER_GAIN, false);
                            break;
                            
                        case 19:
                            miSoundRoomSourcePCActionPerformed(null);
                            break;
                        case 20:
                            miSoundRoomSourceBTActionPerformed(null);
                            break;
                        case 21:
                            miSoundRoomSourceFMActionPerformed(null);
                            break;
                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Не удалось инициализировать модуль управления \"горячими\" клавишами:\r\n" + e.getMessage(),
                    "Ошибка инициализации",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        if (trayImageShow(inactiveImage, APP_TRAY_TOOLTIP)) {
            trayIn();
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        } else {
            setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        
        
        
        //establish connection
        connection = new SupradinConnection();
        connection.open();
        connection.addDataListener(new SupradinDataListener() {

            DefaultTableModel model = (DefaultTableModel) tbMain.getModel();
            DateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");

            @Override
            public void dataRecieved(SupradinConnection connection, SupradinDataMessage supradin) {

                String src = "0x" + DataFormat.byteToHex(supradin.getSrc());
                String srcName = ClunetDictionary.getDeviceById(supradin.getSrc());
                if (srcName != null) {
                    src += " - " + srcName;
                }

                String rcv = "0x" +DataFormat.byteToHex(supradin.getDst());
                String rcvName = ClunetDictionary.getDeviceById(supradin.getDst());
                if (rcvName != null) {
                    rcv += " - " + rcvName;
                }

                String cmd = "0x" +DataFormat.byteToHex(supradin.getCommand());
                String cmdName = ClunetDictionary.getCommandById(supradin.getCommand());
                if (cmdName != null) {
                    cmd += " - " + cmdName;
                }

                String interpretation = ClunetDictionary.toString(supradin.getCommand(), supradin.getData());

                //check if we need autoscroll
                JViewport viewport = (JViewport) tbMain.getParent();
                Rectangle rect = tbMain.getCellRect(tbMain.getRowCount() - 1, 0, true);
                rect.setBounds(0, rect.y - viewport.getViewPosition().y, rect.width, 1);
                boolean autoscroll = new Rectangle(viewport.getExtentSize()).contains(rect);

                //add row
                model.addRow(new Object[]{sdf.format(new Date()), src, rcv, cmd, DataFormat.bytesToHex(supradin.getData()), interpretation});

                //perform autoscroll if we need
                if (autoscroll) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tbMain.scrollRectToVisible(tbMain.getCellRect(tbMain.getRowCount() - 1, 0, true));
                        }
                    });
                }
                
                printMessageCount();
            }
        });
        connection.addDataListener(new ClunetDateTimeResolver());
        connection.connect();
       
        
        //check connection
        connectionChecker = Executors.newSingleThreadScheduledExecutor();
        connectionChecker.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean active = connection.isActive();
                if (btSend.isEnabled() ^ active) {
                    setTitle(String.format("SupradinConsole [%s]", active ? "Подключено" : "Не подключено"));
                    btSearchDevices.setEnabled(active);                              //дополнительно отсеиваем лок по нажатию на "Поиск устройств"
                    btSend.setEnabled(active);
                    
                    mnLight.setEnabled(active);
                    mnSound.setEnabled(active);
                    mnClimate.setEnabled(active);
                    trayImageShow(active ? activeImage : inactiveImage, APP_TRAY_TOOLTIP);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pmTray = new javax.swing.JPopupMenu();
        miShowConsole = new javax.swing.JMenuItem();
        mnSound = new javax.swing.JMenu();
        mnSoundRoom = new javax.swing.JMenu();
        miSoundRoomIncVolume = new javax.swing.JMenuItem();
        miSoundRoomDecVolume = new javax.swing.JMenuItem();
        miSoundRoomMute = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnSoundRoomSource = new javax.swing.JMenu();
        miSoundRoomSourcePC = new javax.swing.JMenuItem();
        miSoundRoomSourceBT = new javax.swing.JMenuItem();
        miSoundRoomSourceFM = new javax.swing.JMenuItem();
        mnLight = new javax.swing.JMenu();
        mnLightCloackroom = new javax.swing.JMenu();
        miLightCloackroomOn = new javax.swing.JMenuItem();
        miLightCloackroomOff = new javax.swing.JMenuItem();
        mnLightBathroom = new javax.swing.JMenu();
        miLightBathroomOn = new javax.swing.JMenuItem();
        miLightBathroomOff = new javax.swing.JMenuItem();
        mnClimate = new javax.swing.JMenu();
        mnFanBathroom = new javax.swing.JMenu();
        mnFanBathroomOn = new javax.swing.JMenuItem();
        mnFanBathroomOff = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        pmMain = new javax.swing.JPopupMenu();
        pmiCopyCell = new javax.swing.JMenuItem();
        pmiCopyRow = new javax.swing.JMenuItem();
        pmiCopyAll = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        pmiClearAll = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        pmiFilter = new javax.swing.JMenuItem();
        pmiResetFilters = new javax.swing.JMenuItem();
        jFilterDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        lbFilterSender = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listFilterSenders = new javax.swing.JList();
        lbFilterReciever = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listFilterRecievers = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        listFilterCommands = new javax.swing.JList();
        lbFilterCommand = new javax.swing.JLabel();
        cbFilterAllSenders = new javax.swing.JCheckBox();
        cbFilterAllRecievers = new javax.swing.JCheckBox();
        cbFilterAllCommands = new javax.swing.JCheckBox();
        btResetFilters = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMain = new JColoredClunetTable();
        jPanel1 = new javax.swing.JPanel();
        lbCommand = new javax.swing.JLabel();
        cbCommand = new javax.swing.JComboBox();
        lbAddress = new javax.swing.JLabel();
        cbAddress = new javax.swing.JComboBox();
        lbPriority = new javax.swing.JLabel();
        cbPriority = new javax.swing.JComboBox();
        lbData = new javax.swing.JLabel();
        edData = new javax.swing.JTextField();
        btSend = new javax.swing.JButton();
        btSearchDevices = new javax.swing.JButton();
        lbNumDevices = new javax.swing.JLabel();
        lbNumMessages = new javax.swing.JLabel();

        miShowConsole.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        miShowConsole.setText("Показать консоль");
        miShowConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miShowConsoleActionPerformed(evt);
            }
        });
        pmTray.add(miShowConsole);

        mnSound.setText("Звук");

        mnSoundRoom.setLabel("Комната");

        miSoundRoomIncVolume.setText("Прибавить громкость");
        miSoundRoomIncVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomIncVolumeActionPerformed(evt);
            }
        });
        mnSoundRoom.add(miSoundRoomIncVolume);

        miSoundRoomDecVolume.setText("Убавить громкость");
        miSoundRoomDecVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomDecVolumeActionPerformed(evt);
            }
        });
        mnSoundRoom.add(miSoundRoomDecVolume);

        miSoundRoomMute.setText("Выключить звук");
        miSoundRoomMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomMuteActionPerformed(evt);
            }
        });
        mnSoundRoom.add(miSoundRoomMute);
        mnSoundRoom.add(jSeparator2);

        mnSoundRoomSource.setText("Источник сигнала");
        mnSoundRoomSource.setToolTipText("");

        miSoundRoomSourcePC.setText("Компьютер");
        miSoundRoomSourcePC.setToolTipText("");
        miSoundRoomSourcePC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomSourcePCActionPerformed(evt);
            }
        });
        mnSoundRoomSource.add(miSoundRoomSourcePC);

        miSoundRoomSourceBT.setText("Bluetooth");
        miSoundRoomSourceBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomSourceBTActionPerformed(evt);
            }
        });
        mnSoundRoomSource.add(miSoundRoomSourceBT);

        miSoundRoomSourceFM.setText("FM-приемник");
        miSoundRoomSourceFM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSoundRoomSourceFMActionPerformed(evt);
            }
        });
        mnSoundRoomSource.add(miSoundRoomSourceFM);

        mnSoundRoom.add(mnSoundRoomSource);

        mnSound.add(mnSoundRoom);
        mnSoundRoom.getAccessibleContext().setAccessibleDescription("");

        pmTray.add(mnSound);

        mnLight.setText("Освещение");

        mnLightCloackroom.setText("Гардеробная");

        miLightCloackroomOn.setText("Включить");
        miLightCloackroomOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLightCloackroomOnActionPerformed(evt);
            }
        });
        mnLightCloackroom.add(miLightCloackroomOn);

        miLightCloackroomOff.setText("Выключить");
        miLightCloackroomOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLightCloackroomOffActionPerformed(evt);
            }
        });
        mnLightCloackroom.add(miLightCloackroomOff);

        mnLight.add(mnLightCloackroom);

        mnLightBathroom.setText("Ванная (шкафчик)");

        miLightBathroomOn.setText("Включить");
        miLightBathroomOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLightBathroomOnActionPerformed(evt);
            }
        });
        mnLightBathroom.add(miLightBathroomOn);

        miLightBathroomOff.setText("Выключить");
        miLightBathroomOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miLightBathroomOffActionPerformed(evt);
            }
        });
        mnLightBathroom.add(miLightBathroomOff);

        mnLight.add(mnLightBathroom);

        pmTray.add(mnLight);

        mnClimate.setText("Климат");

        mnFanBathroom.setText("Вентилятор");
        mnFanBathroom.setActionCommand("Вентилятор в ванной");

        mnFanBathroomOn.setText("Включить");
        mnFanBathroomOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFanBathroomOnActionPerformed(evt);
            }
        });
        mnFanBathroom.add(mnFanBathroomOn);

        mnFanBathroomOff.setText("Выключить");
        mnFanBathroomOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFanBathroomOffActionPerformed(evt);
            }
        });
        mnFanBathroom.add(mnFanBathroomOff);

        mnClimate.add(mnFanBathroom);

        pmTray.add(mnClimate);
        pmTray.add(jSeparator1);

        miExit.setText("Выйти");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        pmTray.add(miExit);

        pmiCopyCell.setText("Скопировать ячейку");
        pmiCopyCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiCopyCellActionPerformed(evt);
            }
        });
        pmMain.add(pmiCopyCell);

        pmiCopyRow.setText("Скопировать строку");
        pmiCopyRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiCopyRowActionPerformed(evt);
            }
        });
        pmMain.add(pmiCopyRow);

        pmiCopyAll.setText("Скопировать все");
        pmiCopyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiCopyAllActionPerformed(evt);
            }
        });
        pmMain.add(pmiCopyAll);
        pmMain.add(jSeparator3);

        pmiClearAll.setText("Очистить все...");
        pmiClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiClearAllActionPerformed(evt);
            }
        });
        pmMain.add(pmiClearAll);
        pmMain.add(jSeparator4);

        pmiFilter.setText("Фильтр...");
        pmiFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiFilterActionPerformed(evt);
            }
        });
        pmMain.add(pmiFilter);

        pmiResetFilters.setText("Сбросить все фильтры");
        pmiResetFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiResetFiltersActionPerformed(evt);
            }
        });
        pmMain.add(pmiResetFilters);

        jFilterDialog.setTitle("Фильтр");
        jFilterDialog.setMinimumSize(new java.awt.Dimension(510, 500));
        jFilterDialog.setModal(true);
        jFilterDialog.setResizable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbFilterSender.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbFilterSender.setText("Отправитель");

        listFilterSenders.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(listFilterSenders);

        lbFilterReciever.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbFilterReciever.setText("Получатель");

        listFilterRecievers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(listFilterRecievers);

        listFilterCommands.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(listFilterCommands);

        lbFilterCommand.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lbFilterCommand.setText("Команда");

        cbFilterAllSenders.setText("Выбрать все");
        cbFilterAllSenders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFilterAllSendersActionPerformed(evt);
            }
        });

        cbFilterAllRecievers.setText("Выбрать все");
        cbFilterAllRecievers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFilterAllRecieversActionPerformed(evt);
            }
        });

        cbFilterAllCommands.setText("Выбрать все");
        cbFilterAllCommands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFilterAllCommandsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbFilterAllSenders)
                    .add(lbFilterSender)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFilterAllRecievers)
                    .add(lbFilterReciever))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbFilterCommand)
                            .add(cbFilterAllCommands))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbFilterSender)
                    .add(lbFilterReciever)
                    .add(lbFilterCommand))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFilterAllSenders)
                    .add(cbFilterAllRecievers)
                    .add(cbFilterAllCommands))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .add(jScrollPane3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane4))
                .addContainerGap())
        );

        btResetFilters.setText("Сбросить все фильтры");
        btResetFilters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btResetFiltersActionPerformed(evt);
            }
        });

        jButton3.setText("Ok");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFilterDialogLayout = new org.jdesktop.layout.GroupLayout(jFilterDialog.getContentPane());
        jFilterDialog.getContentPane().setLayout(jFilterDialogLayout);
        jFilterDialogLayout.setHorizontalGroup(
            jFilterDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFilterDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(btResetFilters)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFilterDialogLayout.setVerticalGroup(
            jFilterDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFilterDialogLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(9, 9, 9)
                .add(jFilterDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btResetFilters)
                    .add(jButton3))
                .addContainerGap())
        );

        setTitle("SupradinConsole");
        setMinimumSize(new java.awt.Dimension(600, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMainMouseClicked(evt);
            }
        });

        tbMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Время", "Отправитель", "Получатель", "Команда", "Данные", "Расшифровка"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbMain.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbMain.getTableHeader().setReorderingAllowed(false);
        tbMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMainMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbMain);
        if (tbMain.getColumnModel().getColumnCount() > 0) {
            tbMain.getColumnModel().getColumn(0).setMinWidth(150);
            tbMain.getColumnModel().getColumn(0).setPreferredWidth(150);
            tbMain.getColumnModel().getColumn(0).setMaxWidth(150);
            tbMain.getColumnModel().getColumn(1).setMinWidth(150);
            tbMain.getColumnModel().getColumn(1).setPreferredWidth(150);
            tbMain.getColumnModel().getColumn(1).setMaxWidth(150);
            tbMain.getColumnModel().getColumn(2).setMinWidth(150);
            tbMain.getColumnModel().getColumn(2).setPreferredWidth(150);
            tbMain.getColumnModel().getColumn(2).setMaxWidth(150);
            tbMain.getColumnModel().getColumn(3).setMinWidth(150);
            tbMain.getColumnModel().getColumn(3).setPreferredWidth(150);
            tbMain.getColumnModel().getColumn(3).setMaxWidth(150);
            tbMain.getColumnModel().getColumn(4).setPreferredWidth(300);
            tbMain.getColumnModel().getColumn(5).setPreferredWidth(300);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Отправка команды"));

        lbCommand.setText("Команда:");

        lbAddress.setText("Кому:");

        lbPriority.setText("Приоритет:");

        lbData.setText("Данные:");

        edData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                edDataKeyPressed(evt);
            }
        });

        btSend.setText("Отправить");
        btSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSendActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lbData)
                    .add(lbAddress))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(cbAddress, 0, 182, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(lbCommand)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbCommand, 0, 183, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(lbPriority))
                    .add(edData))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbPriority, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(btSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbCommand)
                    .add(cbCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbAddress)
                    .add(cbAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbPriority)
                    .add(cbPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbData)
                    .add(edData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(22, 22, 22))
        );

        btSearchDevices.setText("Поиск устройств");
        btSearchDevices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSearchDevicesActionPerformed(evt);
            }
        });

        lbNumDevices.setAutoscrolls(true);

        lbNumMessages.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lbNumMessages.setText("Сообщений: 0");
        lbNumMessages.setToolTipText("");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(btSearchDevices)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNumDevices, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 360, Short.MAX_VALUE)
                .add(lbNumMessages, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(btSearchDevices)
                    .add(lbNumDevices, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lbNumMessages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSendActionPerformed

           try {
            int dst = ((ComboBoxItem) cbAddress.getSelectedItem()).getId();
            int prio = ((ComboBoxItem) cbPriority.getSelectedItem()).getId();
            int cmd = ((ComboBoxItem) cbCommand.getSelectedItem()).getId();
            byte[] data = DataFormat.hexToByteArray(edData.getText());

            connection.sendData(new SupradinDataMessage(dst, prio, cmd, data));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Не удалось отправить сообщение:\r\n" + e.getMessage(),
                    "Ошибка при отправке сообщения",
                    JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (tray != null){
            trayIn();
        }else{
            miExitActionPerformed(null);
        }
    }//GEN-LAST:event_formWindowClosing

    private void btSearchDevicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSearchDevicesActionPerformed
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    btSearchDevices.setEnabled(false);
                    lbNumDevices.setText(null);
                    List discoveryResponses = Clunet.sendDiscovery(connection, 1000);
                    lbNumDevices.setText("Всего устройств: " + discoveryResponses.size());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Не удалось выполнить команду.\r\nПричина: " + e.getMessage(),
                            "Ошибка при выполнении команды",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    btSearchDevices.setEnabled(true);
                }
            }
        }).start();
    }//GEN-LAST:event_btSearchDevicesActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        try {
            try {
                if (connectionChecker != null) {
                    connectionChecker.shutdownNow();
                }
            } catch (Exception e) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
            trayImageFree();
            JIntellitype.getInstance().cleanUp();
        } finally {
            System.exit(0);
        }

        //trayIcon.displayMessage("Message caption", "Here's test message", TrayIcon.MessageType.INFO);
    }//GEN-LAST:event_miExitActionPerformed

    private void miShowConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miShowConsoleActionPerformed
        trayOut();
    }//GEN-LAST:event_miShowConsoleActionPerformed

    private void miLightCloackroomOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miLightCloackroomOffActionPerformed
         Commands.switchLightInCloackroom(connection, false);
    }//GEN-LAST:event_miLightCloackroomOffActionPerformed

    private void miLightCloackroomOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miLightCloackroomOnActionPerformed
        Commands.switchLightInCloackroom(connection, true);
    }//GEN-LAST:event_miLightCloackroomOnActionPerformed

    private void miSoundRoomMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomMuteActionPerformed
         Commands.muteInRoom(connection);
    }//GEN-LAST:event_miSoundRoomMuteActionPerformed

    private void miSoundRoomSourcePCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomSourcePCActionPerformed
        Commands.selectSourceOfSoundInRoom(connection, Commands.ROOM_AUDIOSOURCE_PC);
    }//GEN-LAST:event_miSoundRoomSourcePCActionPerformed

    private void miSoundRoomSourceBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomSourceBTActionPerformed
        Commands.selectSourceOfSoundInRoom(connection, Commands.ROOM_AUDIOSOURCE_BLUETOOTH);
    }//GEN-LAST:event_miSoundRoomSourceBTActionPerformed

    private void miSoundRoomSourceFMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomSourceFMActionPerformed
       Commands.selectSourceOfSoundInRoom(connection, Commands.ROOM_AUDIOSOURCE_RADIO);
    }//GEN-LAST:event_miSoundRoomSourceFMActionPerformed

    private void miLightBathroomOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miLightBathroomOnActionPerformed
        Commands.switchLightInMirroredBoxInBathroom(connection, true);
    }//GEN-LAST:event_miLightBathroomOnActionPerformed

    private void miLightBathroomOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miLightBathroomOffActionPerformed
        Commands.switchLightInMirroredBoxInBathroom(connection, false);
    }//GEN-LAST:event_miLightBathroomOffActionPerformed

    private void mnFanBathroomOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnFanBathroomOnActionPerformed
        Commands.switchFanInBathroom(connection, true);
    }//GEN-LAST:event_mnFanBathroomOnActionPerformed

    private void mnFanBathroomOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnFanBathroomOffActionPerformed
        Commands.switchFanInBathroom(connection, false);
    }//GEN-LAST:event_mnFanBathroomOffActionPerformed

    private void miSoundRoomIncVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomIncVolumeActionPerformed
        Commands.changeVolumeOfSoundInRoom(connection, true);
    }//GEN-LAST:event_miSoundRoomIncVolumeActionPerformed

    private void miSoundRoomDecVolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSoundRoomDecVolumeActionPerformed
        Commands.changeVolumeOfSoundInRoom(connection, false);
    }//GEN-LAST:event_miSoundRoomDecVolumeActionPerformed

    private void pmiCopyAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiCopyAllActionPerformed
        String buf = "";
        
        for (int i = 0; i < tbMain.getRowCount(); i++) {
            for (int j = 0; j < tbMain.getColumnCount(); j++) {
                Object obj = tbMain.getValueAt(i, j);
                if (obj != null) {
                    buf += obj.toString();
                }
                buf += ";";
            }
            buf += "\r\n";
        }

        putToClipboard(buf);
    }//GEN-LAST:event_pmiCopyAllActionPerformed

    private Point tbMainClickPoint = null;
 
    private void tbMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMainMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            
            pmiCopyCell.setEnabled(evt.getSource() == tbMain);
            pmiCopyRow.setEnabled(evt.getSource() == tbMain);
            pmiCopyAll.setEnabled(evt.getSource() == tbMain);
            
            pmMain.show(tbMain, evt.getX(), evt.getY());
            tbMainClickPoint = evt.getPoint();
        }
    }//GEN-LAST:event_tbMainMouseClicked

    private void putToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }
    
    private void pmiCopyCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiCopyCellActionPerformed
        int row = tbMain.rowAtPoint(tbMainClickPoint);
        int col = tbMain.columnAtPoint(tbMainClickPoint);

        Object obj = tbMain.getValueAt(row, col);
        if (obj != null) {
            putToClipboard(obj.toString());
        }
    }//GEN-LAST:event_pmiCopyCellActionPerformed

    private void pmiCopyRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiCopyRowActionPerformed
        int row = tbMain.rowAtPoint(tbMainClickPoint);

        String buf = "";
        for (int i = 0; i < tbMain.getColumnCount(); i++) {
            Object obj = tbMain.getValueAt(row, i);
            if (obj != null) {
                buf += obj.toString();
            }
            buf += ";";
        }

        putToClipboard(buf);
    }//GEN-LAST:event_pmiCopyRowActionPerformed

    private void pmiClearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiClearAllActionPerformed
        ((DefaultTableModel)tbMain.getModel()).setRowCount(0);
        printMessageCount();
    }//GEN-LAST:event_pmiClearAllActionPerformed

    private void pmiFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiFilterActionPerformed
        jFilterDialog.setLocation(getLocationOnScreen().x + getSize().width / 2 - jFilterDialog.getSize().width / 2,
                getLocationOnScreen().y + getSize().height / 2 - jFilterDialog.getSize().height / 2);
        jFilterDialog.setVisible(true);
    }//GEN-LAST:event_pmiFilterActionPerformed

    private void pmiResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiResetFiltersActionPerformed
        tbMain.setRowSorter(null);
        btResetFiltersActionPerformed(null);
        printMessageCount();
    }//GEN-LAST:event_pmiResetFiltersActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //reset at first
        tbMain.setRowSorter(null);
        
        //prepare the filter
        List<RowFilter<Object, Object>> filters = new ArrayList(3);

        List<RowFilter<Object, Object>> srcFilters = new ArrayList();
        DefaultListModel model = (DefaultListModel) listFilterSenders.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            CheckBoxItem cbi = (CheckBoxItem) model.getElementAt(i);
            if (cbi.isChecked()) {
                srcFilters.add(RowFilter.regexFilter(cbi.toString(), 1));
            }
        }
        if (!srcFilters.isEmpty()) {
            filters.add(RowFilter.orFilter(srcFilters));
        }

        List<RowFilter<Object, Object>> rcvFilters = new ArrayList();
        model = (DefaultListModel) listFilterRecievers.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            CheckBoxItem cbi = (CheckBoxItem) model.getElementAt(i);
            if (cbi.isChecked()) {
                rcvFilters.add(RowFilter.regexFilter(cbi.toString(), 2));
            }
        }
        if (!rcvFilters.isEmpty()) {
            filters.add(RowFilter.orFilter(rcvFilters));
        }

        List<RowFilter<Object, Object>> cmdFilters = new ArrayList();
        model = (DefaultListModel) listFilterCommands.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            CheckBoxItem cbi = (CheckBoxItem) model.getElementAt(i);
            if (cbi.isChecked()) {
                cmdFilters.add(RowFilter.regexFilter(cbi.toString(), 3));
            }
        }
        if (!cmdFilters.isEmpty()) {
            filters.add(RowFilter.orFilter(cmdFilters));
        }

        //apply filter if need
        if (!filters.isEmpty()) {
            TableRowSorter<TableModel> sorter = new TableRowSorter(tbMain.getModel());
            sorter.setRowFilter(RowFilter.andFilter(filters));
            tbMain.setRowSorter(sorter);
        }

        jFilterDialog.setVisible(false);
        
        printMessageCount();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void cbFilterAllSendersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterAllSendersActionPerformed
        DefaultListModel model = (DefaultListModel) listFilterSenders.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ((CheckBoxItem) model.getElementAt(i)).setChecked(cbFilterAllSenders.isSelected());
        }
        listFilterSenders.repaint();
    }//GEN-LAST:event_cbFilterAllSendersActionPerformed

    private void cbFilterAllRecieversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterAllRecieversActionPerformed
        DefaultListModel model = (DefaultListModel) listFilterRecievers.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ((CheckBoxItem) model.getElementAt(i)).setChecked(cbFilterAllRecievers.isSelected());
        }
        listFilterRecievers.repaint();
    }//GEN-LAST:event_cbFilterAllRecieversActionPerformed

    private void cbFilterAllCommandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterAllCommandsActionPerformed
        DefaultListModel model = (DefaultListModel) listFilterCommands.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ((CheckBoxItem) model.getElementAt(i)).setChecked(cbFilterAllCommands.isSelected());
        }
        listFilterCommands.repaint();
    }//GEN-LAST:event_cbFilterAllCommandsActionPerformed

    private void btResetFiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetFiltersActionPerformed
        cbFilterAllSenders.setSelected(false);
        cbFilterAllSendersActionPerformed(null);
        cbFilterAllRecievers.setSelected(false);
        cbFilterAllRecieversActionPerformed(null);
        cbFilterAllCommands.setSelected(false);
        cbFilterAllCommandsActionPerformed(null);
    }//GEN-LAST:event_btResetFiltersActionPerformed

    private void edDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edDataKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            btSendActionPerformed(null);
        }
    }//GEN-LAST:event_edDataKeyPressed

    private void trayImageFree() {
        if (tray != null && trayIcon != null){
            tray.remove(trayIcon);
            trayIcon = null;
        }
    }
    
    private boolean trayImageShow(Image trayImage, String tooltip) {
        if (tray != null) {
            try {
                trayImageFree();
                trayIcon = new TrayIcon(trayImage.getScaledInstance(16, -1, Image.SCALE_SMOOTH), tooltip);
                tray.add(trayIcon);

//                    trayIcon.addActionListener(new ActionListener() {
//                        public void actionPerformed(ActionEvent e) {
//                            System.out.println("we will show this message, when will click on balloon only");
//                        }
//                    });
                trayIcon.addMouseListener(new MouseListener() {

                    public void mouseClicked(MouseEvent e) {
                    }

                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if ((getExtendedState() & ICONIFIED) == ICONIFIED) {
                                trayOut();
                            } else {
                                trayIn();
                            }
                        }
                    }

                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            pmTray.setInvoker(pmTray);
                            pmTray.setVisible(true);
                            pmTray.setLocation(e.getX(), e.getY() - pmTray.getHeight());
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                });

                addWindowStateListener(new WindowStateListener() {
                    public void windowStateChanged(WindowEvent e) {
                        if ((e.getNewState() & ICONIFIED) == ICONIFIED) {
                            setVisible(false);
                        }
                    }
                });
                return true;
            } catch (AWTException ex) {
                Logger.getLogger(SupradinConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private void trayIn(){
        setVisible(false);
        setExtendedState(getExtendedState() | JFrame.ICONIFIED);
    }
    
    private void trayOut(){
        setVisible(true);
        setExtendedState(getExtendedState() & (~JFrame.ICONIFIED));
    }
    
    private void printMessageCount(){
        if (tbMain.getRowCount() != tbMain.getModel().getRowCount()){   //has filtered
            lbNumMessages.setText(String.format("Сообщений: %d / %d", tbMain.getRowCount(), tbMain.getModel().getRowCount()));
            lbNumMessages.setForeground(Color.RED);
        }else{
            lbNumMessages.setText(String.format("Сообщений: %d", tbMain.getRowCount()));
            lbNumMessages.setForeground(Color.BLACK);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();
            for (int idx = 0; idx < installedLookAndFeels.length; idx++) {
                if ("Windows".equals(installedLookAndFeels[idx].getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SupradinConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SupradinConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SupradinConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SupradinConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SupradinConsole();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btResetFilters;
    private javax.swing.JButton btSearchDevices;
    private javax.swing.JButton btSend;
    private javax.swing.JComboBox cbAddress;
    private javax.swing.JComboBox cbCommand;
    private javax.swing.JCheckBox cbFilterAllCommands;
    private javax.swing.JCheckBox cbFilterAllRecievers;
    private javax.swing.JCheckBox cbFilterAllSenders;
    private javax.swing.JComboBox cbPriority;
    private javax.swing.JTextField edData;
    private javax.swing.JButton jButton3;
    private javax.swing.JDialog jFilterDialog;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JLabel lbAddress;
    private javax.swing.JLabel lbCommand;
    private javax.swing.JLabel lbData;
    private javax.swing.JLabel lbFilterCommand;
    private javax.swing.JLabel lbFilterReciever;
    private javax.swing.JLabel lbFilterSender;
    private javax.swing.JLabel lbNumDevices;
    private javax.swing.JLabel lbNumMessages;
    private javax.swing.JLabel lbPriority;
    private javax.swing.JList listFilterCommands;
    private javax.swing.JList listFilterRecievers;
    private javax.swing.JList listFilterSenders;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miLightBathroomOff;
    private javax.swing.JMenuItem miLightBathroomOn;
    private javax.swing.JMenuItem miLightCloackroomOff;
    private javax.swing.JMenuItem miLightCloackroomOn;
    private javax.swing.JMenuItem miShowConsole;
    private javax.swing.JMenuItem miSoundRoomDecVolume;
    private javax.swing.JMenuItem miSoundRoomIncVolume;
    private javax.swing.JMenuItem miSoundRoomMute;
    private javax.swing.JMenuItem miSoundRoomSourceBT;
    private javax.swing.JMenuItem miSoundRoomSourceFM;
    private javax.swing.JMenuItem miSoundRoomSourcePC;
    private javax.swing.JMenu mnClimate;
    private javax.swing.JMenu mnFanBathroom;
    private javax.swing.JMenuItem mnFanBathroomOff;
    private javax.swing.JMenuItem mnFanBathroomOn;
    private javax.swing.JMenu mnLight;
    private javax.swing.JMenu mnLightBathroom;
    private javax.swing.JMenu mnLightCloackroom;
    private javax.swing.JMenu mnSound;
    private javax.swing.JMenu mnSoundRoom;
    private javax.swing.JMenu mnSoundRoomSource;
    private javax.swing.JPopupMenu pmMain;
    private javax.swing.JPopupMenu pmTray;
    private javax.swing.JMenuItem pmiClearAll;
    private javax.swing.JMenuItem pmiCopyAll;
    private javax.swing.JMenuItem pmiCopyCell;
    private javax.swing.JMenuItem pmiCopyRow;
    private javax.swing.JMenuItem pmiFilter;
    private javax.swing.JMenuItem pmiResetFilters;
    private javax.swing.JTable tbMain;
    // End of variables declaration//GEN-END:variables

}

class JColoredClunetTable extends JTable {

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);

        if (!isCellSelected(row, column)) {
            if (row % 2 == 0) {
                comp.setBackground(Color.LIGHT_GRAY);
            } else {
                comp.setBackground(Color.WHITE);
            }
        }
        return comp;
    }
}

class ComboBoxItem {

    private final int id;
    private final String value;

    public ComboBoxItem(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("0x%s - %s", DataFormat.byteToHex(id), value);
    }
}

class CheckBoxItem extends ComboBoxItem {

    private boolean checked = false;

    public CheckBoxItem(int id, String value) {
        super(id, value);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

class FilterListCellRenderer extends JCheckBox implements ListCellRenderer<Object> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        setEnabled(list.isEnabled());

        if (value != null && value instanceof CheckBoxItem) {
            CheckBoxItem cbi = (CheckBoxItem) value;
            setText(cbi.toString());
            setSelected(cbi.isChecked());
        }

        return this;
    }
    
    
    
    

}
