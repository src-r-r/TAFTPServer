#!/bin/sh
cd src
javac io/github/srcrr/*.java
rm -f TATFTP.jar
jar -cvfe ../TATFTP.jar io.github.srcrr.TATFTPMain io/github/srcrr/*.class
