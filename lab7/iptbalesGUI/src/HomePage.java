import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class HomePage extends JFrame implements ActionListener {
    public static int rulenum = 6;
    JLabel tableTitle;
    JButton jb_add, jb_del, jb_submit;
    JCheckBox func_syn, func_dos, func_tcp, func_trojan, func_port, func_ping;
    Vector<Object> columnNames;
    Vector<Vector<Object>> rowData;
    JTable jt_rule;
    JScrollPane jsp_center;

    public HomePage() {
        this.setLayout(null);

        tableTitle = new JLabel("Filter rule");
        tableTitle.setBounds(20, 5, 100, 25);
        this.add(tableTitle, null);

        columnNames = new Vector<>();
        columnNames.add("Filter table");
        columnNames.add("Source IP");
        columnNames.add("Destination IP");
        columnNames.add("Source port");
        columnNames.add("Destination port");
        columnNames.add("Allow/Discard");
        rowData = new Vector<>();
        getConfig();
        jt_rule = new JTable(rowData, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jsp_center = new JScrollPane(jt_rule);
        jsp_center.setBounds(20, 30, 460, 200);
        this.add(jsp_center, null);

        jb_add = new JButton("Add to");
        jb_add.setBounds(20, 240, 50, 25);
        jb_add.addActionListener(this);
        this.add(jb_add, null);

        jb_del = new JButton("Delete");
        jb_del.setBounds(100, 240, 50, 25);
        jb_del.addActionListener(this);
        this.add(jb_del, null);

        func_syn = new JCheckBox("Limit the number of SYN requests");
        func_syn.setBounds(20, 270, 230, 25);
        this.add(func_syn, null);

        func_dos = new JCheckBox("Prevent DOS attacks");
        func_dos.setBounds(20, 300, 230, 25);
        this.add(func_dos, null);

        func_tcp = new JCheckBox("Limit single IP access");
        func_tcp.setBounds(20, 330, 230, 25);
        this.add(func_tcp, null);

        func_trojan = new JCheckBox("Prevent rebound Trojans");
        func_trojan.setBounds(250, 270, 400, 25);
        this.add(func_trojan, null);

        func_port = new JCheckBox("Ban FTP, Telnet");
        func_port.setBounds(250, 300, 400, 25);
        this.add(func_port, null);

        func_ping = new JCheckBox("Prevent ping attacks");
        func_ping.setBounds(250, 330, 400, 25);
        this.add(func_ping, null);

        jb_submit = new JButton();
        jb_submit.setText("Application rules");
        jb_submit.setBounds(380, 380, 100, 25);
        jb_submit.addActionListener(this);
        this.add(jb_submit);

        this.setBounds(0, 0, 500, 450);
        this.setVisible(true);
        this.setTitle("IpTables");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    public static boolean testRoot() {
        Process p;

        try {
            p = Runtime.getRuntime().exec("su"); // Allow command su

            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
            os.writeBytes("exit\n");
            os.flush();

            try {
                p.waitFor();
                // Exit normally, indicating that it is an administrator
                // Otherwise it is not an administrator
                return p.exitValue() != 255;
            } catch (InterruptedException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Set appearance
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isRoot = testRoot(); // Set whether the test is an administrator
        if (isRoot) {
            HomePage page = new HomePage();
        } else {
            JOptionPane.showMessageDialog(null, "The program needs to be run with administrator rights，please sudo java -jar name-of.jar", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb_add) {
            new AddRulePage(this);
        } else if (e.getSource() == jb_del) {
            int row = jt_rule.getSelectedRow();
            deleteConfig(row);
            refresh();
        } else if (e.getSource() == jb_submit) {
            boolean[] choose = {
                    func_syn.isSelected(),
                    func_dos.isSelected(),
                    func_tcp.isSelected(),
                    func_trojan.isSelected(),
                    func_port.isSelected(),
                    func_ping.isSelected()
            };
            int flag = 0;
            for (int i = choose.length - 1; i >= 0; i--)
                flag = 2 * flag + (choose[i] ? 1 : 0);
            if (ApplyRule.play(flag, rowData) == 0)
                JOptionPane.showMessageDialog(null, "Rule applied successfully", "prompt",
                        JOptionPane.INFORMATION_MESSAGE);
            else if (ApplyRule.play(flag, rowData) == -1)
                JOptionPane.showMessageDialog(null, "Unable to open file，Rule application failed", "prompt",
                        JOptionPane.ERROR_MESSAGE);
            else if (ApplyRule.play(flag, rowData) == -2)
                JOptionPane.showMessageDialog(null, "Error writing file，Rule application failed", "prompt",
                        JOptionPane.ERROR_MESSAGE);
            else if (ApplyRule.play(flag, rowData) == -3)
                JOptionPane.showMessageDialog(null, "Failed to write to iptables，Please check permissions", "prompt",
                        JOptionPane.ERROR_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, "Rule application failed", "prompt",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        rowData.removeAllElements();
        getConfig();
        jt_rule.updateUI();
    }

    private void getConfig() {
        File f = new File("file");
        if (!f.exists())
            f.mkdir();
        File config = new File("file/config.txt"); // Open config.txt
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(config)); // Read configuration file
            String rule;
            while ((rule = br.readLine()) != null) {
                String[] value = rule.split("#");
                Vector<Object> vector = new Vector<>();
                vector.add((value[0].trim().equals("0")) ? "INPUT" :
                        (value[0].trim().equals("1") ? "FORWARD" : "OUTPUT"));
                vector.addAll(Arrays.asList(value).subList(1, value.length - 1));
                vector.add((value[value.length - 1].trim().equals("0")) ? "throw away" : "allow");
                rowData.add(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteConfig(int row) {
        File file = new File("file/config.txt"); // Open the configuration file
        BufferedReader br = null;
        FileOutputStream out = null;
        List<String> list = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(file)); // Read configuration file，Skip the line to be deleted
            int tp = -1;
            String text = br.readLine();
            tp++;
            while (text != null) {
                if (tp != row) {
                    list.add(text + "\r\n");
                }
                text = br.readLine();
                tp++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out = new FileOutputStream("file/config.txt"); // Write back configuration file
            for (String s : list) {
                out.write(s.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
