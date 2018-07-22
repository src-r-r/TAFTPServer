Totally Awesome TFTP Server
===========================

To compile the server:

    $ bash compile_server.sh

To run the server:

    $ java -jar TATFTP.jar [port_number]

The default port is 69.

To upload a variety of files (empty, small, medium, large) to the server:

    $ bash test_server.txt [port_number]

Note on `run_server.sh`
-----------------------

It uses port 1027 by default since port 69 is unavailable on Linux. The
requirements call for port 69, which is why it is bound to it by default.
