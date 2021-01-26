import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ApplyRule {
    public ApplyRule() {
    }

    public static int play(int flag, Vector<Vector<Object>> data) {
        boolean[] choose = new boolean[6];

        for (int i = 0; i < 6; ++i) {
            choose[i] = (flag & 1 << i) > 0;
        }

        File script = new File("file/script.sh");
        if (!script.exists()) {
            try {
                script.createNewFile();
            } catch (IOException var11) {
                var11.printStackTrace();
                return -1;
            }
        }

        FileWriter fileWritter;
        try {
            fileWritter = new FileWriter(script.getAbsolutePath());
        } catch (IOException var10) {
            var10.printStackTrace();
            return -1;
        }

        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

        try {
            bufferWritter.write("#!/bin/sh\niptables -F\n");

            for (Vector<Object> datum : data) {
                bufferWritter.write("iptables -I " + datum.get(0) + " ");
                String s = (String) datum.get(1);
                if (s.length() > 0) {
                    bufferWritter.write("-s " + s + " ");
                }

                s = (String) datum.get(3);
                if (s.length() > 0) {
                    bufferWritter.write("-p tcp --sport " + s + " ");
                }

                s = (String) datum.get(2);
                if (s.length() > 0) {
                    bufferWritter.write("-d " + s + " ");
                }

                s = (String) datum.get(4);
                if (s.length() > 0) {
                    bufferWritter.write("-p tcp --dport " + s + " ");
                }

                if (datum.get(5).equals("Allow")) {
                    bufferWritter.write("-j ACCEPT\n");
                } else {
                    bufferWritter.write("-j DROP\n");
                }
            }

            if (choose[0]) {
                bufferWritter.write("iptables -I INPUT -p tcp --syn -m connlimit --connlimit-above 15 -j DROP\n");
            }

            if (choose[1]) {
                bufferWritter.write("iptables -I INPUT -p tcp --dport 22 -m connlimit --connlimit-above 3 -j DROP\niptables -I INPUT -p tcp --dport 22 -m state --state NEW -m recent --set --name SSH\niptables -I INPUT -p tcp --dport 22 -m state --state NEW -m recent --update --seconds 300 --hitcount 3 --name SSH -j DROP\n");
            }

            if (choose[2]) {
                bufferWritter.write("iptables -I INPUT -p tcp --dport 80 -m connlimit --connlimit-above 30 -j DROP\n");
            }

            if (choose[3]) {
                bufferWritter.write("iptables -A OUTPUT -m state --state NEW -j DROP\n");
            }

            if (choose[4]) {
                bufferWritter.write("iptables -A OUTPUT -p tcp --sport 21 -j DROP\n");
                bufferWritter.write("iptables -A INPUT -p tcp --dport 21 -j DROP\n");
                bufferWritter.write("iptables -A OUTPUT -p tcp --sport 23 -j DROP\n");
                bufferWritter.write("iptables -A INPUT -p tcp --dport 23 -j DROP\n");
            }

            if (choose[5]) {
                bufferWritter.write("iptables -A INPUT -p icmp --icmp-type echo-request -m limit --limit 1/m -j ACCEPT");
            }

            bufferWritter.close();
//            if (!writeIptables()) {
//                return -3;
//            } else {
//                return 0;
//            }
        } catch (IOException var9) {
            var9.printStackTrace();
            return -2;
        }
//        ??
        return 0;
    }

//    private static boolean writeIptables() {
//        try {
//            Process p = Runtime.getRuntime().exec("sh file/script.sh");
//
//            try {
//                p.waitFor();
//                return p.exitValue() != 255;
//            } catch (InterruptedException var2) {
//                return false;
//            }
//        } catch (IOException var3) {
//            return false;
//        }
//    }
}
