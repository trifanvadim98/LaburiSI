#!/bin/sh
iptables -F
iptables -I INPUT -s 10.10.0.2 -d 172.0.0.1 -j DROP
iptables -I OUTPUT -s 172.0.0.1 -d 10.10.0.3 -j DROP
iptables -I INPUT -s 176.16.0.10 -j DROP
