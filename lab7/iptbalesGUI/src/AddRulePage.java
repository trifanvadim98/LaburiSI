import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AddRulePage extends JFrame implements ActionListener {
    JPanel jp_main;
    JPanel jp_center;
    JComboBox comboBox1;
    JComboBox comboBox2;
    JLabel[] jl_colname;
    JTextField[] jtf_value;
    JButton jb_save_and_exit;
    HomePage hp;

    public AddRulePage(HomePage hp) {
        this.hp = hp;
        this.jb_save_and_exit = new JButton("Save and exit");
        this.jb_save_and_exit.addActionListener(this);
        int rulenum = HomePage.rulenum;
        this.jl_colname = new JLabel[rulenum];
        this.jl_colname[0] = new JLabel("Filter table");
        this.jl_colname[1] = new JLabel("Source IP");
        this.jl_colname[2] = new JLabel("Destination IP");
        this.jl_colname[3] = new JLabel("Source port");
        this.jl_colname[4] = new JLabel("Destination port");
        this.jl_colname[5] = new JLabel("Allow/Discard");
        String[] choose1 = new String[]{"Throw away", "Allow"};
        this.comboBox1 = new JComboBox(choose1);
        this.jtf_value = new JTextField[rulenum - 2];

        for (int i = 0; i < rulenum - 2; ++i) {
            this.jtf_value[i] = new JTextField(10);
            this.jtf_value[i].setText("");
        }

        String[] choose2 = new String[]{"INPUT", "FORWARD", "OUTPUT"};
        this.comboBox2 = new JComboBox(choose2);
        this.jp_center = new JPanel(new GridLayout(rulenum, 2));
        this.jp_center.add(this.jl_colname[0]);
        this.jp_center.add(this.comboBox2);

        for (int i = 1; i < rulenum - 1; ++i) {
            this.jp_center.add(this.jl_colname[i]);
            this.jp_center.add(this.jtf_value[i - 1]);
        }

        this.jp_center.add(this.jl_colname[rulenum - 1]);
        this.jp_center.add(this.comboBox1);
        this.jp_main = new JPanel();
        this.jp_main.add(this.jp_center);
        this.jp_main.add(this.jb_save_and_exit, "South");
        this.add(this.jp_main);
        this.setBounds(100, 100, 350, 220);
        this.setVisible(true);
        this.setTitle("firewall");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.jb_save_and_exit) {
            if (this.issValid()) {
                this.writeInConfig();
                this.hp.refresh();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Please check the format of the input information", "prompt", 0);
            }
        }

    }

    private void writeInConfig() {
        File config = new File("file/config.txt");
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config, true)));
            StringBuilder rule = new StringBuilder();
            int table = this.comboBox2.getSelectedIndex();
            rule.append(table).append("#");

            for (int i = 0; i < HomePage.rulenum - 2; ++i) {
                rule.append(this.jtf_value[i].getText()).append("#");
            }

            String isEnable = (String) this.comboBox1.getSelectedItem();
            assert isEnable != null;
            if (isEnable.equals("Throw away")) {
                rule.append("0\n");
            } else {
                rule.append("1\n");
            }

            bw.write(rule.toString());
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

    }

    private boolean issValid() {
        String tmp = "";
        int allNull = 0;

        int i;
        for (i = 0; i < 2; ++i) {
            tmp = this.jtf_value[i].getText();
            if (tmp != null && !tmp.equals("")) {
                String[] ipmask = tmp.split("/");
                if (ipmask.length > 2) {
                    return false;
                }

                if (ipmask.length == 2) {
                    try {
                        int num = Integer.parseInt(ipmask[1]);
                        if (num < 0 || num > 32) {
                            return false;
                        }
                    } catch (Exception var10) {
                        return false;
                    }

                    tmp = ipmask[0];
                }

                String[] parts = tmp.split("\\.");
                if (parts.length != 4) {
                    return false;
                }

                for (int j = 0; j < 4; ++j) {
                    try {
                        int num = Integer.parseInt(parts[j]);
                        if (num < 0 || num > 255) {
                            return false;
                        }
                    } catch (Exception var9) {
                        return false;
                    }
                }
            } else {
                ++allNull;
            }
        }

        for (i = 2; i < 4; ++i) {
            try {
                tmp = this.jtf_value[i].getText();
                if (tmp != null && !tmp.equals("")) {
                    int num = Integer.parseInt(tmp);
                    if (num < 0 || num > 65535) {
                        return false;
                    }
                } else {
                    ++allNull;
                }
            } catch (Exception var8) {
                return false;
            }
        }

        return allNull != 4;
    }
}
